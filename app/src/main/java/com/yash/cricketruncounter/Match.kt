package com.yash.cricketruncounter

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yash.cricketruncounter.databinding.ActivityMatchBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Match : AppCompatActivity() {
    private lateinit var bindingMatch: ActivityMatchBinding
    private var wasNoBall = false
    private var isSecondInning = false
    private var totalRuns = Array<Int>(2) { 0 }
    private var totalOvers = Array<Float>(2) { 0.0F }
    private var wickets = Array<Int>(2) { 0 }
    private var inning: Int = 0
    private var fours = 0
    private var sixes = 0
    private var balls: Int = ((GlobalVariables.tOvers.toInt()) * 6)
    private var target: Int = 0
    private val scoreList: Array<MutableList<Score>> = arrayOf(mutableListOf(), mutableListOf())
    private lateinit var firstAdapter : MyAdapter
    private lateinit var secondAdapter : MyAdapter
    private lateinit var firstRCView : RecyclerView
    private lateinit var secondRCView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindingMatch = ActivityMatchBinding.inflate(layoutInflater)
        val view = bindingMatch.root
        setContentView(view)

//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // lock orientation

        // setting height of scrollView
        bindingMatch.btnRunsWickets.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                // remove the listener to avoid multiple calls
                bindingMatch.btnRunsWickets.viewTreeObserver.removeOnGlobalLayoutListener(this)

                // get the height of buttons
                val height = bindingMatch.btnRunsWickets.height

                // available height
                val available = (resources.displayMetrics.heightPixels) - height

                // setting height of scrollView
                val params : ViewGroup.LayoutParams = bindingMatch.scrollView.layoutParams
                params.height = available
                bindingMatch.scrollView.layoutParams = params
            }
        })

        // setting width of container four and six
        val width : Int = resources.displayMetrics.widthPixels / 2

        // getting params of containers
        val paramsFour : ViewGroup.LayoutParams = bindingMatch.containerFours.layoutParams
        val paramsSix : ViewGroup.LayoutParams = bindingMatch.containerSix.layoutParams

        // changing width of containers
        paramsFour.width = width - 50
        paramsSix.width = width - 50

        // setting params of containers after changing width
        bindingMatch.containerFours.layoutParams = paramsFour
        bindingMatch.containerSix.layoutParams = paramsSix

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        GlobalVariables.tOvers = intent.getFloatExtra("totalOvers", 0.0F)
        GlobalVariables.tPlayers = intent.getIntExtra("totalPlayer", 1)

        balls = ((GlobalVariables.tOvers.toInt()) * 6)

        bindingMatch.totalOvers.text = "overs ${GlobalVariables.tOvers}"
        bindingMatch.totalWickets.text = "out of ${GlobalVariables.tPlayers} wickets"

        bindingMatch.run1.setOnClickListener {
            updateRuns(1)
        }
        bindingMatch.run2.setOnClickListener {
            updateRuns(2)
        }
        bindingMatch.run3.setOnClickListener {
            updateRuns(3)
        }
        bindingMatch.run4.setOnClickListener {
            updateRuns(4)
            bindingMatch.fours.text = "${++fours}"
        }
        bindingMatch.run6.setOnClickListener {
            updateRuns(6)
            bindingMatch.sixes.text = "${++sixes}"
        }
        bindingMatch.wicket.setOnClickListener {
            updateWickets()
        }
        bindingMatch.dot.setOnClickListener {
            updateRuns(0)
            if (wasNoBall) wasNoBall = false
        }
        bindingMatch.wide.setOnClickListener {
            noBallAndWideBall()
        }
        bindingMatch.noBall.setOnClickListener {
            noBallAndWideBall()
            wasNoBall = true
        }

        // firstInning recyclerView
        firstRCView = findViewById(R.id.firstInnings)
        firstAdapter = MyAdapter(this@Match,scoreList[0])
        firstRCView.adapter = firstAdapter
        firstRCView.layoutManager = LinearLayoutManager(this@Match)

        // secondInning recyclerView
        secondRCView = findViewById(R.id.secondInnings)
        secondAdapter = MyAdapter(this@Match,scoreList[1])
        secondRCView.adapter = secondAdapter
        secondRCView.layoutManager = LinearLayoutManager(this@Match)
    }

    private fun noBallAndWideBall(){
        totalRuns[inning] = totalRuns[inning] + 1
        bindingMatch.runs.text = "${totalRuns[inning]} / ${wickets[inning]}"
    }

    private fun updateRuns(runs: Short) {
        totalRuns[inning] = totalRuns[inning] + runs
        bindingMatch.runs.text = "${totalRuns[inning]} / ${wickets[inning]}"
        if (wasNoBall) {
            wasNoBall = false
        }
        updateOvers()
        if (isSecondInning) updateRemainingRuns(runs);checkRuns(totalRuns[inning])
        if (checkOvers(totalOvers[inning])) return
    }

    private fun updateWickets() {
        if (!wasNoBall) {
            wickets[inning] = wickets[inning] + 1
            bindingMatch.runs.text = "${totalRuns[inning]} / ${wickets[inning]}"
        } else {
            wasNoBall = false
        }
        updateOvers()
        checkWickets(wickets[inning])
        if (checkOvers(totalOvers[inning])) return
    }

    private fun updateOvers() {
        if (!wasNoBall) {
            if (totalOvers[inning] < ((Math.floor(totalOvers[inning].toDouble()) + 0.5).toFloat())) {
                totalOvers[inning] = (Math.round((totalOvers[inning] + 0.1F) * 10) / 10.0).toFloat()
            } else {
                totalOvers[inning] = (Math.round(totalOvers[inning]).toFloat())
                // updating recyclerView
                updateRecyclerView()
            }
            bindingMatch.over.text = totalOvers[inning].toString()
        }
    }

    private fun checkOvers(current: Float): Boolean {
        if (current == GlobalVariables.tOvers && !isSecondInning) {
            startSecondInning()
            return true
        } else if (current == GlobalVariables.tOvers && isSecondInning && totalRuns[inning] < totalRuns[0]) {
            bindingMatch.result.text = "Team 1 is WINNER"
            disableButtons()
        }
        return false
    }

    private fun checkWickets(currentWicket: Int) {
        if (currentWicket == GlobalVariables.tPlayers && !isSecondInning) {
            updateRecyclerView()
            startSecondInning()
        } else if (currentWicket == GlobalVariables.tPlayers && totalRuns[inning] < totalRuns[0]) {
            updateRecyclerView()
            bindingMatch.result.text = "Team 1 is WINNER"
            disableButtons()
        }
    }

    private fun checkRuns(currentRuns: Int) {
        if (currentRuns > totalRuns[0]) {
            updateRecyclerView()
            bindingMatch.result.text = "Team 2 is WINNER"
            disableButtons()
        }
    }

    private fun startSecondInning() {
        isSecondInning = true
        bindingMatch.over.text = "0.0"
        bindingMatch.runs.text = "0 / 0"
        target = totalRuns[0] + 1
        bindingMatch.runsNeeded.text = "${target} runs needed in ${balls} balls"
        bindingMatch.runsNeeded.alpha = 1F
        inning = 1
        bindingMatch.result.text = "2nd Inning"
    }

    private fun updateRemainingRuns(runs: Short) {
        target -= runs
        if (target < 0) {
            target = 0
        }
        bindingMatch.runsNeeded.text = "${target} runs needed in ${--balls} balls"
    }

    private fun updateRecyclerView(){
        scoreList[inning].add(
            Score(
                over = totalOvers[inning],
                score = "${totalRuns[inning]} / ${wickets[inning]}"
            )
        )
        if (!isSecondInning) {
            firstAdapter.notifyItemInserted(scoreList[inning].size - 1)
            firstRCView.scrollToPosition(scoreList[inning].size - 1)
        } else {
            secondAdapter.notifyItemInserted(scoreList[inning].size - 1)
            secondRCView.scrollToPosition(scoreList[inning].size - 1)
        }
    }

    private fun disableButtons() {
        bindingMatch.run1.isEnabled = false
        bindingMatch.run2.isEnabled = false
        bindingMatch.run3.isEnabled = false
        bindingMatch.run4.isEnabled = false
        bindingMatch.run6.isEnabled = false
        bindingMatch.wicket.isEnabled = false
        bindingMatch.dot.isEnabled = false
        bindingMatch.wide.isEnabled = false
        bindingMatch.noBall.isEnabled = false
    }
}