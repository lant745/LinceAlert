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
import com.example.alertlince.controller.UsuarioDao




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
        val context = requireContext()
        val num = UsuarioDao(context)
        val contactos = num.obtenerContactos()

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
                    num.insertarContactos(nombre, apellido, relacion, telefono, correo)
                    actualizarTabla(contactos)
                    Toast.makeText(context, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Por favor, completa al menos el nombre y teléfono", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    // Método para actualizar la tabla con la lista de contactos
    private fun actualizarTabla(contactos: List<Map<String, String>>) {
        val tableLayout = binding.root.findViewById<TableLayout>(R.id.tableContactos)

        tableLayout.removeAllViews()

        // Crear la fila de encabezado
        val headerRow = TableRow(context).apply {
            addView(createTextView("ID"))
            addView(createTextView("Nombre"))
            addView(createTextView("Apellido"))
            addView(createTextView("Relación"))
            addView(createTextView("Teléfono"))
            addView(createTextView("Correo"))
        }
        tableLayout.addView(headerRow)

        // Recorrer la lista de contactos y agregar una fila para cada uno
        contactos.forEach { contacto ->
            val row = TableRow(context).apply {
                addView(createTextView(contacto["idContacto"] ?: ""))
                addView(createTextView(contacto["nombre"] ?: ""))
                addView(createTextView(contacto["apellido"] ?: ""))
                addView(createTextView(contacto["relacion"] ?: ""))
                addView(createTextView(contacto["telefono"] ?: ""))
                addView(createTextView(contacto["correo"] ?: ""))

                setOnClickListener {
                    // Suponiendo que mostrarOpcionesContacto toma un índice o un identificador de contacto
                    mostrarOpcionesContacto(contacto["idContacto"]?.toInt() ?: -1)
                }
            }
            tableLayout.addView(row)
        }
    }


    // Llamada al método desde otro lugar, por ejemplo, dentro de un método donde obtienes los contactos
    fun cargarContactos() {
        val context = requireContext()
        val num = UsuarioDao(context)

        // Obtener la lista de contactos desde la base de datos (asegurándote de que sea una lista del tipo adecuado)
        val contactos = num.obtenerContactos()  // Este método debe devolver la lista de contactos
        actualizarTabla(contactos) // Pasar la lista de contactos al método para actualizar la tabla
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
        val context = requireContext()
        val num = UsuarioDao(context)

        // Obtener la lista de contactos desde la base de datos (esto devuelve una MapList)
        val contactos = num.obtenerContactos()
        val contacto = contactos[index].toMutableMap()  // Usamos la lista actualizada aquí

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editar_contacto, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombreEditar)
        val etApellido = dialogView.findViewById<EditText>(R.id.etApellidoEditar)
        val etRelacion = dialogView.findViewById<EditText>(R.id.etRelacionEditar)
        val etTelefono = dialogView.findViewById<EditText>(R.id.etTelefonoEditar)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreoEditar)

        // Rellenar los campos con los datos actuales del contacto
        etNombre.setText(contacto["nombre"])
        etApellido.setText(contacto["apellido"])
        etRelacion.setText(contacto["relacion"])
        etTelefono.setText(contacto["telefono"])
        etCorreo.setText(contacto["correo"])

        AlertDialog.Builder(requireContext())
            .setTitle("Editar Contacto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                // Obtener el ID del contacto (suponiendo que ya está en el mapa)
                val id = contacto["idContacto"]?.toString() ?: return@setPositiveButton

                // Obtener los datos de los EditText
                val nom = etNombre.text.toString().trim()
                val ape = etApellido.text.toString().trim()
                val rela = etRelacion.text.toString().trim()
                val telefono = etTelefono.text.toString().trim()
                val correo = etCorreo.text.toString().trim()

                // Guardar los cambios en la base de datos
                num.editarContacto(id, nom, ape, rela, telefono, correo)

                // Volver a cargar la lista de contactos actualizada
                val updatedContactos = num.obtenerContactos()

                // Actualizar la tabla para reflejar los cambios
                actualizarTabla(updatedContactos)

                Toast.makeText(context, "Contacto actualizado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }





    private fun eliminarContacto(index: Int) {
        val context = requireContext()
        val num = UsuarioDao(context)

        // Obtener la lista de contactos desde la base de datos (esto devuelve una MapList)
        val contactos = num.obtenerContactos()

        // Obtener el contacto a eliminar (usando el índice)
        val contactoAEliminar = contactos[index]

        // Obtener el id del contacto a eliminar
        val idContacto = contactoAEliminar["idContacto"]?.toInt() ?: return

        // Eliminar el contacto de la base de datos
        num.eliminarContacto(idContacto.toString())

        // Actualizar la lista de contactos después de eliminar
        val updatedContactos = num.obtenerContactos()

        // Actualizar la tabla para reflejar los cambios
        actualizarTabla(updatedContactos)

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