
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

class RegisterUser : Fragment() {

    private lateinit var email: TextInputEditText
    private lateinit var telefono: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var btnSignUp: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnInicio: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragmento
        return inflater.inflate(R.layout.activity_registeruser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializando los campos de entrada
        email = view.findViewById(R.id.email)
        telefono = view.findViewById(R.id.telefono)
        password = view.findViewById(R.id.password)

        // Inicializando los botones
        btnSignUp = view.findViewById(R.id.sign_up)
        btnGoogle = view.findViewById(R.id.btn_google)
        btnInicio = view.findViewById(R.id.btn_inicio)

        // Funcionalidad del botón de registro
        btnSignUp.setOnClickListener {
            val emailText = email.text.toString().trim()
            val telefonoText = telefono.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // Validación básica
            if (emailText.isEmpty() || telefonoText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí puedes agregar el código para registrar al usuario
            registerUser(emailText, telefonoText, passwordText)
        }

        // Funcionalidad para el botón de "No tienes cuenta?"
        btnInicio.setOnClickListener {
            // Aquí puedes cambiar de fragmento a LoginFragment
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, LoginUser()) // Asegúrate de que tengas un contenedor adecuado
            fragmentTransaction.commit()
        }
    }

    // Función para registrar al usuario
    private fun registerUser(email: String, telefono: String, password: String) {
        // Aquí va tu lógica de registro, ya sea a través de una API o base de datos
        Toast.makeText(context, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()

        // Por ejemplo, después del registro, puedes ir a la página principal:
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
