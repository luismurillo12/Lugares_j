package com.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.model.Lugar


class LugarDao {


    // tres variables usadas para poder generar la estructura en la nube
    private val coleccion1 = "lugaresApp"
    private val coleccion2 = "misLugares"
    private val usuario = Firebase.auth.currentUser?.email.toString()

    // con esto se contiene la conexion a la base de datos
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // se debe inicializar la info
    // con esto se inicializa la configuracion en Firestore
    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    fun saveLugar(lugar: Lugar) {
        // para definir un documentro en la nube
        val documento: DocumentReference
        if (lugar.id.isEmpty()) {// si esta vacio se crea un nuevo documento
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document()
            lugar.id =
                documento.id // con esto ya conseguimos el id alfanumerico de cuando se creo ese docuymeto (lugar)

        } else { // si el id ya existe o tiene algo entonces se va a modificar ese documeto que seria un "lUGAR"
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document(lugar.id) /// con esto lo que hace es ir y modificar lo que cumplacon ese id
        }

        //Ahora se crea o modifica el documento
        documento.set(lugar) // llamado asincrono
            .addOnSuccessListener {
                Log.d("saveLugar", "Lugar creado/ Actualizado")
            }
            .addOnCanceledListener {
                Log.e("saveLugar", "Lugar No creado / Modificado")
            }
    }

    fun deleteLugar(lugar: Lugar) {
        // se valida si el lugar tiene id para poder borrarlo
        if (lugar.id.isNotEmpty()) {// si NO esta vacio se puede eliminar
            firestore
                .collection(coleccion1) //aplicacion
                .document(usuario) //usuario
                .collection(coleccion2) // lugares
                .document(lugar.id) // id
                .delete() //borra el documento

                .addOnSuccessListener {
                    Log.d("deleteLugar", "Lugar Eliminado")
                }
                .addOnCanceledListener {
                    Log.e("deleteLugar", "Lugar No Eliminado")
                }
        }
    }


    fun getLugares(): MutableLiveData<List<Lugar>> {

        val listaLugares = MutableLiveData<List<Lugar>>()
        firestore
            .collection(coleccion1) //aplicacion
            .document(usuario) //usuario
            .collection(coleccion2) // lugares
            .addSnapshotListener { instantanea, e ->
                if (e != null) { // se dio un error capturando la imagen de la info
                    return@addSnapshotListener
                }
                //en este punto todo salio bien y no hay errores
                if (instantanea != null) { // si se pudo recuperar la info
                    val lista = ArrayList<Lugar>()
                    // se recorre la instantanea documento por documento convirtiendolo en lugar y agregando
                    // a la lista
                    instantanea.documents.forEach{
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar != null){ // si se pudo convertir el documento en un lugar entoces se agrega
                            lista.add(lugar) // se agrega un lugar a la lista
                        }
                    }

                    listaLugares.value = lista
                }
            }

        return listaLugares

    }
}