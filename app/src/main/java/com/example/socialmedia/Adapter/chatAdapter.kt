package com.example.socialmedia.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.models.Chat
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class chatAdapter(options: FirestoreRecyclerOptions<Chat>):
              FirestoreRecyclerAdapter<Chat,chatAdapter.ChatViewHolder>(options){
    companion object{
        const val  MSG_BY_SELF=0
        const val  MSG_BY_OTHER=1
    }
    class ChatViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
         val chatText: TextView = itemView.findViewById(R.id.chat_Text)
         val chatAuthor: TextView = itemView.findViewById(R.id.chat_author)
    }

    override fun getItemViewType(position: Int): Int {
        return if(getItem(position).author.id==UserUtils.user?.id){
            MSG_BY_SELF
        }else{
            MSG_BY_OTHER
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
      var view: View? = null
        view = if(viewType== MSG_BY_SELF){
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_self,parent,false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_other,parent,false)
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
         holder.chatText.text = model.text
        holder.chatAuthor.text= model.author.name
    }
}