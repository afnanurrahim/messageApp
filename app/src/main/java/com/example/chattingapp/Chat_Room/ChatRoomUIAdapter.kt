package com.example.chattingapp.Chat_Room;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.R


class ChatRoomUIAdapter(private val mChats:List<ChatRoom.AddChats>): RecyclerView.Adapter<ChatRoomUIAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewUserChat = itemView.findViewById<TextView>(R.id.textViewUserChat)
        val textViewSenderChat = itemView.findViewById<TextView>(R.id.textViewSenderChat)
    }

    override fun getItemViewType(position: Int): Int {
        val chat:ChatRoom.AddChats=mChats.get(position)
        return chat.user_or_sender
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomUIAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        var chatView=inflater.inflate(viewType,parent,false)
        return ViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val textView:TextView
        val chat:ChatRoom.AddChats=mChats.get(position)

        if (chat.user_or_sender==R.layout.display_format_users_chats){
            textView=holder.textViewUserChat
        }else{
            textView=holder.textViewSenderChat
        }
        textView.setText(chat.chat)
    }
    override fun getItemCount(): Int {
        return mChats.size
    }
}

