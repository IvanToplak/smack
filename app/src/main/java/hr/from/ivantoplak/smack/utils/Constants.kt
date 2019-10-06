package hr.from.ivantoplak.smack.utils

const val AVATAR_MIN = 0
const val AVATAR_MAX = 27
const val COLOR_MIN = 0
const val COLOR_MAX = 255
const val RES_DRAWABLE = "drawable"
const val AVATAR_DEFAULT_DRAWABLE = "profileDefault"
const val AVATAR_DEFAULT_BACKGROUND_COLOR = "[0.5, 0.5, 0.5, 1]"
const val AVATAR_DRAWABLE_PREFIX_LIGHT = "light"
const val AVATAR_DRAWABLE_PREFIX_DARK = "dark"
const val COLOR_DEFAULT_ALPHA = "1"

//API URLs
const val BASE_URL = "https://it-smack-chat.herokuapp.com/v1/"
const val SOCKET_URL = "https://it-smack-chat.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel/"

//JSON field names
const val EMAIL = "email"
const val PASSWORD = "password"
const val NAME = "name"
const val AVATAR_NAME = "avatarName"
const val AVATAR_COLOR = "avatarColor"
const val MONGODB_ID = "_id"
const val DESCRIPTION = "description"

const val ERROR = "ERROR"
const val COULD_NOT_REGISTER_USER = "Could not register user"
const val COULD_NOT_LOGIN_USER = "Could not login user"
const val COULD_NOT_ADD_USER = "Could not add user"
const val COULD_NOT_FIND_USER = "Could not find user"
const val COULD_NOT_FIND_CHANNELS = "Could not find channels"
const val CONTENT_TYPE_APPLICATION_JSON_CHARSET_UTF8 = "application/json; charset=utf-8"
const val USER = "user"
const val TOKEN = "token"
const val JSON = "JSON"
const val EXC = "EXC"
const val SCANNER = "SCANNER"
const val AUTHORIZATION = "Authorization"
const val BEARER = "Bearer"
const val SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN = "Something went wrong, please try again."
const val MAKE_SURE_USER_NAME_EMAIL_AND_PASSWORD_ARE_FILLED_IN =
    "Make sure user name, email, and password are filled in."
const val MAKE_SURE_EMAIL_AND_PASSWORD_ARE_FILLED_IN = "Make sure email and password are filled in."
const val LOGOUT = "Logout"
const val LOGIN = "Login"
const val ADD = "Add"
const val CANCEL = "Cancel"

//Socket Constants
const val CHANNEL_CREATED = "channelCreated"
const val NEW_CHANNEL = "newChannel"

//Broadcast Constants
const val BROADCAST_USER_DATA_CHANGE = "BROADCAST_USER_DATA_CHANGE"


//Shared Preferences Constants
const val PREFS_FILE = "prefs"
const val IS_LOGGED_IN = "isLoggedIn"
const val AUTH_TOKEN = "authToken"
const val USER_EMAIL = "userEmail"

