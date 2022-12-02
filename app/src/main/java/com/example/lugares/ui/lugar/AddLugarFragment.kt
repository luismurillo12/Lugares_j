package com.example.lugares.ui.lugar

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ViewModel.lugarViewModel
import com.example.lugares.R
import com.example.lugares.databinding.FragmentAddLugarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares_j.utiles.AudioUtiles
import com.lugares_j.utiles.ImagenUtiles
import com.model.Lugar


class AddLugarFragment : Fragment() {
    private lateinit var lugarViewModel: lugarViewModel

    private var _binding: FragmentAddLugarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var audioUtiles: AudioUtiles
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(com.ViewModel.lugarViewModel::class.java)
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        binding.btAdd.setOnClickListener {
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            subeNota()
        }

        activaGPS()

        // en esta siguiente linea se hace en el mismo orden que sale en el contructor anterior de audioUltiles
        audioUtiles = AudioUtiles(
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio)
        )

        // Este se hace para lo da la foto
        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                imagenUtiles.actualizaFoto()
            }
        }

        // esto mismo se hace para imagen utiles
        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity
        )


        return binding.root
    }

    // ESTA FUNCION SUBE LA NOTA DE AUDIO A STORAGE y pasa la ruta publica a la siguiente funcion
    private fun subeNota() {
        val archivoLocal = audioUtiles.audioFile
        if (archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()) {
            // se fija la ruta al archivo de audio
            val rutaLocal = Uri.fromFile(archivoLocal)
            // se establece la rutra de audio en la nube
            val rutaNube =
                "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${archivoLocal.name}"
            //se hace la referencia real  debido a que lo anterior es solo un string
            val referecia: StorageReference = Firebase.storage.reference.child(rutaNube)
            // se sube el archivo yu se establece el Listen para saber que hacer
            referecia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referecia.downloadUrl
                        .addOnSuccessListener {
                            // Se obtiene la ruta publica del archivo
                            val rutaAudio = it.toString()
                            subeImagen(rutaAudio)
                        }
                }
                .addOnFailureListener {
                    subeImagen("")
                }
        }else{ // no hay foto o existe un error que no la puede leer
            subeImagen("")
        }
    }

    private fun subeImagen(rutaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen)
        val archivoLocal = imagenUtiles.imagenFile
        if (archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()) {
            // se fija la ruta al archivo de foto
            val rutaLocal = Uri.fromFile(archivoLocal)
            // se establece la rutra de audio en la nube
            val rutaNube =
                "lugares/${Firebase.auth.currentUser?.email}/imagenes/${archivoLocal.name}"
            //se hace la referencia real  debido a que lo anterior es solo un string
            val referecia: StorageReference = Firebase.storage.reference.child(rutaNube)
            // se sube el archivo yu se establece el Listen para saber que hacer
            referecia.putFile(rutaLocal)
                .addOnSuccessListener {
                    referecia.downloadUrl
                        .addOnSuccessListener {
                            // Se obtiene la ruta publica del archivo
                            val rutaImagen = it.toString()
                            addLugar(rutaAudio, rutaImagen)
                        }
                }
                .addOnFailureListener {
                    addLugar(rutaAudio, "")
                }
        }else{ // no hay foto o existe un error que no la puede leer
            addLugar(rutaAudio, "")
        }
    }

    private fun activaGPS() {
        if (requireActivity()
                .checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED &&
            requireActivity()
                .checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            //SI ESTAMOS ACA HAY QUE PEDIR UBICACION
            requireActivity()
                .requestPermissions(
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), 105
                )
        } else {
            ///Si tenemos permiso se busca la ubicacion
            val ubicacion: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            ubicacion.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    binding.tvLatitud.text = "${location.latitude}"
                    binding.tvLongitud.text = "${location.longitude}"
                    binding.tvAltura.text = "${location.altitude}"
                } else {
                    binding.tvLatitud.text = "0.0"
                    binding.tvLongitud.text = "0.0"
                    binding.tvAltura.text = "0.0"
                }
            }
        }
    }


    //con este metodo se agregan los datos del lugar
    private fun addLugar(rutaAudio: String, rutaImagen: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_lugar)
        val nombre = binding.etNombre.text.toString() // optiene el dato digitado en el formulario
        if (nombre.isNotEmpty()) {

            val correo =
                binding.etCorreo.text.toString() // optiene el dato digitado en el formulario
            val telefono =
                binding.etTelefono.text.toString() // optiene el dato digitado en el formulario
            val web = binding.etWeb.text.toString() // optiene el dato digitado en el formulario
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("", nombre, correo, telefono, web, latitud, longitud, altura, rutaAudio, rutaImagen)

            //Se procede a guardar y registrar el lugar
            lugarViewModel.saveLugar(lugar)

            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_added),
                Toast.LENGTH_SHORT
            )
                .show()

            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {
            //No se puede registrar el lugar por que falta info
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}