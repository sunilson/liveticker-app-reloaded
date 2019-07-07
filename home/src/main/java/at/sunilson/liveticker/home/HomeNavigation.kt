package at.sunilson.liveticker.home

interface HomeNavigation {
    fun shareLivetickerFromHome()
    fun createLiveticker()
    fun openLiveticker(id: String)
    fun login()
}