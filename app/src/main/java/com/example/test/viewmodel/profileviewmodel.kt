package com.example.test.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.test.data.AppDatabase
import com.example.test.data.Profile
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val profileDao = AppDatabase.getDatabase(application).profileDao()
    val profile: LiveData<Profile?> = profileDao.getProfile()

    fun saveProfile(profile: Profile) = viewModelScope.launch {
        profileDao.insert(profile)
    }

    fun deleteProfile() = viewModelScope.launch {
        profileDao.deleteAll()
    }
}
