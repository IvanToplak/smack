package hr.from.ivantoplak.smack.controller

import android.app.Application
import hr.from.ivantoplak.smack.utils.SharedPrefs

class App : Application() {

    companion object {
        lateinit var prefs: SharedPrefs
    }

    override fun onCreate() {
        super.onCreate()
        prefs = SharedPrefs(applicationContext)
    }
}