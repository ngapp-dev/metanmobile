package com.ngapp.metanmobile.core.share

import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {

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
        val controller = UIActivityViewController(
            activityItems = listOf(title, text),
            applicationActivities = null,
        )
        UIApplication.sharedApplication.keyWindow?.rootViewController
            ?.presentViewController(controller, animated = true, completion = null)
    }
}
