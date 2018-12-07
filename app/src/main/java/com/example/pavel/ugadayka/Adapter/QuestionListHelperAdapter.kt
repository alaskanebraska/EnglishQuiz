package com.example.pavel.ugadayka.Adapter

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Interface.IOnRecycleViewClickListener
import com.example.pavel.ugadayka.Model.CurrentQuestion
import com.example.pavel.ugadayka.R


class QuestionListHelperAdapter (internal var context: Context,
                                 internal  var answerSheetList:List<CurrentQuestion>): RecyclerView.Adapter<QuestionListHelperAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_question_list_hepler_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return answerSheetList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.text_question_num.text = (position+1).toString()
        if (answerSheetList[position].type == Common.ANSWER_TYPE.RIGHT_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_right_answer_item)
        else if (answerSheetList[position].type == Common.ANSWER_TYPE.WRONG_ANSWER)
            holder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_wrong_item)
        else holder.layout_wrapper.setBackgroundResource(R.drawable.grid_item_no_answer)

        //when user click to this item, we will navigation this question
        holder.setiOnReclerViewItemClickListener(object: IOnRecycleViewClickListener{
            override fun OnClic(view: View, position: Int) {
                LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(Common.KEY_GO_TO_QUESTION).putExtra(Common.KEY_GO_TO_QUESTION,position ))
            }

        })
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView),View.OnClickListener
    {
        override fun onClick(p0: View?) {
            iOnRecylerViewItemClickListener.OnClic(p0!!,adapterPosition)
        }

        internal  var text_question_num: TextView
        internal  var layout_wrapper: LinearLayout

        lateinit var iOnRecylerViewItemClickListener: IOnRecycleViewClickListener

        fun setiOnReclerViewItemClickListener (iOnRecycleViewClickListener: IOnRecycleViewClickListener)
        {

            this.iOnRecylerViewItemClickListener = iOnRecycleViewClickListener
        }
        init {
            text_question_num = itemView.findViewById(R.id.text_question_num) as TextView
            layout_wrapper = itemView.findViewById(R.id.layout_wrapper) as LinearLayout

            itemView.setOnClickListener(this)
        }

    }

}
