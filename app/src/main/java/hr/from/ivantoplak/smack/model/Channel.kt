package hr.from.ivantoplak.smack.model

data class Channel(val name: String = "", val description: String = "", val id: String = "") {
    override fun toString(): String {
        return "#$name"
    }
}
