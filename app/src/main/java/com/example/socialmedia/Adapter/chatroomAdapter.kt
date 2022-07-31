package com.example.socialmedia.Adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.ChatFragment
import com.example.socialmedia.MainActivity
import com.example.socialmedia.R
import com.example.socialmedia.models.ChatRoom
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class chatroomAdapter(options: FirestoreRecyclerOptions<ChatRoom>,val context: Context)
    :FirestoreRecyclerAdapter<ChatRoom,chatroomAdapter.ChatRoomViewHolder>(options){

        class ChatRoomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
         val chatRoomName: TextView=itemView.findViewById(R.id.chatroomName)!!
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
          val view= LayoutInflater.from(parent.context).inflate(R.layout.item_chatroom,parent,false)
         return  ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int, model: ChatRoom) {
       holder.chatRoomName.text = model.name
     holder.itemView.setOnClickListener {
         val bundle= Bundle()
         bundle.putString("chatroomId",model.id)

         val chatFragment= ChatFragment()
         chatFragment.arguments= bundle
         (context as MainActivity).supportFragmentManager.beginTransaction()
             .replace(R.id.fragment_container,chatFragment)
             .addToBackStack(null)
             .commit()
     }
    }
}