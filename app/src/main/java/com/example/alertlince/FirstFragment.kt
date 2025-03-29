package com.example.alertlince

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.alertlince.controller.UsuarioDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FirstFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.homeprincipal, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Encuentra el botón por su ID
        val btnAlerta: Button = view.findViewById(R.id.btn_alerta)

        // Configura el evento al hacer clic
        btnAlerta.setOnClickListener {
            obtenerUbicacion { ubicacion ->
                if (ubicacion != null) {
                    val mensaje = "¡Alerta de emergencia! Ubicación: $ubicacion"
                    sendSms(mensaje)  // Llama al método para enviar SMS con la ubicación
                } else {
                    Toast.makeText(requireContext(), "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    // Función para obtener la ubicación del usuario
    private fun obtenerUbicacion(callback: (String?) -> Unit) {
        // Verificar si se tienen los permisos necesarios
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Solicitar permisos si no han sido otorgados
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1 // Código de solicitud de permiso
            )
            callback(null)
            return
        }

        // Verificar si el GPS está habilitado
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // Avisar al usuario que habilite el GPS
            callback(null)
            return
        }

        // Obtener la última ubicación conocida
        val task: Task<Location> = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val ubicacion = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                callback(ubicacion) // Pasar la ubicación al callback
            } else {
                callback(null) // Si no se pudo obtener la ubicación
            }
        }
    }

    // Manejar el resultado de la solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            // Si el permiso fue concedido
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion { ubicacion ->
                    // Aquí puedes manejar la ubicación obtenida
                    Log.d("Ubicacion", "Ubicación obtenida: $ubicacion")
                }
            } else {
                // El permiso fue denegado
                Log.d("Ubicacion", "Permiso de ubicación denegado")
            }
        }
    }

    // Método para enviar SMS a la lista de números
    fun sendSms(message: String) {
        val context = requireContext()
        val conController = UsuarioDao(context) // Instancia el controlador
        val conNumeros = conController.obtenerNumeros() // Obtienes la lista de números

        val smsManager = SmsManager.getDefault() // Obtienes el SmsManager por defecto

        // Recorre los números de teléfono y envía el mensaje
        for (numero in conNumeros) {
            try {
                smsManager.sendTextMessage(numero, null, message, null, null)
                println("Mensaje enviado a: $numero")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
