package shady.samir.carsharing.data.models

data class User (
    val  id: String? =null,
    var userName:String?=null,
    var userImg: String? =null,
    var userEmail:String?=null,
    var userPhone:String?=null,
    var haveCar:Boolean?= false,
)