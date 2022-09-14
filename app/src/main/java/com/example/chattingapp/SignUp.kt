package com.example.chattingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.Chat_Room.ChatRoom
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class SignUp : AppCompatActivity() {

    private lateinit var ButtonSignUp : Button
    private lateinit var EditTextUsername: EditText
    private lateinit var EditTextEmailId: EditText
    private lateinit var EditTextPassword: EditText
    private lateinit var mAuth: FirebaseAuth    // to authenticate email and password
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()


        mAuth = FirebaseAuth.getInstance()

        EditTextUsername = findViewById(R.id.UsernameEditText)
        EditTextEmailId=findViewById(R.id.EmailIdEditText)
        EditTextPassword=findViewById(R.id.PasswordEditText)
        ButtonSignUp =findViewById(R.id.SignupButton);

        ButtonSignUp.setOnClickListener(){
            val Email = EditTextEmailId.text.toString()
            val Password = EditTextPassword.text.toString()
            val username = EditTextUsername.text.toString()
            if(emailAlreadyExist(Email)){
                signUp(Email,Password,username)
            }

        }
    }
    private fun signUp(email: String,password: String, username: String){

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToFirebase(username, email)

                    val intent = Intent(this@SignUp,getProfilePhoto::class.java)
                    intent.putExtra("User's uid",FirebaseAuth.getInstance().uid)
                    intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)     // to clear the previous (Log In) activity
                    startActivity(intent)

                } else {
                    // If sign in fails, display a message to the user.
                    EditTextEmailId.setBackgroundResource(R.drawable.login_error)
                    EditTextPassword.setBackgroundResource(R.drawable.login_error)

                    Toast.makeText(this@SignUp,"Check your email and password",Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun emailAlreadyExist(email: String):Boolean{
        var bool =true
        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult> { task ->
                val isNewUser = task.result.signInMethods!!.isEmpty()
                if (!isNewUser){
                    Toast.makeText(this@SignUp,"Email id already exist",Toast.LENGTH_SHORT).show()
                }
                bool=isNewUser
            })
        return bool
    }

    private fun saveUserToFirebase(username:String,emailId:String){
        val uid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user=User(uid, username, emailId)

        ref.setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this@SignUp,"Welcome",Toast.LENGTH_SHORT).show()
            }

    }

    class User (val uid:String , val username:String , val emailId:String){
        constructor(): this("","","")
    }

}