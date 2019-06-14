package at.sunilson.liveticker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val authenticationRepository: IAuthenticationRepository by inject()
    private val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authenticationRepository.currentUser.observe(this, Observer {
            if (it == null) {
                //TODO
            } else {

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