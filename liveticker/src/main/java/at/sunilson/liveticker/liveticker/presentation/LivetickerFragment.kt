package at.sunilson.liveticker.liveticker.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.FragmentLivetickerBinding
import at.sunilson.liveticker.location.MapFragmentCreator
import at.sunilson.liveticker.location.MapOptions
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.dialogs.inputDialog.InputDialog
import at.sunilson.liveticker.presentation.interfaces.InputDialogListener
import at.sunilson.liveticker.presentation.views.LockableBottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator
import kotlinx.android.synthetic.main.fragment_liveticker.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import kotlin.math.min

class LivetickerFragment : BaseFragment<LivetickerViewModel>(), InputDialogListener {

    override val viewModel: LivetickerViewModel by viewModel()
    private val adapter: LivetickerRecyclerAdapter by inject()
    private val commentsRecyclerAdapter: CommentsRecyclerAdapter by inject()
    private val mapFragmentCreator: MapFragmentCreator by inject()
    private val arguments: LivetickerFragmentArgs by navArgs()

    private val bottomSheetBehavior: LockableBottomSheetBehavior<ConstraintLayout> by lazy {
        BottomSheetBehavior.from(liveticker_bottom_sheet) as LockableBottomSheetBehavior
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadLiveticker(arguments.id)

        activity?.onBackPressedDispatcher?.addCallback(this, enabled = true) {
            if (bottomSheetBehavior.state != STATE_COLLAPSED && bottomSheetBehavior.state != STATE_HIDDEN) {
                bottomSheetBehavior.state = STATE_COLLAPSED
            } else {
                findNavController().popBackStack()
            }
        }
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

        comment_list.adapter = commentsRecyclerAdapter

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
                    bottom_sheet_backdrop.alpha = min(0.75f, slideOffset)
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    viewModel.loadComments()
                }
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            if (!isStateSaved && isAdded) {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.liveticker_preview_container, mapFragmentCreator(MapOptions(true)))
                    .commit()
            }
        }
    }

    override fun inputHappened(string: String) {
        Timber.d("Got input from dialog $string")
        viewModel.addComment(string)
    }

    override fun onNavigationEvent(event: NavigationEvent) {
        when (event) {
            is LivetickerViewModel.AddComment -> {
                InputDialog.newInstance("Kommentar hinzufügen", "Kommentar eingeben", "Hinzufügen")
                    .show(childFragmentManager, null)
            }
        }
    }
}