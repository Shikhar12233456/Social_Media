package com.example.socialmedia.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.models.Comment
import com.example.socialmedia.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils

class CommentsAdapter(options: FirestoreRecyclerOptions<Comment>, val context: Context):
    FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentViewHolder>(options) {

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val commentText: TextView = itemView.findViewById(R.id.comment_text)
            val commentAuthor: TextView = itemView.findViewById(R.id.author_text)
            val commentTime: TextView = itemView.findViewById(R.id.Comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment,parent,false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comment) {
     val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted= DateTimeUtils.formatWithStyle(date,DateTimeStyle.LONG)
        holder.commentText.text = model.text
        holder.commentAuthor.text=model.author.name
        holder.commentTime.text = dateFormatted

    }
}