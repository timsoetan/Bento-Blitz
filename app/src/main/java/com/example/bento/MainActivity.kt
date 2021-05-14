package com.example.bento

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.drawable.AnimationDrawable
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.Window
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_game.ivTitle
import androidx.core.content.res.ResourcesCompat.getFont
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.background
import android.widget.*
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        StatusBarUtil.setTransparent(this)

        val backgroundAnimation = background.background as AnimationDrawable
        backgroundAnimation.start()

        val titleAnimation = AnimationUtils.loadAnimation(this, R.anim.title_animation)
        ivTitle.startAnimation(titleAnimation)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.new_game))
        builder.setMessage(getString(R.string.request_name))
        builder.setIcon(R.drawable.ic_bento_box)

        val nameInput = EditText(this)
        val inputParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        nameInput.layoutParams = inputParams

        builder.setView(nameInput)
        builder.setPositiveButton(getString(R.string.start)) { _, _ ->

        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            nameInput.text.clear()
        }

        val alert = builder.create()

        alert.setCanceledOnTouchOutside(false)

        correctFont(listOf(btnPlay, btnLeaderboard, btnHowTo))

        val goToHowTo = Intent(this, HowToActivity::class.java)
        val goToLeaderboard = Intent(this, LeaderBoardActivity::class.java)

        btnPlay.setOnClickListener {
            alert.show()

            val view: Window? = alert.window
            val title: TextView = view!!.findViewById(R.id.alertTitle)
            val message: TextView = view!!.findViewById(android.R.id.message)
            val btnPos = alert.getButton(AlertDialog.BUTTON_POSITIVE)
            val btnNeg = alert.getButton(AlertDialog.BUTTON_NEGATIVE)

            btnPos.setOnClickListener {
                var closeDialog = false

                if (nameInput.text.isEmpty()) {
                    showErrorToast(1)
                } else if (!nameInput.text.chars().allMatch(Character::isLetter)) {
                    showErrorToast(2)
                } else {
                    closeDialog = true

                    val goToGame = Intent(this, GameActivity::class.java)
                    goToGame.putExtra(getString(R.string.name), nameInput.text.toString())
                    startActivity(goToGame)
                    this.overridePendingTransition(R.anim.slide_up, R.anim.do_nothing)
                }

                if (closeDialog) {
                    nameInput.text.clear()
                    alert.dismiss()
                }
            }

            val layoutParams = btnPos.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10f
            btnPos.layoutParams = layoutParams
            btnNeg.layoutParams = layoutParams

            correctFont(title)
            correctFont(message)
            correctFont(nameInput)
            correctFont(listOf(btnPos, btnNeg))
            message.textSize = 18F
            btnPos.textSize = 18F
            btnPos.background = null
            btnNeg.textSize = 18F
            btnNeg.background = null

            nameInput.hint = getString(R.string.prompt_type)
            nameInput.filters = (arrayOf<InputFilter>(InputFilter.AllCaps()))
            nameInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            nameInput.isCursorVisible = false
            nameInput.background = null
            nameInput.gravity = Gravity.CENTER

            message.gravity = Gravity.CENTER
        }

        btnLeaderboard.setOnClickListener {
            startActivity(goToLeaderboard)
            this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        btnHowTo.setOnClickListener {
            startActivity(goToHowTo)
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    fun showErrorToast(int: Int) {
        val context = applicationContext
        val inflater = layoutInflater

        val customToastView = inflater.inflate(R.layout.custom_toast, null)

        if (int == 1) {
            customToastView.findViewById<TextView>(R.id.message).text =
                getString(R.string.error_name_empty)
        } else {
            customToastView.findViewById<TextView>(R.id.message).text =
                getString(R.string.error_name)
        }

        val customToast = Toast(context)

        customToast.view = customToastView
        customToast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        customToast.duration = Toast.LENGTH_LONG
        customToast.show()
    }

    fun correctFont(btnList: List<Button>) {
        val btnFont = getFont(this, R.font.bento_blitz_font)

        for (btn in btnList) {
            btn.typeface = btnFont
        }
    }

    fun correctFont(txtView: TextView) {
        val btnFont = getFont(this, R.font.bento_blitz_font)

        txtView.typeface = btnFont
    }

}
