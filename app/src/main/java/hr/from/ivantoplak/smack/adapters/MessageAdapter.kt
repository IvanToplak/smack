package hr.from.ivantoplak.smack.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hr.from.ivantoplak.smack.R
import hr.from.ivantoplak.smack.model.Message
import hr.from.ivantoplak.smack.services.UserDataService
import hr.from.ivantoplak.smack.utils.*
import kotlinx.android.synthetic.main.message_list_view.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val context: Context, private val messages: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.message_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindMessage(context, messages[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindMessage(context: Context, message: Message) {
            val resourceId = context.resources.getIdentifier(
                message.userAvatar,
                RES_DRAWABLE,
                context.packageName
            )
            itemView.messageUserImage.setImageResource(resourceId)
            itemView.messageUserImage.setBackgroundColor(UserDataService.returnAvatarColor(message.userAvatarColor))
            itemView.messageUserNameLbl.text = message.userName
            itemView.messageTimestampLbl.text = getDateString(message.timeStamp)
            itemView.messageBodyLbl.text = message.message
        }
    }

    fun getDateString(isoString: String): String {
        //2019-09-23T10:15:45.123Z -> Monday 10:15 AM
        val isoFormatter = SimpleDateFormat(ISO_DATE_TIME_PATTERN, Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        var convertedDate = Date()
        try {
            convertedDate = isoFormatter.parse(isoString) as Date
        } catch (e: ParseException) {
            Log.e(PARSE, CANNOT_PARSE_DATE)
        }

        val outDateFormatter = SimpleDateFormat(CUSTOM_DATE_TIME_PATTERN, Locale.getDefault())
        return outDateFormatter.format(convertedDate)
    }
}