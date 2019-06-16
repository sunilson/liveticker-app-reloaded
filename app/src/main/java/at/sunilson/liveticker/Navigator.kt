package at.sunilson.liveticker

import androidx.navigation.NavController
import at.sunilson.liveticker.home.HomeNavigation

class Navigator : HomeNavigation {

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

    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}