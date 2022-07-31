package com.example.socialmedia.models

import com.example.socialmedia.models.User

data class Post(
    val text: String = "",
    val uri: String ?= null,
    val user: User = User(),
    val time:Long=0L,
    val likeList: MutableList<String> = mutableListOf()
                 )
