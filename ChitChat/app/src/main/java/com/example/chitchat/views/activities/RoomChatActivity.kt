package com.example.chitchat.views.activities

import android.content.ContentValues.TAG
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.example.chitchat.R
import com.example.chitchat.models.Message
import com.example.chitchat.models.SocketHandler
import com.example.chitchat.models.SocketHandler.mSocket
import com.example.chitchat.utils.Constants
import com.example.chitchat.views.adapter.RoomChatAdapter
import com.example.chitchat.views.epoxy.ChatRoomEpoxyController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.combine

class RoomChatActivity : AppCompatActivity() {

    //Emit: send info from one endpoint to another endpoint
    //On: listener for an event

    private lateinit var sendMsgBtn: ImageView
    private lateinit var txtMessageField: EditText
    private var allMessages = ArrayList<Message>()
    private lateinit var roomChatRv : EpoxyRecyclerView
    private lateinit var epoxyController: ChatRoomEpoxyController
    private var roomName : String? = ""
    private var userName : String? = ""
    var senderRoom: String? = ""
    var receiverRoom: String? = ""
    private lateinit var mDbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_chat)
        val extras = intent.extras

        roomName = extras?.getString(Constants.ROOM_NAME)
        val receiverUid = extras?.getString(Constants.USER_ID)
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        userName = extras?.getString(Constants.USER_NAME)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = userName

//        SocketHandler.setSocket()
//        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()

//        mSocket.connect()
        sendMsgBtn = findViewById(R.id.btnSendMsg)
        txtMessageField = findViewById(R.id.txtMessageField)
        roomChatRv = findViewById(R.id.roomChatRv)

        epoxyController = ChatRoomEpoxyController(allMessages)
        roomChatRv.setControllerAndBuildModels(epoxyController)

        txtMessageField.addTextChangedListener { object : TextWatcher {
                                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                        TODO("Not yet implemented")
                                    }

                                    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                                        mSocket.emit("typing", roomName)
                                    }

                                    override fun afterTextChanged(p0: Editable?) {
                                        TODO("Not yet implemented")
                                    }
                                }
                                }




        mSocket.on("typing-stat"){args->
            if(args[0] != null){

                if(args[0] as Boolean){

                    runOnUiThread{
                        Log.i(TAG, "typing.....")
                        Toast.makeText(this, "typing...", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }

        sendMsgBtn.setOnClickListener {
            val messageContent = txtMessageField.text.toString()
            txtMessageField.setText("")
            val message = Message(userName, messageContent, roomName!!, Constants.MINE_CHAT)
            allMessages.add(message)
//            mDbRef.child("chats").child(roomName!!).child(senderRoom!!).child("messages").push()
//                .setValue(message)
//                .addOnSuccessListener {
//                    mDbRef.child("chats").child(roomName!!).child(receiverRoom!!).child("messages").push()
//                        .setValue(message)
//                }

//            mDbRef.child("chats").child(roomName!!).child(senderRoom!!).child("messages")
//                .addValueEventListener(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        allMessages.clear()
//                         for(postSnapshot in snapshot.children){
//                             val msg = postSnapshot.getValue(Message::class.java)
//                             allMessages.add(msg!!)
//                         }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//                })
            epoxyController.requestModelBuild()

            roomChatRv.visibility = View.VISIBLE

            roomChatRv.scrollToPosition(allMessages.size - 1)
            Log.d(TAG,
                "send-message-to-server:   ${message.messageContent} :: ${message.senderType}")

            mSocket.emit("send-message-to-server", messageContent, 1, roomName)

        }

        val displaySocketId = Emitter.Listener {
            Log.d(TAG, "You connected with id: ${mSocket.id()}")
            //TODO solve this error
//            Toast.makeText(this, "You connected with id: ${mSocket.id()}", Toast.LENGTH_SHORT).show()
        }



        mSocket.on(Socket.EVENT_CONNECT, displaySocketId)

        mSocket.on("send-message-to-receiver") { args ->
            val messageFromServer = Message("x", "", roomName, -1)
            if (args[0] != null && args[1]!=null) {
                val messageContent = args[0] as String

                messageFromServer.messageContent = messageContent
                messageFromServer.senderType = Constants.PARTNER_CHAR
            }

            runOnUiThread {

                Log.d(TAG,
                    "send-message-to-receiver:   ${messageFromServer.messageContent} :: ${messageFromServer.senderType}")

                allMessages.add(messageFromServer)
                roomChatRv.visibility = View.VISIBLE

                epoxyController.requestModelBuild()
                roomChatRv.scrollToPosition(allMessages.size - 1)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }

}