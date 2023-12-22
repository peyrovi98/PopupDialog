package com.github.peyrovi98.popupdialog.sample

import android.content.Context
import android.content.res.ColorStateList
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.peyrovi98.popupdialog.databinding.ItemMessageBinding
import java.util.Date

class MessageAdapter(
    val context: Context,
    val listener: EventListener,
    val list: ArrayList<Message> = ArrayList()
) :
    RecyclerView.Adapter<MessageAdapter.Holder>() {
    class Holder(val viewBinding: ItemMessageBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface EventListener {
        fun onItemClickListener(view: View, message: Message)
    }

    init {
        addMessage("Hello", false)
        addMessage("How are you?", false)
        addMessage("Hello", true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemMessageBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        list[position].let { message ->
            holder.viewBinding.apply {
                textBox.text = message.textMessage
                spacer.isVisible = message.isMine
                layoutBox.backgroundTintList = ColorStateList.valueOf(
                    root.context.getColor(
                        if (message.isMine) android.R.color.holo_green_light
                        else android.R.color.holo_orange_light
                    )
                )
                DateFormat.format("hh:mm", Date(message.timeStamp)).toString().let {
                    dateBox.text = it
                }
                layoutBox.setOnClickListener {
                    listener.onItemClickListener(it, message)
                }
            }
        }
    }

    fun addMessage(textMessage: String, isMine: Boolean = true) {
        list.add(Message(textMessage, System.currentTimeMillis(), isMine))
        notifyItemInserted(list.size - 1)
    }
}