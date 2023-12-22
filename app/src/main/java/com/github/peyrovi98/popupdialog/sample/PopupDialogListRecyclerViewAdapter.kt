package com.github.peyrovi98.popupdialog.sample

import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.github.peyrovi98.popupdialog.databinding.LiPopupDialogListBinding

class PopupDialogListRecyclerViewAdapter(
    private val dialog: DialogFragment,
    private val listItem: List<DialogListItem>?,
    private val eventListener: DialogListRecyclerViewAdapterEventListener?
) : RecyclerView.Adapter<PopupDialogListRecyclerViewAdapter.Holder>() {
    private val eventHandler = Handler(Looper.myLooper()!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val views = LiPopupDialogListBinding.inflate(
            LayoutInflater.from(dialog.requireActivity()),
            parent,
            false
        )
        return Holder(views)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = listItem!![position]
        holder.views.imageViewIcon.let {
            it.setImageResource(item.imageDrawable)
            if (item.isNegativeItem)
                it.imageTintList = ColorStateList.valueOf(it.context.getColor(android.R.color.holo_red_light))
            else
                it.imageTintList =
                    ColorStateList.valueOf(it.context.getColor(android.R.color.black))

        }
        holder.views.textViewTitle.let {
            it.text = it.context.getString(item.titleResource)
            if (item.isNegativeItem)
                it.setTextColor(it.context.getColor(android.R.color.holo_red_light))
            else
                it.setTextColor(it.context.getColor(android.R.color.black))
        }
        holder.views.layout.setOnClickListener {
            eventHandler.postDelayed({ dialog.dismiss() }, 200)
            eventListener?.onDialogListItemClickListener(item.id)
        }

        val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
        if (position == listItem.lastIndex) {
            params.bottomMargin = 24
            params.topMargin = 8
        } else if (position == 0) {
            params.bottomMargin = 8
            params.topMargin = 24
        } else {
            params.bottomMargin = 8
            params.topMargin = 8
        }
        holder.itemView.layoutParams = params
    }

    override fun getItemCount(): Int {
        return listItem!!.size
    }

    class Holder(var views: LiPopupDialogListBinding) : RecyclerView.ViewHolder(
        views.root
    )
}