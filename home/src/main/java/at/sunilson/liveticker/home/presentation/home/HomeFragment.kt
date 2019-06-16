package at.sunilson.liveticker.home.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.home.databinding.FragmentHomeBinding
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private val homeNavigation: HomeNavigation by inject()
    private val adapter: LivetickerRecyclerAdapter by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentHomeBinding>(inflater, R.layout.fragment_home, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragment_home_liveticker_list.layoutManager = LinearLayoutManager(context)
        fragment_home_liveticker_list.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 400
            setInterpolator(OvershootInterpolator())
        }
        fragment_home_liveticker_list.adapter = adapter

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                HomeViewModel.AddLiveTicker -> homeNavigation.createLiveticker()
                is HomeViewModel.ShareLiveticker -> homeNavigation.showSharingDialog()
                is HomeViewModel.OpenLiveticker -> homeNavigation.openLiveticker()
            }
        })
    }
}