package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.data.local.dao.TripImageDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.entity.TripImageEntity
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

@HiltViewModel
class TripGalleryViewModel @Inject constructor(
    private val tripImageDao: TripImageDao,
    private val userDao: UserDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val selectedTripId = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val tripImages: StateFlow<List<TripImageEntity>> = selectedTripId
        .flatMapLatest { tripId ->
            val userId = currentUserId()
            if (tripId == null) {
                flowOf(emptyList())
            } else {
                tripImageDao.observeImagesForTrip(tripId, userId)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun loadTrip(tripId: String) {
        selectedTripId.value = tripId.toLongOrNull()
    }

    fun attachImages(tripId: String, imageUris: List<String>) {
        val numericTripId = tripId.toLongOrNull()
        val userId = currentUserId()

        if (numericTripId == null || imageUris.isEmpty()) return

        viewModelScope.launch {
            try {
                ensureLocalUser(userId)
                val images = imageUris.distinct().map { uri ->
                    TripImageEntity(
                        tripId = numericTripId,
                        userId = userId,
                        imageUri = uri
                    )
                }
                tripImageDao.insertImages(images)
                Log.i(TAG, "Attached ${images.size} image(s) to trip=$numericTripId")
            } catch (e: Exception) {
                Log.e(TAG, "Error attaching trip images", e)
            }
        }
    }


    private suspend fun ensureLocalUser(uid: String) {
        if (userDao.getUserById(uid) != null) return

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        userDao.insertUserIfMissing(
            UserEntity(
                firebaseUid = uid,
                username = "user_${uid.take(12)}",
                email = firebaseUser?.email ?: "$uid@local.voyagetime"
            )
        )
    }

    fun deleteImage(image: TripImageEntity) {
        val userId = currentUserId()
        viewModelScope.launch {
            try {
                tripImageDao.deleteImage(image.id, userId)
                Log.i(TAG, "Deleted image=${image.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting trip image", e)
            }
        }
    }

    private fun currentUserId(): String = authRepository.currentUserId() ?: LOCAL_USER_ID

    companion object {
        private const val TAG = "TripGalleryViewModel"
        private const val LOCAL_USER_ID = "local_user"
    }
}
