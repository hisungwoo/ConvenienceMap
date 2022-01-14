package com.ilsamil.conveniencemap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.utils.ChangeType

class ListFacInfoAdapter : RecyclerView.Adapter<ListFacInfoAdapter.ListViewHolder>(){
    private var eItems: List<ServList> = ArrayList<ServList>()

    interface OnItemClickListener{
        fun onItemClick(v:View, data: ServList, pos : Int)
    }

    private var listener : ListFacInfoAdapter.OnItemClickListener? = null
    fun setOnItemClickListener(listener : ListFacInfoAdapter.OnItemClickListener) {
        this.listener = listener
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var listFaclnmTextView : TextView = itemView.findViewById(R.id.list_faclnm_tv)
        var listFacltycdTextView : TextView = itemView.findViewById(R.id.list_facltycd_tv)
        var listLcmnadTextView : TextView = itemView.findViewById(R.id.list_lcmnad_tv)

        fun bind(item: ServList) {
            val pos = adapterPosition
            if(pos != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView, item, pos)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListFacInfoAdapter.ListViewHolder, position: Int) {
        val serv : ServList = eItems[position]
        holder.listFaclnmTextView.text= serv.faclNm
        holder.listLcmnadTextView.text= serv.lcMnad

        val faclTyCdChange = serv.faclTyCd?.let { ChangeType().changeType(it) }
        holder.listFacltycdTextView.text= faclTyCdChange
        holder.bind(eItems[position])
    }

    override fun getItemCount(): Int = eItems.size

    fun updateItems(items: List<ServList>) {
        eItems = items
        notifyDataSetChanged()//UI갱신
    }


}