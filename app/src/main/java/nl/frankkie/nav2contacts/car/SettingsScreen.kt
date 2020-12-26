package nl.frankkie.nav2contacts.car

import androidx.preference.PreferenceManager
import com.google.android.libraries.car.app.CarContext
import com.google.android.libraries.car.app.Screen
import com.google.android.libraries.car.app.model.*
import nl.frankkie.nav2contacts.R

class SettingsScreen(carContext: CarContext) : Screen(carContext) {
    override fun getTemplate(): Template {
        val templateBuilder = ListTemplate.builder()
        val prefs = PreferenceManager.getDefaultSharedPreferences(carContext)

        val itemListBuilder = ItemList.builder()
        val rowBuilder = Row.builder()
        rowBuilder.setTitle(carContext.getString(R.string.favorites_only))
        rowBuilder.setToggle(Toggle.builder {
            prefs.edit().putBoolean(PREF_FAVORITES_ONLY, it).apply()
        }
            .setChecked(prefs.getBoolean(PREF_FAVORITES_ONLY, false))
            .build()
        )
        itemListBuilder.addItem(rowBuilder.build())
        //
        templateBuilder.setHeaderAction(Action.BACK)
        templateBuilder.setTitle(carContext.getString(R.string.settings))
        templateBuilder.addList(itemListBuilder.build(), carContext.getString(R.string.settings))
        return templateBuilder.build()
    }

    companion object {
        const val PREF_FAVORITES_ONLY = "pref_filter_favorites_only"
    }

}