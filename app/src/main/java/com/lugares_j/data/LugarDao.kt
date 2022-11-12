package com.lugares_j.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.lugares_j.model.Lugar


class LugarDao {
    //Variables usadas para poder generar la estructura en la nube
    private val coleccion1 = "lugaresApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misApp"

    //Contiene la conexion a la base de datos
    private var firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings= FirebaseFirestoreSettings.Builder().build()
    }

     fun saveLugar(lugar: Lugar){
         //para definir un documento en la nube
         val documento : DocumentReference

         if(lugar.id.isEmpty()){ //Si esta vacio es nuevo documento
             documento= firestore.collection(coleccion1).document(usuario)
                 .collection(coleccion2).document()
             lugar.id= documento.id

         }else{ //si el id tiene algo entoncs se va a modificar ese documento
             documento= firestore.collection(coleccion1).document(usuario)
                 .collection(coleccion2).document(lugar.id)
         }

         //Ahora se modifica o crea
         documento.set(lugar).addOnSuccessListener {
             Log.d("saveLugar", "Lugar creado/actualizado")
         }
             .addOnCanceledListener {
                 Log.d("saveLugar", "Lugar NO creado/actualizado")
             }


     }

     fun deleteLugar(lugar: Lugar){
         //Se valida si el lugar tiene id para poder borrarlo
         if(lugar.id.isNotEmpty()){ //Si no esta vacio es nuevo documento
             firestore.collection(coleccion1).document(usuario)
                 .collection(coleccion2).document(lugar.id).delete()
                 .addOnSuccessListener {
                 Log.d("deleteLugar", "Lugar Eliminado")
             }
                 .addOnCanceledListener {
                     Log.d("deleteLugar", "Lugar NO Eliminado")
                 }
         }
    }

    fun getLugares() : MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()
        firestore.collection(coleccion1)
            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener{
                instantanea, e ->
                if(e != null){// se dio un error capturando la imagen de la info
                    return@addSnapshotListener
                }
                //Si estamos aca no hubo error
                if (instantanea != null){//si se pudo recuperar la info
                    val lista = ArrayList<Lugar>()
                    //Se recorre la instantanea documento por documento convirtiendolo en lugar
                    instantanea.documents.forEach{
                        val lugar= it.toObject(Lugar::class.java)
                        if (lugar != null){//Si se pudo convertir el documetno en un Lugar
                            lista.add(lugar)
                        }
                    }
                    listaLugares.value= lista
                }
            }
        return listaLugares
    }




}