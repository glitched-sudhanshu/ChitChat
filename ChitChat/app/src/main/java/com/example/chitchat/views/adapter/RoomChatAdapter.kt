package com.example.chitchat.views.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.databinding.MessageItemBinding
import com.example.chitchat.models.Message
import com.example.chitchat.utils.Constants

class RoomChatAdapter(private val context: Context, private val allMessages : ArrayList<Message>) : RecyclerView.Adapter<RoomChatAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : MessageItemBinding = MessageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = allMessages[position]
        holder.messageTxt.text = message.messageContent
        if (message.senderType == Constants.MINE_CHAT) {
            val parameters = holder.cvMsgItem.layoutParams as ViewGroup.MarginLayoutParams
            parameters.leftMargin = 150
            holder.chatRoomRl.gravity = Gravity.END

        } else if (message.senderType == Constants.PARTNER_CHAR) {
            val parameters = holder.cvMsgItem.layoutParams as ViewGroup.MarginLayoutParams

            parameters.rightMargin = 150

            holder.chatRoomRl.gravity = Gravity.START
        }

    }

    override fun getItemCount(): Int {
        return allMessages.size
    }

    class ViewHolder(itemView : MessageItemBinding) : RecyclerView.ViewHolder(itemView.root){
        val messageTxt = itemView.messageTxt
        val cvMsgItem = itemView.cvMsgItem
        val chatRoomRl = itemView.chatRoomRl
    }
}