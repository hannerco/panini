package com.example.afinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SeleccionBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.bottomsheet_seleccion,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        val seleccion =
            arguments?.getInt("seleccion") ?: 1

        view.findViewById<TextView>(
            R.id.tvTitulo
        ).text =
            "Selección $seleccion"

        val recycler =
            view.findViewById<RecyclerView>(
                R.id.rvLaminas
            )

        val db =
            LaminaDatabase(
                requireContext()
            )

        val laminas =
            db.obtenerLaminasSeleccion(
                seleccion
            )

        recycler.layoutManager =
            GridLayoutManager(
                requireContext(),
                5
            )

        recycler.adapter =
            NumeroLaminaAdapter(
                laminas
            ) { lamina ->

                android.app.AlertDialog.Builder(
                    requireContext()
                )
                    .setTitle("Lámina ${lamina.numeroLamina}")
                    .setMessage(
                        when {
                            lamina.repetidas > 0 ->
                                "Tienes ${lamina.repetidas} repetidas"

                            lamina.obtenida == 1 ->
                                "Ya tienes esta lámina"

                            else ->
                                "¿Acabas de obtener esta lámina?"
                        }
                    )
                    .setPositiveButton(
                        when {
                            lamina.repetidas > 0 ->
                                "Agregar repetida"

                            lamina.obtenida == 1 ->
                                "Agregar repetida"

                            else ->
                                "Registrar"
                        }
                    ) { _, _ ->

                        db.registrarObtencion(
                            lamina.numero
                        )

                        dismiss()

                        SeleccionBottomSheet
                            .newInstance(seleccion)
                            .show(
                                parentFragmentManager,
                                "seleccion"
                            )
                    }
                    .setNegativeButton(
                        "Cancelar",
                        null
                    )
                    .show()
            }
    }

    companion object {

        fun newInstance(
            seleccion: Int
        ): SeleccionBottomSheet {

            val fragment =
                SeleccionBottomSheet()

            val args = Bundle()

            args.putInt(
                "seleccion",
                seleccion
            )

            fragment.arguments = args

            return fragment
        }
    }
}