package at.sunilson.liveticker

import androidx.navigation.NavController
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.home.presentation.home.HomeFragmentDirections
import at.sunilson.liveticker.liveticker.LivetickerNavigation
import at.sunilson.liveticker.login.LoginNavigation

class Navigator : HomeNavigation, LoginNavigation, LivetickerNavigation {

    private var navController: NavController? = null

    override fun createLiveticker() {
        navController?.navigate(R.id.action_homeFragment_to_liveticker_creation_graph)
    }

    override fun openLiveticker(id: String) {
        navController?.navigate(HomeFragmentDirections.actionHomeFragmentToLivetickerGraph(id))
    }

    override fun shareLivetickerFromHome(viewUrl: String, editUrl: String?) {
        navController?.navigate(AppGraphDirections.moveToSharing(viewUrl, editUrl))
    }

    override fun shareLivetickerFromLiveticker(viewUrl: String, editUrl: String?) {
        navController?.navigate(AppGraphDirections.moveToSharing(viewUrl, editUrl))
    }

    override fun moveToHome() {
        navController?.navigate(R.id.move_to_home)
    }

    override fun login() {
        navController?.navigate(R.id.action_homeFragment_to_login_graph)
    }

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}