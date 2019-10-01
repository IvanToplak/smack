package hr.from.ivantoplak.smack.controller

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hr.from.ivantoplak.smack.R
import hr.from.ivantoplak.smack.services.AuthService
import hr.from.ivantoplak.smack.utils.*
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlin.random.Random

class CreateUserActivity : AppCompatActivity() {

    private var userAvatar = AVATAR_DEFAULT_DRAWABLE
    private var avatarColor = AVATAR_DEFAULT_BACKGROUND_COLOR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
    }

    fun generateUserAvatar(view: View) {
        val color = Random.nextBoolean()
        val avatar = (AVATAR_MIN..AVATAR_MAX).random()

        userAvatar =
            if (color) "$AVATAR_DRAWABLE_PREFIX_LIGHT$avatar" else "$AVATAR_DRAWABLE_PREFIX_DARK$avatar"
        val resourceId = resources.getIdentifier(userAvatar, RES_DRAWABLE, packageName)
        createUserAvatarImageView.setImageResource(resourceId)
    }

    fun generateBackgroundColorBtnClicked(view: View) {
        val colorRange = (COLOR_MIN..COLOR_MAX)
        val red = colorRange.random()
        val green = colorRange.random()
        val blue = colorRange.random()

        createUserAvatarImageView.setBackgroundColor(Color.rgb(red, green, blue))
        val savedRed = red.toDouble() / COLOR_MAX
        val savedGreen = green.toDouble() / COLOR_MAX
        val savedBlue = blue.toDouble() / COLOR_MAX

        avatarColor = "[$savedRed, $savedGreen, $savedBlue, $COLOR_DEFAULT_ALPHA]"
    }

    fun createUserBtnClicked(view: View) {
        //TODO replace dummy values with real ones
        AuthService.registerUser(
            this,
            "j@j1.com",
            "123456"
        ) { completed -> println("###Completed: $completed") }
    }
}
