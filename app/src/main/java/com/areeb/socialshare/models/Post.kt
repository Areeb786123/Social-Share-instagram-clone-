package com.areeb.socialshare.models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description:String="",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    var creation_time_ms:Long = 0,
    var user : User? =null

)