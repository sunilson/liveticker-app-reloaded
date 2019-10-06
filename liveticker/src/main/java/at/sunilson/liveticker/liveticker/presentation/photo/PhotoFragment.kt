package at.sunilson.liveticker.liveticker.presentation.photo

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.FragmentPhotoBinding
import at.sunilson.liveticker.liveticker.presentation.UploadViewModel
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotoFragment : BaseFragment<PhotoViewModel, PhotoNavigationEvent>() {

    override val viewModel: PhotoViewModel by viewModel()
    private val uploadViewModel: UploadViewModel by sharedViewModel()
    private val args: PhotoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadImage(Uri.parse(args.uri))
    }

    override fun onResume() {
        super.onResume()
        setNavColors(transparent = true)
    }

    override fun onNavigationEvent(event: PhotoNavigationEvent) {
        Do exhaustive when (event) {
            is PhotoNavigationEvent.UploadImage -> {
                uploadViewModel.uploadImageToLiveticker(args.livetickerId, event.uri, "")
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = generateBinding<FragmentPhotoBinding>(
            inflater,
            R.layout.fragment_photo,
            container
        )
        binding.viewModel = viewModel
        return binding.root
    }
}