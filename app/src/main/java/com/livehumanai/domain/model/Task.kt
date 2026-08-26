package com.livehumanai.domain.model

data class Task(
    val id: String,
    val goal: String,
    val status: TaskStatus,
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val iterationCount: Int = 0,
    val maxIterations: Int = 10,
    val failureReason: String? = null,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val deadline: Long? = null,
    val memoryBudget: Long? = null,
    val tokenBudget: Int? = null,
    val toolBudget: Int? = null,
    val successCriteria: String? = null,
    val modelUsed: String? = null,
    val confidence: Float? = null
)

enum class TaskStatus {
    QUEUED,
    RUNNING,
    PAUSED,
    WAITING,
    COMPLETED,
    FAILED,
    CANCELLED
}

enum class TaskPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}
