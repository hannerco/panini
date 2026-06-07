package com.example.afinal

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NumeroLaminaAdapter(
    private val laminas: List<Lamina>,
    private val onClick: (Lamina) -> Unit
) : RecyclerView.Adapter<NumeroLaminaAdapter.ViewHolder>() {

    class ViewHolder(
        itemView: android.view.View
    ) : RecyclerView.ViewHolder(itemView) {

        val tvNumero: TextView =
            itemView.findViewById(R.id.tvNumero)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_numero_lamina,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val lamina = laminas[position]

        if (lamina.repetidas > 0) {

            holder.tvNumero.text =
                "${lamina.numeroLamina}\n(${lamina.repetidas})"

        } else {

            holder.tvNumero.text =
                lamina.numeroLamina.toString()
        }

        if (lamina.repetidas > 0) {

            holder.tvNumero.setBackgroundColor(
                Color.YELLOW
            )

        } else if (lamina.obtenida == 1) {

            holder.tvNumero.setBackgroundColor(
                Color.GREEN
            )

        } else {

            holder.tvNumero.setBackgroundColor(
                Color.LTGRAY
            )
        }

        holder.itemView.setOnClickListener {
            onClick(lamina)
        }
    }

    override fun getItemCount(): Int =
        laminas.size
}