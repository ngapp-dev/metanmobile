package com.ngapp.metanmobile.core.share

import com.ngapp.metanmobile.core.model.news.NewsResource
import com.ngapp.metanmobile.core.model.station.UserStationResource

/** Opens the platform share sheet for user-facing station and news content. */
expect class ShareManager() {
    fun shareStation(station: UserStationResource?)

    fun shareNews(news: NewsResource?)
}
