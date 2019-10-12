package hr.from.ivantoplak.smack.utils

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import hr.from.ivantoplak.smack.R

fun validateEmail(context: Context, emailView: EditText): Boolean {
    val email = emailView.text.toString().trim()

    return when {
        email.isEmpty() -> {
            emailView.error = context.getString(R.string.email_address_is_required)
            false
        }
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            emailView.error = context.getString(R.string.enter_valid_email_address)
            false
        }
        else -> {
            emailView.error = null
            true
        }
    }
}

fun validatePassword(context: Context, passwordView: EditText): Boolean {
    val passwordRegex = "^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            //"(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[!\"#$%&'*+,-./:;<=>?@^_`{|}~])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 8 characters
            "$"

    val password = passwordView.text.toString().trim()

    return when {
        password.isEmpty() -> {
            passwordView.error = context.getString(R.string.password_is_required)
            false
        }
        !passwordRegex.toRegex().matches(password) -> {
            passwordView.error = context.getString(R.string.enter_valid_password)
            false
        }
        else -> {
            passwordView.error = null
            true
        }
    }
}

fun validateUserName(context: Context, usernameView: EditText): Boolean {
    val userName = usernameView.text.toString().trim()

    return when {
        userName.isEmpty() -> {
            usernameView.error = context.getString(R.string.username_is_required)
            false
        }
        userName.length > 20 -> {
            usernameView.error = context.getString(R.string.username_is_too_long)
            false
        }
        else -> {
            usernameView.error = null
            true
        }
    }
}

fun validateChannelName(context: Context, channelNameView: EditText): Boolean {
    val channelName = channelNameView.text.toString().trim()

    return when {
        channelName.isEmpty() -> {
            channelNameView.error = context.getString(R.string.channel_name_is_required)
            false
        }
        channelName.length > 30 -> {
            channelNameView.error = context.getString(R.string.channel_name_is_too_long)
            false
        }
        else -> {
            channelNameView.error = null
            true
        }
    }
}