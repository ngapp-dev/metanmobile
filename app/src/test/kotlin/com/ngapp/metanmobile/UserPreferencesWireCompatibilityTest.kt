package com.ngapp.metanmobile

import com.ngapp.metanmobile.core.datastore.DarkThemeConfigProto as LegacyDarkThemeConfigProto
import com.ngapp.metanmobile.core.datastore.NewsSortingConfigProto as LegacyNewsSortingConfigProto
import com.ngapp.metanmobile.core.datastore.NewsSortingTypeProto as LegacyNewsSortingTypeProto
import com.ngapp.metanmobile.core.datastore.SortingOrderProto as LegacySortingOrderProto
import com.ngapp.metanmobile.core.datastore.StationSortingConfigProto as LegacyStationSortingConfigProto
import com.ngapp.metanmobile.core.datastore.StationTypeProto as LegacyStationTypeProto
import com.ngapp.metanmobile.core.datastore.StationsSortingTypeProto as LegacyStationsSortingTypeProto
import com.ngapp.metanmobile.core.datastore.UserPreferences as LegacyUserPreferences
import com.ngapp.metanmobile.core.datastore.shared.UserPreferences as SharedUserPreferences
import kotlin.test.assertEquals
import okio.Buffer
import org.junit.Test

/**
 * Guards the update path from the old protobuf-lite DataStore to common Wire DataStore. The
 * Android class name is deliberately different, but protobuf's persisted contract is tags and
 * wire types; every persisted tag is exercised here from legacy writer to common reader.
 */
class UserPreferencesWireCompatibilityTest {
    @Test
    fun `Wire reads preferences bytes written by legacy protobuf`() {
        val legacy = LegacyUserPreferences.newBuilder()
            .setHasDoneIntToStringIdMigration(true)
            .addDeprecatedFavoriteStationResourceCodes("deprecated-station")
            .setHasDoneListToMapMigration(true)
            .putFavoriteStationResourceCodes("station-42", true)
            .putViewedNewsResourceIds("news-17", true)
            .setDarkThemeConfig(LegacyDarkThemeConfigProto.DARK_THEME_CONFIG_DARK)
            .setShouldHideOnboarding(true)
            .setNewsSortingConfig(
                LegacyNewsSortingConfigProto.newBuilder()
                    .setSortTypeConfig(LegacyNewsSortingTypeProto.NAME)
                    .setSortOrderConfig(LegacySortingOrderProto.DESC)
                    .build(),
            )
            .setStationSortingConfig(
                LegacyStationSortingConfigProto.newBuilder()
                    .setSortTypeConfig(LegacyStationsSortingTypeProto.STATION_NAME)
                    .setSortOrderConfig(LegacySortingOrderProto.ASC)
                    .addActiveStationTypesConfig(LegacyStationTypeProto.CNG)
                    .addActiveStationTypesConfig(LegacyStationTypeProto.SERVICE)
                    .build(),
            )
            .setTotalUsageTime(123_456L)
            .setIsReviewShown(true)
            .addHomeReorderable("FAQ")
            .setIsHomeLastNewsExpanded(true)
            .build()

        val shared = SharedUserPreferences.ADAPTER.decode(Buffer().write(legacy.toByteArray()))

        assertEquals(true, shared.has_done_int_to_string_id_migration)
        assertEquals(listOf("deprecated-station"), shared.deprecated_favorite_station_resource_codes)
        assertEquals(true, shared.has_done_list_to_map_migration)
        assertEquals(mapOf("station-42" to true), shared.favorite_station_resource_codes)
        assertEquals(mapOf("news-17" to true), shared.viewed_news_resource_ids)
        assertEquals("DARK_THEME_CONFIG_DARK", shared.dark_theme_config.name)
        assertEquals(true, shared.should_hide_onboarding)
        assertEquals("NAME", shared.news_sorting_config?.sort_type_config?.name)
        assertEquals("DESC", shared.news_sorting_config?.sort_order_config?.name)
        assertEquals("STATION_NAME", shared.station_sorting_config?.sort_type_config?.name)
        assertEquals("ASC", shared.station_sorting_config?.sort_order_config?.name)
        assertEquals(
            listOf("CNG", "SERVICE"),
            shared.station_sorting_config?.active_station_types_config?.map { it.name },
        )
        assertEquals(123_456L, shared.total_usage_time)
        assertEquals(true, shared.is_review_shown)
        assertEquals(listOf("FAQ"), shared.home_reorderable)
        assertEquals(true, shared.is_home_last_news_expanded)
    }
}
