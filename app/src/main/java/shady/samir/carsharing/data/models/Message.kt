package shady.samir.carsharing.data.models

import java.util.*

data class Message (
    var msgID: String ?=null,
    var senderId: String ?=null,
    val receiverID: String ?=null,
    var dataMsg: String ?=null,
    var date: Date = Date(),
)