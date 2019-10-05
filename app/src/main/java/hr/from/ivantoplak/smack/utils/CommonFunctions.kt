package hr.from.ivantoplak.smack.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

/**
 * Shows toast error message to the user.
 *
 * @param context Context - who called this method
 */
fun errorToast(context: Context) {
    Toast.makeText(
        context, SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN,
        Toast.LENGTH_LONG
    ).show()
}

fun hideKeyboard(context: Context, view: View?) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputManager.isAcceptingText) {
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}