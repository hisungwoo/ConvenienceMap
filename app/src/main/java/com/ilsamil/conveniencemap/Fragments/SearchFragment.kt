package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentMapBinding
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.model.FacInfoAdapter
import com.ilsamil.conveniencemap.model.ServList

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var imm : InputMethodManager

    companion object {
        fun newInstance() : SearchFragment {
            return SearchFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )

        val adapter = FacInfoAdapter()
        binding.recyclerView.adapter = adapter

        val items = listOf(
            ServList("111", "111", 1.2, 1.2, "인크커피", "대표자", "카페", "서울 금천구 가산디지털2로 127-20 (가산동)"
                , "Y", "영업", "구분", "ID"
            ),
            ServList("111", "111", 1.2, 1.2, "투썸플레이스", "대표자", "카페", "서울 가산동 20-1 오류 243-1"
                , "Y", "영업", "구분", "ID"
            ),
            ServList("111", "111", 1.2, 1.2, "스타벅스 구로점", "대표자", "카페", "서울 구로구 35-230 오리오피스텔"
                , "Y", "영업", "구분", "ID"
            ),
            ServList("111", "111", 1.2, 1.2, "인크커피", "대표자", "카페", "서울 금천구 가산디지털2로 127-20 (가산동)"
                , "Y", "영업", "구분", "ID"
            )
        )

        adapter.updateItems(items)



        return binding.root
    }

    override fun onStart() {
        super.onStart()
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEt.requestFocus()
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
    }

}