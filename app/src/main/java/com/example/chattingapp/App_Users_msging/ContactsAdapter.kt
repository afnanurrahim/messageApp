package com.example.chattingapp.App_Users_msging

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.Chat_Room.ChatRoom
import com.example.chattingapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ContactsAdapter (private val mContacts: List<List_of_users.Contact>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTextView = itemView.findViewById<TextView>(R.id.contact_name)
        val imgViewProfilePic =itemView.findViewById<ImageView>(R.id.ImgViewprofilePic)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.contacts_list, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val contact: List_of_users.Contact = mContacts.get(position)
        val textView = viewHolder.nameTextView
        textView.setText(contact.name)
        getProfilePic(contact.uid,viewHolder)
        Log.d("ERROR","OCCURRED")

        viewHolder.itemView.setOnClickListener(View.OnClickListener {
            val intent= Intent(it.context, ChatRoom::class.java)
            intent.putExtra("Person name",contact.name)
            intent.putExtra("Person uid",contact.uid)
            it.context.startActivity(intent)
            (it.context as Activity).finish()
        })
    }

    fun getProfilePic(uid:String,viewHolder: ViewHolder){
        val firebaseStore= FirebaseFirestore.getInstance()
        firebaseStore.document("/profile_pictures_url/$uid")
            .get().addOnSuccessListener {
                val filename= it.data?.get("Filename")
                val ref = FirebaseStorage.getInstance().getReference("/profilePicture/$filename")
                ref.downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(viewHolder.imgViewProfilePic)
                    Log.d("ERROR", it.toString())
                }

            }

    }

    override fun getItemCount(): Int {
        return mContacts.size
    }
}