package com.example.alertlince

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

        // Si es la primera vez que se crea la actividad, carga el fragmento de LoginUser
        if (savedInstanceState == null) {
            loadFragment(LoginUser())  // Cargar el fragmento de LoginUser inicialmente
        }
    }

    // Función para cargar un fragmento en el contenedor
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null) // Esto permite ir atrás entre fragmentos
        transaction.commit()
    }
}
