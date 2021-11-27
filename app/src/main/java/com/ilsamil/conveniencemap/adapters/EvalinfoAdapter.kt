package com.ilsamil.conveniencemap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.model.EvalInfoList

class EvalinfoAdapter : RecyclerView.Adapter<EvalinfoAdapter.EvalinfoViewHolder>(){
    private var eItems: List<EvalInfoList> = ArrayList<EvalInfoList>()

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
        var evalinfoText =serv.evalInfo

        when (serv.evalInfo) {
            "계단 또는 승강설비" -> {
                evalinfoText = "계단,승강설비"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_stairs)
            }
            "대변기" -> {
                evalinfoText = "대변기"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_wc)
            }
            "복도" -> {
                evalinfoText = "복도시설"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_theaters)
            }
            "소변기" -> {
                evalinfoText = "소변기"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_wc)
            }
            "일반사항" -> {
                evalinfoText = "일반사항"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_transfer_within_a_station)
            }
            "장애인전용주차구역" -> {
                evalinfoText = "장애인주차"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_emoji_transportation)
            }
            "주출입구 높이차이 제거" -> {
                evalinfoText = "주출입구 높이"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_signal_cellular_bar)
            }
            "주출입구 접근로" -> {
                evalinfoText = "접근로"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_accessible)
            }
            "출입구(문)" -> {
                evalinfoText = "출입구(문)"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_sensor_door)
            }
            "해당시설 층수" -> {
                evalinfoText = "해당시설 층수"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_elevator)
            }
            "경보 및 피난설비" -> {
                evalinfoText = "경보 및 피난설비"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_add_alert)
            }
            "매표소" -> {
                evalinfoText = "매표소"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_store)
            }
            "판매기" -> {
                evalinfoText = "판매기"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_storefront)
            }
            "음료대" -> {
                evalinfoText = "음료대"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_touch_app)
            }
            "비치용품" -> {
                evalinfoText = "비치용품"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_group_work)
            }
            "접수대 및 작업대" -> {
                evalinfoText = "접수대 및 작업대"
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_calendar)
            }
            else -> {
                evalinfoText = serv.evalInfo
                holder.evalinfoImg.setImageResource(R.drawable.ic_baseline_more)
            }
        }






        holder.evalinfoTextView.text = evalinfoText
    }

    override fun getItemCount(): Int = eItems.size

    fun updateItems(items: List<EvalInfoList>) {
        eItems = items
        notifyDataSetChanged() //UI 갱신
    }

}