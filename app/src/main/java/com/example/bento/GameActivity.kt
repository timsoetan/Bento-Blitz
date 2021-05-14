package com.example.bento

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.activity_game.*
import java.lang.Math.floor
import java.util.*

class GameActivity : AppCompatActivity() {

    var timer = Timer()
    var timeHandler = Handler()

    var gameRunning = false

    var actionPerfomed = false

    var showSashimi = false

    var score = 0

    var highScore = 0

    var name = ""

    var sashimiTimer = 0

    var originalGameBound = 0

    var currGameBound = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        name = intent.getStringExtra(getString(R.string.name))

        val frameAnimation = background.background as AnimationDrawable
        frameAnimation.start()

        val titleAnimation = AnimationUtils.loadAnimation(this, R.anim.title_animation)
        ivTitle.startAnimation(titleAnimation)

        pullHighScore()
        countDown()
    }

    fun pullHighScore() {
        val sharedPrefGameData = this?.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
        highScore = sharedPrefGameData.getInt("HIGH_SCORE", 0)
        tvHighScoreCount.text = highScore.toString()
    }

    fun countDown() {
        ivSushi.visibility = View.INVISIBLE
        ivSashimi.visibility = View.INVISIBLE
        ivWasabi.visibility = View.INVISIBLE

        object : CountDownTimer(4000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                if ((millisUntilFinished / 1000).toString() != "0") {
                    tvTimer.text = (millisUntilFinished / 1000).toString()
                } else {
                    tvTimer.text = getString(R.string.go)
                }
            }

            override fun onFinish() {
                tvTimer.visibility = View.GONE
                ivSushi.visibility = View.VISIBLE
                ivSashimi.visibility = View.VISIBLE
                ivWasabi.visibility = View.VISIBLE

                initializeGame()
            }
        }.start()
    }

    fun initializeGame() {
        gameRunning = true

        ivBento.x = 0F
        ivSushi.y = 2000F
        ivSushi.x = floor(Math.random() * (gameBound.width - ivSushi.width)).toFloat()
        ivSashimi.y = 2000F
        ivWasabi.y = 2000F
        ivWasabi.x = floor(Math.random() * (gameBound.width - ivWasabi.width)).toFloat()

        originalGameBound = gameBound.layoutParams.width
        currGameBound = originalGameBound

        tvUserScoreCount.text = score.toString()

        val timeTask = object : TimerTask() {
            override fun run() {
                timeHandler.post(Runnable {
                    updateBentoPos()
                    updateSushiPos()
                    updateSashimiPos()
                    updateWasabiPos()
                })
            }
        }

        startTimer(timeTask)
    }

    fun startTimer(task: TimerTask) {
        timer = Timer()
        timer.schedule(task, 0, 10)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameRunning) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                actionPerfomed = true

            } else if (event.action == MotionEvent.ACTION_UP) {
                actionPerfomed = false

            }
        }
        return true
    }

    fun updateBentoPos() {
        if (actionPerfomed) {
            ivBento.x += 10F
        } else {
            ivBento.x -= 10F
        }

        if (ivBento.x < 0) {
            ivBento.x = 0F
        }
        if (gameBound.width - ivBento.height < ivBento.x) {
            ivBento.x = (gameBound.width - ivBento.height).toFloat()
        }
    }

    fun updateSushiPos() {
        ivSushi.y += 10F

        val sushiTargetX = ivSushi.x + ivSushi.width / 2
        val sushiTargetY = ivSushi.y + ivSushi.height / 2

        if (checkCollision(sushiTargetX, sushiTargetY)) {
            ivSushi.y = (gameBound.height + 100).toFloat()
            score += 10
        }

        if (ivSushi.y > gameBound.height) {
            sushiRandomizer()
            ivSushi.y = -100F
            ivSushi.x = floor(Math.random() * (gameBound.width - ivSushi.width)).toFloat()
        }

        tvUserScoreCount.text = score.toString()
    }

    fun updateSashimiPos() {
        sashimiTimer += 10

        if (sashimiTimer % 10000 == 0 && !showSashimi) {
            showSashimi = true
            ivSashimi.y = -20F
            ivSashimi.x = floor(Math.random() * (gameBound.width - ivSashimi.width)).toFloat()
        }

        if (showSashimi) {
            ivSashimi.y += 14F

            val sashimiTargetX = ivSashimi.x + ivSashimi.width / 2
            val sashimiTargetY = ivSashimi.y + ivSashimi.height / 2

            if (checkCollision(sashimiTargetX, sashimiTargetY)) {
                ivSashimi.y = gameBound.height + 30F
                score += 20

                if (currGameBound + 25 < originalGameBound) {
                    val newGameBound = gameBound.width + 25
                    updateGameBound(newGameBound)
                }
            }

            if (ivSashimi.y > gameBound.height) {
                showSashimi = false
            }
        }

        tvUserScoreCount.text = score.toString()
    }

    fun updateWasabiPos() {
        ivWasabi.y += 12F

        val wasabiTargetX = ivWasabi.x + ivWasabi.width / 2
        val wasabiTargetY = ivWasabi.y + ivWasabi.height / 2

        if (checkCollision(wasabiTargetX, wasabiTargetY)) {
            ivWasabi.y = (gameBound.height + 100).toFloat()

            val newGameBound = gameBound.width - 50
            updateGameBound(newGameBound)

            if (gameBound.width <= ivBento.width) {
                endGame()
            }
        }

        if (ivWasabi.y > gameBound.height) {
            ivWasabi.y = -100F
            ivWasabi.x = floor(Math.random() * (gameBound.width - ivWasabi.width)).toFloat()
        }
    }

    fun updateGameBound(updatedBound: Int) {
        val params = gameBound.layoutParams
        params.width = updatedBound
        currGameBound = updatedBound
    }

    fun checkCollision(x: Float, y: Float): Boolean {
        if (ivBento.x <= x && x <= ivBento.x + ivBento.height
            && ivBento.y <= y && y <= gameBound.height
        ) {
            return true
        }
        return false
    }

    fun sushiRandomizer() {
        var sushiType = floor(Math.random() * 3) + 1

        when (sushiType) {
            1.0 -> ivSushi.setImageResource(R.drawable.ic_sushi1)
            2.0 -> ivSushi.setImageResource(R.drawable.ic_sushi2)
            3.0 -> ivSushi.setImageResource(R.drawable.ic_sushi3)
        }
    }

    fun endGame() {
        timer.cancel()
        gameRunning = false

        saveData()

        showEndGameDialog()
    }

    fun saveData() {
        if (score > highScore) {
            showToast()

            highScore = score

            tvHighScoreCount.text = highScore.toString()

            val sharedPrefGameData = this?.getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE)
            val editorGame = sharedPrefGameData.edit()
            editorGame.putInt("HIGH_SCORE", highScore)
            editorGame.commit()
        }

        val sharedPrefUserScores = this?.getSharedPreferences("USER_SCORES", Context.MODE_PRIVATE)
        val editorUserScores = sharedPrefUserScores.edit()

        val currNames = sharedPrefUserScores.getString("names", "")
        if (currNames.isEmpty()) {
            editorUserScores.putString("names", name)
        } else {
            editorUserScores.putString("names", currNames + "|" + name)
        }
        editorUserScores.putString(name + "_number", score.toString())
        editorUserScores.commit()
    }

    fun resetGame() {
        val initialIntent = intent
        finish()
        startActivity(initialIntent)
        this.overridePendingTransition(R.anim.slide_up, R.anim.do_nothing)
    }

    fun showEndGameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.game_over))
        builder.setMessage("")
        builder.setIcon(R.drawable.ic_bento_box)

        val finalScore = TextView(this)
        finalScore.text = getString(R.string.final_score) + score.toString()
        finalScore.textSize = 18F
        finalScore.setTextColor(resources.getColor(R.color.colorAccent))
        val inputParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        finalScore.layoutParams = inputParams

        builder.setView(finalScore)
        builder.setPositiveButton(getString(R.string.play_again)) { _, _ ->
            resetGame()
        }

        builder.setNegativeButton(getString(R.string.exit)) { dialog, _ ->
            dialog.dismiss()
            val goToMain = Intent(this, MainActivity::class.java)
            finish()
            startActivity(goToMain)
            this.overridePendingTransition(R.anim.slide_up, R.anim.do_nothing)
        }

        val alert = builder.create()

        alert.setCanceledOnTouchOutside(false)

        alert.show()

        val view: Window? = alert.window
        val title: TextView = view!!.findViewById(R.id.alertTitle)
        val btnPos = alert.getButton(AlertDialog.BUTTON_POSITIVE)
        val btnNeg = alert.getButton(AlertDialog.BUTTON_NEGATIVE)

        val layoutParams = btnPos.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        btnPos.layoutParams = layoutParams
        btnNeg.layoutParams = layoutParams

        correctFont(title)
        correctFont(finalScore)
        correctFont(listOf(btnPos, btnNeg))
        btnPos.textSize = 18F
        btnPos.background = null
        btnNeg.textSize = 18F
        btnNeg.background = null

        finalScore.gravity = Gravity.CENTER
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

    fun showToast() {
        val context = applicationContext
        val inflater = layoutInflater

        val customToastView = inflater.inflate(R.layout.custom_toast, null)

        customToastView.findViewById<TextView>(R.id.message).text =
            getString(R.string.new_high_score)


        val customToast = Toast(context)

        customToast.view = customToastView
        customToast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        customToast.duration = Toast.LENGTH_LONG
        customToast.show()
    }

}
