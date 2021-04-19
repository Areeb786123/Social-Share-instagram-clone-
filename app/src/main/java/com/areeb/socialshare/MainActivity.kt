package com.areeb.socialshare

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.areeb.socialshare.models.Post
import com.areeb.socialshare.models.PostAdapter
import com.areeb.socialshare.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG ="MainActivity"

class MainActivity : AppCompatActivity() {

    private var signedInUser: User? = null
    private lateinit var firestoreDb : FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    lateinit var adapter: PostAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setting.setOnClickListener   {
            val setting_intent = Intent(this, com.areeb.socialshare.setting::class.java)
            startActivity(setting_intent)
        }

        create_post.setOnClickListener {
            val create_intent =Intent(this,CreatePost::class.java)
            startActivity(create_intent)
        }
        chatting.setOnClickListener {
            val chatting_intent =Intent(this,Chatting::class.java)
            startActivity(chatting_intent)

        }

        //create the layout file which represents one post
        //create data source
        posts= mutableListOf()
            //create the adapter
        adapter= PostAdapter(this,posts)
        //bind the adapter and layout manager to the RV
        rvPost.adapter=adapter
        rvPost.layoutManager= LinearLayoutManager(this)




        firestoreDb = FirebaseFirestore.getInstance()
        val postReferce = firestoreDb
            .collection("posts")
            .orderBy("creation_time_ms", Query.Direction.DESCENDING)


        postReferce.addSnapshotListener{ snapshot,exception ->
            if (exception != null || snapshot == null){
                val toast = Toast.makeText(applicationContext, "Exception occur while quering post", Toast.LENGTH_SHORT)
                toast.show()
                return@addSnapshotListener
            }
            val postList=snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()

            for (post in postList){
                Log.i(TAG,"Post ${post}")
            }

        }




    }
}