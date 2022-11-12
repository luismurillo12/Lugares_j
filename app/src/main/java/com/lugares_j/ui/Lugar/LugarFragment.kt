package com.lugares_j.ui.Lugar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lugares_j.R
import com.lugares_j.ViewModel.LugarViewModel
import com.lugares_j.adapter.LugarAdapter
import com.lugares_j.databinding.FragmentLugarBinding

class LugarFragment : Fragment() {

    private var _binding: FragmentLugarBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lugarViewModel =
            ViewModelProvider(this)[(LugarViewModel::class.java)]

        _binding = FragmentLugarBinding.inflate(inflater, container, false)

        binding.addLugarFabButton.setOnClickListener{
            findNavController().navigate(R.id.action_nav_lugar_to_addLugarFragment)
        }

        //se genera el recycler view para ver la informacion
        val lugarAddapter=LugarAdapter()
        val reciclador = binding.reciclador
        reciclador.adapter= lugarAddapter
        reciclador.layoutManager=LinearLayoutManager(requireContext())

        lugarViewModel.getLugares.observe(viewLifecycleOwner){
            it -> lugarAddapter.setListaLugares(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}