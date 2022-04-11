package com.ilsamil.conveniencemap.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.adapters.ListFacInfoAdapter
import com.ilsamil.conveniencemap.databinding.FragmentListBinding
import com.ilsamil.conveniencemap.model.ServList

/*
    목록보기 프래그먼트

    MainViewModel에서 시설정보 Array를 받아
    RecyclerView 사용하여 목록 표시

 */

class ListFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentListBinding
    private var mapServList = arrayListOf<ServList>()

    private var categoryStatus = 0

    // Category.newInstance() 사용을 위해 생성
    companion object {
        fun newInstance() : ListFragment {
            return ListFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel.mainStatus.value = 4
        binding = FragmentListBinding.inflate(inflater, container, false)

        val cggNm: String = mainViewModel.mapCggNm
        mapServList = mainViewModel.mapServList

        val listCnt = mapServList.size
        binding.listLocationTv.text = cggNm
        binding.listCount.text = "총 ${listCnt.toString()} 건"

        val adapter = ListFacInfoAdapter()
        binding.listRecyclerview.layoutManager = LinearLayoutManager(container?.context,
            RecyclerView.VERTICAL,
            false
        )
        binding.listRecyclerview.adapter = adapter
        adapter.updateItems(mapServList)
        mainViewModel.getLocationListFacl(cggNm)
        binding.listRecyclerview.smoothScrollToPosition(0)
        itemClick(adapter)


        binding.apply {
            listShopBtn.setOnClickListener {
                categoryClick(1)
            }

            listLivingBtn.setOnClickListener {
                categoryClick(2)
            }

            listEducationBtn.setOnClickListener {
                categoryClick(3)
            }

            listPublicBtn.setOnClickListener {
                categoryClick(4)
            }

            listBackImg.setOnClickListener {
                val listFragment = activity?.supportFragmentManager?.findFragmentByTag("list")
                if (listFragment != null) {
                    mainViewModel.mainStatus.value = 1
                    activity?.supportFragmentManager?.beginTransaction()?.remove(listFragment)?.commit()
                }
            }

            listMoveTopBtn.setOnClickListener {
                binding.listRecyclerview.smoothScrollToPosition(0)
            }
        }

        return binding.root
    }

    private fun categoryClick(btn : Int) {
        listCategoryClear()
        val listAdapter = ListFacInfoAdapter()

        if (btn == categoryStatus) {
            categoryStatus = 0
            binding.apply {
                listRecyclerview.adapter = listAdapter
                listAdapter.updateItems(mapServList)
                listCount.text = "총 ${mapServList.size} 건"
            }
        } else {
            when(btn) {
                1 -> {
                    val shopServList = mainViewModel.shopServList
                    categoryStatus = 1
                    binding.apply {
                        listRecyclerview.adapter = listAdapter
                        listAdapter.updateItems(shopServList)
                        listShopBtn.apply {
                            setBackgroundResource(R.drawable.button_list_category_click)
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(Color.WHITE)
                        }
                        listCount.text = "총 ${shopServList.size} 건"
                    }
                }
                2 -> {
                    val livingServList = mainViewModel.livingServList
                    categoryStatus = 2
                    binding.apply {
                        listRecyclerview.adapter = listAdapter
                        listAdapter.updateItems(livingServList)
                        listLivingBtn.apply {
                            setBackgroundResource(R.drawable.button_list_category_click)
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(Color.WHITE)
                        }
                        listCount.text = "총 ${livingServList.size} 건"
                    }
                }
                3 -> {
                    val educationServList = mainViewModel.educationServList
                    categoryStatus = 3
                    binding.apply {
                        listRecyclerview.adapter = listAdapter
                        listAdapter.updateItems(educationServList)
                        listEducationBtn.apply {
                            setBackgroundResource(R.drawable.button_list_category_click)
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(Color.WHITE)
                        }
                        listCount.text = "총 ${educationServList.size} 건"
                    }
                }
                4 -> {
                    val publicServList = mainViewModel.publicServList
                    categoryStatus = 4
                    binding.apply {
                        listRecyclerview.adapter = listAdapter
                        listAdapter.updateItems(publicServList)
                        listPublicBtn.apply {
                            setBackgroundResource(R.drawable.button_list_category_click)
                            setTypeface(null, Typeface.BOLD)
                            setTextColor(Color.WHITE)
                        }
                        listCount.text = "총 ${publicServList.size} 건"
                    }
                }
            }
        }
        itemClick(listAdapter)
    }

    private fun listCategoryClear() {
        binding.apply {
            listShopBtn.setBackgroundResource(R.color.button_transparency)
            listShopBtn.setTypeface(null, Typeface.NORMAL)
            listShopBtn.setTextColor(ContextCompat.getColor(context!!, R.color.list_category_text))

            listLivingBtn.setBackgroundResource(R.color.button_transparency)
            listLivingBtn.setTypeface(null, Typeface.NORMAL)
            listLivingBtn.setTextColor(ContextCompat.getColor(context!!, R.color.list_category_text))

            listEducationBtn.setBackgroundResource(R.color.button_transparency)
            listEducationBtn.setTypeface(null, Typeface.NORMAL)
            listEducationBtn.setTextColor(ContextCompat.getColor(context!!, R.color.list_category_text))

            listPublicBtn.setBackgroundResource(R.color.button_transparency)
            listPublicBtn.setTypeface(null, Typeface.NORMAL)
            listPublicBtn.setTextColor(ContextCompat.getColor(context!!, R.color.list_category_text))
        }
    }

    private fun itemClick(adapter : ListFacInfoAdapter) {
        adapter.setOnItemClickListener(object : ListFacInfoAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: ServList, pos : Int) {
                mainViewModel.movePin.value = data
                val listFragment = activity?.supportFragmentManager?.findFragmentByTag("list")
                if (listFragment != null) {
                    activity?.supportFragmentManager?.beginTransaction()?.remove(listFragment)?.addToBackStack(null)?.commit()
                }
            }
        })
    }


}