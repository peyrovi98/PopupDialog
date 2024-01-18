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
import com.github.peyrovi98.GuideFrame
import com.github.peyrovi98.popupdialog.databinding.ComGithubPeyrovi98PopupdialogDialogPopUpBinding


class PopUpDialog(
    private val selectedView: View,
    private val popupView: View,
    private val guideFrame: GuideFrame = GuideFrame(),
    private val isHighlighted: Boolean = false,
    private val timeDelay: Long = 500
) : DialogFragment(), PopUpDialogIFace {
    private var binding: ComGithubPeyrovi98PopupdialogDialogPopUpBinding? = null
    private var presenter: PopUpPresenter? = null

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
        binding?.let {
            presenter = PopUpPresenter(PopUpModel(), this, requireActivity(), guideFrame)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializer()
    }

    private fun initializer() {
        binding?.apply {
            viewPopup.addView(popupView)
            presenter?.prepare(selectedView, popupView, timeDelay)
            root.setOnClickListener {
                this@PopUpDialog.dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        presenter?.release()
    }

    override fun prepareBackground(bitmap: Bitmap?) {
        if (isHighlighted)
            binding?.imageViewCloneBackground?.apply {
                setImageBitmap(bitmap)
                setBackgroundColor(requireContext().getColor(android.R.color.white))
            }
    }

    override fun prepareItemSelected(bitmap: Bitmap, x: Float, y: Float) {
        binding?.imageViewCloneSelected?.apply {
            val params = layoutParams
            params.height = bitmap.height
            params.width = bitmap.width
            layoutParams = params
            this.x = x
            this.y = y
            visibility = if (isHighlighted) View.VISIBLE else View.INVISIBLE
            setImageBitmap(bitmap)
        }
    }

    override fun preparePopupDialog(x: Float, y: Float) {
        binding?.viewPopup?.apply {
            if (visibility != View.INVISIBLE)
                return
            this.x = x
            this.y = y
            scaleX = .9f
            scaleY = .9f
            alpha = 0f
            visibility = View.VISIBLE
            animate().scaleX(1f).scaleY(1f).alpha(1f).start()
        }
        binding?.imageViewCloneSelected?.isVisible = isHighlighted
    }
}