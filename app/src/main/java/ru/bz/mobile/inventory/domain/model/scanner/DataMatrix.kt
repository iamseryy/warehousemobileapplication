package ru.bz.mobile.inventory.domain.model.scanner


data class DataMatrix(
    var cwar: String?,
    var loca: String?,
    var item: String?,
    var clot: String?
) {
    fun isNotEmpty():Boolean = !cwar.isNullOrEmpty() || !loca.isNullOrEmpty() || !item.isNullOrEmpty() || !clot.isNullOrEmpty()
}
class DataMatrixException : Exception() {

}