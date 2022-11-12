package com.lugares_j.ui.Lugar

import android.app.AlertDialog
import android.content.Intent
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
import com.lugares_j.R
import com.lugares_j.ViewModel.LugarViewModel
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.databinding.FragmentLugarBinding
import com.lugares_j.databinding.FragmentUpdateLugarBinding
import com.lugares_j.model.Lugar


class UpdateLugarFragment : Fragment() {
    //se define un objeto para obtener los argumentos pasados al fragmento
    private val args by navArgs<UpdateLugarFragmentArgs>()

 private lateinit var lugarViewModel : LugarViewModel

    private var _binding: FragmentUpdateLugarBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLongitud.text= args.lugar.longitud.toString()
        binding.tvLatitud.text= args.lugar.latitud.toString()
        binding.tvAltura.text= args.lugar.altura.toString()


        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)
        binding.btUpdate.setOnClickListener{UpdateLugar()}
        binding.btDelete.setOnClickListener{deleteLugar()}

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }
        binding.btWeb.setOnClickListener { verWeb() }
        binding.btLocation.setOnClickListener { verMapa() }

        return binding.root
    }

    private fun verMapa() {
        TODO("Not yet implemented")
    }

    private fun verWeb() {

        val valor = binding.etWeb.text.toString()
        if(valor.isNotEmpty()){// comentario
            val uri = "http://$valor&text="
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsapp() {
        val valor = binding.etTelefono.text.toString()
        if(valor.isNotEmpty()){
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$valor&text="+
                    getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data= Uri.parse(uri)
            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
                getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun llamarLugar() {
        TODO("Not yet implemented")
    }

    private fun escribirCorreo() {
        val valor = binding.etCorreo.text.toString()
        if(valor.isNotEmpty()){
            val intent = Intent(Intent.ACTION_SEND)
            intent.type="message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(valor))
            intent.putExtra(Intent.EXTRA_SUBJECT,
            getString(R.string.msg_saludos)+" "+binding.etNombre.text)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_correo))
            startActivity(intent)

        }else{
            Toast.makeText(requireContext(),
            getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun deleteLugar() {
       val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle(R.string.bt_delete_lugar)
        alerta.setMessage(getString(R.string.msg_lugar_deleted)+"${args.lugar.nombre}")
        alerta.setPositiveButton(getString(R.string.msg_si_deleted)){_,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_deleted),Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }
        alerta.setNegativeButton(getString(R.string.msg_no_deleted)){_,_ ->
            alerta.create().show()
        }
    }

    //con este metodo se agregan los datos del lugar
   private fun UpdateLugar() {
    val nombre = binding.etNombre.text.toString() // optiene el dato digitado en el formulario
    if (nombre.isNotEmpty()) {

        val correo = binding.etCorreo.text.toString() // optiene el dato digitado en el formulario
        val telefono = binding.etTelefono.text.toString() // optiene el dato digitado en el formulario
        val web = binding.etWeb.text.toString() // optiene el dato digitado en el formulario

        val lugar = Lugar(args.lugar.id, nombre, correo, telefono, web, args.lugar.latitud, args.lugar.longitud, args.lugar.altura, args.lugar.rutaAudio, args.lugar.rutaImagen)

        //Se procede a guardar y registrar el lugar
        lugarViewModel.saveLugar(lugar)

        Toast.makeText(requireContext(), getString(R.string.msg_lugar_updated), Toast.LENGTH_SHORT)
            .show()

        findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
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