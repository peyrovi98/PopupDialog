package com.github.peyrovi98.popupdialog

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.github.peyrovi98.popupdialog.databinding.DialogPopUpBinding


class PopUpDialog(
    private val selectedView: View,
    private val popupView: View,
    private val bitmapOffsetY: Int = 0,
    private var offsetY: Int = 0,
    private var offsetX: Int = 0,
    private val isHighlighted: Boolean = false,
) : DialogFragment(), Runnable {
    private var bitmap: Bitmap?=null
    private val dialogHandler = Handler(Looper.myLooper()!!)
    private var binding: DialogPopUpBinding? = null
    private var screenX = 0f
    private var screenY = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogPopUpBinding.inflate(inflater, container, false)
        requireContext().resources.displayMetrics.apply {
            screenX = widthPixels.toFloat()
            screenY = heightPixels.toFloat()
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializer()
    }

    private fun initializer() {
        binding?.apply {
            selectedView.drawToBitmap().let {
                imageViewCloneSelected.apply {
                    val params = layoutParams
                    params.height = it.height
                    params.width = it.width
                    layoutParams = params
                    y = selectedView.y + bitmapOffsetY + offsetY
                    bitmap = it
                }.setImageBitmap(it)
            }

            root.setOnClickListener {
                this@PopUpDialog.dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dialogHandler.postDelayed(this@PopUpDialog, 100)
    }

    override fun getTheme(): Int {
        return R.style.com_github_peyrovi98_popupdialog_DialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogHandler.removeCallbacks(this)
        bitmap?.recycle()
    }

    override fun run() {
        binding?.apply {
//            containerRecyclerView.apply {
//                if (visibility != View.INVISIBLE)
//                    return
////                val alignMessageBox =
////                    selectedView.findViewById<ConstraintLayout>(R.id.messageLayout)
////                alignMessageBox?.let { x = findX(it, this) } ?: kotlin.run {
//                    x = findX(imageViewClone, this)
////                }
//                y = findY(imageViewClone, this)
//                scaleX = .9f
//                scaleY = .9f
//                alpha = 0f
//                visibility = View.VISIBLE
//                animate().scaleX(1f).scaleY(1f).alpha(1f).start()
//            }
//            imageViewClone.isVisible = isHighlighted
        }
    }

    private fun findX(alignView: View, attachmentView: View): Float {
        var targetX: Float
        targetX = if (alignView.x < screenX / 2) {
            (alignView.x + alignView.width / 2)
        } else {
            (alignView.x + alignView.width / 2) - attachmentView.width
        }
        if (targetX < 0)
            targetX = 0f
        else if (screenX < targetX + attachmentView.width + 10)
            targetX = screenX - (attachmentView.width + 10f)
        return targetX
    }

    private fun findY(alignView: View, attachmentView: View): Float {
        var targetY: Float
        targetY = if (alignView.y < (screenY / 2) - alignView.measuredHeight) {
            alignView.y + (alignView.measuredHeight + 10)
        } else {
            alignView.y - (attachmentView.height + 10)
        }
        if (targetY < 0)
            targetY = 0f
        else if (screenY < targetY + attachmentView.height + 10)
            targetY = screenY - (attachmentView.measuredHeight + 10f)
        return targetY
    }

    private fun getBitmapFromViewUsingCanvas(v: View): ArrayList<Any> {
        val result = ArrayList<Any>()
//        val bitmap =
//            Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(bitmap)
//        v.draw(canvas)
//        var offsetY = v.y.toInt()
//        if (offsetY < 0) {
//            offsetY *= -1
//        } else {
//            offsetY = 0
//        }
//        val height: Int = if (v.height - offsetY > views.recyclerView.height && offsetY > 0)
//            views.recyclerView.height
//        else if (v.height - offsetY > views.recyclerView.height && offsetY == 0)
//            views.recyclerView.height - v.y.toInt()
//        else if (v.y + v.height > views.recyclerView.height)
//            v.height - (v.y.toInt() + v.height - views.recyclerView.height)
//        else
//            v.height - offsetY
//
//        result.add(
//            Bitmap.createBitmap(
//                bitmap,
//                0,
//                offsetY,
//                v.width,
//                height
//            )
//        )
//        bitmap.recycle()
//        result.add(offsetY)
        return result
    }

//    private fun messageOptionPopup(v: View, event: DecryptedEventEntity) {
//        val result = getBitmapFromViewUsingCanvas(v)
//        PopUpDialog(
//            selectedView = v,
//            bitmap = result[0] as Bitmap,
//            bitmapOffsetY = result[1] as Int,
//            popupList = ArrayList<DialogListItem>().apply {
//                add(DialogListItem(R.drawable.ic_reply_square_2, R.string.reply))
//                if (event.canEdit()) DialogListItem(R.drawable.ic_edit_3, R.string.edit)
//                add(DialogListItem(R.drawable.ic_copy_1, R.string.copy))
//                if (event.canForward()) add(
//                    DialogListItem(
//                        R.drawable.forward_square_1,
//                        R.string.forward
//                    )
//                )
//                if (event.isMine) add(DialogListItem(R.drawable.ic_trash_1, R.string.delete, true))
//            },
//            onItemSelectListener = object : DialogListRecyclerViewAdapterEventListener {
//                override fun onDialogListItemClickListener(title: Int) {
//                    when (title) {
//                        R.string.reply -> {
//                            hideLongEventIcons()
//                            roomViewModel.handle(RoomViewActions.ReplyModeEnabled)
//                            renderReplyEditMessage(event, false)
//                        }
//
//                        R.string.edit -> {
//                            hideLongEventIcons()
//                            roomViewModel.handle(RoomViewActions.EditModeEnabled)
//                            renderReplyEditMessage(event, true)
//                        }
//
//                        R.string.forward -> {
//                            cancelEventLongClickToolbar()
//                            ForwardMessageDialog(
//                                requireContext(),
//                                session.getRoomService().getRoomRepository(),
//                                glide
//                            ) { roomEntity, alertDialog ->
//                                Timber.d("kael on room clicked $roomEntity")
//                                alertDialog.dismiss()
//                                findNavController().navigate(R.id.action_roomFragment_self,
//                                    Bundle().apply {
//                                        putString("roomId", roomEntity.roomId)
//                                        putBoolean("isFromUserList", false)
//                                        putBoolean("isDirect", roomEntity.isDirect ?: false)
//                                        putString(
//                                            "forwardedEvent", gson.toJson(
//                                                event.copy(
//                                                    isForwarded = true,
//                                                    roomId = roomEntity.roomId!!,
//                                                    serverId = null,
//                                                    id = null,
//                                                    txnId = UUID.randomUUID().toString()
//                                                )
//                                            )
//                                        )
//                                    })
//                            }.show()
//                        }
//
//                        R.string.delete -> {
//                            AlertDialog.Builder(requireContext())
//                                .setTitle(getString(R.string.delete_message))
//                                .setMessage(getString(R.string.do_you_want_to_delete_this_message))
//                                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
//                                    roomViewModel.handle(RoomViewActions.RedactSelectedEvent)
//                                    dialog.dismiss()
//                                }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
//                                    dialog.dismiss()
//                                }.create().show()
//                        }
//                    }
//                }
//            },
//            offsetY = views.recyclerView.y.toInt(),
//            isHighlighted = true
//        ).show(parentFragmentManager, "")
//    }
}