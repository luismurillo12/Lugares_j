package com.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lugar(
    var id: String,  // el id es string por la base da datos en la nube
    val nombre: String,
    val correo: String?,
    val telefono: String?,
    val web: String?,
    val latitud: Double?,
    val longitud: Double?,
    val altura: Double?,
    val rutaAudio: String?,
    val rutaImagen: String?

//el ? es para decir que va a hacer nulo, y esta clase es la creacion de la BD
): Parcelable {

    constructor():
            this("",
                "",
                "",
                "",
                "",
                0.0,
                0.0,
                0.0,
                "",
                "")

}
