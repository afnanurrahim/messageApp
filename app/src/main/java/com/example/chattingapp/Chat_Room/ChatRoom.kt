package com.example.chattingapp.Chat_Room

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R
import com.example.chattingapp.SignUp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import java.util.Calendar
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatRoom : AppCompatActivity() {
    lateinit var recyclerViewChats: RecyclerView
    lateinit var arrayListChats: ArrayList<AddChats>
    lateinit var adapter: ChatRoomUIAdapter
    lateinit var buttonSend:Button
    lateinit var editTextMsg:EditText
    lateinit var senderUid:String
    lateinit var userUid:String
    lateinit var firebaseStore :FirebaseFirestore
    lateinit var getCollection :CollectionReference

    var num_of_user_msg:Int = 0
    var total_num_msg:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        val Person = intent.getStringExtra("Person name")
        supportActionBar?.title=Person

        firebaseStore= FirebaseFirestore.getInstance()

        userUid = FirebaseAuth.getInstance().uid?:""
        senderUid = intent.getStringExtra("Person uid").toString()

        getCollection = firebaseStore.collection("/messages/$userUid/$senderUid")

        buttonSend=findViewById(R.id.buttonSend)
        editTextMsg=findViewById(R.id.editTextMsg)
        recyclerViewChats=findViewById(R.id.recyclerViewChats)
        arrayListChats=AddChats.createChatList()
        adapter= ChatRoomUIAdapter(arrayListChats)

        recyclerViewChats.adapter = adapter
        recyclerViewChats.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {        // for fetching chats and realtime chats
        super.onStart()
        getCollection.orderBy("message index").addSnapshotListener{ snapshots,e->

            if (e != null) {
                Log.d("SNAPSHOT LISTENER ERORR", e.toString())
                return@addSnapshotListener
            }
            for (doc in snapshots!!.documentChanges) {
                if (doc.type==DocumentChange.Type.ADDED){
                    val userPattern = Regex("^user")
                    if (userPattern.containsMatchIn(doc.document.id)){
                        arrayListChats.add( AddChats(doc.document.data.get("message").toString(),R.layout.display_format_users_chats))
                        adapter.notifyItemInserted(adapter.itemCount-1)
                        num_of_user_msg++
                    }else{
                        arrayListChats.add( AddChats(doc.document.data.get("message").toString(),R.layout.display_format_senders_chats))
                        adapter.notifyItemInserted(adapter.itemCount-1)
                    }
                    total_num_msg++
                }
            }
        }
    }

    fun sendChats(view: View){

        save_to_field()

        val uid = FirebaseAuth.getInstance().uid?:""
        val msgToSend=editTextMsg.text.toString()

        var message : HashMap<String, Any> = HashMap<String, Any> ()
        message.put("message index",total_num_msg)
        message.put("message",msgToSend)
        message.put("time",Calendar.getInstance().time)

        // save in users database

        firebaseStore.collection("messages/$uid/$senderUid")
            .document("user[$num_of_user_msg]")
            .set(message)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Log.d("message send ",msgToSend)
                }else{
                    Log.d("ERROR in sending msg ",msgToSend)
                }
            }

        // save in senders database

        firebaseStore.collection("messages/$senderUid/$uid")
            .document("sender[$num_of_user_msg]")
            .set(message)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Log.d("message received ",msgToSend)
                }else{
                    Log.d("ERROR in receiving msg ",msgToSend)
                }
            }

//        num_msg_sender++

        editTextMsg.setText("")
        recyclerViewChats.adapter?.let {
            if (it.itemCount!=0){
                recyclerViewChats.smoothScrollToPosition(it.itemCount-1)
            }
        }


    }

    fun save_to_field(){
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sender =snapshot.child("$senderUid").getValue(SignUp.User::class.java)
                val user =snapshot.child("$userUid").getValue(SignUp.User::class.java)

                if (sender != null) {
                    var uid_of_partner : HashMap<String, String> = HashMap<String, String> ()
                    uid_of_partner.put("$senderUid",sender.username)
                    firebaseStore.collection("messages").document("$userUid").set(uid_of_partner,
                        SetOptions.merge())

                    var uid_of_user: HashMap<String, String> = HashMap<String, String> ()
                    if (user != null) {
                        uid_of_user.put(userUid,user.username)
                    }
                    firebaseStore.collection("messages").document(senderUid).set(uid_of_user,SetOptions.merge())
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


    class AddChats(val chat: String, val user_or_sender:Int) {
        companion object {
            fun createChatList():ArrayList<AddChats>{
                val chats=ArrayList<AddChats>()
                return chats
            }
        }
    }


}
