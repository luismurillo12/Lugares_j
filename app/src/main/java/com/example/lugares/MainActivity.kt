package com.example.lugares

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lugares.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Definicion del objeto para hacer la auntenticacion
    private lateinit var auth : FirebaseAuth
    private lateinit var binding : ActivityMainBinding
    private lateinit var googleSingInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // con esto se inicializa para manejar las vistas
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // con esto se inicializa Firebase para usarse en el app
        //se asigna el objeto aith para la autenticacion
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        binding.btRegister.setOnClickListener{ haceRegistro () }
        binding.btLogin.setOnClickListener{ haceLogin () }

        val gso =  GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSingInClient= GoogleSignIn.getClient(this,gso)

        binding.btGoogle.setOnClickListener{ googleSingIn()}

    }

    private fun googleSingIn() {
        val signInIntent = googleSingInClient.signInIntent
        startActivityForResult(signInIntent, 5000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode ==5000){
            val tarea = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = tarea.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(cuenta.id)
            }catch (e: ApiException){

            }
        }
    }

    private fun firebaseAuthWithGoogle(id: String?) {
           val credenciales = GoogleAuthProvider.getCredential(id,null)
        auth.signInWithCredential(credenciales)
            .addOnCompleteListener (this){ task->
                if(task.isSuccessful){ // si logra crear el usuario
                    Log.d("Autenticando","Usuario autenticado")
                   val user = auth.currentUser  // recupero la info del usuario creado
                    actualiza(user)
                }else{
                    Log.d("Autenticando","Error autenticando usuario")
                    actualiza(null)
                }

            }


    }

    private fun haceRegistro() {
        // optenemos la info que ingreso el usuario
        val email = binding.etEmail.text.toString()
        val clave = binding.etClave.text.toString()
        // se llama a la funcion para crear un usuario en firebase (correo/contraseña)
        auth.createUserWithEmailAndPassword(email,clave)
            .addOnCompleteListener (this){ task->
                var user: FirebaseUser? = null
                if(task.isSuccessful){ // si logra crear el usuario
                    Log.d("Autenticando","Usuario Creado")
                    user = auth.currentUser  // recupero la info del usuario creado
                    actualiza(user)
                }else{
                    Log.d("Autenticando","Error creando usuario")
                    actualiza(null)
                }

            }
    }


    private fun haceLogin() {
        // optenemos la info que ingreso el usuario
        val email = binding.etEmail.text.toString()
        val clave = binding.etClave.text.toString()
        // se llama a la funcion para optener un usuario en firebase (correo/contraseña)
        auth.signInWithEmailAndPassword(email,clave)
            .addOnCompleteListener (this){ task->
                var user: FirebaseUser? = null
                if(task.isSuccessful){ // si logra crear el usuario
                    Log.d("Autenticando","Usuario autenticado")
                    user = auth.currentUser  // recupero la info del usuario creado
                    actualiza(user)
                }else{
                    Log.d("Autenticando","Error autenticando usuario")
                    actualiza(null)
                }

            }
    }

    private fun actualiza(user: FirebaseUser?) {
        // si hay un usuario definido se pasa a la pantalla principal
        if(user!=null){
            //se pasa a la siguiente pantalla
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }
    }
    //Se ejeculta cuando el app aparezca en la pantalla ...
    public override fun onStart(){
        super.onStart()
        val usuario=auth.currentUser
        actualiza(usuario)
    }
}
