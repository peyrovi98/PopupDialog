package com.github.peyrovi98.popupdialog

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.github.peyrovi98.GuideFrame

class PopUpPresenter(
    private val model: PopUpModel,
    private val view: PopUpDialogIFace,
    private val activity: FragmentActivity,
    private val guideFrame: GuideFrame = GuideFrame()
) {
    private var isClosingKeyboard = false
    private val selectedViewPosition = IntArray(2)
    private var itemBitmap: Bitmap? = null
    private var backgroundBitmap: Bitmap? = null
    private var screenX = 0f
    private var screenY = 0f

    init {
        activity.resources.displayMetrics.apply {
            screenX = widthPixels.toFloat()
            screenY = heightPixels.toFloat()
        }
    }

    fun prepare(itemView: View, popUpView: View, timeDelay: Long = 200) {
        model.timeDelay(timeDelay).observe(activity as LifecycleOwner) { _ ->
            itemView.getLocationOnScreen(selectedViewPosition)
            selectedViewPosition[1] -= statusBarHeight()
            backgroundBitmap = getBitmapFromViewUsingCanvas(activity.findViewById(android.R.id.content))
            view.prepareBackground(backgroundBitmap)
            itemBitmap = getBitmapFromViewUsingCanvas(itemView)
            itemBitmap = bitmapFixer(itemBitmap!!)
            view.prepareItemSelected(
                itemBitmap,
                selectedViewPosition[0].toFloat() + guideFrame.startX,
                selectedViewPosition[1].toFloat()
            )
            view.preparePopupDialog(
                findPopupX(itemView, popUpView),
                findPopupY(itemView, popUpView)
            )
        }
    }

    private fun statusBarHeight(): Int {
        val rectangle = Rect()
        val window: Window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }

    private fun getBitmapFromViewUsingCanvas(v: View): Bitmap {
        val bitmap =
            Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v.draw(canvas)
        return bitmap

    }

    private fun bitmapFixer(bitmap: Bitmap): Bitmap {
        var offsetX = 0
        var offsetY = 0
        var width = bitmap.width
        var height = bitmap.height
        if (guideFrame.endX != 0) {
            if (selectedViewPosition[0] < guideFrame.startX) {
                offsetX = guideFrame.startX - selectedViewPosition[0]
                width -= offsetX
                selectedViewPosition[0] = guideFrame.startX
            }
            if (selectedViewPosition[0] + width > guideFrame.endX)
                (guideFrame.endX - selectedViewPosition[0]).let {
                    if (it > 0) {
                        width = it
                    }
                }
        }
        if (guideFrame.endY != 0) {
            if (selectedViewPosition[1] < guideFrame.startY) {
                offsetY = guideFrame.startY - selectedViewPosition[1]
                height -= offsetY
                selectedViewPosition[1] = guideFrame.startY
            }
            if (selectedViewPosition[1] + height > guideFrame.endY)
                (guideFrame.endY - selectedViewPosition[1]).let {
                    if (it > 0) {
                        height = it
                    } else
                        isClosingKeyboard = true
                }

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

    private fun findPopupX(itemView: View, popUpView: View): Float {
        var targetX: Float
        targetX = if (selectedViewPosition[0] < screenX / 2) {
            (selectedViewPosition[0].toFloat() + itemView.width / 2)
        } else {
            (selectedViewPosition[0].toFloat() + itemView.width / 2) - popUpView.width
        }
        if (targetX < 0)
            targetX = 0f
        else if (screenX < targetX + itemView.width + 10)
            targetX = screenX - (itemView.width + 10f)
        if (guideFrame.endX != 0)
            if (targetX < guideFrame.startX)
                targetX = guideFrame.startX.toFloat()
            else if (guideFrame.endX < targetX + popUpView.width)
                targetX = guideFrame.endX - popUpView.width.toFloat()
        return targetX
    }

    private fun findPopupY(itemView: View, popUpView: View): Float {
        var targetY: Float
        targetY =
            if (selectedViewPosition[1] < (screenY / 2) - itemView.height) {
                selectedViewPosition[1].toFloat() + (itemView.height + 10)
            } else {
                selectedViewPosition[1].toFloat() - (popUpView.height + 10)
            }
        if (targetY < 0)
            targetY = 0f
        else if (screenY < targetY + itemView.height + 10)
            targetY = screenY - (itemView.measuredHeight + 10f)

        if (guideFrame.endY != 0)
            if (targetY < guideFrame.startY)
                targetY = guideFrame.startY.toFloat()
            else if (guideFrame.endY < targetY + popUpView.height && isClosingKeyboard)
                targetY = guideFrame.endY - popUpView.height.toFloat()
        return targetY
    }

    fun release() {
        backgroundBitmap?.recycle()
        itemBitmap?.recycle()
    }
}
