package com.example.socialmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.socialmedia.Utils.UserUtils
import com.example.socialmedia.auth.AuthenticationActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            startActivity(Intent(this, AuthenticationActivity::class.java))
        }
        UserUtils.getCurrentUser()
        setFragment(FeedFragment())
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemReselectedListener{
            when(it.itemId){
                R.id.feed_item->{
                      setFragment(FeedFragment())
                }
                R.id.search_item->{
                      setFragment(SearchFragment())
                }
                R.id.chat_room_item->{
                      setFragment(ChatRoomFragment())
                }
                R.id.profile_item->{
                      setFragment(ProfileFragment())
                }
            }
        }
    }
    private  fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }

}