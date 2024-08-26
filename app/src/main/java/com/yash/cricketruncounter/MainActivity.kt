package com.yash.cricketruncounter

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yash.cricketruncounter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private var totalOver: Float = 0.0F
    private var totalPlayer : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        if(isDarkModeOn(this)){ // Dark Mode ON
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){ // Material UI
                setTheme(R.style.AppTheme)
            } else { // Custom UI
                setTheme(R.style.AppTheme)
            }
        } else { // Dark Mode OFF
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){ // Material UI
                setTheme(R.style.AppTheme)
            } else { // Custom UI
                setTheme(R.style.AppTheme)
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val begin = findViewById<Button>(R.id.Start)
        val over = findViewById<EditText>(R.id.totalOvers)
        val player = findViewById<EditText>(R.id.totalPlayers)
        begin.setOnClickListener {
            if (over.text.isNotEmpty()) {
                totalOver = over.text.toString().toFloat()
            }
            if (player.text.isNotEmpty()) {
                totalPlayer = player.text.toString().toInt()
            }
            val intent = Intent(this,Match::class.java)
            intent.putExtra("totalOvers",totalOver)
            intent.putExtra("totalPlayer",totalPlayer)
            startActivity(intent)
        }
    }

    private fun isDarkModeOn(context : Context) : Boolean{
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}