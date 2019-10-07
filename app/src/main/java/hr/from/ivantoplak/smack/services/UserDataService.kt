package hr.from.ivantoplak.smack.services

import android.graphics.Color
import android.util.Log
import hr.from.ivantoplak.smack.controller.App
import hr.from.ivantoplak.smack.utils.EXC
import hr.from.ivantoplak.smack.utils.SCANNER
import java.util.*

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    fun returnAvatarColor(components: String): Int {
        val strippedColor = components
            .replace("[", "")
            .replace("]", "")
            .replace(",", "")

        var red = 0
        var green = 0
        var blue = 0

        val scanner = Scanner(strippedColor)
        scanner.use {
            try {
                if (it.hasNext()) {
                    red = (it.nextDouble() * 255).toInt()
                    green = (it.nextDouble() * 255).toInt()
                    blue = (it.nextDouble() * 255).toInt()
                } else {
                    red = 0
                    green = 0
                    blue = 0
                }
            } catch (e: Exception) {
                Log.e(SCANNER, "$EXC: ${e.localizedMessage}")
            }
        }
        return Color.rgb(red, green, blue)
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.prefs.authToken = ""
        App.prefs.userEmail = ""
        App.prefs.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }
}