package com.example.pavel.ugadayka.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.R
import kotlinx.android.synthetic.main.layout_grid_answer_item.view.*

class GridAnswerAdapter(internal var context: Context,
                        internal var answerSheetList: List<CurrentQuestion>):
     RecyclerView.Adapter<GridAnswerAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_grid_answer_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerSheetList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (answerSheetList[position].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
            holder.question_item.setBackgroundResource(R.drawable.grid_right_answer_item)
        else if (answerSheetList[position].type == Common.ANSWER_TYPE.WRONG_ANSWER)
            holder.question_item.setBackgroundResource(R.drawable.grid_item_wrong_item)
        else
            holder.question_item.setBackgroundResource(R.drawable.grid_item_no_answer)
    }

    inner class MyViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        internal var question_item:View
        init {
            question_item = itemView.findViewById(R.id.question_item) as View
        }
    }
}