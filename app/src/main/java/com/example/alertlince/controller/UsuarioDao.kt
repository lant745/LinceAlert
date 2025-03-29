package com.example.alertlince.controller

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alertlince.model.DatabaseHelper

class UsuarioDao(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val requestSmsPermission = 1 // Se corrige el nombre a mayúsculas para seguir la convención

    /**
     * Solicita permisos de SMS en tiempo de ejecución
     */
    fun solicitarPermisosSMS(activity: Activity) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {

            // Si no tiene permiso, solicitarlo
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.SEND_SMS), requestSmsPermission)
        } else {
            Log.d("Permisos", "Permiso SEND_SMS ya concedido.")
        }
    }

    /**
     * Maneja la respuesta del usuario a la solicitud de permisos
     */
    fun manejarRespuestaPermiso(requestCode: Int, grantResults: IntArray) {
        if (requestCode == requestSmsPermission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permisos", "Permiso SEND_SMS concedido.")
            } else {
                Log.e("Permisos", "Permiso SEND_SMS denegado.")
            }
        }
    }

    fun insertarUsuario(correo: String, telefono: String, contrasena: String): Long {
        val db = dbHelper.writableDatabase
        return try {
            val valores = ContentValues().apply {
                put("correo", correo)
                put("telefono", telefono)
                put("contrasena", contrasena)
            }
            db.insert("usuarios", null, valores)
        } catch (e: Exception) {
            e.printStackTrace()
            -1L  // Retorna -1 en caso de error
        } finally {
            db.close()
        }
    }

    fun obtenerUsuario(email: String, contrasena: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM usuarios WHERE email = ? AND contrasena = ?", arrayOf(email, contrasena))
        val usuarioExiste = cursor.moveToFirst()
        cursor.close()
        db.close()
        return usuarioExiste
    }

    fun obtenerNumeros(): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT telefono FROM contactoUsuario", null)
        val numeros = mutableListOf<String>()

        if (cursor.moveToFirst()) {
            val telefonoIndex = cursor.getColumnIndex("telefono")
            if (telefonoIndex >= 0) {
                do {
                    val telefono = cursor.getString(telefonoIndex)
                    numeros.add(telefono)
                } while (cursor.moveToNext())
            } else {
                Log.e("DBError", "La columna 'telefono' no fue encontrada en la base de datos.")
            }
        } else {
            Log.e("DBInfo", "No se encontraron resultados en la base de datos.")
        }

        cursor.close()
        db.close()
        return numeros
    }

    fun editarContacto(id: String, nombre: String, apellido: String, relacionUsuario: String, telefono: String, correo: String): Int {
        val db = dbHelper.writableDatabase
        var resultado = 0
        try {
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
                throw IllegalArgumentException("Todos los campos deben ser completados.")
            }

            val valores = ContentValues().apply {
                put("nombre", nombre)
                put("apellido", apellido)
                put("relacionUsuario", relacionUsuario)
                put("telefono", telefono)
                put("correo", correo)
            }

            resultado = db.update("usuarios", valores, "id = ?", arrayOf(id))
        } catch (e: Exception) {
            Log.e("ActualizarUsuario", "Error al actualizar usuario", e)
        } finally {
            db.close()
        }

        return resultado
    }

    fun eliminarContacto(id: String): Int {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("contactos", "id = ?", arrayOf(id))
        db.close()
        return resultado
    }

    fun insertarContactos(nombre: String, apellido: String, relacion: String, telefono: String, correo: String): Long {
        val db = dbHelper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("relacionUsuario", relacion)
            put("telefono", telefono)
            put("correo", correo)
        }

        val resultado = db.insert("contactoUsuario", null, valores)
        db.close()
        return resultado
    }

    fun obtenerContactos(): List<Map<String, String>> {
        val db = dbHelper.readableDatabase
        val listaContactos = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT idContacto, nombre, apellido, relacionUsuario, telefono, correo FROM contactoUsuario",
            null
        )

        try {
            if (cursor.moveToFirst()) {
                do {
                    val columnIndexIdContacto = cursor.getColumnIndex("idContacto")
                    val columnIndexNombre = cursor.getColumnIndex("nombre")
                    val columnIndexApellido = cursor.getColumnIndex("apellido")
                    val columnIndexRelacion = cursor.getColumnIndex("relacionUsuario")
                    val columnIndexTelefono = cursor.getColumnIndex("telefono")
                    val columnIndexCorreo = cursor.getColumnIndex("correo")

                    if (columnIndexIdContacto >= 0 && columnIndexNombre >= 0 && columnIndexApellido >= 0 &&
                        columnIndexRelacion >= 0 && columnIndexTelefono >= 0 && columnIndexCorreo >= 0) {

                        val contacto = mapOf(
                            "idContacto" to cursor.getInt(columnIndexIdContacto).toString(),
                            "nombre" to cursor.getString(columnIndexNombre),
                            "apellido" to cursor.getString(columnIndexApellido),
                            "relacion" to cursor.getString(columnIndexRelacion),
                            "telefono" to cursor.getString(columnIndexTelefono),
                            "correo" to cursor.getString(columnIndexCorreo)
                        )
                        listaContactos.add(contacto)
                    } else {
                        Log.e("Error", "Una o más columnas no se encuentran en el cursor.")
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }

        return listaContactos
    }

    fun guardarIdUsuarioEnSesion(idUsuario: Long) {
        val sharedPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("idUsuario", idUsuario)
        if (editor.commit()) {
            Log.d("Sesion", "idUsuario guardado: $idUsuario")
        } else {
            Log.e("Sesion", "Error al guardar el idUsuario en las preferencias")
        }
    }

    private fun obtenerIdUsuarioDeSesion(): Long {
        val sharedPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sharedPrefs.getLong("idUsuario", -1)
    }
}

// Clase modelo para Usuario
data class Usuario(val id: String, val email: String, val telefono: String, val password: String)
