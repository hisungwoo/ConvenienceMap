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
import net.daum.mf.map.api.MapPOIItem

class ListFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentListBinding

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
        mainViewModel.mainStatus.value = 4
        binding = FragmentListBinding.inflate(inflater, container, false)

        val cggNm: String = mainViewModel.mapCggNm
        val shopServList = mainViewModel.shopServList as List<ServList>
        val livingServList = mainViewModel.livingServList as List<ServList>
        val educationServList = mainViewModel.educationServList as List<ServList>
        val publicServList = mainViewModel.publicServList as List<ServList>
        val mapServList = mainViewModel.mapServList as List<ServList>
        val listCnt = mapServList.size
        binding.locationTv.text = cggNm
        binding.listCount.text = "총 " + listCnt.toString() + "건"

        val adapter = ListFacInfoAdapter()
        binding.listRecyclerview.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )
        binding.listRecyclerview.adapter = adapter
        adapter.updateItems(mapServList)
        mainViewModel.getLocationListFacl(cggNm)

        adapter.setOnItemClickListener(object : ListFacInfoAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: ServList, pos : Int) {
                mainViewModel.movePin.value = data
                val listFragment = activity?.supportFragmentManager?.findFragmentByTag("list")
                if (listFragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(listFragment)?.addToBackStack(null)?.commit()
                }
            }
        })

        binding.apply {
            listShopBtn.setOnClickListener {
                val listAdapter = ListFacInfoAdapter()
                binding.listRecyclerview.adapter = listAdapter
                listAdapter.updateItems(shopServList)
            }

            listLivingBtn.setOnClickListener {
                val listAdapter = ListFacInfoAdapter()
                binding.listRecyclerview.adapter = listAdapter
                listAdapter.updateItems(livingServList)
            }

            listEducationBtn.setOnClickListener {
                val listAdapter = ListFacInfoAdapter()
                binding.listRecyclerview.adapter = listAdapter
                listAdapter.updateItems(educationServList)
            }

            listPublicBtn.setOnClickListener {
                val listAdapter = ListFacInfoAdapter()
                binding.listRecyclerview.adapter = listAdapter
                listAdapter.updateItems(publicServList)
            }
        }

        return binding.root
    }


}