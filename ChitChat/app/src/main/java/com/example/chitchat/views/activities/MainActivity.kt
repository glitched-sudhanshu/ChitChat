package com.example.chitchat.views.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chitchat.R
import com.example.chitchat.databinding.ActivityMainBinding
import com.example.chitchat.models.SocketHandler
import com.example.chitchat.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAuth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        val btnLogin = mBinding.btnLogin
        val btnSignIn = mBinding.btnSignIn

        btnSignIn.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }
    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnLogin -> {

                    val email = mBinding.txtEmail.text.toString().trim()
                    val password = mBinding.txtPassWord.text.toString().trim()

                    login(email, password)



                }

                R.id.btnSignIn -> {
                    val intent = Intent(this, SignInActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    private fun login(email : String, password : String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val roomName = mBinding.txtRoomName.text.toString().trim()
                    val userName = mBinding.txtUserName.text.toString().trim()
                    SocketHandler.setSocket()
                    SocketHandler.establishConnection()
                    val mSocket = SocketHandler.getSocket()
                    mSocket.connect()
                    Log.i(TAG, "onCreate: $roomName")
                    mSocket.emit("join-room", roomName)
                    val intent = Intent(this, RoomChatActivity::class.java)
                        .putExtra(Constants.ROOM_NAME, roomName)
                        .putExtra(Constants.USER_NAME, userName)
                        .putExtra(Constants.USER_ID, mAuth.currentUser?.uid)
                    startActivity(intent)


                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@MainActivity,
                        "User does not exist",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}