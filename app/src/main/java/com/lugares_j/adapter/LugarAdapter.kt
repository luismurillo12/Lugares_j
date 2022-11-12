package com.lugares_j.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.databinding.LugarFilaBinding
import com.lugares_j.model.Lugar
import com.lugares_j.ui.Lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>(){

    // esto lo que hace es que carga la informacion de los datos en la vista, cada cuadrito de informacion que exista esto lo genera
  inner class LugarViewHolder(private val itemBinding: LugarFilaBinding)
      : RecyclerView.ViewHolder(itemBinding.root){
fun dibuja(lugar: Lugar){
    itemBinding.tvNombre.text = lugar.nombre
    itemBinding.tvCorreo.text = lugar.correo
    itemBinding.tvTelefono.text = lugar.telefono
    itemBinding.vistaFila.setOnClickListener {
        //creo una accion para navegar a updateLugar pasando un argumento lugar
        val action = LugarFragmentDirections.actionNavLugarToUpdateLugarFragment(lugar)
        //se pasa el argumento
        itemView.findNavController().navigate(action)
    }

}
  }
    // esta lista es donde estan los objertos lugar a dibujarse
    private var listaLugares = emptyList<Lugar>()

   // esta funcion crea cajitas para cada lugar o cada item en memoria (vista)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding = LugarFilaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
       return LugarViewHolder(itemBinding)
    }
  //esta funcion toma un lugar y lo envia a pantalla o a dibujar
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
         val lugar = listaLugares[position]
      holder.dibuja(lugar)
    }
 // esta funcion devuelve la cantidad de elementos a dibijar, cajitas o items o registros
    override fun getItemCount(): Int {
         return listaLugares.size
    }

    fun setListaLugares(lugares: List<Lugar>){
        this.listaLugares = lugares
        notifyDataSetChanged()
    }
}