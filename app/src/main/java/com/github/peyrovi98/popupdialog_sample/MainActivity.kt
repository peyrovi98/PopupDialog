package com.github.peyrovi98.popupdialog_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.peyrovi98.popupdialog_sample.databinding.ActivityMainBinding

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
        binding.imageViewClose.setOnClickListener {
            finish()
        }
    }

    override fun onItemClickListener(view: View, message: Message) {
    }
}