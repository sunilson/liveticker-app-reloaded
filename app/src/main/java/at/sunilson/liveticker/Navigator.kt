package at.sunilson.liveticker

import androidx.navigation.NavController
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.liveticker.LivetickerNavigation
import at.sunilson.liveticker.login.LoginNavigation

class Navigator : HomeNavigation, LoginNavigation, LivetickerNavigation {

    private var navController: NavController? = null

    override fun createLiveticker() {
        navController?.navigate(R.id.move_to_liveticker_creation)
    }

    override fun openLiveticker(id: String) {
        navController?.navigate(AppGraphDirections.moveToLiveticker(id))
    }

    override fun shareLivetickerFromHome() {
        navController?.navigate(R.id.move_to_sharing)
    }

    override fun moveToHome() {
        navController?.navigate(R.id.move_to_home)
    }

    override fun login() {
        navController?.navigate(R.id.move_to_login)
    }

    override fun shareLivetickerFromLiveticker() {
        navController?.navigate(R.id.move_to_sharing)
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}