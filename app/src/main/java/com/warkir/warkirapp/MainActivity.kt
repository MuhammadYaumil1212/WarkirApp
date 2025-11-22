package com.warkir.warkirapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.warkir.warkirapp.databinding.ActivityMainBinding

//Activity Utama
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewCompat()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.main)
    }

    /*
    * Fungsi ini mengatur bagaimana tampilan kamu menyesuaikan diri agar tidak ketutupan status bar atau navigation bar.
    * Google mendorong desain Immersive UI / Edge-to-Edge:
      UI boleh sampai tepi layar untuk kesan luas dan modern, tapi tetap harus aman untuk konten.
    *
    * */
    private fun viewCompat() = ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        insets
    }
}