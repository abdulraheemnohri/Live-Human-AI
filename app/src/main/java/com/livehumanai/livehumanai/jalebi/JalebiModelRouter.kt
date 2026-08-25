package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.nativebridge.NativeBridge

/** Selects a model class from the live resource budget; never bypasses the native governor. */
class JalebiModelRouter(
    private val bridge: NativeBridge = NativeBridge.getInstance(),
    private val governor: JalebiRuntimeGovernor = JalebiRuntimeGovernor(bridge)
) {
    data class Route(val modelName: String, val maxTokens: Int, val allowed: Boolean, val reason: String)

    fun route(requestedModel: String, requestedTokens: Int = 512): Route {
        val limits = governor.prepareForExpensiveWork()
        if (limits.maxTokens <= 128 && !limits.allowVision && !limits.allowSpeech) {
            return Route("", 128, false, "critical_resources")
        }
        if (!limits.allowLargeModel && requestedModel.contains("large", ignoreCase = true)) {
            return Route("small", minOf(256, limits.maxTokens), true, "device_resource_limit")
        }
        return Route(requestedModel, minOf(requestedTokens.coerceAtLeast(1), limits.maxTokens), true, "resource_policy")
    }

    fun generate(prompt: String, requestedModel: String = "", requestedTokens: Int = 512): String {
        val route = route(requestedModel, requestedTokens)
        if (!route.allowed) return ""
        return bridge.generate(prompt, route.modelName, maxTokens = route.maxTokens)
    }
}
