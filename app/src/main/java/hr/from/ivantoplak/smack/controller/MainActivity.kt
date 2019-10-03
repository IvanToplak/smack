package hr.from.ivantoplak.smack.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import hr.from.ivantoplak.smack.R
import hr.from.ivantoplak.smack.services.AuthService
import hr.from.ivantoplak.smack.services.UserDataService
import hr.from.ivantoplak.smack.utils.BROADCAST_USER_DATA_CHANGE
import hr.from.ivantoplak.smack.utils.LOGIN
import hr.from.ivantoplak.smack.utils.LOGOUT
import hr.from.ivantoplak.smack.utils.RES_DRAWABLE
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (AuthService.isLoggedIn) {
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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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

        //registering receiver to receive user login event
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver,
            IntentFilter(BROADCAST_USER_DATA_CHANGE)
        )
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun loginBtnNavHeaderClicked(view: View) {
        if (AuthService.isLoggedIn) {
            UserDataService.logout()
            resetNavHeaderLayout()
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun addChannelClicked(view: View) {

    }

    fun sendMessageBtnClicked(view: View) {

    }

    private fun resetNavHeaderLayout() {
        userNameNavHeader.text = ""
        userEmailNavHeader.text = ""
        userImageNavHeader.setImageResource(R.drawable.profiledefault)
        userImageNavHeader.setBackgroundColor(Color.TRANSPARENT)
        loginBtnNavHeader.text = LOGIN
    }
}
