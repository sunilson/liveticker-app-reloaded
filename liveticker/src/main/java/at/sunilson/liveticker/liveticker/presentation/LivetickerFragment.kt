package at.sunilson.liveticker.liveticker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.widget.ConstraintLayout
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.FragmentLivetickerBinding
import at.sunilson.liveticker.location.MapFragmentCreator
import at.sunilson.liveticker.location.MapOptions
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.views.LockableBottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import kotlinx.android.synthetic.main.fragment_liveticker.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LivetickerFragment : BaseFragment<LivetickerViewModel>() {

    override val viewModel: LivetickerViewModel by viewModel()
    private val adapter: LivetickerRecyclerAdapter by inject()
    private val mapFragmentCreator: MapFragmentCreator by inject()

    private val bottomSheetBehavior: LockableBottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(liveticker_bottom_sheet) as LockableBottomSheetBehavior
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentLivetickerBinding>(inflater, R.layout.fragment_liveticker, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBarColor(R.color.statusBarColor)

        liveticker_list.adapter = adapter.apply {
            //TODO
            addAll(listOf("", "", "", "", "", "", "", "", "", "", "", ""))
        }

        //TODO Viewmodel
        liveticker_fab.setOnClickListener {
            liveticker_motionlayout.transitionToEnd()
            liveticker_list.smoothScrollToPosition(adapter.data.size - 1)
        }

        bottomSheetBehavior.state = STATE_HIDDEN
        bottomSheetBehavior.locked = true
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset >= 0) {
                    bottom_sheet_backdrop.alpha = Math.min(0.5f, slideOffset)
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {}
        })

        liveticker_motionlayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun allowsTransition(p0: MotionScene.Transition?) = true
            override fun onTransitionStarted(p0: MotionLayout?, start: Int, end: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, start: Int, end: Int, progress: Float) {}
            override fun onTransitionCompleted(p0: MotionLayout?, state: Int) {
                if (state == liveticker_motionlayout.endState) {
                    bottomSheetBehavior.state = STATE_COLLAPSED
                    liveticker_fab.hide()
                    bottomSheetBehavior.locked = false
                    bottomSheetBehavior.isHideable = false
                } else {
                    bottomSheetBehavior.isHideable = true
                    bottomSheetBehavior.state = STATE_HIDDEN
                    liveticker_fab.show()
                    bottomSheetBehavior.locked = true
                }
            }
        })

        if (!isStateSaved && isAdded) {
            childFragmentManager
                .beginTransaction()
                .replace(R.id.liveticker_preview_container, mapFragmentCreator(MapOptions(true)))
                .commit()
        }

        bla.setOnClickListener { }
    }
}