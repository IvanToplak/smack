package hr.from.ivantoplak.smack.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import hr.from.ivantoplak.smack.controller.App
import hr.from.ivantoplak.smack.model.Channel
import hr.from.ivantoplak.smack.model.Message
import hr.from.ivantoplak.smack.utils.*
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()
    val messages = ArrayList<Message>()

    /**
     * Get channels calls "/channels" endpoint to get all channels from database (channels collection)
     *
     * @param complete Code to run when the response arrives
     */
    fun getChannels(complete: (Boolean) -> Unit) {

        val channelsRequest = object : JsonArrayRequest(Method.GET, URL_GET_CHANNELS, null,
            Response.Listener { response ->
                try {
                    //TODO use GSON or Jackson to deserialize JSON
                    for (i in 0 until response.length()) {
                        val channel = response.getJSONObject(i)
                        val name = channel.getString(NAME)
                        val description = channel.getString(DESCRIPTION)
                        val id = channel.getString(MONGODB_ID)

                        val newChannel = Channel(name, description, id)
                        channels.add(newChannel)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    Log.e(JSON, "$EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.e(ERROR, "$COULD_NOT_FIND_CHANNELS: $error")
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
        App.prefs.requestQueue.add(channelsRequest)
    }

    /**
     * Get messages calls "/message/byChannel/" endpoint to get all messages for specific channel
     * from database (messages collection).
     *
     * @param channelId ID of the selected channel
     * @param complete Code to run when the response arrives
     */
    fun getMessages(channelId: String, complete: (Boolean) -> Unit) {

        val url = "$URL_GET_MESSAGES$channelId"

        val messagesRequest = object : JsonArrayRequest(Method.GET, url, null,
            Response.Listener { response ->
                clearMessages()
                try {
                    //TODO use GSON or Jackson to deserialize JSON
                    for (i in 0 until response.length()) {
                        val message = response.getJSONObject(i)
                        val messageBody = message.getString(MESSAGE_BODY)
                        val chId = message.getString(CHANNEL_ID)
                        val id = message.getString(MONGODB_ID)
                        val userName = message.getString(USER_NAME)
                        val userAvatar = message.getString(USER_AVATAR)
                        val userAvatarColor = message.getString(USER_AVATAR_COLOR)
                        val timeStamp = message.getString(TIME_STAMP)

                        val newMessage = Message(
                            messageBody,
                            userName,
                            chId,
                            userAvatar,
                            userAvatarColor,
                            id,
                            timeStamp
                        )
                        this.messages.add(newMessage)
                    }
                    complete(true)
                } catch (e: JSONException) {
                    Log.e(JSON, "$EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.e(ERROR, "$COULD_NOT_FIND_CHANNELS: $error")
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
        App.prefs.requestQueue.add(messagesRequest)
    }

    fun clearMessages() {
        messages.clear()
    }

    fun clearChannels() {
        channels.clear()
    }
}