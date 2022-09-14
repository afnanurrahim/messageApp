package com.example.chattingapp

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.App_Users_msging.ContactsAdapter
import com.example.chattingapp.Chat_Room.ChatRoom
import com.example.chattingapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class NewMsgsAdapter (val recentMsgs: List<NewMessages.AllChats>) : RecyclerView.Adapter<NewMsgsAdapter.ViewHolder>(){
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewName = itemView.findViewById<TextView>(R.id.textViewName)
        val textViewLastMsg = itemView.findViewById<TextView>(R.id.textViewLastMsg)
        val imgView_new_msg =itemView.findViewById<ImageView>(R.id.imageView_new_msg)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val newMsgView = inflater.inflate(R.layout.display_format_new_messages, parent, false)
        return ViewHolder(newMsgView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recent: NewMessages.AllChats= recentMsgs.get(position)
        holder.textViewName.setText(recent.name_sender)
        holder.textViewLastMsg.setText(recent.last_msg)
        getProfilePic(recent.sender_uid,holder)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent= Intent(it.context, ChatRoom::class.java)
            intent.putExtra("Person name",recent.name_sender)
            intent.putExtra("Person uid",recent.sender_uid)
            it.context.startActivity(intent)
        })

    }
    fun getProfilePic(uid:String,viewHolder: NewMsgsAdapter.ViewHolder){
        val firebaseStore= FirebaseFirestore.getInstance()
        firebaseStore.document("/profile_pictures_url/$uid")
            .get().addOnSuccessListener {
                val filename= it.data?.get("Filename")
                val ref = FirebaseStorage.getInstance().getReference("/profilePicture/$filename")
                ref.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(viewHolder.imgView_new_msg)
                    Log.d("ERROR", it.toString())
                }

            }

    }

    override fun getItemCount(): Int {
        return recentMsgs.size
    }

}