package com.ngapp.metanmobile.core.share

import android.content.Intent
import com.ngapp.metanmobile.core.common.util.UiAndroidPlatformContextProvider
import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

actual class ShareManager {

    private val context = requireNotNull(UiAndroidPlatformContextProvider.context)

    actual fun shareStation(station: UserStationResource?) {
        share(
            title = "${station?.title} • Metan Mobile",
            text = "${station?.title}\n${station?.address}\n${station?.url}",
        )
    }

    actual fun shareNews(news: NewsResource?) {
        share(
            title = "${news?.title} • Metan Mobile",
            text = "${news?.title}\n${news?.description}\n${news?.url}",
        )
    }

    private fun share(title: String, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(sendIntent, null).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(chooserIntent)
    }
}
