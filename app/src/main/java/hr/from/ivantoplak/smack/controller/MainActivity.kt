package hr.from.ivantoplak.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hr.from.ivantoplak.smack.R
import hr.from.ivantoplak.smack.model.Channel
import hr.from.ivantoplak.smack.model.Message
import hr.from.ivantoplak.smack.services.AuthService
import hr.from.ivantoplak.smack.services.MessageService
import hr.from.ivantoplak.smack.services.UserDataService
import hr.from.ivantoplak.smack.utils.*
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel: Channel? = null

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                //set username, email, avatar, and logout button
                userNameNavHeader.text = UserDataService.name
                userEmailNavHeader.text = UserDataService.email
                val resourceId =
                    resources.getIdentifier(UserDataService.avatarName, RES_DRAWABLE, packageName)
                userImageNavHeader.setImageResource(resourceId)
                userImageNavHeader.setBackgroundColor(
                    UserDataService.returnAvatarColor(
                        UserDataService.avatarColor
                    )
                )
                loginBtnNavHeader.text = LOGOUT

                //get channels and show them in the list view
                MessageService.getChannels { completed ->
                    if (completed && MessageService.channels.isNotEmpty()) {
                        selectedChannel = MessageService.channels[0]
                        channelAdapter.notifyDataSetChanged()
                        updateWithChannel()
                    }
                }
            }
        }
    }

    private val onNewChannel = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelName = if (args[0] is String) args[0] as String else ""
                val channelDescription = if (args[1] is String) args[1] as String else ""
                val channelId = if (args[2] is String) args[2] as String else ""

                val newChannel = Channel(channelName, channelDescription, channelId)
                MessageService.channels.add(newChannel)
                channelAdapter.notifyDataSetChanged()
            }
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        if (App.prefs.isLoggedIn) {
            runOnUiThread {
                val channelId = if (args[2] is String) args[2] as String else ""
                if (channelId == selectedChannel?.id) {
                    val messageBody = if (args[0] is String) args[0] as String else ""
                    val userName = if (args[3] is String) args[3] as String else ""
                    val userAvatar = if (args[4] is String) args[4] as String else ""
                    val userAvatarColor = if (args[5] is String) args[5] as String else ""
                    val id = if (args[6] is String) args[6] as String else ""
                    val timeStamp = if (args[7] is String) args[7] as String else ""

                    val newMessage = Message(
                        messageBody,
                        userName,
                        channelId,
                        userAvatar,
                        userAvatarColor,
                        id,
                        timeStamp
                    )

                    MessageService.messages.add(newMessage)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //opening socket connection and registering event listeners
        socket.connect()
        socket.on(CHANNEL_CREATED, onNewChannel)
        socket.on(MESSAGE_CREATED, onNewMessage)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupAdapters()

        channel_list.setOnItemClickListener { _, _, position, _ ->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        //registering receiver to receive user login event
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )

        if (App.prefs.isLoggedIn) {
            AuthService.findUserByEmail(this) { }
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
    }

    fun loginBtnNavHeaderClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            UserDataService.logout()
            resetNavHeaderLayout()
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View) {
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton(ADD) { _, _ ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.addChannelNameTxt)
                    val descTextField =
                        dialogView.findViewById<EditText>(R.id.addChannelDescriptionTxt)
                    val channelName = nameTextField.text.toString()
                    val channelDesc = descTextField.text.toString()

                    //create channel
                    socket.emit(NEW_CHANNEL, channelName, channelDesc)
                }
                .setNegativeButton(CANCEL) { _, _ ->
                }
                .show()
        }
    }

    fun sendMessageBtnClicked(view: View) {
        if (App.prefs.isLoggedIn && messageTextField.text.isNotBlank() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel?.id

            //create new message
            socket.emit(
                NEW_MESSAGE,
                messageTextField.text.toString(),
                userId,
                channelId,
                UserDataService.name,
                UserDataService.avatarName,
                UserDataService.avatarColor
            )
            messageTextField.text.clear()
            hideKeyboard(view.context, currentFocus)
        }
    }

    private fun resetNavHeaderLayout() {
        userNameNavHeader.text = ""
        userEmailNavHeader.text = ""
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        loginBtnNavHeader.text = LOGIN
        channelAdapter.clear()
        mainChannelName.text = PLEASE_LOG_IN
    }

    private fun setupAdapters() {
        channelAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    fun updateWithChannel() {
        mainChannelName.text = "#${selectedChannel?.name}"
        if (selectedChannel != null) {
            MessageService.getMessages(selectedChannel!!.id) { completed ->
                if (completed) {
                    for (message in MessageService.messages) {
                        println(message)
                    }
                }
            }
        }
    }
}
