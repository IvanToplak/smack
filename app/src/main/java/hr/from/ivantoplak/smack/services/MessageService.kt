package hr.from.ivantoplak.smack.services

import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import hr.from.ivantoplak.smack.controller.App
import hr.from.ivantoplak.smack.model.Channel
import hr.from.ivantoplak.smack.utils.*
import org.json.JSONException

object MessageService {

    val channels = ArrayList<Channel>()

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
}