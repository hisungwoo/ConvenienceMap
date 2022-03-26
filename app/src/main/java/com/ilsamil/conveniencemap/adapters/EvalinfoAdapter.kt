package com.ilsamil.conveniencemap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.model.EvalInfoList
import com.ilsamil.conveniencemap.utils.Util

class EvalinfoAdapter : RecyclerView.Adapter<EvalinfoAdapter.EvalinfoViewHolder>(){
    private var eItems: List<EvalInfoList> = ArrayList<EvalInfoList>()
    private var util = Util()

    inner class EvalinfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var evalinfoTextView : TextView = itemView.findViewById(R.id.result_item_evalInfo_tv)
        var evalinfoImg : ImageView = itemView.findViewById(R.id.result_item_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvalinfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return EvalinfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EvalinfoViewHolder, position: Int) {
        val serv : EvalInfoList = eItems[position]
        holder.evalinfoTextView.text = util.changeEvalText(serv.evalInfo)
        holder.evalinfoImg.setImageResource(util.changeEvalImg(serv.evalInfo))
    }

    override fun getItemCount(): Int = eItems.size

    fun updateItems(items: List<EvalInfoList>) {
        eItems = items
        notifyDataSetChanged() //UI 갱신
    }
}