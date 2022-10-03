package com.example.chitchat.views.epoxy

import android.content.res.Resources
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.marginStart
import com.airbnb.epoxy.EpoxyController
import com.example.chitchat.R
import com.example.chitchat.databinding.MessageItemBinding
import com.example.chitchat.models.Message
import com.example.chitchat.utils.Constants

class ChatRoomEpoxyController(val allMessages: ArrayList<Message>) : EpoxyController() {


    override fun buildModels() {
        //msg model

//        for(message in messages){
//            MessagesEpoxyModel(message = message).id("msg").addTo(this)
//        }

        var counter = 0;
        allMessages.forEach{
            MessagesEpoxyModel(it).id(it.userName + counter).addTo(this)
            counter++;
        }
    }

    data class MessagesEpoxyModel(
        val message: Message
    ) : ViewBindingKotlinModel<MessageItemBinding>(R.layout.message_item){
        override fun MessageItemBinding.bind() {

            messageTxt.text = message.messageContent
            if (message.senderType == Constants.MINE_CHAT) {
                val parameters = cvMsgItem.layoutParams as ViewGroup.MarginLayoutParams
                parameters.leftMargin = 150
                chatRoomRl.gravity = Gravity.END

            } else if (message.senderType == Constants.PARTNER_CHAR) {
                val parameters = cvMsgItem.layoutParams as ViewGroup.MarginLayoutParams

                parameters.rightMargin = 150
                messageTxt.setBackgroundColor(Color.WHITE)
                chatRoomRl.gravity = Gravity.START
            }


        }
    }
}