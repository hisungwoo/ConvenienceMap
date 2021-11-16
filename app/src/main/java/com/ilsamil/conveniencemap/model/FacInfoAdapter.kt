package com.ilsamil.conveniencemap.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R



class FacInfoAdapter : RecyclerView.Adapter<FacInfoAdapter.FacInfoViewHolder>() {
    private var mItems: List<ServList> = ArrayList<ServList>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: ServList, pos : Int)
    }

    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    inner class FacInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var faclnmTextView : TextView = itemView.findViewById(R.id.faclnm_tv)
        var lcmnadTextView : TextView = itemView.findViewById(R.id.lcmnad_tv)
        var facltycdTextView : TextView = itemView.findViewById(R.id.facltycd_tv)

        fun bind(item: ServList) {
            val pos = adapterPosition
            if(pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }
    }


    fun updateItems(items: List<ServList>) {
        mItems = items
        notifyDataSetChanged() //UI 갱신
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return FacInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FacInfoViewHolder, position: Int) {
        val serv : ServList = mItems[position]
        holder.faclnmTextView.text = serv.faclNm
        holder.lcmnadTextView.text = serv.lcMnad
        holder.facltycdTextView.text = serv.faclTyCd
        holder.bind(mItems[position])

    }

    override fun getItemCount(): Int = mItems.size


}