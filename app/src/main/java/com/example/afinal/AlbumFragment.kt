package com.example.afinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AlbumFragment : Fragment() {

    private lateinit var recycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_album,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        recycler =
            view.findViewById(R.id.rvAlbum)

        recycler.layoutManager =
            GridLayoutManager(
                requireContext(),
                4
            )

        val spinner =
            view.findViewById<Spinner>(
                R.id.spFiltro
            )

        val opciones = listOf(
            "Todas",
            "Pendientes",
            "Obtenidas",
            "Repetidas"
        )

        spinner.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                opciones
            )

        cargarSelecciones("Todas")

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    cargarSelecciones(
                        opciones[position]
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun cargarSelecciones(
        filtro: String
    ) {

        val db =
            LaminaDatabase(
                requireContext()
            )

        val selecciones =
            mutableListOf<Seleccion>()

        for(i in 1..48){

            val agregar =
                when(filtro){

                    "Pendientes" ->
                        db.seleccionTienePendientes(i)

                    "Obtenidas" ->
                        db.seleccionTieneObtenidas(i)

                    "Repetidas" ->
                        db.seleccionTieneRepetidas(i)

                    else ->
                        true
                }

            if(agregar){

                selecciones.add(
                    Seleccion(i)
                )
            }
        }

        recycler.adapter =
            SeleccionAdapter(
                selecciones
            ){ seleccion ->

                SeleccionBottomSheet
                    .newInstance(
                        seleccion.numero
                    )
                    .show(
                        parentFragmentManager,
                        "seleccion"
                    )
            }
    }
}