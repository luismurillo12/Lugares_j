package com.lugares_j.ui.Lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lugares_j.R
import com.lugares_j.ViewModel.LugarViewModel
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.databinding.FragmentLugarBinding
import com.lugares_j.model.Lugar
import java.util.jar.Manifest


class AddLugarFragment : Fragment() {

 private lateinit var lugarViewModel : LugarViewModel

    private var _binding: FragmentAddLugarBinding? = null
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
        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        binding.btAdd.setOnClickListener{addLugar()}



        return binding.root
    }

    private fun activaGPS(){

    }

//con este metodo se agregan los datos del lugar
   private fun addLugar() {
    val nombre = binding.etNombre.text.toString() // optiene el dato digitado en el formulario
    if (nombre.isNotEmpty()) {

        val correo = binding.etCorreo.text.toString() // optiene el dato digitado en el formulario
        val telefono = binding.etTelefono.text.toString() // optiene el dato digitado en el formulario
        val web = binding.etWeb.text.toString() // optiene el dato digitado en el formulario

        val lugar = Lugar("", nombre, correo, telefono, web,
            0.0, 0.0, 0.0, "", "")

        //Se procede a guardar y registrar el lugar
        lugarViewModel.saveLugar(lugar)

        Toast.makeText(requireContext(), getString(R.string.msg_lugar_added), Toast.LENGTH_SHORT)
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
// ver dodne aplica los cambios en el git 

}