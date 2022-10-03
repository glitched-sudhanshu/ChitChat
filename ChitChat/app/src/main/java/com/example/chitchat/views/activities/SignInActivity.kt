package com.example.chitchat.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.chitchat.R
import com.example.chitchat.databinding.ActivitySignInBinding
import com.example.chitchat.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding : ActivitySignInBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        mBinding.btnSign.setOnClickListener(this)
        mBinding.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.btnSign -> {
                    val email = mBinding.txtEmail.text.toString().trim()
                    val password = mBinding.txtPassWord.text.toString().trim()
                    val cnfPassword = mBinding.txtConfirmPassWord.text.toString().trim()
                    val userName = mBinding.txtUserName.text.toString().trim()

                    signIn(userName, email, password, cnfPassword)
                }
                
                R.id.btnLogin -> {
                    val intent = Intent(this, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }
        }
    }

    private fun signIn(userName : String, email: String, password: String, cnfPassword: String) {
        if(password != cnfPassword){
            Toast.makeText(this@SignInActivity, "Confirm password does not match!", Toast.LENGTH_SHORT).show()
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        addUserToDatabase(userName, email, mAuth.currentUser?.uid!!)
                        // Sign in success, update UI with the signed-in user's information
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        finish()
                        startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@SignInActivity,
                            task.exception.toString(),
                            Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }

    private fun addUserToDatabase(userName: String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(userName, email, uid))
    }
}