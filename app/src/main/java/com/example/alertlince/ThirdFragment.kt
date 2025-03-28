package com.example.alertlince

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.alertlince.databinding.FragmentThirdBinding
import com.example.alertlince.R
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.view.Gravity
import com.example.alertlince.Contacto




class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!

    private val listaContactos = mutableListOf<Contacto>()
    private var idCounter = 1 // Para generar un ID único para cada contacto

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.fabAddContact.setOnClickListener {
            mostrarDialogoAgregarContacto()
        }

        return view
    }

    private fun mostrarDialogoAgregarContacto() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_agregar_contacto, null)
        val nombreInput = dialogView.findViewById<EditText>(R.id.etNombre)
        val apellidoInput = dialogView.findViewById<EditText>(R.id.etApellido)
        val relacionInput = dialogView.findViewById<EditText>(R.id.etRelacion)
        val telefonoInput = dialogView.findViewById<EditText>(R.id.etTelefono)
        val correoInput = dialogView.findViewById<EditText>(R.id.etCorreo)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Agregar Contacto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = nombreInput.text.toString()
                val apellido = apellidoInput.text.toString()
                val relacion = relacionInput.text.toString()
                val telefono = telefonoInput.text.toString()
                val correo = correoInput.text.toString()

                if (nombre.isNotEmpty() && telefono.isNotEmpty()) {
                    val nuevoContacto = Contacto(idCounter++, nombre, apellido, relacion, telefono, correo)
                    listaContactos.add(nuevoContacto)
                    actualizarTabla()
                    Toast.makeText(context, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor, completa al menos el nombre y teléfono", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun actualizarTabla() {
        val tableLayout = binding.root.findViewById<TableLayout>(R.id.tableContactos)

        tableLayout.removeAllViews()

        val headerRow = TableRow(context).apply {
            addView(createTextView("ID"))
            addView(createTextView("Nombre"))
            addView(createTextView("Apellido"))
            addView(createTextView("Relación"))
            addView(createTextView("Teléfono"))
            addView(createTextView("Correo"))
        }
        tableLayout.addView(headerRow)

        listaContactos.forEachIndexed { index, contacto ->
            val row = TableRow(context).apply {
                addView(createTextView(contacto.id.toString()))
                addView(createTextView(contacto.nombre))
                addView(createTextView(contacto.apellido))
                addView(createTextView(contacto.relacion))
                addView(createTextView(contacto.telefono))
                addView(createTextView(contacto.correo))

                setOnClickListener { mostrarOpcionesContacto(index) }
            }
            tableLayout.addView(row)
        }
    }

    private fun mostrarOpcionesContacto(index: Int) {
        val opciones = arrayOf("Editar", "Eliminar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Opciones")
        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> editarContacto(index)
                1 -> eliminarContacto(index)
            }
        }
        builder.show()
    }

    private fun editarContacto(index: Int) {
        val contacto = listaContactos[index]

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_contacto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreEditar)
        val etApellido = dialogView.findViewById<EditText>(R.id.etApellidoEditar)
        val etRelacion = dialogView.findViewById<EditText>(R.id.etRelacionEditar)
        val etTelefono = dialogView.findViewById<EditText>(R.id.etTelefonoEditar)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreoEditar)

        etNombre.setText(contacto.nombre)
        etApellido.setText(contacto.apellido)
        etRelacion.setText(contacto.relacion)
        etTelefono.setText(contacto.telefono)
        etCorreo.setText(contacto.correo)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Contacto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                contacto.nombre = etNombre.text.toString().trim()
                contacto.apellido = etApellido.text.toString().trim()
                contacto.relacion = etRelacion.text.toString().trim()
                contacto.telefono = etTelefono.text.toString().trim()
                contacto.correo = etCorreo.text.toString().trim()

                actualizarTabla()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarContacto(index: Int) {
        listaContactos.removeAt(index)
        actualizarTabla()
        Toast.makeText(context, "Contacto eliminado", Toast.LENGTH_SHORT).show()
    }

    private fun createTextView(text: String): TextView {
        return TextView(context).apply {
            this.text = text
            this.gravity = Gravity.CENTER
            this.setPadding(10, 10, 10, 10)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
