package at.sunilson.liveticker.home.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.home.databinding.FragmentHomeBinding
import at.sunilson.liveticker.presentation.MainViewModel
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.showConfirmationDialog
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<HomeViewModel, HomeNavigationEvent>() {

    override val viewModel: HomeViewModel by viewModel()
    private val mainViewModel: MainViewModel by sharedViewModel()
    private val homeNavigation: HomeNavigation by inject()
    private val adapter: LivetickerRecyclerAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.currentUser.observe(this, Observer { viewModel.refresh() })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentHomeBinding>(inflater, R.layout.fragment_home, container)
        binding.viewModel = viewModel
        binding.mainViewModel = mainViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(R.color.lightStatusBarColor, false)

        bar.setNavigationOnClickListener {
            //TODO Search
        }

        bar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> mainViewModel.logout()
            }

            true
        }

        fragment_home_liveticker_list.layoutManager = LinearLayoutManager(context)
        fragment_home_liveticker_list.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 400
            setInterpolator(OvershootInterpolator())
        }
        fragment_home_liveticker_list.adapter = adapter
    }

    override fun onNavigationEvent(event: HomeNavigationEvent) {
        Do exhaustive when (event) {
            HomeNavigationEvent.AddLiveTicker -> homeNavigation.createLiveticker()
            is HomeNavigationEvent.ShareLiveticker -> homeNavigation.shareLivetickerFromHome(
                event.viewUrl,
                event.editUrl
            )
            is HomeNavigationEvent.OpenLiveticker -> homeNavigation.openLiveticker(event.liveTicker.id)
            is HomeNavigationEvent.Login -> homeNavigation.login()
            is HomeNavigationEvent.DeleteLiveticker -> context?.showConfirmationDialog(
                "Delete",
                "Do you want to delete this Liveticker?",
                "Yes"
            ) { success -> if (success) viewModel.deleteLiveticker(event.liveTicker.id) }
        }
    }
}