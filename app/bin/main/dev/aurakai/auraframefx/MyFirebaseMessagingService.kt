package dev.aurakai.auraframefx

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        // Handle FCM messages here
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        // Handle token refresh here
        super.onNewToken(token)
    }
}
