package at.sunilson.liveticker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.presentation.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val activityViewModel: MainViewModel by viewModel()
    private val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityViewModel.currentUser.observe(this, Observer {
            if (it == null) {
                Timber.d("No user logged in, trying anonymous...")
                //No user logged in, use anonymous user
                activityViewModel.anonymousLogin()
            } else {
                Timber.d("User $it is logged in")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        navigator.bind(nav_host_fragment.findNavController())
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind()
    }


}