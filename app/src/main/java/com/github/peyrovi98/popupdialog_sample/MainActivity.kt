package com.github.peyrovi98.popupdialog_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.peyrovi98.popupdialog.PopUpDialog
import com.github.peyrovi98.popupdialog_sample.databinding.ActivityMainBinding
import com.github.peyrovi98.popupdialog_sample.databinding.DialogExitBinding
import com.github.peyrovi98.popupdialog_sample.databinding.DialogPopUpBinding

class MainActivity : AppCompatActivity(), MessageAdapter.EventListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        MessageAdapter(applicationContext, this).let { messageAdapter ->
            binding.recyclerView.adapter = messageAdapter
            binding.imageViewSend.setOnClickListener {
                if (binding.inputText.text!!.isNotBlank()) {
                    messageAdapter.addMessage(binding.inputText.text!!.toString())
                    binding.inputText.setText("")
                }
            }
        }
        binding.imageViewClose.setOnClickListener { view->
            DialogExitBinding.inflate(layoutInflater).let {
                PopUpDialog(view, it.root).apply {
                    it.buttonCancel.setOnClickListener { dismiss() }
                    it.buttonExit.setOnClickListener {
                        dismiss()
                        finish()
                    }
                }.show(supportFragmentManager, "")
            }

        }
    }

    override fun onItemClickListener(view: View, message: Message) {
        DialogPopUpBinding.inflate(layoutInflater).let { popup->
            popup.recyclerView.layoutManager = LinearLayoutManager(this)
            val list = ArrayList<DialogListItem>()
            list.add(DialogListItem(0, android.R.drawable.ic_menu_search, androidx.appcompat.R.string.search_menu_title))
            list.add(DialogListItem(0, android.R.drawable.ic_delete, androidx.appcompat.R.string.abc_menu_delete_shortcut_label))
            PopUpDialog(
                view,
                popup.root,
                startY = binding.recyclerView.y.toInt(),
                endY = binding.recyclerView.height + binding.recyclerView.y.toInt(),
                isHighlighted = true
            ).apply {
                popup.recyclerView.adapter = PopupDialogListRecyclerViewAdapter(this, list, object :DialogListRecyclerViewAdapterEventListener{
                    override fun onDialogListItemClickListener(id: Int) {
                        Toast.makeText(this@MainActivity, "$id", Toast.LENGTH_SHORT).show()
                    }
                })
            }.show(supportFragmentManager, "")
        }

    }
}