package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.myapplication.R
import com.example.myapplication.http.HttpService
import com.example.myapplication.models.PhotoModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main


class MainActivity : AppCompatActivity() {

    private lateinit var catslinearlayout : LinearLayout
    private lateinit var dogslinearlayout : LinearLayout
    private lateinit var publiclinearlayout : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        catslinearlayout = findViewById(R.id.cats_sv_linearlayout)
        dogslinearlayout = findViewById(R.id.dogs_sv_linearlayout)
        publiclinearlayout = findViewById(R.id.public_sv_linearlayout)
        val actionBar = supportActionBar
        actionBar!!.title = "XGeeks App Test"

        CoroutineScope(Dispatchers.IO).launch {
            val result = HttpService.getInstance().httpGet("?method=flickr.photos.search", "kitten")
            for(each in result)
            {
                updateMainThread(each, "Cats")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val result = HttpService.getInstance().httpGet("?method=flickr.photos.search", "dog")
            for(each in result)
            {
                updateMainThread(each, "Dogs")
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            val result = HttpService.getInstance().httpGet("?method=flickr.photos.getRecent", "")
            for(each in result)
            {
                updateMainThread(each, "Public")
            }
        }
    }


    private fun setUI(photomodel: PhotoModel, ui_to_update: String){

        //TEXT TITLE
        val photoTitle = TextView(this)
        photoTitle.text = photomodel.title
        photoTitle.isSingleLine = false
        photoTitle.ellipsize = TextUtils.TruncateAt.END
        photoTitle.setLines(1)
        photoTitle.width = 450


        //IMAGE
        val img = ImageView(
            this,
        )
        Picasso.get().load(photomodel.img_url).into(img)
        val params2: ViewGroup.LayoutParams = ViewGroup.LayoutParams(500, 400)
        img.setLayoutParams(params2)
        img.setScaleType(ImageView.ScaleType.CENTER_CROP)

        //LINEAR LAYOUT ADDS IMG AND PHOTOTITLE
        val lin = LinearLayout(this)
        lin.addView(img)
        lin.addView(photoTitle)
        lin.orientation = LinearLayout.VERTICAL

        //CARD ADDS LINEAR LAYOUT AND SET ON CLICK LISTENER TO CHANGE ACTIVITY
        val card = CardView(this)
        card.addView(lin)
        val params3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params3.setMargins(8,0,8,0)
        card.setLayoutParams(params3)

        card.setOnClickListener {
            val intent = Intent(this, PhotoDetailActivity::class.java)
            intent.putExtra("PhotoURL", photomodel.img_url)
            intent.putExtra("Title", photomodel.title)
            startActivity(intent)
        }

        //UPDATES UI
        when (ui_to_update) {
            "Cats" -> {
                catslinearlayout.addView(card)
            }
            "Dogs" -> {
                dogslinearlayout.addView(card)
            }
            "Public" -> {
                publiclinearlayout.addView(card)
            }
        }
    }

    private suspend fun updateMainThread(photomodel: PhotoModel, ui_to_update: String){
        withContext(Main){
            setUI(photomodel, ui_to_update)
        }
    }

}
