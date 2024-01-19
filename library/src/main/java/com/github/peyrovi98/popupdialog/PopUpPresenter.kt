package com.github.peyrovi98.popupdialog

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.Window
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import kotlin.math.abs

class PopUpPresenter(
    private val model: PopUpModel,
    private val view: PopUpDialogIFace,
    private val activity: FragmentActivity,
    private val guideFrameView: View?
) {
    private val selectedViewPosition = IntArray(2)
    private var itemBitmap: Bitmap? = null
    private var backgroundBitmap: Bitmap? = null
    private var screenX = 0f
    private var screenY = 0f
    private var startX = 0
    private var endX = 0
    private var startY = 0
    private var endY = 0

    init {
        activity.resources.displayMetrics.apply {
            screenX = widthPixels.toFloat()
            screenY = heightPixels.toFloat()
        }
    }

    fun prepare(itemView: View, popUpView: View, timeDelay: Long = 200) {
        model.timeDelay(timeDelay).observe(activity as LifecycleOwner) { _ ->
            guideFrameView?.let {
                startX = it.x.toInt()
                endX = it.x.toInt() + it.width
                startY = it.y.toInt()
                endY = it.y.toInt() + it.height
            }
            itemView.getLocationOnScreen(selectedViewPosition)
            selectedViewPosition[1] -= statusBarHeight()
            backgroundBitmap =
                getBitmapFromViewUsingCanvas(activity.findViewById(android.R.id.content))
            view.prepareBackground(backgroundBitmap)
            itemBitmap = getBitmapFromViewUsingCanvas(itemView)
            itemBitmap = bitmapFixer(itemBitmap!!)
            view.prepareItemSelected(
                itemBitmap,
                selectedViewPosition[0].toFloat() + startX,
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
        if (endX != 0) {
            if (selectedViewPosition[0] < startX) {
                offsetX = startX - selectedViewPosition[0]
                width -= offsetX
                selectedViewPosition[0] = startX
            }
            if (selectedViewPosition[0] + width > endX)
                (endX - selectedViewPosition[0]).let {
                    if (it > 0) {
                        width = it
                    }
                }
        }
        if (endY != 0) {
            if (selectedViewPosition[1] < startY) {
                offsetY = startY - selectedViewPosition[1]
                height -= offsetY
                selectedViewPosition[1] = startY
            }
            if (selectedViewPosition[1] + height > endY)
                (endY - selectedViewPosition[1]).let {
                    if (it > 0) {
                        height = it
                    }
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
        if (endX != 0)
            if (targetX < startX)
                targetX = startX.toFloat()
            else if (endX < targetX + popUpView.width)
                targetX = endX - popUpView.width.toFloat()
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

        if (endY != 0)
            if (targetY < startY)
                targetY = startY.toFloat()
            else if (endY < targetY + popUpView.height)
                targetY = endY - popUpView.height.toFloat()
        return targetY
    }

    fun release() {
        backgroundBitmap?.recycle()
        itemBitmap?.recycle()
    }
}
