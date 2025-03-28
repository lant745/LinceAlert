package com.example.alertlince

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.alertlince.model.DatabaseHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔥 Inicializar la base de datos automáticamente
        val dbHelper = DatabaseHelper(this)
        dbHelper.writableDatabase // Esto crea la BD si no existe

        // 🔑 Verificar si hay sesión iniciada
        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si el usuario ya está autenticado, redirigirlo a la actividad principal (Vistas)
            startActivity(Intent(this, Vistas::class.java))
            finish() // Evita que el usuario vuelva al login con el botón "atrás"
        } else {
            // Si no hay sesión, mostrar el fragmento de LoginUser
            if (savedInstanceState == null) {
                loadFragment(LoginUser())
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
