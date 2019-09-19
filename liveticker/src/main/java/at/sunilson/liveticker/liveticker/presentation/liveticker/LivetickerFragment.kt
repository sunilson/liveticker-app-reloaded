package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.EXTRA_OUTPUT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.activity.addCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.sunilson.liveticker.core.models.TextLiveTickerEntry
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.liveticker.LivetickerNavigation
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.FragmentLivetickerBinding
import at.sunilson.liveticker.liveticker.presentation.comments.CommentsRecyclerAdapter
import at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView.LivetickerEntryRecyclerAdapter
import at.sunilson.liveticker.location.MapFragmentCreator
import at.sunilson.liveticker.location.MapOptions
import at.sunilson.liveticker.presentation.*
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.dialogs.inputDialog.InputDialog
import at.sunilson.liveticker.presentation.interfaces.InputDialogListener
import at.sunilson.liveticker.presentation.views.LockableBottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import kotlinx.android.synthetic.main.fragment_liveticker.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class LivetickerFragment : BaseFragment<LivetickerViewModel, LivetickerNavigationEvent>(),
    InputDialogListener {

    override val viewModel: LivetickerViewModel by viewModel()
    private val livetickerNavigation: LivetickerNavigation by inject()
    private val mapFragmentCreator: MapFragmentCreator by inject()
    private val arguments: LivetickerFragmentArgs by navArgs()
    private var currentImageUri: Uri? = null

    private val commentsBehavior: LockableBottomSheetBehavior<ConstraintLayout>
        get() = from(liveticker_bottom_sheet) as LockableBottomSheetBehavior

    private val addBehavior: LockableBottomSheetBehavior<ConstraintLayout>
        get() = from(liveticker_add_bottom_sheet) as LockableBottomSheetBehavior
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            arguments.sharingId != null -> viewModel.loadLivetickerFromShareUrl(arguments.sharingId!!)
            arguments.id != null -> viewModel.loadLiveticker(arguments.id!!)
            else -> error("One of the given arguments must be set!")
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, enabled = true) {
            when {
                addBehavior.state == STATE_EXPANDED -> addBehavior.state = STATE_COLLAPSED
                addBehavior.state == STATE_COLLAPSED -> addBehavior.state = STATE_HIDDEN
                commentsBehavior.state != STATE_COLLAPSED && commentsBehavior.state != STATE_HIDDEN -> commentsBehavior.state =
                    STATE_HIDDEN
                fab_motion_layout.currentState != fab_motion_layout.startState -> fab_motion_layout.transitionToStart()
                else -> if (!findNavController().popBackStack()) {
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (!enter) {
            liveticker_container.apply { setPadding(0, 0, 0, 0) }
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onResume() {
        super.onResume()
        setNavColors(
            statusColor = R.color.colorPrimary,
            transparent = true,
            onlyNavTransparent = true
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = generateBinding<FragmentLivetickerBinding>(
            inflater,
            R.layout.fragment_liveticker,
            container
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO In viewmodel
        back_button.setOnClickListener { activity?.onBackPressed() }

        initializeEntryList()
        initializeCommentList()
        initializeImageList()
        initializeMap()
        initializeMainMotionLayout()
        initializeCommentsBottomSheet()
        initializeAddBottomSheet()
        initializeInsets()
    }

    override fun inputHappened(string: String) {
        Timber.d("Got input from dialog $string")
        viewModel.addComment(string)
    }

    override fun onNavigationEvent(event: LivetickerNavigationEvent) {
        Do exhaustive when (event) {
            is LivetickerNavigationEvent.AddComment -> {
                InputDialog.newInstance("Kommentar hinzufügen", "Kommentar eingeben", "Hinzufügen")
                    .show(childFragmentManager, null)
            }
            is LivetickerNavigationEvent.Share -> livetickerNavigation.shareLivetickerFromLiveticker(
                event.viewUrl,
                event.editUrl
            )
            is LivetickerNavigationEvent.ShowComments -> {
                hideFabs()
                addBehavior.state = STATE_HIDDEN
                commentsBehavior.state = STATE_EXPANDED
            }
            is LivetickerNavigationEvent.AddEntry -> {
                hideFabs()
                checkImagePermissions()
            }
            is LivetickerNavigationEvent.TakePicture -> takeImage(event.imageUri)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_INTENT && resultCode == RESULT_OK) {
            findNavController().navigate(LivetickerFragmentDirections.moveToPhoto(currentImageUri.toString()))
        }
    }

    private fun takeImage(fileUri: Uri) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            if (it.canBeHandled(requireContext())) {
                it.putExtra(EXTRA_OUTPUT, fileUri)
                safeStartActivityForResult(it, IMAGE_INTENT)
                currentImageUri = fileUri
            }
        }
    }

    private fun checkImagePermissions() {
        if (requireContext().hasPermissions(READ_EXTERNAL_STORAGE)) {
            addBehavior.state = STATE_COLLAPSED
            viewModel.loadImages()
        } else {
            requestPermissions(
                arrayOf(READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST) {
            addBehavior.state = STATE_COLLAPSED
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.loadImages()
            }
        }
    }

    private fun initializeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(requireView()) { v, insets ->
            liveticker_container.apply { setPadding(0, insets.systemWindowInsetTop, 0, 0) }
            liveticker_fab.setMargins(
                bottom = insets.systemWindowInsetBottom + 8.convertToPx(
                    requireContext()
                )
            )
            fab_motion_layout.apply { setPadding(0, insets.systemWindowInsetBottom, 0, 0) }
            insets
        }
    }

    private fun initializeImageList() {
        image_list.adapter = ImageRecyclerAdapter {
            findNavController().navigate(LivetickerFragmentDirections.moveToPhoto(it.uri.toString()))
        }
    }

    private fun initializeCommentList() {
        comment_list.adapter = CommentsRecyclerAdapter {}
    }

    private fun initializeEntryList() {
        liveticker_list.adapter = LivetickerEntryRecyclerAdapter {}
        liveticker_list.setEntries(
            listOf(
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", ""),
                TextLiveTickerEntry("", 0L, "", "")
            )
        )
    }

    private fun initializeMap() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            if (!isStateSaved && isAdded) {
                childFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0)
                    .replace(
                        R.id.liveticker_preview_container,
                        mapFragmentCreator(MapOptions(true))
                    )
                    .commit()
            }
        }
    }

    private fun initializeMainMotionLayout() {
        liveticker_motionlayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            override fun allowsTransition(p0: MotionScene.Transition?) = true
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                hideFabs()
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}
        })
    }

    private fun initializeAddBottomSheet() {
        addBehavior.state = STATE_HIDDEN
        addBehavior.locked = false
    }

    private fun initializeCommentsBottomSheet() {
        commentsBehavior.state = STATE_HIDDEN
        commentsBehavior.locked = false
    }

    private fun hideFabs() {
        if (fab_motion_layout.currentState == fab_motion_layout.endState) {
            fab_motion_layout.transitionToStart()
        }
    }

    companion object {
        const val PERMISSION_REQUEST = 234
        const val IMAGE_INTENT = 345
    }
}