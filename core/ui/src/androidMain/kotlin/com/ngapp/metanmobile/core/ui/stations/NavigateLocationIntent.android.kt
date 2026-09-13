package com.ngapp.metanmobile.core.ui.stations

import android.content.Intent
import android.net.Uri
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.model.station.UserStationResource
import com.ngapp.metanmobile.core.ui.UiAndroidPlatformContextProvider

actual fun openStationLocation(station: UserStationResource) {
    val context = UiAndroidPlatformContextProvider.context ?: return

    val uriYandex = if (station.yandexTag.isNotEmpty()) {
        "https://yandex.ru/maps/org/${station.yandexTag}"
    } else {
        "yandexmaps://maps.yandex.ru/?pt=0,0&z=12&text=${station.latitude},${station.longitude}&l=map"
    }
    val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(uriYandex))
    intentYandex.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intentYandex.setPackage("ru.yandex.yandexmaps")

    val uriGoogle = if (station.googleMapsTag.isNotEmpty()) {
        "https://maps.google.com/maps?cid=${station.googleMapsTag}"
    } else {
        "geo:0,0?q=${station.latitude},${station.longitude}"
    }
    val intentGoogle = Intent(Intent.ACTION_VIEW, Uri.parse(uriGoogle))
    intentGoogle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intentGoogle.setPackage("com.google.android.apps.maps")

    val title = context.getString(SharedRes.strings.core_ui_text_select_application.resourceId)
    val chooserIntent = Intent.createChooser(intentGoogle, title)
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Intent>(intentYandex))
    context.startActivity(chooserIntent)
}
