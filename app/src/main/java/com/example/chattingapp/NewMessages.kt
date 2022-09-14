package com.example.chattingapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.App_Users_msging.List_of_users
import com.example.chattingapp.Chat_Room.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NewMessages : AppCompatActivity() {
    lateinit var recyclerViewLastMsg: RecyclerView
    lateinit var arrayListNewMsgs: ArrayList<AllChats>
    lateinit var adapter: NewMsgsAdapter
    lateinit var hashmapAllPartners:HashMap<String,AllChats>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_messages)

        IsUserLoggedIn()
        hashmapAllPartners=HashMap()
        arrayListNewMsgs=AllChats.createNewMsgsList()
        recyclerViewLastMsg= findViewById(R.id.recyclerViewLastMsgs)
        adapter= NewMsgsAdapter(arrayListNewMsgs)

//        getAllChatPartners()
        recyclerViewLastMsg.adapter=adapter
        recyclerViewLastMsg.layoutManager=LinearLayoutManager(this)

    }

        val firebaseStore=FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        getAllChatPartners()
    }
        @RequiresApi(Build.VERSION_CODES.N)
        fun getLastChat(getMsgData:DocumentReference, partner: MutableMap.MutableEntry<String, Any>){
            var lastMsg:String=""
            getMsgData.collection("${partner.key}").orderBy("message index")
                .addSnapshotListener{snapshots,e->
                    if (e != null) {
                        Log.d("SNAPSHOT LISTENER ERORR", e.toString())
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        if (hashmapAllPartners.containsKey(partner.key)){
                            Log.d("JAA RAHA HAI KI NAHI","HA")
                            val toReplace=hashmapAllPartners.get(partner.key)
                            adapter.notifyItemRemoved(arrayListNewMsgs.indexOf(toReplace))
                            arrayListNewMsgs.remove(toReplace)
                        }
                        lastMsg=snapshots.last().data.get("message").toString()
                        val chatValue=AllChats(partner.value.toString(), partner.key, lastMsg)
                        hashmapAllPartners.put(partner.key,chatValue)
                        arrayListNewMsgs.add(0,chatValue)
                        adapter.notifyItemInserted(0)
                        return@addSnapshotListener
                    }
                }
        }
        @RequiresApi(Build.VERSION_CODES.N)
        fun getAllChatPartners(){

            val uid = FirebaseAuth.getInstance().uid?:""

            val getMsgData=firebaseStore.collection("/messages")
                .document("$uid")
//
            getMsgData.get().addOnCompleteListener{
                val documentSnapshot: DocumentSnapshot = it.getResult()
                val group=documentSnapshot.getData()
                if (group != null) {
                    for (partner in group){
                        Log.d("HOW MANY PARTNERS","NOT MUCH")
                        Log.d("PERSON",partner.key)
                        getLastChat(getMsgData,partner)
//                        getMsgData.collection("${partner.value}")
                    }
                }
            }
        }



    fun IsUserLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if (uid==null){
            val intent = Intent(this@NewMessages,LogIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)     // to clear the previous activity
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.menu_list_of_users->{
                val intent = Intent(this@NewMessages, List_of_users::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@NewMessages,LogIn::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)     // to clear the previous activity
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_options,menu)
        return super.onCreateOptionsMenu(menu)
    }

    class AllChats(val name_sender:String,val sender_uid:String, val last_msg:String){
        companion object {
            fun createNewMsgsList(): ArrayList<AllChats> {
                val arrayList = ArrayList<AllChats>()
                return arrayList
            }
        }
    }
}