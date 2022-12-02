package com.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lugares.databinding.LugarFilaBinding
import com.example.lugares.ui.lugar.AddLugarFragmentDirections
import com.example.lugares.ui.lugar.lugarFragmentDirections
import com.model.Lugar

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    // esto lo que hace es que carga la informacion de los datos en la vista, cada cuadrito de informacion que exista esto lo genera
    inner class LugarViewHolder(private val itemBinding: LugarFilaBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun dibuja(lugar: Lugar) {
            itemBinding.tvNombre.text = lugar.nombre
            itemBinding.tvCorreo.text = lugar.correo
            itemBinding.tvTelefono.text = lugar.telefono
            Glide.with(itemBinding.root.context)
                .load(lugar.rutaImagen)
                .circleCrop()
                .into(itemBinding.imagen)
            //Creo una accion para navegar a updateLugar pasando un argumento lugar
            itemBinding.vistaFila.setOnClickListener {
                val action = lugarFragmentDirections.actionNavLugarToUpdateLugarFragment(lugar)

                //Efectivamente se pasa al fragmento
                itemView.findNavController().navigate(action)
            }

        }
    }

    // esta lista es donde estan los objertos lugar a dibujarse
    private var listaLugares = emptyList<Lugar>()

    // esta funcion crea cajitas para cada lugar o cada item en memoria (vista)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding =
            LugarFilaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun setListaLugares(lugares: List<Lugar>) {
        this.listaLugares = lugares
        notifyDataSetChanged()
    }
}