package hr.from.ivantoplak.smack.utils

import android.content.Context
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