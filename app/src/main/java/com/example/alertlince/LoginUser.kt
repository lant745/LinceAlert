package com.example.alertlince

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alertlince.controller.UsuarioDao
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
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validar la entrada
            if (validateInput(email, password)) {
                // Supongamos que el login es exitoso
                val intent = Intent(activity, Vistas::class.java)
                startActivity(intent)

                // Finaliza la actividad actual para evitar que el usuario regrese al login
                activity?.finish()
            }
            signInButton.isEnabled = false
            login(email,password)
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

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() ||  password.isEmpty()) {
            Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Ingrese un email válido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length < 6) {
            Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun login(email: String, password: String){
        val context = requireContext()
        val usuarioDao = UsuarioDao(context)
        val resultado = usuarioDao.obtenerUsuario(email,password)


        signInButton.isEnabled = true
        if (resultado) {  // Si la inserción fue exitosa
            Toast.makeText(context, "Sesion exitosa", Toast.LENGTH_SHORT).show()

            // Reemplazar la actividad actual por la nueva actividad (com.example.alertlince.MainActivity)
            activity?.let {
                val intent = Intent(it, Vistas::class.java)
                it.startActivity(intent)
                requireActivity().finish()  // Finalizar la actividad actual si es necesario
            }
        } else {
            Toast.makeText(context, "Error de Autenticación", Toast.LENGTH_SHORT).show()
        }

    }
}
