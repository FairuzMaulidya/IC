package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.test.data.AppDatabase
import com.example.test.data.Profile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileDao = AppDatabase.getDatabase(application).profileDao()

    private val _username = MutableLiveData<String>()

    private val profileLiveData = MediatorLiveData<Profile?>()
    val profile: LiveData<Profile?> = profileLiveData

    fun loadProfile(username: String) {
        _username.value = username

        profileLiveData.removeSource(profileLiveData) // membersihkan source lama

        val source = profileDao.getProfileByUsername(username)
        profileLiveData.addSource(source) { profileData ->
            profileLiveData.value = profileData
        }
    }

    fun saveProfile(profile: Profile) = viewModelScope.launch {
        profileDao.insertProfile(profile)
    }


    fun updatePhotoUri(uri: String) {
        val currentProfile = profileLiveData.value
        if (currentProfile != null) {
            val updatedProfile = currentProfile.copy(photoUri = uri)
            profileLiveData.value = updatedProfile // update UI langsung
            saveProfile(updatedProfile)           // simpan ke database
        }
    }
}
