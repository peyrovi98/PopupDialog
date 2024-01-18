package com.github.peyrovi98.popupdialog_sample

import android.content.Context
import android.content.res.ColorStateList
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.peyrovi98.popupdialog_sample.databinding.ItemMessageBinding
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
        addMessage(
            "1\n" +
                    "2\n" +
                    "3\n" +
                    "4\n" +
                    "5\n" +
                    "6\n" +
                    "7\n" +
                    "8\n" +
                    "9\n" +
                    "10\n" +
                    "11\n" +
                    "12\n" +
                    "13\n" +
                    "14\n" +
                    "15\n" +
                    "16\n" +
                    "17\n" +
                    "18\n" +
                    "19\n" +
                    "20\n" +
                    "21\n" +
                    "22\n" +
                    "23\n" +
                    "24\n" +
                    "25\n" +
                    "26\n" +
                    "27\n" +
                    "28\n" +
                    "29\n" +
                    "30\n" +
                    "31\n" +
                    "32\n" +
                    "33\n" +
                    "34\n" +
                    "35\n" +
                    "36\n" +
                    "37\n" +
                    "38\n" +
                    "39\n" +
                    "40\n" +
                    "41\n" +
                    "42\n" +
                    "43\n" +
                    "44\n" +
                    "45\n" +
                    "46\n" +
                    "47\n" +
                    "48\n" +
                    "49\n" +
                    "50\n", true
        )
        addMessage("Hello", true)
        addMessage("Hello", false)
        addMessage("How are you?", true)
        addMessage("I'm fine", false)
        addMessage("How are you?", false)
        addMessage("I'm fine too", true)
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