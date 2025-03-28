package com.example.alertlince.controller

import android.content.ContentValues
import android.content.Context
import com.example.alertlince.model.DatabaseHelper

class UsuarioDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    // Insertar un usuario
    fun insertarUsuario(  correo: String, telefono: String, contrasena: String): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("correo", correo)
            put("telefono", telefono)
            put("contrasena", contrasena)
        }
        val resultado = db.insert("usuarios", null, valores)
        db.close()
        return resultado  // Retorna el ID del nuevo usuario o -1 si hay error
    }

    // Obtener un usuario por ID
    // Obtener un usuario por email y contraseña
    fun obtenerUsuario(email: String, contrasena: String): Boolean {
        val db = dbHelper.readableDatabase

        // Ejecutar la consulta SQL para buscar al usuario por correo y contraseña
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ? AND contrasena = ?", arrayOf(email, contrasena))

        // Comprobar si se encontró el usuario
        val usuarioExiste = cursor.moveToFirst()

        cursor.close()
        db.close()

        // Si el cursor se mueve a la primera fila, significa que el usuario fue encontrado
        return usuarioExiste
    }




    // Actualizar un usuario
    fun actualizarUsuario(id: String, email: String, telefono: String, password: String): Int {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("email", email)
            put("telefono", telefono)
            put("password", password)
        }
        val resultado = db.update("usuarios", valores, "id = ?", arrayOf(id))
        db.close()
        return resultado  // Retorna el número de filas afectadas
    }

    // Eliminar un usuario por ID
    fun eliminarUsuario(id: String): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("usuarios", "id = ?", arrayOf(id))
        db.close()
        return resultado  // Retorna el número de filas eliminadas
    }

    fun insertarContactos(nombre: String, apellido: String, telefono: String, correo: String): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("telefono", telefono)
            put("correo", correo)
        }
        val resultado = db.insert("usuarios", null, valores)
        db.close()
        return resultado // Retorna el ID del nuevo usuario o -1 si hay error
    }

}

// Clase modelo para Usuario
data class Usuario(val id: String, val email: String, val telefono: String, val password: String)
