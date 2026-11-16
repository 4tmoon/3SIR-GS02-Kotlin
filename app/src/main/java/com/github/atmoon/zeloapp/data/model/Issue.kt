package com.github.atmoon.zeloapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.abs

object IssueCategory {
    const val ACCESSIBILITY = "Acessibilidade"
    const val SAFETY = "Segurança"
    const val INFRASTRUCTURE = "Infraestrutura"
    val all = listOf(ACCESSIBILITY, SAFETY, INFRASTRUCTURE)
}

object IssueStatus {
    const val COLLECTING = "Em Coleta"
    const val FORMAL = "Solicitação Formal Gerada"
    const val ACCEPTED = "Aceita pela prefeitura"
}

object IssueSeverity {
    const val LOW = "low"
    const val MEDIUM = "medium"
    const val HIGH = "high"
    val all = listOf(LOW, MEDIUM, HIGH)

    fun label(value: String): String = when (value) {
        LOW -> "Baixa"
        HIGH -> "Alta"
        else -> "Média"
    }

    fun rank(value: String): Int = when (value) {
        HIGH -> 3
        MEDIUM -> 2
        else -> 1
    }
}

enum class IssueSort(val label: String) {
    RECENT("Mais recentes"),
    MOST_SUPPORTED("Mais apoiadas"),
    NEAR_GOAL("Perto da meta"),
    URGENT("Mais urgentes")
}

@Entity(tableName = "issues")
data class Issue(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val address: String,
    val likes: Int,
    val activeRegionalUsers: Int,
    val status: String,
    val severity: String = IssueSeverity.MEDIUM,
    val supportedByMe: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val reachedAt: Long? = null,
    val noticeGeneratedAt: Long? = null,
    val decisionAt: Long? = null,
    val acceptedByCity: Boolean = false
) {
    companion object {
        const val THRESHOLD_RATIO = 0.8
    }

    val goalSupports: Int
        get() = (activeRegionalUsers * 8 + 9) / 10

    val supportsMissing: Int
        get() = if (likes >= goalSupports) 0 else goalSupports - likes

    val reachedThreshold: Boolean
        get() = likes >= goalSupports

    val hasNotice: Boolean
        get() = noticeGeneratedAt != null

    val progress: Float
        get() = if (activeRegionalUsers == 0) 0f
        else (likes.toFloat() / activeRegionalUsers.toFloat()).coerceIn(0f, 1f)

    val noticeNumber: String
        get() {
            val tail = if (id.length > 4) id.takeLast(4) else id.padStart(4, '0')
            return "$tail/2026"
        }

    val verificationCode: String
        get() {
            val raw = abs("$id|$title|$address|$activeRegionalUsers".hashCode())
            return "ZL-${raw.toString(16).uppercase().padStart(8, '0')}"
        }
}