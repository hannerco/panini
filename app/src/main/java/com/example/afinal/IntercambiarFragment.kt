package com.example.afinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class IntercambiarFragment : Fragment() {

    private lateinit var spDoy: Spinner
    private lateinit var spRecibo: Spinner
    private lateinit var btnIntercambiar: Button
    private lateinit var tvEstado: TextView

    // Listas paralelas a lo que se muestra en cada spinner,
    // para poder mapear la posición seleccionada -> Lamina real.
    private var repetidas: List<Lamina> = emptyList()
    private var pendientes: List<Lamina> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_intercambiar,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        spDoy = view.findViewById(R.id.spDoy)
        spRecibo = view.findViewById(R.id.spRecibo)
        btnIntercambiar = view.findViewById(R.id.btnIntercambiar)
        tvEstado = view.findViewById(R.id.tvEstado)

        btnIntercambiar.setOnClickListener {
            intercambiar()
        }

        cargarDatos()
    }

    // Se recarga al volver a la pestaña para reflejar
    // láminas obtenidas o repetidas en otras pantallas.
    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    private fun cargarDatos() {

        val db = LaminaDatabase(requireContext())

        repetidas = db.obtenerRepetidas()
        pendientes = db.obtenerPendientes()

        spDoy.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            repetidas.map {
                "Sel ${it.seleccion} · Lámina ${it.numeroLamina}  (${it.repetidas} rep.)"
            }
        )

        spRecibo.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            pendientes.map {
                "Sel ${it.seleccion} · Lámina ${it.numeroLamina}"
            }
        )

        val puedeIntercambiar =
            repetidas.isNotEmpty() && pendientes.isNotEmpty()

        btnIntercambiar.isEnabled = puedeIntercambiar

        tvEstado.text = when {
            repetidas.isEmpty() ->
                "No tienes láminas repetidas para intercambiar."
            pendientes.isEmpty() ->
                "¡Ya completaste el álbum! No quedan pendientes."
            else ->
                "Elige qué lámina das y cuál recibes, luego pulsa el botón."
        }
    }

    private fun intercambiar() {

        val posDoy = spDoy.selectedItemPosition
        val posRecibo = spRecibo.selectedItemPosition

        if (posDoy !in repetidas.indices ||
            posRecibo !in pendientes.indices
        ) {
            return
        }

        val laminaQueDoy = repetidas[posDoy]
        val laminaQueRecibo = pendientes[posRecibo]

        val db = LaminaDatabase(requireContext())

        val exito = db.realizarIntercambio(
            laminaQueDoy.numero,
            laminaQueRecibo.numero
        )

        if (exito) {
            Toast.makeText(
                requireContext(),
                "Intercambio realizado: diste Sel ${laminaQueDoy.seleccion}·" +
                        "${laminaQueDoy.numeroLamina} y obtuviste Sel " +
                        "${laminaQueRecibo.seleccion}·${laminaQueRecibo.numeroLamina}",
                Toast.LENGTH_LONG
            ).show()

            cargarDatos()
        } else {
            Toast.makeText(
                requireContext(),
                "No se pudo intercambiar (no quedan repetidas de esa lámina).",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}