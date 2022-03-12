package shady.samir.carsharing.data.models

data class MessageModel(
    var msgID: String,
    var senderId: String,
    val receiverID: String,
    var dataMsg: String,
    var date: String,
    var time: String,
    var isReceiver: Boolean
)