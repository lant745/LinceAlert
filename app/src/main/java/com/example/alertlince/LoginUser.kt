package com.example.alertlince


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class LoginUser : Fragment() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signInButton: Button
    private lateinit var signUpTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        val rootView = inflater.inflate(R.layout.activity_loginuser, container, false)

        // Obtener referencias a las vistas
        emailEditText = rootView.findViewById(R.id.email)
        passwordEditText = rootView.findViewById(R.id.password)
        signInButton = rootView.findViewById(R.id.btn_signin)
        signUpTextView = rootView.findViewById(R.id.sign_up)

        // Manejar el evento de clic en el botón de inicio de sesión
        signInButton.setOnClickListener {
            // Aquí se verificarían las credenciales de usuario
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Supongamos que el login es exitoso
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)

                // Finaliza la actividad actual para evitar que el usuario regrese al login
                activity?.finish()
            } else {
                // Si los campos están vacíos, mostrar un mensaje de error
                Toast.makeText(activity, "Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show()
            }
        }

        // Manejar el evento de clic en el enlace para registrarse
        signUpTextView.setOnClickListener {
            // Navegar al fragmento de registro (puedes hacerlo como un fragmento también)
            val registerUserFragment = RegisterUser()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, registerUserFragment)
                .addToBackStack(null) // Agregar a la pila de retroceso para poder volver
                .commit()
        }

        return rootView
    }
}
