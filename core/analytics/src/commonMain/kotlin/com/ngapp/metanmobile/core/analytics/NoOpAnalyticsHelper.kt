package com.ngapp.metanmobile.core.analytics

class NoOpAnalyticsHelper : AnalyticsHelper { override fun logEvent(event: AnalyticsEvent) = Unit }
