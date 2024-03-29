package mx.itesm.banmxmovil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CarritoFragment : Fragment() {

    val args : CarritoFragmentArgs by navArgs()
    val db = Firebase.firestore
    lateinit var carritoList : ArrayList<ArrayList<String>>
    lateinit var recyclerViewCarrito : RecyclerView
    private lateinit var myAdapter: carritoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_carrito, container, false)
        carritoList = ArrayList()
        carritoList.toMutableList()
        db.collection("usuarios/${args.idUsuario}/carrito")
            .get()
            .addOnSuccessListener { result ->
                var contador = 0
                for (document in result) {
                    //Log.d("PRUEBA FIREBASE", "${document.id} => ${document.data}")
                    carritoList.add(contador, arrayListOf(document.data["nombre"].toString(), document.data["cantidad"].toString(), document.data["imagen"].toString(), document.data["precio"].toString()))
                }
                //Log.d("PRUEBA FIREBASE", carritoList[0][0])
                myAdapter = carritoAdapter(carritoList, onClickDelete = {position -> onDeletedItem(position)})

                recyclerViewCarrito = view.findViewById(R.id.carritoListR)

                val llm = LinearLayoutManager(context)

                llm.orientation = LinearLayoutManager.VERTICAL

                recyclerViewCarrito.layoutManager = llm

                recyclerViewCarrito.adapter = myAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("PRUEBA ERROR FIREBASE", "Error getting documents.", exception)
            }



        // verificamos usuario
        if(Firebase.auth.currentUser == null) {

            // SIGNIFICA QUE HAY NECESIDAD DE RE-VALIDAR EL USUARIO
            // podrías redireccionar / terminar esta actividad
            Toast.makeText(context, "REVALIDA!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        // Carrito a perfil
        view.findViewById<ImageView>(R.id.configCarrito).setOnClickListener {
            //findNavController().navigate(R.id.action_carritoFragment_to_perfilFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToPerfilFragment2(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }

        // Carrito a apadrinar
        view.findViewById<ImageView>(R.id.apadrinarCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_carritoFragment_to_apadrinarFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToApadrinarFragment2(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.procederAComprarBotonCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_carritoFragment_to_pagoFragment)
            /*
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToPagoFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
            */
            var cantidadTotal = 0

            db.collection("usuarios/${args.idUsuario}/carrito")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        var cantidadSumMult = 0
                        cantidadSumMult = (document.data["precio"].toString().toInt()) * (document.data["cantidad"].toString().toInt())
                        cantidadTotal = cantidadSumMult + cantidadTotal
                    }
                    Log.d("EDIT ERROR","Volley error: "+ cantidadTotal.toString())

                    val intent = Intent(requireActivity(), Transaccion::class.java)
                    intent.putExtra("cantidad", cantidadTotal.toString())
                    startActivity(intent)

                }.addOnFailureListener { exception ->
                    Log.w("PRUEBA ERROR FIREBASE", "Error getting documents.", exception)
                }



        }

        view.findViewById<ImageView>(R.id.homeCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_perfilFragment_to_misTarjetasPerfilFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToInicioFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }

        return view
    }

    fun onDeletedItem(position : Int){
        carritoList.removeAt(position)
        myAdapter.setItems(carritoList)
    }
    /*
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_carrito, container, false)
        carritoList = ArrayList()
        db.collection("usuarios/${args.idUsuario}/carrito")
            .get()
            .addOnSuccessListener { result ->
                var contador = 0
                for (document in result) {
                    //Log.d("PRUEBA FIREBASE", "${document.id} => ${document.data}")
                    carritoList.add(contador, arrayListOf(document.data["nombre"].toString(), document.data["cantidad"].toString(), document.data["imagen"].toString(), document.data["precio"].toString()))
                }
                //Log.d("PRUEBA FIREBASE", carritoList[0][0])
                val adapter = carritoAdapter(carritoList)

                recyclerViewCarrito = view.findViewById(R.id.carritoListR)

                val llm = LinearLayoutManager(context)

                llm.orientation = LinearLayoutManager.VERTICAL

                recyclerViewCarrito.layoutManager = llm

                recyclerViewCarrito.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("PRUEBA ERROR FIREBASE", "Error getting documents.", exception)
            }



        // verificamos usuario
        if(Firebase.auth.currentUser == null) {

            // SIGNIFICA QUE HAY NECESIDAD DE RE-VALIDAR EL USUARIO
            // podrías redireccionar / terminar esta actividad
            Toast.makeText(context, "REVALIDA!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        // Carrito a perfil
        view.findViewById<ImageView>(R.id.configCarrito).setOnClickListener {
            //findNavController().navigate(R.id.action_carritoFragment_to_perfilFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToPerfilFragment2(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }

        // Carrito a apadrinar
        view.findViewById<ImageView>(R.id.apadrinarCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_carritoFragment_to_apadrinarFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToApadrinarFragment2(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.procederAComprarBotonCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_carritoFragment_to_pagoFragment)
            /*
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToPagoFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
             */
            var cantidadTotal = 0

            db.collection("usuarios/${args.idUsuario}/carrito")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        document.data["precio"].toString()
                    }

                }.addOnFailureListener { exception ->
                    Log.w("PRUEBA ERROR FIREBASE", "Error getting documents.", exception)
                }


            val intent = Intent(requireActivity(), Transaccion::class.java)
            intent.putExtra("cantidad", cantidadTotal.toString())
            startActivity(intent)
        }

        view.findViewById<ImageView>(R.id.homeCarrito).setOnClickListener{
            //findNavController().navigate(R.id.action_perfilFragment_to_misTarjetasPerfilFragment2)
            val action = CarritoFragmentDirections
                .actionCarritoFragmentToInicioFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }

        return view
    }
    */
}