package com.example.pavel.ugadayka

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.pavel.ugadayka.Adapter.GridAnswerAdapter
import com.example.pavel.ugadayka.Adapter.MyFragmentAdapter
import com.example.pavel.ugadayka.Adapter.QuestionListHelperAdapter
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Common.SpacesItemDecoration
import com.example.pavel.ugadayka.DBHelper.DBHelper
import com.example.pavel.ugadayka.DBHelper.OnlineDBHelper
import com.example.pavel.ugadayka.Interface.MyCallback
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.Model.Question
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_question_actvity.*
import kotlinx.android.synthetic.main.app_bar_question_actvity.*
import kotlinx.android.synthetic.main.content_question_actvity.*
import java.lang.StringBuilder
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer

class QuestionActvity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val CODE_GET_RESULT = 9999

    lateinit var countDownTimer:CountDownTimer
    lateinit var adapter: GridAnswerAdapter
    lateinit var questionListHelperAdapter: QuestionListHelperAdapter
    lateinit var recycler_helper_answer_sheet: RecyclerView

    var isAnswerModeView = false

    var time_play = Common.TOTAL_TIME
    lateinit var txt_wrong_answer: TextView
    lateinit var txt_right_answer: TextView

    internal var goToQuestionNum:BroadcastReceiver = object:BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action!!.toString() == Common.KEY_GO_TO_QUESTION)
            {
                val question:Int = intent!!.getIntExtra(Common.KEY_GO_TO_QUESTION,-1)
                if (question != -1)
                    view_pager.currentItem = question
                drawer_layout.closeDrawer(Gravity.LEFT)
            }
        }

    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(goToQuestionNum)
        
        if (Common.fragmentList !=null)
            Common.fragmentList.clear()
        if (Common.answerSheetList != null)
            Common.answerSheetList.clear()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_actvity)
        setSupportActionBar(toolbar)

        LocalBroadcastManager.getInstance(this).registerReceiver(goToQuestionNum, IntentFilter(Common.KEY_GO_TO_QUESTION))


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        recycler_helper_answer_sheet = nav_view.getHeaderView(0).findViewById<View>(R.id.answer_sheet) as RecyclerView
        recycler_helper_answer_sheet.setHasFixedSize(true)
        recycler_helper_answer_sheet.layoutManager = GridLayoutManager(this,3)
        recycler_helper_answer_sheet.addItemDecoration(SpacesItemDecoration(2))

        val btn_done:Button = nav_view.getHeaderView(0).findViewById<View>(R.id.btn_done) as Button
        txt_right_answer = findViewById(R.id.txt_right_answer)
        btn_done.setOnClickListener {
            if(!isAnswerModeView)
            {
                MaterialStyledDialog.Builder(this@QuestionActvity)
                        .setTitle("Finish ?")
                        .setDescription("Do you really want tot finish ?")
                        .setIcon(R.drawable.ic_mood_white_24dp)
                        .setNegativeText("No")
                        .onNegative { dialog, which -> dialog.dismiss()  }
                        .setPositiveText("Yes")
                        .onPositive { dialog, which -> finishGame()
                            drawer_layout.closeDrawer(Gravity.LEFT)}
                        .show()
            }
            else {
                finishGame()
            }

        }


        //Получаем вопрос в данной категориии
        genQuestion()


    }

    private fun countCorrectedAnswer() {
        Common.right_answer_count = 0
        Common.wrong_answer_count = 0

        for (item:CurrentQuestion in Common.answerSheetList)
        {
            if (item.type == Common.ANSWER_TYPE.RIGHT_ANSWER)
                Common.right_answer_count++
            else if (item.type == Common.ANSWER_TYPE.WRONG_ANSWER)
                Common.wrong_answer_count++
        }
    }

    private fun genFragmentList() {
        for (i in Common.questionList.indices)
        {
            val bundle = Bundle()
            bundle.putInt("index",i)
            val fragment = Question_fragment()
            fragment.arguments = bundle
            Common.fragmentList.add(fragment)
        }
    }

    private fun genItems() {
        for (i in Common.questionList.indices)
                Common.answerSheetList.add(CurrentQuestion(i,Common.ANSWER_TYPE.NO_ANSWER))

    }

    private fun countTimer() {
        countDownTimer = object:CountDownTimer(Common.TOTAL_TIME.toLong(),1000)
        {
            override fun onFinish() {
                finishGame()
            }

            override fun onTick(interval: Long) {
                txt_timer.text = (java.lang.String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(interval),
                        TimeUnit.MILLISECONDS.toSeconds(interval) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(interval)) ))
                time_play -= 1000
            }

        }.start()
    }


    private fun finishGame() {
        val position = view_pager.currentItem
        val questionFragment = Common.fragmentList[position]

        val question_state:CurrentQuestion = questionFragment.selectedAnswer()
        Common.answerSheetList[position] = question_state
        adapter.notifyDataSetChanged()

        questionListHelperAdapter.notifyDataSetChanged()

        countCorrectedAnswer()


        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
        txt_wrong_answer.text = "${Common.wrong_answer_count}"

        if (question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
        {
            questionFragment.showCorrectAnswer()
            questionFragment.disableAnswer()
        }

        val intent = Intent(this@QuestionActvity,ResultActivity::class.java)
        Common.timer = Common.TOTAL_TIME - time_play
        Common.no_answer_count = Common.questionList.size - (Common.right_answer_count + Common.wrong_answer_count)
        Common.data_question = StringBuilder(Gson().toJson(Common.answerSheetList))
        startActivityForResult(intent,CODE_GET_RESULT)
        countDownTimer.cancel()


    }

    private fun setupQuestion()
    {
        if (Common.questionList.size > 0)
        {
            txt_timer.visibility = View .VISIBLE
            txt_right_answer.visibility = View.VISIBLE

            countTimer()
            //Генерируем айтем для grid_answer
            genItems()
            grid_answer.setHasFixedSize(true)
            if (Common.questionList.size > 0)
                grid_answer.layoutManager = GridLayoutManager(this, if (Common.questionList.size > 5)
                    Common.questionList.size/2 else Common.questionList.size)
            adapter = GridAnswerAdapter(this,Common.answerSheetList)


            grid_answer.adapter = adapter


            //Генерируем fragment List
            genFragmentList()

            val fragmentAdapter = MyFragmentAdapter(supportFragmentManager,this,Common.fragmentList)
            view_pager.offscreenPageLimit = Common.questionList.size
            view_pager.adapter = fragmentAdapter//связываем вопрос с view Pager
            sliding_tabs.setupWithViewPager(view_pager)

            //Событие
            view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{

                val SCROLLING_RIGHT = 0
                val SCROLLING_LEFT = 0
                val SCROLLING_UNDETERMINED = 0

                var currentScrollDirection = SCROLLING_UNDETERMINED

                private val isScrollDirectionUndetermined:Boolean
                    get() = currentScrollDirection == SCROLLING_UNDETERMINED
                private val isScrollDirectionRight:Boolean
                    get() = currentScrollDirection == SCROLLING_RIGHT
                private val isScrollDirectionLeft:Boolean
                    get() = currentScrollDirection == SCROLLING_LEFT

                private fun setScrollingDirection(positionOffset:Float)
                {
                    if (1 - positionOffset >= 0.5)
                        this.currentScrollDirection = SCROLLING_RIGHT
                    else if (1 - positionOffset <= 0.5)
                        this.currentScrollDirection = SCROLLING_LEFT
                }

                override fun onPageScrollStateChanged(state: Int) {
                    if (state == ViewPager.SCROLL_STATE_IDLE)
                        this.currentScrollDirection = SCROLLING_UNDETERMINED
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    if (isScrollDirectionUndetermined)
                        setScrollingDirection(positionOffset)
                }

                override fun onPageSelected(position: Int) {
                    val questionFragment:Question_fragment
                    var positionn = 0
                    if (position > 0)
                    {
                        if (isScrollDirectionRight)
                        {
                            questionFragment = Common.fragmentList[position-1]
                            positionn = position-1
                        }
                        else if (isScrollDirectionLeft)
                        {
                            questionFragment = Common.fragmentList[position+1]
                            positionn = position+1
                        }
                        else
                        {
                            questionFragment = Common.fragmentList[position]
                        }
                    }
                    else
                    {
                        questionFragment = Common.fragmentList[0]
                        positionn = 0
                    }

                    if (Common.answerSheetList[positionn].type == Common.ANSWER_TYPE.NO_ANSWER)
                    {
                        //если хочу показать правильный ответ
                        val question_state:CurrentQuestion = questionFragment.selectedAnswer()
                        Common.answerSheetList[positionn] = question_state
                        adapter.notifyDataSetChanged()

                        questionListHelperAdapter.notifyDataSetChanged()

                        countCorrectedAnswer()

                        txt_right_answer.text = ("${Common.right_answer_count} / ${Common.questionList.size}")
                        txt_wrong_answer.text = "${Common.wrong_answer_count}"

                        if (question_state.type != Common.ANSWER_TYPE.NO_ANSWER)
                        {
                            questionFragment.showCorrectAnswer()
                            questionFragment.disableAnswer()
                        }
                    }
                }

            })

            txt_right_answer.text = "${Common.right_answer_count}/${Common.questionList.size}"
            questionListHelperAdapter = QuestionListHelperAdapter(this,Common.answerSheetList)
            recycler_helper_answer_sheet.adapter = questionListHelperAdapter

        }
    }

    private fun genQuestion() {
        if (!Common.isOnline) {
            Common.questionList = DBHelper.getInstance(this)
                    .getQuestionByCategory(Common.selectedCategory!!.id)


            if (Common.questionList.size == 0) {
                MaterialStyledDialog.Builder(this)
                        .setTitle("Oppps")
                        .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                        .setDescription("We have not questions in this ${Common.selectedCategory!!.name} category")
                        .setPositiveText("OK")
                        .onPositive { dialog, which ->
                            dialog.dismiss()
                            finish()
                        }.show()

            }
            else
            setupQuestion()
        }
        else {

            val dialog = SpotsDialog.Builder().setContext(this)
                    .setCancelable(false)
                    .build()
           if (!dialog.isShowing)
                dialog.show()
                OnlineDBHelper.getInstacne(this, FirebaseDatabase.getInstance())
                        .readData(object : MyCallback {
                            override fun setQuestionList(questionList: List<Question>) {
                                Common.questionList.clear()
                                Common.questionList = questionList as MutableList<Question>

                                if (Common.questionList.size == 0) {
                                    MaterialStyledDialog.Builder(this@QuestionActvity)
                                            .setTitle("Oppps")
                                            .setIcon(R.drawable.ic_sentiment_very_dissatisfied_black_24dp)
                                            .setDescription("We have not questions in this ${Common.selectedCategory!!.name} category")
                                            .setPositiveText("OK")
                                            .onPositive { dialog, which ->
                                                dialog.dismiss()
                                                finish()
                                            }.show()
                                } else
                                    if(dialog.isShowing)
                                        dialog.dismiss()
                                    setupQuestion()
                            }


                        }, Common.selectedCategory!!.name!!.replace(" ", "")
                                .replace("/", "_"))
            }

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            this.finish()//Закроет активити когда нажмешь на кнопку назад
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item:MenuItem = menu!!.findItem(R.id.menu_wrong_answer)
        val layout:ConstraintLayout = item.actionView as ConstraintLayout
        txt_wrong_answer = layout.findViewById(R.id.txt_wrong_answer) as TextView
        txt_wrong_answer.text = 0.toString()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        menuInflater.inflate(R.menu.question_actvity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.menu_done ->
            {
                if(!isAnswerModeView)
                {
                    MaterialStyledDialog.Builder(this)
                            .setTitle("Finish ?")
                            .setDescription("Do you really want tot finish ?")
                            .setIcon(R.drawable.ic_mood_white_24dp)
                            .setNegativeText("No")
                            .onNegative { dialog, which -> dialog.dismiss()  }
                            .setPositiveText("Yes")
                            .onPositive { dialog, which -> finishGame()
                                drawer_layout.closeDrawer(Gravity.LEFT)}
                            .show()
                }
                else {
                    finishGame()
                }
            }
        }
         return true

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_GET_RESULT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                val action = data!!.getStringExtra("action")
                if (action == null || TextUtils.isEmpty(action))
                {
                    val questionIndex = data.getIntExtra(Common.KEY_BACK_FROM_RESULT,-1)
                    view_pager.currentItem = questionIndex
                    txt_right_answer.text = ("0 / ${Common.questionList.size}")

                    isAnswerModeView = true
                    countDownTimer!!.cancel()

                    txt_wrong_answer.visibility = View.GONE
                    txt_right_answer.visibility = View.GONE
                    txt_timer.visibility = View.GONE

                }
                else
                {
                    if (action.equals("doquizagain"))
                    {
                        view_pager.currentItem = 0
                        isAnswerModeView = false



                        txt_wrong_answer.visibility = View.VISIBLE
                        txt_right_answer.visibility = View.VISIBLE
                        txt_timer.visibility = View.VISIBLE


                        for (i in Common.fragmentList.indices) {
                            Common.fragmentList[i].resetQuestion()
                        }

                        for(i in Common.answerSheetList.indices)
                            Common.answerSheetList[i].type = Common.ANSWER_TYPE.NO_ANSWER

                        txt_right_answer.text = ("0 / ${Common.questionList.size}")
                        txt_wrong_answer.text = ("0")
                        adapter.notifyDataSetChanged()
                        questionListHelperAdapter.notifyDataSetChanged()

                        countTimer()
                    }
                    else if (action.equals("viewanswer")) {
                        view_pager.currentItem = 0
                        isAnswerModeView = true
                        countDownTimer!!.cancel()

                        txt_wrong_answer.visibility = View.GONE
                        txt_right_answer.visibility = View.GONE
                        txt_timer.visibility = View.GONE

                        for (i in Common.fragmentList.indices) {
                            Common.fragmentList[i].showCorrectAnswer()
                            Common.fragmentList[i].disableAnswer()
                        }
                    }
                }
            }
        }
    }
}
