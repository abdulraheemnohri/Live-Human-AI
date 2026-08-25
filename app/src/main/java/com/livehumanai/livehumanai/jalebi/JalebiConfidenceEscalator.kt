package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.nativebridge.NativeBridge

/** Bounded confidence escalation: small -> medium -> large -> verification. */
class JalebiConfidenceEscalator(
    private val bridge: NativeBridge = NativeBridge.getInstance(),
    private val governor: JalebiRuntimeGovernor = JalebiRuntimeGovernor(bridge)
) {
    data class Result(
        val answer: String,
        val confidence: Float,
        val model: String,
        val escalations: Int,
        val verified: Boolean,
        val stoppedReason: String
    )

    fun run(
        prompt: String,
        initialModel: String,
        threshold: Float = 0.90f,
        maxEscalations: Int = 2,
        maxTokens: Int = 512,
        confidence: (answer: String, model: String) -> Float
    ): Result {
        val models = listOf(initialModel, "qwen3-1.7b-q4", "qwen3-4b-q4").distinct()
        var answer = ""
        var score = 0f
        var used = initialModel
        var escalations = 0

        for (index in models.indices) {
            if (index > maxEscalations) break
            val route = JalebiModelRouter(bridge, governor).route(models[index], maxTokens)
            if (!route.allowed) return Result(answer, score, used, escalations, false, "resource_policy")
            answer = bridge.generate(prompt, route.modelName, 0.2f, route.maxTokens)
            used = route.modelName
            score = confidence(answer, used).coerceIn(0f, 1f)
            if (score >= threshold) {
                val verified = verify(answer, used, route.maxTokens, confidence)
                return Result(answer, score, used, escalations, verified, if (verified) "verified" else "verification_failed")
            }
            if (index < models.lastIndex) escalations++
        }
        return Result(answer, score, used, escalations, false, "confidence_below_threshold")
    }

    private fun verify(
        answer: String,
        model: String,
        maxTokens: Int,
        confidence: (String, String) -> Float
    ): Boolean {
        if (answer.isBlank()) return false
        val route = JalebiModelRouter(bridge, governor).route("small", minOf(256, maxTokens))
        if (!route.allowed) return false
        val check = bridge.generate(
            "Verify this answer. Return only PASS if it is internally consistent and supported by the provided answer:\n$answer",
            route.modelName,
            0f,
            route.maxTokens
        )
        return check.contains("PASS", ignoreCase = true) && confidence(answer, model) >= 0.90f
    }
}
