package com.ilsamil.conveniencemap.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.adapters.ListFacInfoAdapter
import com.ilsamil.conveniencemap.databinding.FragmentListBinding
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding
import com.ilsamil.conveniencemap.model.ServList

class ListFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentListBinding
    private lateinit var cggNm: String

    // Category.newInstance() 사용을 위해 생성
    companion object {
        fun newInstance() : ListFragment {
            return ListFragment()
        }
    }

    // 메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 프레그먼트를 안고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    // 레이아웃과 연결 (액티비티의 setContentView와 같음)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.listRecyclerview.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )

        val bundle = arguments
        cggNm = if(bundle != null) {
            bundle.getString("cggNm").toString()
        } else {
            ""
        }


        val adapter = ListFacInfoAdapter()
        binding.listRecyclerview.adapter = adapter

        mainViewModel.locationFaclListLiveData.observe(this, Observer {
            adapter.updateItems(it)
        })

        mainViewModel.getLocationListFacl(cggNm)


        adapter.setOnItemClickListener(object : ListFacInfoAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ServList, pos : Int) {
                mainViewModel.movePin.value = data
                val categoryFragment = activity?.supportFragmentManager?.findFragmentByTag("category")
                if (categoryFragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(categoryFragment)?.addToBackStack(null)?.commit()
                }
            }

        })



        return binding.root


    }





}