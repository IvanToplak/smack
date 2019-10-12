package hr.from.ivantoplak.smack.controller

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
        createUserSpinner.visibility = View.INVISIBLE
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
        enableSpinner(true)
        val userName = createUserUserNameTxt.text.toString()
        val email = createUserEmailTxt.text.toString()
        val password = createUserPasswordTxt.text.toString()

        //username, email and password validation
        if ((!validateUserName(this, createUserUserNameTxt))
                .or(!validateEmail(this, createUserEmailTxt))
                .or(!validatePassword(this, createUserPasswordTxt))
        ) {
            enableSpinner(false)
            return
        }

        AuthService.registerUser(
            email,
            password
        ) { registerSuccess ->
            if (registerSuccess) {
                AuthService.loginUser(email, password) { loginSuccess ->
                    if (loginSuccess) {
                        AuthService.createUser(
                            userName,
                            email,
                            userAvatar,
                            avatarColor
                        ) { createSuccess ->
                            if (createSuccess) {
                                //inform receivers that user is logged in
                                val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                                LocalBroadcastManager.getInstance(this)
                                    .sendBroadcast(userDataChange)

                                enableSpinner(false)
                                finish()
                            } else {
                                errorToast(this)
                                enableSpinner(false)
                            }
                        }
                    } else {
                        errorToast(this)
                        enableSpinner(false)
                    }
                }
            } else {
                errorToast(this)
                enableSpinner(false)
            }
        }
    }

    private fun enableSpinner(enable: Boolean) {
        createUserSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        createUserBtn.isEnabled = !enable
        createUserAvatarImageView.isEnabled = !enable
        avatarBackgroundColorBtn.isEnabled = !enable
    }
}
