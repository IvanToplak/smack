package hr.from.ivantoplak.smack.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import hr.from.ivantoplak.smack.R
import hr.from.ivantoplak.smack.services.AuthService
import hr.from.ivantoplak.smack.utils.errorToast
import hr.from.ivantoplak.smack.utils.hideKeyboard
import hr.from.ivantoplak.smack.utils.validateEmail
import hr.from.ivantoplak.smack.utils.validatePassword
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginSpinner.visibility = View.INVISIBLE
    }

    fun loginLoginBtnClicked(view: View) {
        hideKeyboard(view.context, currentFocus)
        enableSpinner(true)
        val email = loginEmailTxt.text.toString()
        val password = loginPasswordTxt.text.toString()

        //email and password validation
        if ((!validateEmail(this, loginEmailTxt))
                .or(!validatePassword(this, loginPasswordTxt))
        ) {
            enableSpinner(false)
            return
        }

        AuthService.loginUser(email, password) { loginSuccess ->
            if (loginSuccess) {
                AuthService.findUserByEmail(this) { findSuccess ->
                    if (findSuccess) {
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
    }

    fun loginCreateUserBtnClicked(view: View) {
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    private fun enableSpinner(enable: Boolean) {
        loginSpinner.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        loginLoginBtn.isEnabled = !enable
        loginCreateUserBtn.isEnabled = !enable
    }
}
