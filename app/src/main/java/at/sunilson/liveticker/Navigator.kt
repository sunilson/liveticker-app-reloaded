package at.sunilson.liveticker

import androidx.navigation.NavController
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.login.LoginNavigation

class Navigator : HomeNavigation, LoginNavigation {

    private var navController: NavController? = null

    override fun createLiveticker() {
        navController?.navigate(R.id.move_to_liveticker_creation)
    }

    override fun openLiveticker() {
        navController?.navigate(R.id.move_to_liveticker)
    }

    override fun showSharingDialog() {
        navController?.navigate(R.id.move_to_sharing)
    }

    override fun moveToHome() {
        navController?.navigate(R.id.move_to_home)
    }

    override fun login() {
        navController?.navigate(R.id.move_to_login)
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}