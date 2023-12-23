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
import androidx.core.view.drawToBitmap
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

    init {
        setStyle(STYLE_NORMAL, R.style.com_github_peyrovi98_popupdialog_DialogTheme)
    }

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
            if (isHighlighted)
                requireActivity().findViewById<View>(android.R.id.content)?.let {
                    imageViewCloneBackground.setImageBitmap(it.drawToBitmap())
                    imageViewCloneBackground.setBackgroundColor(requireContext().getColor(android.R.color.white))
                }
            viewPopup.addView(popupView)
            getBitmapFromViewUsingCanvas(selectedView).let {
                imageViewCloneSelected.apply {
                    val params = layoutParams
                    params.height = it.height
                    params.width = it.width
                    layoutParams = params
                    x = selectedViewPosition[0].toFloat() + startX
                    y = selectedViewPosition[1].toFloat()
                    bitmap = it
                    visibility = if (isHighlighted) View.VISIBLE else View.INVISIBLE

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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogHandler.removeCallbacks(this)
        bitmap?.recycle()
    }

    override fun run() {
        binding?.apply {
            viewPopup.apply {
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
            (selectedViewPosition[0].toFloat() + binding!!.imageViewCloneSelected.width / 2)
        } else {
            (selectedViewPosition[0].toFloat() + binding!!.imageViewCloneSelected.width / 2) - binding!!.viewPopup.width
        }
        if (targetX < 0)
            targetX = 0f
        else if (screenX < targetX + binding!!.imageViewCloneSelected.width + 10)
            targetX = screenX - (binding!!.imageViewCloneSelected.width + 10f)
        if (endX != 0)
            if (targetX < startX)
                targetX = startX.toFloat()
            else if (endX < targetX + binding!!.viewPopup.width)
                targetX = endX - binding!!.viewPopup.width.toFloat()
        return targetX
    }

    private fun findPopupY(): Float {
        var targetY: Float
        targetY =
            if (selectedViewPosition[1] < (screenY / 2) - binding!!.imageViewCloneSelected.height) {
                selectedViewPosition[1].toFloat() + (binding!!.imageViewCloneSelected.height + 10)
            } else {
                selectedViewPosition[1].toFloat() - (binding!!.viewPopup.height + 10)
            }
        if (targetY < 0)
            targetY = 0f
        else if (screenY < targetY + binding!!.imageViewCloneSelected.height + 10)
            targetY = screenY - (binding!!.imageViewCloneSelected.measuredHeight + 10f)

        if (endY != 0)
            if (targetY < startY)
                targetY = startY.toFloat()
            else if (endY < targetY + binding!!.viewPopup.height)
                targetY = endY - binding!!.viewPopup.height.toFloat()
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
}