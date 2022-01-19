package com.ilsamil.conveniencemap.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilsamil.conveniencemap.MainViewModel
import com.ilsamil.conveniencemap.R
import com.ilsamil.conveniencemap.databinding.FragmentDetailBinding
import com.ilsamil.conveniencemap.databinding.FragmentSearchBinding

class DetailFragment : Fragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentDetailBinding

    companion object {
        fun newInstance() : DetailFragment {
            return DetailFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)



        mainViewModel.detailLiveData.observe( this, Observer {
            Log.d("ttest", "디테일 옵저버")

            binding.detailFaclNmTv.text = it.faclNm
            binding.detailLcMnadTv.text = it.lcMnad
            binding.detailEstbDateTv.text = it.estbDate
            binding.detailStaTv.text = it.salStaNm
            binding.detailTypeTv.text = it.faclTyCd.toString()
            binding.detailRprnTv.text = it.faclRprnNm
            binding.detailEvalInfoTv.text = it.evalInfo
        })



        return binding.root
    }

}



















