package at.sunilson.liveticker.home

interface HomeNavigation {
    fun shareLivetickerFromHome(viewUrl: String, editUrl: String?)
    fun createLiveticker()
    fun openLiveticker(id: String)
    fun login()
}