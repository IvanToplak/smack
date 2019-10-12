package hr.from.ivantoplak.smack.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import hr.from.ivantoplak.smack.R

/**
 * Shows toast error message to the user.
 *
 * @param context Context - who called this method
 */
fun errorToast(context: Context) {
    Toast.makeText(
        context, context.getString(R.string.something_went_wrong_please_try_again),
        Toast.LENGTH_LONG
    ).show()
}

/**
 * Hides keyboard if it is shown on the screen.
 *
 * @param context Context - who called this method
 * @param view View in focus
 */
fun hideKeyboard(context: Context, view: View?) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputManager.isAcceptingText && view != null) {
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}