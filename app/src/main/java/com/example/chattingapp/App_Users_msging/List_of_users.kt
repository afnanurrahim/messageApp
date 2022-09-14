package com.example.chattingapp.App_Users_msging

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.SignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class List_of_users : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var arrayListContacts: ArrayList<Contact>
    lateinit var contactAdapter: ContactsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_users)

        supportActionBar?.title = "Select user"

        recyclerView = findViewById(R.id.recyclerView)

        arrayListContacts = Contact.createContactsList()
        contactAdapter = ContactsAdapter(arrayListContacts)
        fetchUsers()

        recyclerView.adapter = contactAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchUsers(){
        val Useruid = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(SignUp.User::class.java) // format of data saved in firebase
                    if (user != null && user.uid!=Useruid) {
                        arrayListContacts.add(0, Contact(user.username,user.uid))
                        contactAdapter.notifyItemInserted(0)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // https://guides.codepath.com/android/using-the-recyclerview

    class Contact(val name: String,val uid:String) {       // pass the pic of user here

        companion object {
            fun createContactsList(): ArrayList<Contact> {
                val contacts = ArrayList<Contact>()
                return contacts
            }
        }
    }

}