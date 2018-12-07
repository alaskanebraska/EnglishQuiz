package com.example.pavel.ugadayka


import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Interface.IAnswerSelect
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.Model.Question
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_question.*
import java.lang.Exception
import java.lang.StringBuilder


class Question_fragment : Fragment(),IAnswerSelect {
    override fun selectedAnswer(): CurrentQuestion {
        //Удалить все дубликаты в select_answer
        Common.selectedVales.distinct()
        Common.selectedVales.sort()

        if (Common.answerSheetList[questionIndex].type == Common.ANSWER_TYPE.NO_ANSWER)
        {
            val currentQuestion = CurrentQuestion(questionIndex,Common.ANSWER_TYPE.NO_ANSWER)
            val result = StringBuilder()
            if(Common.selectedVales.size > 1)
            {
                val arrayAnswer = Common.selectedVales.toTypedArray()
                //Здесь мы будем получть первый симво ответа
                //пр.: arr[0]= A. Paris
                // arr[1] = B. NewYork
                //Правильный ответ A,B
                //Поэтому кбудем получать A and B в массив

                for (i in arrayAnswer!!.indices)
                    if (i < arrayAnswer!!.size-1)
                        result.append(StringBuilder((arrayAnswer!![i] as String).substring(0,1)).append(","))
                else
                        result.append((arrayAnswer!![i] as String).substring(0,1))
            }
            else if (Common.selectedVales.size == 1) //Если только один
            {
                val arrayAnswer = Common.selectedVales.toTypedArray()
                result.append(StringBuilder((arrayAnswer!![0] as String).substring(0,1)))
            }
            if (question!=null)
            {
                if (!TextUtils.isEmpty(result)) {
                    if (result.toString() == question!!.correctAnswer)
                        currentQuestion.type = Common.ANSWER_TYPE.RIGHT_ANSWER
                    else
                        currentQuestion.type = Common.ANSWER_TYPE.WRONG_ANSWER
                }
                else
                    currentQuestion.type = Common.ANSWER_TYPE.NO_ANSWER
            }
            else
            {
                Toast.makeText(activity,"Can not get question",Toast.LENGTH_SHORT).show()
                currentQuestion.type = Common.ANSWER_TYPE.NO_ANSWER
            }
            Common.selectedVales.clear()//Почистить выбранный массив
            return currentQuestion
        }
        else
            return Common.answerSheetList[questionIndex]
    }

    override fun showCorrectAnswer() {
        val correctAnswer = question!!.correctAnswer!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
        for (answer in correctAnswer)
        {
            if(answer.equals("A"))
            {
                ckb_A.setTypeface(null,Typeface.BOLD)
                ckb_A.setTextColor(Color.RED)
            }
            else  if(answer.equals("B"))
            {
                ckb_B.setTypeface(null,Typeface.BOLD)
                ckb_B.setTextColor(Color.RED)
            }
            else  if(answer.equals("C"))
            {
                ckb_C.setTypeface(null,Typeface.BOLD)
                ckb_C.setTextColor(Color.RED)
            }
            else  if(answer.equals("D"))
            {
                ckb_D.setTypeface(null,Typeface.BOLD)
                ckb_D.setTextColor(Color.RED)
            }
        }

    }

    override fun disableAnswer() {
        ckb_A.isEnabled = false
        ckb_B.isEnabled = false
        ckb_C.isEnabled = false
        ckb_D.isEnabled = false
    }


    override fun resetQuestion() {
        ckb_A.isEnabled = true
        ckb_B.isEnabled = true
        ckb_C.isEnabled = true
        ckb_D.isEnabled = true

        ckb_A.isChecked = false
        ckb_B.isChecked = false
        ckb_C.isChecked = false
        ckb_D.isChecked = false

        ckb_A.setTypeface(null,Typeface.NORMAL)
        ckb_A.setTextColor(Color.BLACK)
        ckb_B.setTypeface(null,Typeface.NORMAL)
        ckb_B.setTextColor(Color.BLACK)
        ckb_C.setTypeface(null,Typeface.NORMAL)
        ckb_C.setTextColor(Color.BLACK)
        ckb_D.setTypeface(null,Typeface.NORMAL)
        ckb_D.setTextColor(Color.BLACK)

        Common.selectedVales.clear()
    }

    lateinit var txt_question_text:TextView
    lateinit var ckb_A:CheckBox
    lateinit var ckb_B:CheckBox
    lateinit var ckb_C:CheckBox
    lateinit var ckb_D:CheckBox

    lateinit var layout_image:FrameLayout
    lateinit var progress_bar:ProgressBar

    var question:Question ?= null
    var questionIndex = -1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val itemView:View = inflater.inflate(R.layout.fragment_question, container, false)

        questionIndex = arguments!!.getInt("index",-1)
        layout_image = itemView.findViewById(R.id.layout_image) as FrameLayout
        question = Common.questionList[questionIndex]

        if (question!=null)
        {
            progress_bar = itemView.findViewById(R.id.progress_bar) as ProgressBar

            if (question!!.isImageQuestion)
            {
                val imageView = itemView.findViewById<View>(R.id.img_question) as ImageView
                //Получаем изображение

                Picasso.get().load(question!!.questionImage).into(imageView,
                        object:Callback{
                            override fun onSuccess() {
                                progress_bar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                img_question.setImageResource(R.drawable.ic_error_outline_black_24dp)
                            }

                        })
            }

            else layout_image.visibility = View.GONE
            txt_question_text = itemView.findViewById(R.id.txt_question_text) as TextView
            txt_question_text.text = question!!.questionText

            ckb_A = itemView.findViewById(R.id.ckb_A) as CheckBox
            ckb_A.text = question!!.answerA
            ckb_B = itemView.findViewById(R.id.ckb_B) as CheckBox
            ckb_B.text = question!!.answerB
            ckb_C = itemView.findViewById(R.id.ckb_C) as CheckBox
            ckb_C.text = question!!.answerC
            ckb_D = itemView.findViewById(R.id.ckb_D) as CheckBox
            ckb_D.text = question!!.answerD

            ckb_A.setOnCheckedChangeListener { compoundButton, b ->
                if(b)
                Common.selectedVales.add(ckb_A.text.toString())
                else
                    Common.selectedVales.remove(ckb_A.text.toString())
            }
            ckb_B.setOnCheckedChangeListener { compoundButton, b ->
                if(b)
                    Common.selectedVales.add(ckb_B.text.toString())
                else
                    Common.selectedVales.remove(ckb_B.text.toString())
            }
            ckb_C.setOnCheckedChangeListener { compoundButton, b ->
                if(b)
                    Common.selectedVales.add(ckb_C.text.toString())
                else
                    Common.selectedVales.remove(ckb_C.text.toString())
            }
            ckb_D.setOnCheckedChangeListener { compoundButton, b ->
                if(b)
                    Common.selectedVales.add(ckb_D.text.toString())
                else
                    Common.selectedVales.remove(ckb_D.text.toString())
            }

        }

        return itemView
    }


}
