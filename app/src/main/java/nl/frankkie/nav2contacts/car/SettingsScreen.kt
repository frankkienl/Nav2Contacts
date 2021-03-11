package nl.frankkie.nav2contacts.car

import androidx.preference.PreferenceManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*
import nl.frankkie.nav2contacts.R

class SettingsScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        val templateBuilder = ListTemplate.Builder()
        val prefs = PreferenceManager.getDefaultSharedPreferences(carContext)

        val itemListBuilder = ItemList.Builder()
        val rowBuilder = Row.Builder()
        rowBuilder.setTitle(carContext.getString(R.string.favorites_only))
        rowBuilder.setToggle(Toggle.Builder {
            prefs.edit().putBoolean(PREF_FAVORITES_ONLY, it).apply()
        }
            .setChecked(prefs.getBoolean(PREF_FAVORITES_ONLY, false))
            .build()
        )
        itemListBuilder.addItem(rowBuilder.build())
        //
        templateBuilder.setHeaderAction(Action.BACK)
        templateBuilder.setTitle(carContext.getString(R.string.settings))
        templateBuilder.setSingleList(itemListBuilder.build())
        return templateBuilder.build()
    }

    companion object {
        const val PREF_FAVORITES_ONLY = "pref_filter_favorites_only"
    }

}