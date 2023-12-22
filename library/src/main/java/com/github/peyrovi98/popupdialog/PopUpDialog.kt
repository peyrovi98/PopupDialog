package com.github.peyrovi98.popupdialog

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.github.peyrovi98.popupdialog.databinding.ComGithubPeyrovi98PopupdialogDialogPopUpBinding


class PopUpDialog(
    private val selectedView: View,
    private val popupView: View,
    private var startX: Int = 0,
    private var endX: Int = 0,
    private var startY: Int = 0,
    private var endY: Int = 0,
    private val isHighlighted: Boolean = false,
) : DialogFragment(), Runnable {
    private var bitmap: Bitmap? = null
    private val dialogHandler = Handler(Looper.myLooper()!!)
    private var binding: ComGithubPeyrovi98PopupdialogDialogPopUpBinding? = null
    private var screenX = 0f
    private var screenY = 0f
    private val selectedViewPosition = IntArray(2)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            ComGithubPeyrovi98PopupdialogDialogPopUpBinding.inflate(inflater, container, false)
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
            getBitmapFromViewUsingCanvas(selectedView).let {
                imageViewCloneSelected.apply {
                    val params = layoutParams
                    params.height = it.height
                    params.width = it.width
                    layoutParams = params
                    x = selectedViewPosition[0].toFloat() + startX
                    y = selectedViewPosition[1].toFloat()
                    bitmap = it
                    isVisible = isHighlighted

                }.setImageBitmap(it)
            }

            root.setOnClickListener {
                this@PopUpDialog.dismiss()
            }
        }
    }

//    private fun getParentsSumY(y:Float, v:View) :Float{
//        var mY = 0f
//        v.parent?.let {
//            m += getParentsSumY()
//        }
//    }

    private fun statusBarHeight(): Int {
        val rectangle = Rect()
        val window: Window = requireActivity().window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top
//        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        return statusBarHeight
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
            viewPopup.apply {
                addView(popupView)
                if (visibility != View.INVISIBLE)
                    return
                x = findPopupX()
                y = findPopupY()
                scaleX = .9f
                scaleY = .9f
                alpha = 0f
                visibility = View.VISIBLE
                animate().scaleX(1f).scaleY(1f).alpha(1f).start()
            }
            imageViewCloneSelected.isVisible = isHighlighted
        }
    }

    private fun findPopupX(): Float {
        var targetX: Float
        targetX = if (selectedViewPosition[0] < screenX / 2) {
            (selectedViewPosition[0].toFloat() + selectedView.width / 2)
        } else {
            (selectedViewPosition[0].toFloat() + selectedView.width / 2) - binding!!.imageViewCloneSelected.width
        }
        if (targetX < 0)
            targetX = 0f
        else if (screenX < targetX + binding!!.imageViewCloneSelected.width + 10)
            targetX = screenX - (binding!!.imageViewCloneSelected.width + 10f)
        return targetX
    }

    private fun findPopupY(): Float {
        var targetY: Float
        targetY = if (selectedViewPosition[1] < (screenY / 2) - selectedView.height) {
            selectedViewPosition[1].toFloat() + (selectedView.height + 10)
        } else {
            selectedViewPosition[1].toFloat() - (binding!!.imageViewCloneSelected.height + 10)
        }
        if (targetY < 0)
            targetY = 0f
        else if (screenY < targetY + binding!!.imageViewCloneSelected.height + 10)
            targetY = screenY - (binding!!.imageViewCloneSelected.measuredHeight + 10f)
        return targetY
    }

    private fun getBitmapFromViewUsingCanvas(v: View): Bitmap {
        val result: Bitmap
        selectedView.getLocationOnScreen(selectedViewPosition)
        selectedViewPosition[1] -= statusBarHeight()
        var bitmap =
            Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        bitmap = bitmapFixer(bitmap)

        return bitmap

    }

    private fun bitmapFixer(bitmap: Bitmap): Bitmap {
        var offsetX = 0
        var offsetY = 0
        var width = bitmap.width
        var height = bitmap.height
        if (endX != 0) {
            if (selectedViewPosition[0] < startX) {
                offsetX = startX - selectedViewPosition[0]
                width -= offsetX
                selectedViewPosition[0] = startX
            }
            if (selectedViewPosition[0] + width > endX)
                width = endX - selectedViewPosition[0]
        }
        if (endY != 0) {
            if (selectedViewPosition[1] < startY) {
                offsetY = startY - selectedViewPosition[1]
                height -= offsetY
                selectedViewPosition[1] = startY
            }
            if (selectedViewPosition[1] + height > endY)
                height = endY - selectedViewPosition[1]

        }
        val result = Bitmap.createBitmap(
            bitmap,
            offsetX,
            offsetY,
            width,
            height
        )
        bitmap.recycle()
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