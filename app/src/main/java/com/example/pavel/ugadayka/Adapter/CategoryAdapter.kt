package com.example.pavel.ugadayka.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.pavel.ugadayka.Common.Common
import com.example.pavel.ugadayka.Interface.IOnRecycleViewClickListener
import com.example.pavel.ugadayka.Model.Category
import com.example.pavel.ugadayka.Model.Question
import com.example.pavel.ugadayka.QuestionActvity
import com.example.pavel.ugadayka.R

class CategoryAdapter (internal var context:Context,
                       internal var listcategory:List<Category>):
RecyclerView.Adapter<CategoryAdapter.MyViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView:View = LayoutInflater.from(context).inflate(R.layout.category_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listcategory.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_category_name.text = listcategory[position].name
        holder.setiOnRecycleViewClickListener(object :IOnRecycleViewClickListener{
            override fun OnClic(view: View, position: Int) {
                Common.selectedCategory = listcategory[position]
                val intent = Intent(context,QuestionActvity::class.java)
                context.startActivity(intent)
            }

        })
    }


    inner class MyViewHolder (itemView:View): RecyclerView.ViewHolder(itemView),View.OnClickListener
    {
        internal var txt_category_name:TextView
        internal var card_category:CardView
        internal lateinit var iOnRecycleViewClickListener: IOnRecycleViewClickListener

        fun setiOnRecycleViewClickListener(iOnRecycleViewClickListener: IOnRecycleViewClickListener)
        {
            this.iOnRecycleViewClickListener=iOnRecycleViewClickListener
        }
        init {
            txt_category_name = itemView.findViewById(R.id.txt_category_name) as TextView
            card_category = itemView.findViewById(R.id.card_category) as CardView
            itemView.setOnClickListener(this)
        }
        override fun onClick(view: View) {
            iOnRecycleViewClickListener.OnClic(view,adapterPosition)
        }

    }
}