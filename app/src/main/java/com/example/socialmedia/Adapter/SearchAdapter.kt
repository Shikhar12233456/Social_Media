package com.example.socialmedia.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.models.User
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter(options: FirestoreRecyclerOptions<com.example.socialmedia.models.User>, val context: Context):
        FirestoreRecyclerAdapter<com.example.socialmedia.models.User,SearchAdapter.SearchViewHolder>(options){
    class SearchViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
      val userImage: CircleImageView = itemView.findViewById(R.id.profile_Image)
      val nameText: TextView = itemView.findViewById(R.id.user_name)
      val followButton: Button = itemView.findViewById(R.id.follow_button)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user,parent,false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
        model: com.example.socialmedia.models.User
    ) {
       holder.nameText.text = model.name
        if(UserUtils.user?.following?.contains(snapshots.getSnapshot(holder.adapterPosition).id)!!){
            holder.followButton.text ="Following"
        }else{
            holder.followButton.text="Follow"
        }
        holder.followButton.setOnClickListener {
            val fireStore= FirebaseFirestore.getInstance()
            val userDocument=fireStore.collection("Users").document(UserUtils.user?.id!!)
            userDocument.get().addOnCompleteListener {
                if(it.isSuccessful){
                    val user= it.result?.toObject(User::class.java)

                    if (holder.followButton.text=="Following"){
                        user?.following?.remove(snapshots.getSnapshot(holder.adapterPosition).id)
                        user?.let { useRID->
                            userDocument.set(useRID)
                        }
                        holder.followButton.text="Follow"
                    }else{
                        user?.following?.add(snapshots.getSnapshot(holder.adapterPosition).id)
                        user?.let {useRID->
                            userDocument.set(useRID)
                        }
                        holder.followButton.text="Following"
                    }
                }else{
                    Toast.makeText(context,it.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
    }

}