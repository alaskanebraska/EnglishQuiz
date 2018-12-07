package com.example.pavel.ugadayka.Common

import com.example.pavel.ugadayka.Model.Category
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.Model.Question
import com.example.pavel.ugadayka.Question_fragment
import java.lang.StringBuilder

object Common {
    val TOTAL_TIME = 20*60*1000//20 min
    var answerSheetListFiltered:MutableList<CurrentQuestion> = ArrayList()
    var answerSheetList:MutableList<CurrentQuestion> = ArrayList()
    var questionList:MutableList<Question> = ArrayList()
    var selectedCategory:Category?=null
    var fragmentList:MutableList<Question_fragment> = ArrayList()
    var selectedVales:MutableList<String> = ArrayList()

    var timer = 0
    var right_answer_count = 0
    var wrong_answer_count = 0
    var no_answer_count = 0
    var data_question = StringBuilder()
    val KEY_GO_TO_QUESTION: String? = "position_go_to"
    val KEY_BACK_FROM_RESULT: String? = "back from result"
    val KEY_ONLINE_MODE: String? = "ONLINE_MODE"
    var isOnline: Boolean = false

    enum class ANSWER_TYPE{
        NO_ANSWER,
        RIGHT_ANSWER,
        WRONG_ANSWER
    }
}