package com.example.presensiusingmap.data

import androidx.lifecycle.LiveData

class PresensiRepository(private val presensiDao: PresensiDao) {
    val readALlData : LiveData<List<Presensi>> = presensiDao.readAllData()

    suspend fun addPresensi(presensi:Presensi){
        presensiDao.addPresensi(presensi)
    }

//    suspend fun updateUser(user:User){
//        userDao.updateUser(user)
//    }
}