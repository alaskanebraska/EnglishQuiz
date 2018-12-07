package com.example.pavel.ugadayka.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.R
import kotlinx.android.synthetic.main.layout_result_item.view.*
import java.lang.StringBuilder

class ResultGridAdapter (internal var context:Context,
                         internal var answerSheetList: List<CurrentQuestion>): RecyclerView.Adapter<ResultGridAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView:View = LayoutInflater.from(context).inflate(R.layout.layout_result_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerSheetList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.btn_question_num.text = StringBuilder("Question").append(answerSheetList[position].questionIndex+1)

        if (answerSheetList[position].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
        {
            holder.btn_question_num.setBackgroundResource(R.drawable.grid_right_answer_item)
            val img = context.resources.getDrawable(R.drawable.ic_check_white_24dp)
            holder.btn_question_num.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,img)
        }
        else  if (answerSheetList[position].type == Common.ANSWER_TYPE.WRONG_ANSWER)
        {
            holder.btn_question_num.setBackgroundResource(R.drawable.grid_item_wrong_item)
            val img = context.resources.getDrawable(R.drawable.ic_clear_white_24dp)
            holder.btn_question_num.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,img)
        }
        else
        {
            holder.btn_question_num.setBackgroundResource(R.drawable.grid_item_no_answer)
            val img = context.resources.getDrawable(R.drawable.ic_error_outline_white_24dp)
            holder.btn_question_num.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,null,img)
        }
    }


    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        internal var btn_question_num: Button

        init {
            btn_question_num = itemView.findViewById(R.id.btn_question) as Button
            btn_question_num.setOnClickListener {

                //Когда пользователь кликает на вопрос в Резульатт актиивити, мы попадаем обартно к вопросам
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(Intent(Common.KEY_BACK_FROM_RESULT)
                                .putExtra(Common.KEY_BACK_FROM_RESULT,answerSheetList[adapterPosition].questionIndex))
            }
        }

    }
}