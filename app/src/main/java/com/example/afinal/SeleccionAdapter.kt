package com.example.afinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SeleccionAdapter(
    private val selecciones: List<Seleccion>,
    private val onClick: (Seleccion) -> Unit
) : RecyclerView.Adapter<SeleccionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val tvSeleccion: TextView =
            itemView.findViewById(R.id.tvSeleccion)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_seleccion,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val seleccion = selecciones[position]

        holder.tvSeleccion.text =
            "Selección ${seleccion.numero}"

        holder.itemView.setOnClickListener {
            onClick(seleccion)
        }
    }

    override fun getItemCount(): Int =
        selecciones.size
}