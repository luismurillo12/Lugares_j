package com.ViewModel

import android.app.Application
import androidx.lifecycle.*
import com.data.LugarDao
import com.model.Lugar
import com.repository.LugarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class lugarViewModel (application: Application) : AndroidViewModel(application){

    private val lugarRepository: LugarRepository = LugarRepository(LugarDao())

    val getLugares: MutableLiveData<List<Lugar>> = lugarRepository.getLugares


    fun saveLugar(lugar: Lugar){
        viewModelScope.launch(Dispatchers.IO){
            lugarRepository.saveLugar(lugar)
        }
    }
    fun deleteLugar(lugar:Lugar){
        viewModelScope.launch(Dispatchers.IO){
            lugarRepository.deleteLugar(lugar)
        }
    }
}