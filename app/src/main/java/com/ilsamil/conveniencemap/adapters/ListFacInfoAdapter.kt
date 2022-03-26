package com.ilsamil.conveniencemap.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.model.ServList
import com.ilsamil.conveniencemap.utils.Util

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
        var listItemImg : ImageView = itemView.findViewById(R.id.list_item_img)

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
        if (serv.faclTyCd == "UC0U04") {

        } else if(serv.faclTyCd == "000000") {
            holder.listFaclnmTextView.text = ""
            holder.listLcmnadTextView.text = ""
            holder.listItemImg.setImageResource(R.drawable.button_list)

        } else {
            holder.listFaclnmTextView.text = serv.faclNm
            holder.listLcmnadTextView.text = serv.lcMnad

            val util = Util()

            serv.faclTyCd.let {
                holder.listFacltycdTextView.text = util.changeFaclType(it)
                when(util.changeFaclCategory(it.toString())) {
                    "음식 및 상점" -> holder.listItemImg.setImageResource(R.drawable.category_button_shop_img)
                    "생활시설" -> holder.listItemImg.setImageResource(R.drawable.category_button_living_img)
                    "교육시설" -> holder.listItemImg.setImageResource(R.drawable.category_button_education_img)
                    "기타" -> holder.listItemImg.setImageResource(R.drawable.category_button_public_img)
                }
            }
            holder.bind(eItems[position])
        }
    }

    override fun getItemCount(): Int = eItems.size

    fun updateItems(items: List<ServList>) {
        eItems = items
        notifyDataSetChanged()  // UI갱신
    }

}