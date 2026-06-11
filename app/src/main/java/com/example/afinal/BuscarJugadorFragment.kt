package com.example.afinal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class BuscarJugadorFragment : Fragment() {

    // Referencias a la UI
    private lateinit var etNombre: EditText
    private lateinit var btnBuscar: Button
    private lateinit var ivFoto: ImageView
    private lateinit var tvNombre: TextView
    private lateinit var tvEquipo: TextView
    private lateinit var tvNacionalidad: TextView
    private lateinit var tvFechaNacimiento: TextView
    private lateinit var tvPosicion: TextView
    private lateinit var tvEstado: TextView
    private lateinit var tvNoEncontrado: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_buscar_jugador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Conectar variables con las vistas del XML
        etNombre         = view.findViewById(R.id.etNombreJugador)
        btnBuscar        = view.findViewById(R.id.btnBuscar)
        ivFoto           = view.findViewById(R.id.ivFotoJugador)
        tvNombre         = view.findViewById(R.id.tvNombre)
        tvEquipo         = view.findViewById(R.id.tvEquipo)
        tvNacionalidad   = view.findViewById(R.id.tvNacionalidad)
        tvFechaNacimiento= view.findViewById(R.id.tvFechaNacimiento)
        tvPosicion       = view.findViewById(R.id.tvPosicion)
        tvEstado         = view.findViewById(R.id.tvEstado)
        tvNoEncontrado   = view.findViewById(R.id.tvNoEncontrado)

        btnBuscar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            if (nombre.isNotEmpty()) buscarJugador(nombre)
        }
    }

    private fun buscarJugador(nombre: String) {
        // La llamada HTTP no puede hacerse en el hilo principal, usamos thread{}
        thread {
            try {
                val nombreEncoded = java.net.URLEncoder.encode(nombre, "UTF-8")
                val url = URL("https://www.thesportsdb.com/api/v1/json/3/searchplayers.php?p=$nombreEncoded")

                val conexion = url.openConnection() as HttpURLConnection
                conexion.requestMethod = "GET"
                conexion.connect()

                val respuesta = conexion.inputStream.bufferedReader().readText()
                val json = JSONObject(respuesta)
                val players = json.optJSONArray("player")

                if (players != null && players.length() > 0) {
                    // Tomamos el primer resultado
                    val p = players.getJSONObject(0)
                    val jugador = Jugador(
                        nombre          = p.optString("strPlayer"),
                        equipo          = p.optString("strTeam"),
                        nacionalidad    = p.optString("strNationality"),
                        fechaNacimiento = p.optString("dateBorn"),
                        posicion        = p.optString("strPosition"),
                        estado          = p.optString("strStatus"),
                        fotoUrl         = p.optString("strThumb")
                    )
                    // Volvemos al hilo principal para actualizar la UI
                    requireActivity().runOnUiThread { mostrarJugador(jugador) }
                } else {
                    requireActivity().runOnUiThread { mostrarNoEncontrado() }
                }

            } catch (e: Exception) {
                requireActivity().runOnUiThread { mostrarNoEncontrado() }
            }
        }
    }

    private fun mostrarJugador(jugador: Jugador) {
        tvNoEncontrado.visibility = View.GONE

        tvNombre.text          = jugador.nombre
        tvEquipo.text          = "Equipo: ${jugador.equipo}"
        tvNacionalidad.text    = "Nacionalidad: ${jugador.nacionalidad}"
        tvFechaNacimiento.text = "Fecha de nacimiento: ${jugador.fechaNacimiento}"
        tvPosicion.text        = "Posición: ${jugador.posicion}"
        tvEstado.text          = "Estado: ${jugador.estado}"

        // Hacer visible cada vista
        listOf(ivFoto, tvNombre, tvEquipo, tvNacionalidad,
            tvFechaNacimiento, tvPosicion, tvEstado)
            .forEach { it.visibility = View.VISIBLE }

        // Glide carga la imagen desde la URL directamente en el ImageView
        Glide.with(this).load(jugador.fotoUrl).into(ivFoto)
    }

    private fun mostrarNoEncontrado() {
        listOf(ivFoto, tvNombre, tvEquipo, tvNacionalidad,
            tvFechaNacimiento, tvPosicion, tvEstado)
            .forEach { it.visibility = View.GONE }
        tvNoEncontrado.visibility = View.VISIBLE
    }
}