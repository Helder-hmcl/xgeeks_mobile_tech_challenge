package com.example.myapplication.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.squareup.picasso.Picasso

class PhotoDetailActivity : AppCompatActivity() {
    private lateinit var img : ImageView
    private lateinit var text : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        img = findViewById(R.id.imageView)
        text = findViewById(R.id.ph_det_textview)

        val imgstr=intent.getStringExtra("PhotoURL")
        val title=intent.getStringExtra("Title")

        val actionBar = supportActionBar
        actionBar!!.title = title
        actionBar.setDisplayHomeAsUpEnabled(true)

        Picasso.get().load(imgstr).into(img);
        //img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        text.text = title
    }
}