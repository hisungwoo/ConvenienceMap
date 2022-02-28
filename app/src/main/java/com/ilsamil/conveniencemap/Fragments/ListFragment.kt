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
        mainViewModel.mainStatus.value = 4
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

        val shopServList = bundle?.getSerializable("shopServList") as List<ServList>
        val livingServList = bundle?.getSerializable("livingServList") as List<ServList>
        val educationServList = bundle?.getSerializable("educationServList") as List<ServList>
        val hospitalServList = bundle?.getSerializable("hospitalServList") as List<ServList>
        val publicServList = bundle?.getSerializable("publicServList") as List<ServList>

        val mapServList = bundle?.getSerializable("mapServList") as List<ServList>

        val adapter = ListFacInfoAdapter()
        binding.listRecyclerview.adapter = adapter
        adapter.updateItems(mapServList)


        val listCnt = mapServList.size
        binding.locationTv.text = cggNm
        binding.listCount.text = "총 " + listCnt.toString() + "건"


//        val adapter = ListFacInfoAdapter()
//        binding.listRecyclerview.adapter = adapter
//        mainViewModel.locationFaclListLiveData.observe(this, Observer {
//            adapter.updateItems(it)
//        })





        binding.listBackImg.setOnClickListener {
            val listFragment = activity?.supportFragmentManager?.findFragmentByTag("list")
            if (listFragment != null) {
                activity?.supportFragmentManager?.beginTransaction()?.remove(listFragment)?.addToBackStack(null)?.commit()
                mainViewModel.mainStatus.value = 1
            }
        }

        adapter.setOnItemClickListener(object : ListFacInfoAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ServList, pos : Int) {
                mainViewModel.movePin.value = data
                val listFragment = activity?.supportFragmentManager?.findFragmentByTag("list")
                if (listFragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(listFragment)?.addToBackStack(null)?.commit()
                }

            }

        })

        binding.listShopBtn.setOnClickListener {
            val adapter = ListFacInfoAdapter()
            binding.listRecyclerview.adapter = adapter
            adapter.updateItems(shopServList)
        }

        binding.listLivingBtn.setOnClickListener {
            val adapter = ListFacInfoAdapter()
            binding.listRecyclerview.adapter = adapter
            adapter.updateItems(livingServList)
        }

        binding.listEducationBtn.setOnClickListener {
            val adapter = ListFacInfoAdapter()
            binding.listRecyclerview.adapter = adapter
            adapter.updateItems(educationServList)
        }

        binding.listPublicBtn.setOnClickListener {
            val adapter = ListFacInfoAdapter()
            binding.listRecyclerview.adapter = adapter
            adapter.updateItems(publicServList)
        }





        return binding.root
    }


}