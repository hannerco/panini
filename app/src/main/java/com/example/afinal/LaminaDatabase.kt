package com.example.afinal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LaminaDatabase(context: Context) : SQLiteOpenHelper(
    context,
    "album2026.db",
    null,
    1
) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("""
        CREATE TABLE laminas(
            numero INTEGER PRIMARY KEY,
            seleccion INTEGER,
            numero_lamina INTEGER,
            obtenida INTEGER,
            repetidas INTEGER
        )
    """.trimIndent())

        var consecutivo = 1

        for(seleccion in 1..48){

            for(lamina in 1..20){

                val values = ContentValues().apply {
                    put("numero", consecutivo)
                    put("seleccion", seleccion)
                    put("numero_lamina", lamina)
                    put("obtenida", 0)
                    put("repetidas", 0)
                }

                db.insert("laminas", null, values)

                consecutivo++
            }
        }
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS laminas")
        onCreate(db)
    }

    fun obtenerPendientes(): MutableList<Lamina>{

        val lista = mutableListOf<Lamina>()

        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM laminas WHERE obtenida = 0",
            null
        )

        while(cursor.moveToNext()){

            lista.add(
                Lamina(
                    cursor.getInt(0), // numero
                    cursor.getInt(1), // seleccion
                    cursor.getInt(2), // numero_lamina
                    cursor.getInt(3), // obtenida
                    cursor.getInt(4)  // repetidas
                )
            )
        }

        cursor.close()

        return lista
    }

    fun registrarLamina(numero:Int){

        val values = ContentValues()

        values.put("obtenida",1)

        writableDatabase.update(
            "laminas",
            values,
            "numero=?",
            arrayOf(numero.toString())
        )
    }

    fun obtenerLaminasDeSeleccion(
        seleccion:Int
    ): MutableList<Lamina>{

        val lista = mutableListOf<Lamina>()

        val cursor = readableDatabase.rawQuery(
            """
        SELECT *
        FROM laminas
        WHERE seleccion = ?
        ORDER BY numero_lamina
        """,
            arrayOf(seleccion.toString())
        )

        while(cursor.moveToNext()){

            lista.add(
                Lamina(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
        }

        cursor.close()

        return lista
    }

    fun registrarObtencion(id:Int){

        val cursor = readableDatabase.rawQuery(
            "SELECT obtenida,repetidas FROM laminas WHERE numero=?",
            arrayOf(id.toString())
        )

        if(cursor.moveToFirst()){

            val obtenida = cursor.getInt(0)
            val repetidas = cursor.getInt(1)

            val values = ContentValues()

            if(obtenida == 0){

                values.put("obtenida",1)

            }else{

                values.put("repetidas",repetidas + 1)
            }

            writableDatabase.update(
                "laminas",
                values,
                "numero=?",
                arrayOf(id.toString())
            )
        }

        cursor.close()
    }

    fun obtenerLaminasSeleccion(
        seleccion:Int
    ): MutableList<Lamina>{

        val lista = mutableListOf<Lamina>()

        val cursor = readableDatabase.rawQuery(
            """
        SELECT *
        FROM laminas
        WHERE seleccion = ?
        ORDER BY numero_lamina
        """,
            arrayOf(seleccion.toString())
        )

        while(cursor.moveToNext()){

            lista.add(
                Lamina(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
        }

        cursor.close()

        return lista
    }

    fun seleccionTienePendientes(
        seleccion: Int
    ): Boolean {

        val cursor = readableDatabase.rawQuery(
            """
        SELECT COUNT(*)
        FROM laminas
        WHERE seleccion = ?
        AND obtenida = 0
        """,
            arrayOf(seleccion.toString())
        )

        cursor.moveToFirst()

        val cantidad = cursor.getInt(0)

        cursor.close()

        return cantidad > 0
    }

    fun seleccionTieneObtenidas(
        seleccion: Int
    ): Boolean {

        val cursor = readableDatabase.rawQuery(
            """
        SELECT COUNT(*)
        FROM laminas
        WHERE seleccion = ?
        AND obtenida = 1
        """,
            arrayOf(seleccion.toString())
        )

        cursor.moveToFirst()

        val cantidad = cursor.getInt(0)

        cursor.close()

        return cantidad > 0
    }

    fun seleccionTieneRepetidas(
        seleccion: Int
    ): Boolean {

        val cursor = readableDatabase.rawQuery(
            """
        SELECT COUNT(*)
        FROM laminas
        WHERE seleccion = ?
        AND repetidas > 0
        """,
            arrayOf(seleccion.toString())
        )

        cursor.moveToFirst()

        val cantidad = cursor.getInt(0)

        cursor.close()

        return cantidad > 0
    }
}