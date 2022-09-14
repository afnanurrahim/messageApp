package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class LogIn : AppCompatActivity() {

    private lateinit var ButtonSignUp : Button
    private lateinit var ButtonLogIn : Button
    private lateinit var EditTextEmailId: EditText
    private lateinit var EditTextPassword: EditText
    private lateinit var mAuth: FirebaseAuth  // to authenticate email and password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()
        mAuth = FirebaseAuth.getInstance()

        EditTextEmailId=findViewById(R.id.EmailIdEditText)
        EditTextPassword=findViewById(R.id.PasswordEditText)
        ButtonSignUp =findViewById(R.id.SignupButton);
        ButtonLogIn = findViewById(R.id.LogInButton)

        ButtonSignUp.setOnClickListener(){
            val SignUpActivity = Intent(this,SignUp::class.java)
            startActivity(SignUpActivity)
        }

        ButtonLogIn.setOnClickListener(){
            val EmailId = EditTextEmailId.text.toString()
            val Password = EditTextPassword.text.toString()

            login(EmailId,Password)
        }


    }

    private fun login(email: String,password: String){

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LogIn,NewMessages::class.java)
                    intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)     // to clear the previous (Log In) activity
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.

                    EditTextEmailId.setBackgroundResource(R.drawable.login_error)
                    EditTextPassword.setBackgroundResource(R.drawable.login_error)

                   Toast.makeText(this@LogIn,"User does not exist",Toast.LENGTH_SHORT).show()
                }
            }
    }
}