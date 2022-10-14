package mx.itesm.banmxmovil

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class editarPerfilFragment : Fragment() {

    val args : editarPerfilFragmentArgs by navArgs()
    val db = Firebase.firestore
    val storage = Firebase.storage
    val storageRef = storage.reference

    val user = Firebase.auth.currentUser

    lateinit var profilePicture : ImageView

    var photoUrl: Uri? = Uri.parse("https://firebasestorage.googleapis.com/v0/b/banmxmovil.appspot.com/o/PerfilPredeterminado%2FPerfilPredeterminado.png?alt=media&token=5dee5118-a05e-4163-b2a7-bdc2dc95da22")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_editar_perfil, container, false)

        //Obtenemos URL
        storageRef.child("PerfilPredeterminado/PerfilPredeterminado.png").downloadUrl.addOnSuccessListener { url ->
            // Got the download URL for 'users/me/profile.png'
            photoUrl = Uri.parse("$url")
        }.addOnFailureListener {
            // Handle any errors
        }

        // verificamos usuario
        if(user == null) {

            // SIGNIFICA QUE HAY NECESIDAD DE RE-VALIDAR EL USUARIO
            // podrías redireccionar / terminar esta actividad
            Toast.makeText(context, "REVALIDA!", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }else {
            user?.let {
                // Name, email address, and profile photo Url
                //val name = user.displayName
                //val email = user.email
                photoUrl = user.photoUrl

                // Check if user's email is verified
                //val emailVerified = user.isEmailVerified

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getToken() instead.
                //val uid = user.uid
                // Foto de perfil
            }
        }

        profilePicture = view.findViewById(R.id.imagenPerfilEditarPerfil2)
        profilePicture.setImageURI(photoUrl)
        // Cambiar foto perfil
        profilePicture.setOnClickListener{
            pickImageFromGallery()
        }

        // Guardamos cambios
        view.findViewById<Button>(R.id.guardarBotonEditarPerfil).setOnClickListener{
            val action = editarPerfilFragmentDirections
                .actionEditarPerfilFragmentToPerfilFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)

            val data = hashMapOf(
                "nombre" to view.findViewById<EditText>(R.id.nameInputEditarPerfil).text.toString()
            )
            db.collection("usuarios").document("${args.idUsuario}").set(data)
            Toast.makeText(context, "Información Guardada", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<ImageButton>(R.id.regresarBotonEditarPerfil).setOnClickListener{
            val action = editarPerfilFragmentDirections
                .actionEditarPerfilFragmentToPerfilFragment(
                    args.idUsuario
                )
            findNavController().navigate(action)
        }

        view.findViewById<Button>(R.id.borrarCuentaBotonEditarPerfil).setOnClickListener{
            val user = Firebase.auth.currentUser!!

            db.collection("usuarios").document("${args.idUsuario}")
                .delete()
                .addOnSuccessListener {
                    Log.d("FIRESTORE DELETE", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w("FIRESTORE DELETE", "Error deleting document", e)
                    Toast.makeText(
                        context,
                        "CUENTA NO SE PUDO BORRAR: $e",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "CUENTA BORRADA EXITOSAMENTE",
                            Toast.LENGTH_SHORT
                        ).show()
                        requireActivity().finish()
                        Log.d("FIREBASE AUTH DELETE", "User account deleted.")
                    }
                }
        }

        return view
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        requireActivity().startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.data != null){
                val image = data.data
                // Create a reference to 'images/mountains.jpg'
                val imagesRef = storageRef.child("images/${args.idUsuario}/${image!!.lastPathSegment}")
                var uploadTask = imagesRef.putFile(image!!)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    profilePicture.setImageURI(image)
                    //Obtenemos URL
                    storageRef.child("images/${args.idUsuario}/${image.lastPathSegment}").downloadUrl.addOnSuccessListener { url ->
                        // Got the download URL for 'users/me/profile.png'
                        // Subimos a auth
                        val profileUpdates = userProfileChangeRequest {
                            //displayName = "${args.idUsuario}"
                            photoUri = Uri.parse("$url")
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("AUTH FIREBASE", "User profile updated.")
                                }
                            }
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
            }
        }

    }


    companion object {
        val IMAGE_REQUEST_CODE = 1_000;
    }

}