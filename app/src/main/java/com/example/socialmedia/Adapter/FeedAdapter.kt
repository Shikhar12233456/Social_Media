package com.example.socialmedia.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.CommentsActivity
import com.example.socialmedia.models.Post
import com.example.socialmedia.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context): FirestoreRecyclerAdapter<Post,FeedAdapter.FeedViewHolder>(options) {
        class FeedViewHolder(itemView: android.view.View): RecyclerView.ViewHolder(itemView){
           val postImage: ImageView = itemView.findViewById(R.id.post_image)
           val postText: TextView = itemView.findViewById(R.id.Post_Text)
           val authorText: TextView = itemView.findViewById(R.id.post_author)
           val time: TextView = itemView.findViewById(R.id.post_time)
           val commentCount: TextView = itemView.findViewById(R.id.comment_count)
           val likeIcon: ImageView = itemView.findViewById(R.id.like_icon)
          val commentIcon: ImageView = itemView.findViewById(R.id.comment_icon)
           val likeCount: TextView = itemView.findViewById(R.id.like_count)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false)
        return FeedViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, model: Post) {

        val date=DateTimeUtils.formatDate(model.time)
        val  dateFormat= DateTimeUtils.formatWithStyle(date,DateTimeStyle.LONG)
        holder.postText.text =model.text
        holder.authorText.text=model.user.name
        holder.time.text = dateFormat
        holder.likeCount.text = model.likeList.size.toString()
       Glide.with(context)
           .load(model.uri)
           .centerCrop()
           .placeholder(R.mipmap.back_img)
           .into(holder.postImage)
        val fireStore = FirebaseFirestore.getInstance()
        val userid = FirebaseAuth.getInstance().currentUser?.uid
     val postDocument = fireStore.collection("Posts").document(snapshots.getSnapshot(holder.adapterPosition).id)

       postDocument.collection("Comments").get().addOnCompleteListener {
           if(it.isSuccessful){
               holder.commentCount.text= it.result?.size().toString()
           }
       }
        postDocument.get().addOnCompleteListener {
            if(it.isSuccessful){
               val post = it.result?.toObject(Post::class.java)
                post?.likeList?.let {list->
                    if(list.contains(userid)){
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,R.drawable.liked))
                    }else{
                        holder.likeIcon.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_baseline_favorite_border_24))
                    }

                    holder.likeIcon.setOnClickListener{
                        if(post.likeList.contains(userid)){
                            post.likeList.remove(userid)
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_baseline_favorite_border_24
                                ))

                        }else{
                            post.likeList.add(userid!!)
                            holder.likeIcon.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,R.drawable.liked))
                        }
                        postDocument.set(post)
                    }
                }
            }else{
                Toast.makeText(context,"Something went wrong. Please Try Again",Toast.LENGTH_LONG).show()
            }
        }
        holder.commentIcon.setOnClickListener {
            val intent = Intent(context,CommentsActivity::class.java)
            intent.putExtra("postId",snapshots.getSnapshot(holder.adapterPosition).id)
            context.startActivity(intent)
        }
    }
}