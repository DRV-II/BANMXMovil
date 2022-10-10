package mx.itesm.banmxmovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class donarFragment : Fragment() {
    val args : donarFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_donar, container, false)
        if(Firebase.auth.currentUser == null) {

            // SIGNIFICA QUE HAY NECESIDAD DE RE-VALIDAR EL USUARIO
            // podrías redireccionar / terminar esta actividad
            Toast.makeText(context, "REVALIDA!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        view.findViewById<Button>(R.id.botonDonarBienFragment).setOnClickListener{
            val action = donarFragmentDirections
                .actionDonarFragmentToPagoFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.configDonar).setOnClickListener{
            val action = donarFragmentDirections
                .actionDonarFragmentToPerfilFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.apadrinarDonar).setOnClickListener{
            val action = donarFragmentDirections
                .actionDonarFragmentToApadrinarFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }
        view.findViewById<Button>(R.id.cartDonar).setOnClickListener{
            val action = donarFragmentDirections
                .actionDonarFragmentToCarritoFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }



        return view
    }


}