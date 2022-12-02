package com.example.lugares.ui.lugar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ViewModel.lugarViewModel
import com.bumptech.glide.Glide
import com.example.lugares.R
import com.example.lugares.databinding.FragmentUpdateLugarBinding
import com.model.Lugar


class UpdateLugarFragment : Fragment() {
    //se define un objeto para obtener dos argumentos pasados al fragmento
    private val args by navArgs<UpdateLugarFragmentArgs>()


    private lateinit var lugarViewModel: lugarViewModel

    private var _binding: FragmentUpdateLugarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

            //objeto mediaplayer para escuchar audio desde la nube
            private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel =
            ViewModelProvider(this).get(lugarViewModel::class.java)
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        // con estas lineas le pasamos los parametros de los valores ya creados a los que le demos click, en este caso a un ligar seleccionado previamente creado
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)

        binding.tvLongitud.text = args.lugar.longitud.toString()
        binding.tvLatitud.text = args.lugar.latitud.toString()
        binding.tvAltura.text = args.lugar.altura.toString()

        binding.btUpdate.setOnClickListener { updateLugar() }
        binding.btDelete.setOnClickListener { deleteLugar() }

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }
        binding.btWeb.setOnClickListener { verWeb() }
        binding.btLocation.setOnClickListener { verEnMapa() }

        if (args.lugar.rutaAudio?.isNotEmpty()==true) {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.lugar.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled = true
        }else{
            binding.btPlay.isEnabled = false
        }
        binding.btPlay.setOnClickListener{mediaPlayer.start()}

        if (args.lugar.rutaImagen?.isNotEmpty()==true) {

            Glide.with(requireContext())
                .load(args.lugar.rutaImagen)
                .circleCrop()
                .into(binding.imagen)
        }


        return binding.root
    }

    private fun escribirCorreo() {
        val valor = binding.etCorreo.text.toString()
        if (valor.isNotEmpty()) { // si el correo no esta vacio se intenta envia el correo
            //llamar los recursos que tiene el celular -- mapas, correos, mensajes, llamadas etc

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(valor))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos) + " " + binding.etNombre.text
            )
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_mensaje_correo))

            startActivity(intent)
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun llamarLugar() {
        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) { // si el numero no esta vacio se intenta envia el mensaje
            //llamar los recursos que tiene el celular -- mapas, correos, mensajes, llamadas etc

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$valor")

            if (requireActivity()
                    .checkSelfPermission(android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requireActivity()
                    .requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CALL_PHONE), 105)
            }else{
                // si se tiene el permiso de hacer la llamada
                requireActivity().startActivity(intent)
            }
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsapp() {
        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) { // si el numero no esta vacio se intenta envia el mensaje
            //llamar los recursos que tiene el celular -- mapas, correos, mensajes, llamadas etc

            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$valor&text=" + getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)

            startActivity(intent)
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun verWeb() {
        val valor = binding.etWeb.text.toString()
        if (valor.isNotEmpty()) { // si la direccion web no esta vacio se intenta envia el mensaje
            //llamar los recursos que tiene el celular -- mapas, correos, mensajes, llamadas etc


            val uri = "http://$valor"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun verEnMapa() {
    val latitud = binding.tvLatitud.text.toString().toDouble()
    val longitud = binding.tvLongitud.text.toString().toDouble()

        if (latitud.isFinite() && longitud.isFinite()) {
            val uri = "geo$latitud,$longitud?z18" //z18 es un zoom de 1800 pies
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteLugar() {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle(R.string.bt_delete_lugar)
        alerta.setMessage(getString(R.string.msg_pregunta_deleted) + "${args.lugar.nombre}?")
        alerta.setPositiveButton(getString(R.string.msg_si)) { _, _ ->
            lugarViewModel.deleteLugar(args.lugar) //efectivamente borra el lugar
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_deleted),
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        }
        alerta.setNegativeButton(getString(R.string.msg_no)) { _, _ -> }
        alerta.create().show()
    }

    //con este metodo se agregan los datos del lugar
    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString() // optiene el dato digitado en el formulario
        if (nombre.isNotEmpty()) {
            val correo =
                binding.etCorreo.text.toString() // optiene el dato digitado en el formulario
            val telefono =
                binding.etTelefono.text.toString() // optiene el dato digitado en el formulario
            val web = binding.etWeb.text.toString() // optiene el dato digitado en el formulario


            val lugar = Lugar(
                args.lugar.id,
                nombre,
                correo,
                telefono,
                web,
                args.lugar.latitud,
                args.lugar.longitud,
                args.lugar.altura,
                args.lugar.rutaAudio,
                args.lugar.rutaImagen
            )
            //Se procede a guardar y registrar el lugar
            lugarViewModel.saveLugar(lugar)
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_updated),
                Toast.LENGTH_SHORT
            )
                .show()
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        } else {
            //No se puede Actualizar el lugar por que falta info
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