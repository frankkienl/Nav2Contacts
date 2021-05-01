package nl.frankkie.nav2contacts.car

data class MyContact(
    val name: String,
    val starred: Boolean,
    var addresses: ArrayList<MyContactAddress> = arrayListOf()
) {
    override fun equals(other: Any?): Boolean {
        if (other is MyContact) {
            return this.name == other.name
        }
        return false
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

data class MyContactAddress(
    val street: String,
    val city: String,
    val country: String,
    var latitude: Double? = null,
    var longitude: Double? = null
) {
    override fun equals(other: Any?): Boolean {
        if (other is MyContactAddress) {
            return (this.hashCode() == other.hashCode())
        }
        return false
    }

    override fun hashCode(): Int {
        var result = street.hashCode()
        result = 31 * result + city.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }
}