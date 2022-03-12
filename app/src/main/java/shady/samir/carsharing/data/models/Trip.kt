package shady.samir.carsharing.data.models

data class Trip(
    val id: String ?=null,
    var tripTitle: String ?=null,
    var tripDesc: String ?=null,
    var tripPrice: Int ?=null,
    var carModel: String ?=null,
    var numberSeats: Int ?=null,
    var tripImage: String ?=null,
    var userId: String ?=null,
    var goTime: String ?=null,
    var backTime: String ?=null,
    var joined:Int?=0,
)