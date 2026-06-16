package com.example.myapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AgeGoRepository
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.CommunityPost
import com.example.myapplication.data.CommunityComment
import com.example.myapplication.data.CommunityPostType
import com.example.myapplication.data.RepositoryProvider
import com.example.myapplication.data.Student
import com.example.myapplication.data.Workout
import com.example.myapplication.data.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AgeGoUiState(
    val isLoading: Boolean = true,
    val students: List<Student> = emptyList(),
    val workouts: List<Workout> = emptyList(),
    val announcements: List<Announcement> = emptyList(),
    val communityPosts: List<CommunityPost> = emptyList(),
    val events: List<Event> = emptyList(),
    val isDemo: Boolean = false,
    val message: String? = null
)

class AgeGoViewModel(
    private val repository: AgeGoRepository = RepositoryProvider.create()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AgeGoUiState())
    val uiState: StateFlow<AgeGoUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data = repository.loadDashboard()
            _uiState.value = AgeGoUiState(
                students = data.students,
                workouts = data.workouts,
                announcements = data.announcements,
                communityPosts = demoCommunityPosts(data.workouts),
                events = data.events,
                isDemo = data.isDemo,
                message = data.message
            )
        }
    }

    fun saveStudent(student: Student) {
        _uiState.update { state ->
            val normalized = student.copy(
                id = student.id.ifBlank { java.util.UUID.randomUUID().toString() },
                status = student.status.ifBlank { "active" }
            )
            val exists = state.students.any { it.id == normalized.id }
            state.copy(
                students = if (exists) {
                    state.students.map { if (it.id == normalized.id) normalized else it }
                } else {
                    listOf(normalized) + state.students
                }
            )
        }
    }

    fun deleteStudent(studentId: String) {
        _uiState.update { state ->
            state.copy(students = state.students.filterNot { it.id == studentId })
        }
    }

    fun saveWorkout(workout: Workout) {
        _uiState.update { state ->
            val normalized = workout.copy(id = workout.id.ifBlank { java.util.UUID.randomUUID().toString() })
            val exists = state.workouts.any { it.id == normalized.id }
            state.copy(
                workouts = if (exists) {
                    state.workouts.map { if (it.id == normalized.id) normalized else it }
                } else {
                    listOf(normalized) + state.workouts
                }
            )
        }
    }

    fun deleteWorkout(workoutId: String) {
        _uiState.update { state ->
            state.copy(workouts = state.workouts.filterNot { it.id == workoutId })
        }
    }

    fun saveEvent(event: Event) {
        _uiState.update { state ->
            val normalized = event.copy(id = event.id.ifBlank { java.util.UUID.randomUUID().toString() })
            val exists = state.events.any { it.id == normalized.id }
            state.copy(
                events = if (exists) {
                    state.events.map { if (it.id == normalized.id) normalized else it }
                } else {
                    listOf(normalized) + state.events
                }
            )
        }
    }

    fun deleteEvent(eventId: String) {
        _uiState.update { state ->
            state.copy(events = state.events.filterNot { it.id == eventId })
        }
    }

    fun saveCommunityPost(post: CommunityPost) {
        _uiState.update { state ->
            val normalized = post.copy(id = post.id.ifBlank { java.util.UUID.randomUUID().toString() })
            state.copy(communityPosts = listOf(normalized) + state.communityPosts)
        }
    }

    fun toggleCommunityLike(postId: String) {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id != postId) {
                        post
                    } else {
                        post.copy(
                            liked = !post.liked,
                            likes = (post.likes + if (post.liked) -1 else 1).coerceAtLeast(0)
                        )
                    }
                }
            )
        }
    }

    fun addCommunityComment(postId: String, content: String = "Novo comentario") {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            comments = post.comments + 1,
                            commentThreads = post.commentThreads + CommunityComment(
                                id = java.util.UUID.randomUUID().toString(),
                                authorName = "Voce",
                                content = content
                            )
                        )
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun replyCommunityComment(postId: String, commentId: String, content: String) {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(
                            comments = post.comments + 1,
                            commentThreads = post.commentThreads.addReplyToComment(
                                commentId,
                                CommunityComment(
                                    id = java.util.UUID.randomUUID().toString(),
                                    authorName = "Voce",
                                    content = content
                                )
                            )
                        )
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun toggleCommunityCommentLike(postId: String, commentId: String) {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map { post ->
                    if (post.id == postId) {
                        post.copy(commentThreads = post.commentThreads.toggleCommentLike(commentId))
                    } else {
                        post
                    }
                }
            )
        }
    }

    fun shareCommunityPost(postId: String) {
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map {
                    if (it.id == postId) it.copy(shares = it.shares + 1) else it
                }
            )
        }
    }
}

private fun List<CommunityComment>.addReplyToComment(
    commentId: String,
    reply: CommunityComment
): List<CommunityComment> = map { comment ->
    when {
        comment.id == commentId -> comment.copy(replies = comment.replies + reply)
        comment.replies.isNotEmpty() -> comment.copy(replies = comment.replies.addReplyToComment(commentId, reply))
        else -> comment
    }
}

private fun List<CommunityComment>.toggleCommentLike(commentId: String): List<CommunityComment> = map { comment ->
    when {
        comment.id == commentId -> comment.copy(
            liked = !comment.liked,
            likes = (comment.likes + if (comment.liked) -1 else 1).coerceAtLeast(0)
        )
        comment.replies.isNotEmpty() -> comment.copy(replies = comment.replies.toggleCommentLike(commentId))
        else -> comment
    }
}

private fun demoCommunityPosts(workouts: List<Workout>): List<CommunityPost> = listOf(
    CommunityPost(
        id = "post-1",
        type = CommunityPostType.POST,
        title = "Recado do treino",
        content = "Amanha teremos rodagem leve. Hidratem bem e cheguem 10 minutos antes.",
        target = "groups",
        authorName = "Marina Alves",
        commentThreads = listOf(
            CommunityComment("comment-1", "Rafael Souza", "Confirmado, prof!"),
            CommunityComment("comment-2", "Camila Lima", "Vou chegar mais cedo para aquecer.")
        ),
        likes = 12,
        comments = 3,
        shares = 1
    ),
    CommunityPost(
        id = "poll-1",
        type = CommunityPostType.POLL,
        title = "Melhor horario para o longao",
        content = "Escolha o horario que funciona melhor para o grupo.",
        target = "events",
        authorName = "Coach Ana",
        commentThreads = listOf(
            CommunityComment("comment-3", "Bruno Martins", "6h30 fica perfeito."),
            CommunityComment("comment-4", "Marina Alves", "Prefiro 7h, mas consigo adaptar.")
        ),
        pollOptions = listOf("6h", "6h30", "7h"),
        likes = 8,
        comments = 5
    ),
    CommunityPost(
        id = "challenge-1",
        type = CommunityPostType.CHALLENGE,
        title = "Desafio da semana",
        content = "Complete o treino proposto e compartilhe seu resultado na comunidade.",
        target = "groups",
        authorName = "Coach Ana",
        commentThreads = listOf(
            CommunityComment("comment-5", "Camila Lima", "Esse eu vou fazer hoje."),
            CommunityComment("comment-6", "Rafael Souza", "Bora subir esse pace.")
        ),
        linkedWorkoutId = workouts.firstOrNull()?.id,
        likes = 15,
        comments = 7,
        shares = 4
    )
)
