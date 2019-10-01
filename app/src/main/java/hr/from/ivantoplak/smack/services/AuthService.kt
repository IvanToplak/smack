package hr.from.ivantoplak.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import hr.from.ivantoplak.smack.utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userEmail = ""
    var authToken = ""

    /**
     * User registration calls "account/register" endpoint to create a user in the database.
     *
     * @param context Context - who called this method
     * @param email User's email
     * @param password User's password
     * @param complete Code to run when the response arrives
     */
    fun registerUser(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val requestBody = createAuthRequestBody(email, password)

        //create a request
        val registerRequest = object : StringRequest(Method.POST, URL_REGISTER,
            Response.Listener {
                complete(true)
            },
            Response.ErrorListener { error ->
                Log.e(ERROR, "$COULD_NOT_REGISTER_USER: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF8
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        //add the request to request queue
        Volley.newRequestQueue(context).add(registerRequest)
    }

    /**
     * User login calls "account/login" endpoint to login a user and get the token.
     *
     * @param context Context - who called this method
     * @param email User's email
     * @param password User's password
     * @param complete Code to run when the response arrives
     */
    fun loginUser(
        context: Context,
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val requestBody = createAuthRequestBody(email, password)

        //create a request
        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
            Response.Listener { response ->
                try {
                    userEmail = response.getString(USER)
                    authToken = response.getString(TOKEN)
                    isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.e(JSON, "$EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.e(ERROR, "$COULD_NOT_LOGIN_USER: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF8
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        //add the request to request queue
        Volley.newRequestQueue(context).add(loginRequest)
    }

    /**
     * Creates a request body from user's credentials.
     *
     * @param email User's email
     * @param password User's password
     * @return JSON string
     */
    private fun createAuthRequestBody(email: String, password: String): String {
        val jsonBody = JSONObject()
        jsonBody.put(EMAIL, email)
        jsonBody.put(PASSWORD, password)
        return jsonBody.toString()
    }
}
