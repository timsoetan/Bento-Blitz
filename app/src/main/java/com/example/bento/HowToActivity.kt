package com.example.bento

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.core.content.res.ResourcesCompat
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_how_to.*
class HowToActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_how_to)

        StatusBarUtil.setTransparent(this)

        val backgroundAnimation = background.background as AnimationDrawable
        backgroundAnimation.start()

        val imageAnimation = AnimationUtils.loadAnimation(this, R.anim.image_animation)
        ivSushiOne.startAnimation(imageAnimation)
        ivSushiTwo.startAnimation(imageAnimation)
        ivSushiThree.startAnimation(imageAnimation)
        ivSashimi.startAnimation(imageAnimation)
        ivWasabi.startAnimation(imageAnimation)

        val btnFont = ResourcesCompat.getFont(this, R.font.bento_blitz_font)
        btnBack.typeface = btnFont

        btnBack.setOnClickListener {
            onBackPressed()
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
