package com.example.bento

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_leader_board.*
import kotlin.collections.ArrayList

class LeaderBoardActivity : AppCompatActivity() {

    val users: ArrayList<User> = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        StatusBarUtil.setTransparent(this)

        loadFromSharedPreferences()

        rvUsers.layoutManager = LinearLayoutManager(this)

        rvUsers.adapter = RecyclerAdapter(this, users)

        val backgroundAnimation = background.background as AnimationDrawable
        backgroundAnimation.start()

        val trophyAnimationOne = ivTrophyOne.background as AnimationDrawable
        val trophyAnimationTwo = ivTrophyTwo.background as AnimationDrawable
        trophyAnimationOne.start()
        trophyAnimationTwo.start()

        val imageAnimation = AnimationUtils.loadAnimation(this, R.anim.image_animation)
        ivTrophyOne.startAnimation(imageAnimation)
        ivTrophyTwo.startAnimation(imageAnimation)

        val btnFont = ResourcesCompat.getFont(this, R.font.bento_blitz_font)
        btnBackTwo.typeface = btnFont
        btnClear.typeface = btnFont

        btnBackTwo.setOnClickListener {
            onBackPressed()
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        btnClear.setOnClickListener {
            showDialog()
        }
    }

    fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.clear_leaderboard))
        builder.setMessage(R.string.warning)
        builder.setIcon(R.drawable.ic_bento_box)

        builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            val sharedPrefGameData = this?.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
            val editorGame = sharedPrefGameData.edit()
            editorGame.clear().commit()

            val sharedPrefUserScores =
                this?.getSharedPreferences("USER_SCORES", Context.MODE_PRIVATE)
            val editorUserScores = sharedPrefUserScores.edit()
            editorUserScores.clear().commit()

            users.clear()
            rvUsers.adapter?.notifyDataSetChanged()

            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }

        val alert = builder.create()

        alert.setCanceledOnTouchOutside(false)

        alert.show()

        val view: Window? = alert.window
        val title: TextView = view!!.findViewById(R.id.alertTitle)
        val message: TextView = view!!.findViewById(android.R.id.message)
        val btnPos = alert.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNeg = alert.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPos.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        btnPos.layoutParams = layoutParams
        btnNeg.layoutParams = layoutParams

        correctFont(title)
        correctFont(message)
        correctFont(listOf(btnPos, btnNeg))
        message.textSize = 18F
        btnPos.textSize = 18F
        btnPos.background = null
        btnNeg.textSize = 18F
        btnNeg.background = null

        message.gravity = Gravity.CENTER
    }

    fun loadFromSharedPreferences() {
        users.clear()
        val sharedPref = this?.getSharedPreferences("USER_SCORES", Context.MODE_PRIVATE)
        val names = sharedPref.getString("names", "").split("|")
        if (!sharedPref.getString("names", "").isEmpty()) {
            for (name in names) {
                val score = sharedPref.getString(name + "_number", "")
                val user = User(name, score.toInt())
                var duplicate = false

                for (user in users) {
                    if (user.name == name) {
                        duplicate = true
                        break
                    }
                }

                if (!duplicate) {
                    users.add(user)
                }
            }
        }
    }

    fun correctFont(btnList: List<Button>) {
        val btnFont = ResourcesCompat.getFont(this, R.font.bento_blitz_font)

        for (btn in btnList) {
            btn.typeface = btnFont
        }
    }

    fun correctFont(txtView: TextView) {
        val btnFont = ResourcesCompat.getFont(this, R.font.bento_blitz_font)

        txtView.typeface = btnFont
    }
}
