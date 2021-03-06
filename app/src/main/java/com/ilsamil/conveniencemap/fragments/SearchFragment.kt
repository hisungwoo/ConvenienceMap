package com.ilsamil.conveniencemap.fragments

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.adapters.FacInfoAdapter
import com.ilsamil.conveniencemap.model.ServList

/*
    검색 프래그먼트

    공공데이터 포털 API와 연동으로 검색 기능 개발
    검색결과를 Recyclerview로 표시
    결과목록 클릭 시 검색프래그먼트를 remove 후 MainActivity의 Map에 표시

 */

class SearchFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
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
    ): View {
        mainViewModel.mainStatus.value = 2
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val adapter = FacInfoAdapter()
        binding.apply {
            searchRecyclerVw.layoutManager = LinearLayoutManager(container?.context,
                RecyclerView.VERTICAL,
                false
            )
            binding.searchRecyclerVw.adapter = adapter
        }

        // LiveData를 통해 검색 결과 표시
        mainViewModel.searchFaclLiveData.observe(this, Observer {
            if (it != null) {
                binding.searchNullTv.visibility = View.GONE
                binding.searchRecyclerVw.visibility = View.VISIBLE
                adapter.updateItems(it)
            } else {
                binding.searchRecyclerVw.visibility = View.GONE
                binding.searchNullTv.visibility = View.VISIBLE
            }

            binding.searchProgressBar.visibility = View.GONE
        })


        // 검색 결과 클릭 시 프래그먼트 remove
        adapter.setOnItemClickListener(object : FacInfoAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: ServList, pos: Int) {
                mainViewModel.movePin.value = data
                val searchFragment = activity?.supportFragmentManager?.findFragmentByTag("search")
                if (searchFragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(searchFragment)?.addToBackStack(null)?.commit()
                }
            }
        })


        binding.apply {

            // 키보드 엔터 혹은 검색버튼 클릭 시 mainViewModel 함수 호출
            searchEt.setOnKeyListener { view, i, keyEvent ->
                if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.searchEt.windowToken, 0)
                    binding.searchProgressBar.visibility = View.VISIBLE

                    val searchText = binding.searchEt.text.toString()
                    mainViewModel.getSearchFacl(searchText)

                    true
                } else {
                    false
                }
            }

            searchBackIbtn.setOnClickListener {
                mainViewModel.mainStatus.value = 1
                activity?.supportFragmentManager
                    ?.beginTransaction()
                    ?.remove(this@SearchFragment)
                    ?.commit()
            }
        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.searchEt.requestFocus()
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)
    }

}