package com.example.mythrowtrash.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mythrowtrash.R
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportFragmentManager.beginTransaction().apply{
            replace(
                container.id,
                EditMainFragment.getInstance(intent.getIntExtra(EditMainFragment.ID,0))
            )
            commit()
        }
    }
}
