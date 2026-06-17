package com.example.myapplication.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AgeGoRepository
import com.example.myapplication.data.Announcement
import com.example.myapplication.data.CommunityPost
import com.example.myapplication.data.CommunityComment
import com.example.myapplication.data.DirectoryItem
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
    val groups: List<DirectoryItem> = emptyList(),
    val routines: List<DirectoryItem> = emptyList(),
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
                isLoading = false,
                students = data.students,
                workouts = data.workouts,
                announcements = data.announcements,
                communityPosts = data.communityPosts,
                events = data.events,
                groups = data.groups,
                routines = data.routines,
                isDemo = data.isDemo,
                message = data.message
            )
        }
    }

    fun saveStudent(student: Student) {
        viewModelScope.launch {
            val saved = repository.saveStudent(student)
            _uiState.update { state ->
                val normalized = saved.copy(status = saved.status.ifBlank { "active" })
                val exists = state.students.any { it.id == normalized.id }
                state.copy(students = if (exists) state.students.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.students)
            }
        }
    }

    fun deleteStudent(studentId: String) {
        viewModelScope.launch {
            repository.deleteStudent(studentId)
            _uiState.update { state -> state.copy(students = state.students.filterNot { it.id == studentId }) }
        }
    }

    fun saveWorkout(workout: Workout) {
        viewModelScope.launch {
            val normalized = repository.saveWorkout(workout)
            _uiState.update { state ->
                val exists = state.workouts.any { it.id == normalized.id }
                state.copy(workouts = if (exists) state.workouts.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.workouts)
            }
        }
    }

    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            repository.deleteWorkout(workoutId)
            _uiState.update { state -> state.copy(workouts = state.workouts.filterNot { it.id == workoutId }) }
        }
    }

    fun saveEvent(event: Event) {
        viewModelScope.launch {
            val normalized = repository.saveEvent(event)
            _uiState.update { state ->
                val exists = state.events.any { it.id == normalized.id }
                state.copy(events = if (exists) state.events.map { if (it.id == normalized.id) normalized else it } else listOf(normalized) + state.events)
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            repository.deleteEvent(eventId)
            _uiState.update { state -> state.copy(events = state.events.filterNot { it.id == eventId }) }
        }
    }

    fun saveGroup(group: DirectoryItem) {
        viewModelScope.launch {
            val saved = repository.saveGroup(group)
            _uiState.update { state ->
                val exists = state.groups.any { it.id == saved.id }
                state.copy(groups = if (exists) state.groups.map { if (it.id == saved.id) saved else it } else listOf(saved) + state.groups)
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroup(groupId)
            _uiState.update { state -> state.copy(groups = state.groups.filterNot { it.id == groupId }) }
        }
    }

    fun saveRoutine(routine: DirectoryItem) {
        viewModelScope.launch {
            val saved = repository.saveRoutine(routine)
            _uiState.update { state ->
                val exists = state.routines.any { it.id == saved.id }
                state.copy(routines = if (exists) state.routines.map { if (it.id == saved.id) saved else it } else listOf(saved) + state.routines)
            }
        }
    }

    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
            _uiState.update { state -> state.copy(routines = state.routines.filterNot { it.id == routineId }) }
        }
    }

    fun saveCommunityPost(post: CommunityPost) {
        val tempId = "local-${java.util.UUID.randomUUID()}"
        val localPost = post.copy(id = tempId)
        _uiState.update { state -> state.copy(communityPosts = listOf(localPost) + state.communityPosts) }
        viewModelScope.launch {
            val normalized = repository.saveCommunityPost(post)
            _uiState.update { state ->
                state.copy(
                    communityPosts = state.communityPosts.map { if (it.id == tempId) normalized else it }
                )
            }
        }
    }

    fun toggleCommunityLike(postId: String) {
        viewModelScope.launch { repository.toggleCommunityLike(postId) }
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
        viewModelScope.launch { repository.addCommunityComment(postId, content) }
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
        viewModelScope.launch { repository.addCommunityComment(postId, content, commentId) }
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
        viewModelScope.launch { repository.toggleCommunityCommentLike(commentId) }
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
        viewModelScope.launch { repository.shareCommunityPost(postId) }
        _uiState.update { state ->
            state.copy(
                communityPosts = state.communityPosts.map {
                    if (it.id == postId) it.copy(shares = it.shares + 1) else it
                }
            )
        }
    }

    suspend fun uploadMedia(context: Context, uri: Uri): String =
        repository.uploadMedia(context.contentResolver, uri)
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
