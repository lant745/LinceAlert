package com.example.alertlince

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Si es la primera vez que se crea la actividad, carga el fragmento de RegisterUser
        if (savedInstanceState == null) {
            loadFragment(RegisterUser())  // Cargar el fragmento de RegisterUser inicialmente
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
