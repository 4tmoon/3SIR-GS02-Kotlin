package com.github.atmoon.zeloapp.data.repository

import com.github.atmoon.zeloapp.data.local.IssueDao
import com.github.atmoon.zeloapp.data.model.Comment
import com.github.atmoon.zeloapp.data.model.Issue
import com.github.atmoon.zeloapp.data.model.IssueCategory
import com.github.atmoon.zeloapp.data.model.IssueSeverity
import com.github.atmoon.zeloapp.data.model.IssueStatus
import kotlinx.coroutines.flow.Flow

class IssueRepository(private val dao: IssueDao) {

    fun observeIssues(): Flow<List<Issue>> = dao.observeAllIssues()

    fun observeComments(issueId: String): Flow<List<Comment>> = dao.observeComments(issueId)

    suspend fun ensureInitialData() {
        if (dao.countIssues() > 0) return
        val now = System.currentTimeMillis()
        fun daysAgo(days: Int) = now - days * 86_400_000L

        val initialIssues = listOf(
            Issue(
                id = "1",
                title = "Calçada esburacada",
                description = "Impossível passar com cadeira de rodas devido a buracos profundos.",
                category = IssueCategory.ACCESSIBILITY,
                address = "Av. Paulista, 1000 - São Paulo, SP",
                likes = 35,
                activeRegionalUsers = 50,
                status = IssueStatus.COLLECTING,
                severity = IssueSeverity.HIGH,
                createdAt = daysAgo(3)
            ),
            Issue(
                id = "8",
                title = "Piso tátil danificado",
                description = "O piso tátil está quebrado e pessoas com deficiência visual correm risco de queda.",
                category = IssueCategory.ACCESSIBILITY,
                address = "Rua Vergueiro, 1200 - São Paulo, SP",
                likes = 39,
                activeRegionalUsers = 50,
                status = IssueStatus.COLLECTING,
                severity = IssueSeverity.HIGH,
                createdAt = daysAgo(6) // Falta apenas 1 apoio para atingir 80% (40/50)!
            ),
            Issue(
                id = "4",
                title = "Buraco na avenida",
                description = "Buraco grande no meio da pista, oferecendo risco grave de acidentes.",
                category = IssueCategory.INFRASTRUCTURE,
                address = "Av. Pedro Álvares Cabral - São Paulo, SP",
                likes = 20,
                activeRegionalUsers = 60,
                status = IssueStatus.COLLECTING,
                severity = IssueSeverity.HIGH,
                createdAt = daysAgo(1)
            ),
            Issue(
                id = "6",
                title = "Bueiro entupido",
                description = "A água da chuva não escoa e alaga a rua inteira em dias de temporal.",
                category = IssueCategory.INFRASTRUCTURE,
                address = "Rua da Consolação, 1500 - São Paulo, SP",
                likes = 41,
                activeRegionalUsers = 50,
                status = IssueStatus.FORMAL,
                severity = IssueSeverity.MEDIUM,
                createdAt = daysAgo(9),
                reachedAt = daysAgo(2),
                noticeGeneratedAt = daysAgo(2)
            ),
            Issue(
                id = "2",
                title = "Poste sem luz",
                description = "Escuridão total à noite, comprometendo a segurança dos pedestres.",
                category = IssueCategory.SAFETY,
                address = "Rua Augusta, 500 - São Paulo, SP",
                likes = 42,
                activeRegionalUsers = 50,
                status = IssueStatus.ACCEPTED,
                severity = IssueSeverity.HIGH,
                createdAt = daysAgo(12),
                reachedAt = daysAgo(6),
                noticeGeneratedAt = daysAgo(6),
                decisionAt = daysAgo(2),
                acceptedByCity = true
            )
        )

        val initialComments = listOf(
            Comment("c1", "1", "Marina S.", "Passo por aqui todo dia e é muito difícil para quem usa cadeira de rodas.", daysAgo(2)),
            Comment("c2", "1", "Carlos A.", "Já apoiei! Espero que resolvam logo.", daysAgo(1)),
            Comment("c3", "8", "Joana P.", "Falta apenas 1 apoio para gerar o ofício oficial!", now - 3_600_000L),
            Comment("c4", "2", "Rafael M.", "Que bom que a prefeitura aceitou a solicitação!", now - 7_200_000L)
        )

        dao.insertAllIssues(initialIssues)
        dao.insertAllComments(initialComments)
    }

    suspend fun toggleSupport(issue: Issue): Issue {
        val supported = !issue.supportedByMe
        val updatedLikes = (issue.likes + if (supported) 1 else -1).coerceAtLeast(0)
        var updated = issue.copy(likes = updatedLikes, supportedByMe = supported)

        if (updated.reachedAt == null) {
            if (updated.reachedThreshold) {
                val now = System.currentTimeMillis()
                updated = updated.copy(
                    status = IssueStatus.FORMAL,
                    reachedAt = now,
                    noticeGeneratedAt = now
                )
            } else {
                updated = updated.copy(status = IssueStatus.COLLECTING)
            }
        }
        dao.updateIssue(updated)
        return updated
    }

    suspend fun createIssue(
        title: String,
        description: String,
        category: String,
        address: String,
        severity: String
    ): Issue {
        val now = System.currentTimeMillis()
        val issue = Issue(
            id = now.toString(),
            title = title.trim(),
            description = description.trim(),
            category = category,
            address = address.trim(),
            likes = 1,
            activeRegionalUsers = 50,
            status = IssueStatus.COLLECTING,
            severity = severity,
            supportedByMe = true,
            createdAt = now
        )
        dao.insertIssue(issue)
        return issue
    }

    suspend fun deleteIssue(issue: Issue) {
        dao.deleteIssue(issue)
    }

    suspend fun addComment(issueId: String, text: String) {
        val now = System.currentTimeMillis()
        dao.insertComment(
            Comment(
                id = now.toString(),
                issueId = issueId,
                author = "Você",
                text = text.trim(),
                createdAt = now
            )
        )
    }
}