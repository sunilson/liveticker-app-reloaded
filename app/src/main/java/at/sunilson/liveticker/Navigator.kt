package at.sunilson.liveticker

import androidx.navigation.NavController

class Navigator {

    private var navController: NavController? = null


    fun bind(navController: NavController) {
        this.navController = navController
    }

    fun unbind() {
        navController = null
    }
}