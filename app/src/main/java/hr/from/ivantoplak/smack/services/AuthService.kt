package hr.from.ivantoplak.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import hr.from.ivantoplak.smack.controller.App
import hr.from.ivantoplak.smack.utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    /**
     * User registration calls "account/register" endpoint to create a user in the database
     * (accounts collection).
     *
     * @param email User's email
     * @param password User's password
     * @param complete Code to run when the response arrives
     */
    fun registerUser(
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
        App.prefs.requestQueue.add(registerRequest)
    }

    /**
     * User login calls "account/login" endpoint to login a user and get the token.
     *
     * @param email User's email
     * @param password User's password
     * @param complete Code to run when the response arrives
     */
    fun loginUser(
        email: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val requestBody = createAuthRequestBody(email, password)

        //create a request
        val loginRequest = object : JsonObjectRequest(Method.POST, URL_LOGIN, null,
            Response.Listener { response ->
                try {
                    App.prefs.userEmail = response.getString(USER)
                    App.prefs.authToken = response.getString(TOKEN)
                    App.prefs.isLoggedIn = true
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
        App.prefs.requestQueue.add(loginRequest)
    }

    /**
     * Create user calls "user/add" endpoint to create a user in the database (users collection).
     *
     * @param name User's name
     * @param email User's email
     * @param avatarName Avatar name (bitmap name)
     * @param avatarColor Avatar background color
     * @param complete Code to run when the response arrives
     */
    fun createUser(
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val requestBody = createUserRequestBody(name, email, avatarName, avatarColor)

        //create a request
        val createRequest = object : JsonObjectRequest(Method.POST, URL_CREATE_USER, null,
            Response.Listener { response ->
                try {
                    //TODO use GSON or Jackson to deserialize JSON
                    UserDataService.name = response.getString(NAME)
                    UserDataService.email = response.getString(EMAIL)
                    UserDataService.avatarName = response.getString(AVATAR_NAME)
                    UserDataService.avatarColor = response.getString(AVATAR_COLOR)
                    UserDataService.id = response.getString(MONGODB_ID)
                    complete(true)
                } catch (e: JSONException) {
                    Log.e(JSON, "$EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.e(ERROR, "$COULD_NOT_ADD_USER: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF8
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                return mutableMapOf<String, String>().apply {
                    this[AUTHORIZATION] = "$BEARER ${App.prefs.authToken}"
                }
            }
        }

        //add the request to request queue
        App.prefs.requestQueue.add(createRequest)
    }

    /**
     * Find by email calls "user/byEmail" endpoint to get a user from database (users collection).
     *
     * @param context Context - who called this method
     * @param complete Code to run when the response arrives
     */
    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {
        val findUserRequest =
            object : JsonObjectRequest(Method.GET, "$URL_GET_USER${App.prefs.userEmail}", null,
                Response.Listener { response ->
                    try {
                        //TODO use GSON or Jackson to deserialize JSON
                        UserDataService.name = response.getString(NAME)
                        UserDataService.email = response.getString(EMAIL)
                        UserDataService.avatarName = response.getString(AVATAR_NAME)
                        UserDataService.avatarColor = response.getString(AVATAR_COLOR)
                        UserDataService.id = response.getString(MONGODB_ID)

                        val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                        LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)
                        complete(true)
                    } catch (e: JSONException) {
                        Log.e(JSON, "$EXC: ${e.localizedMessage}")
                        complete(false)
                    }
                },
                Response.ErrorListener { error ->
                    Log.e(ERROR, "$COULD_NOT_FIND_USER: $error")
                    complete(false)
                }) {
                override fun getBodyContentType(): String {
                    return CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF8
                }

                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf<String, String>().apply {
                        this[AUTHORIZATION] = "$BEARER ${App.prefs.authToken}"
                    }
                }
            }

        //add the request to request queue
        App.prefs.requestQueue.add(findUserRequest)
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

    /**
     * Creates a request body from user's data.
     *
     * @param name User's name
     * @param email User's email
     * @param avatarName Avatar name (bitmap name)
     * @param avatarColor Avatar background color
     * @return JSON string
     */
    private fun createUserRequestBody(
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String
    ): String {
        val jsonBody = JSONObject()
        jsonBody.put(NAME, name)
        jsonBody.put(EMAIL, email)
        jsonBody.put(AVATAR_NAME, avatarName)
        jsonBody.put(AVATAR_COLOR, avatarColor)
        return jsonBody.toString()
    }
}
