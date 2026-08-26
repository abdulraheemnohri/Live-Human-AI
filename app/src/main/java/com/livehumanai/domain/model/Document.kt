package com.livehumanai.domain.model

data class Document(
    val id: String,
    val title: String,
    val category: String = "Documents",
    val sizeBytes: Long,
    val createdAt: Long,
    val updatedAt: Long = System.currentTimeMillis(),
    val chunkCount: Int = 0,
    val filePath: String? = null,
    val mimeType: String? = null,
    val isIndexed: Boolean = false,
    val language: String? = null,
    val source: DocumentSource = DocumentSource.IMPORTED,
    val embeddingModel: String? = null
)

enum class DocumentSource {
    IMPORTED,
    CAPTURED,
    GENERATED,
    SHARED
}
