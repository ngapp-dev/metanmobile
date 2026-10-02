package com.ngapp.metanmobile.core.datastore

import com.ngapp.metanmobile.core.datastore.shared.NewsSortingConfigProto
import com.ngapp.metanmobile.core.datastore.shared.NewsSortingTypeProto
import com.ngapp.metanmobile.core.datastore.shared.SortingOrderProto
import com.ngapp.metanmobile.core.datastore.shared.StationSortingConfigProto
import com.ngapp.metanmobile.core.datastore.shared.StationTypeProto
import com.ngapp.metanmobile.core.datastore.shared.StationsSortingTypeProto
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.model.station.StationType
import com.ngapp.metanmobile.core.model.userdata.NewsSortingConfig
import com.ngapp.metanmobile.core.model.userdata.NewsSortingType
import com.ngapp.metanmobile.core.model.userdata.SortingOrder
import com.ngapp.metanmobile.core.model.userdata.StationSortingConfig
import com.ngapp.metanmobile.core.model.userdata.StationSortingType

internal fun NewsSortingConfigProto.toModel() = NewsSortingConfig(
    sortingType = sort_type_config.toModel(),
    sortingOrder = sort_order_config.toModel(),
)

internal fun NewsSortingConfig.toProto() = NewsSortingConfigProto(
    sort_type_config = sortingType.toProto(),
    sort_order_config = sortingOrder.toProto(),
)

private fun NewsSortingTypeProto.toModel() = when (this) {
    NewsSortingTypeProto.DATE -> NewsSortingType.DATE
    NewsSortingTypeProto.NAME -> NewsSortingType.NAME
}

private fun NewsSortingType.toProto() = when (this) {
    NewsSortingType.DATE -> NewsSortingTypeProto.DATE
    NewsSortingType.NAME -> NewsSortingTypeProto.NAME
}

private fun SortingOrderProto.toModel() = when (this) {
    SortingOrderProto.ASC -> SortingOrder.ASC
    SortingOrderProto.DESC -> SortingOrder.DESC
}

private fun SortingOrder.toProto() = when (this) {
    SortingOrder.ASC -> SortingOrderProto.ASC
    SortingOrder.DESC -> SortingOrderProto.DESC
}

internal fun StationSortingConfigProto.toModel() = StationSortingConfig(
    sortingType = sort_type_config.toModel(),
    sortingOrder = sort_order_config.toModel(),
    activeStationTypes = active_station_types_config.map(StationTypeProto::toModel),
)

internal fun StationSortingConfig.toProto() = StationSortingConfigProto(
    sort_type_config = sortingType.toProto(),
    sort_order_config = sortingOrder.toProto(),
    active_station_types_config = activeStationTypes.map(StationType::toProto),
)

private fun StationsSortingTypeProto.toModel() = when (this) {
    StationsSortingTypeProto.DISTANCE -> StationSortingType.DISTANCE
    StationsSortingTypeProto.STATION_NAME -> StationSortingType.STATION_NAME
}

private fun StationSortingType.toProto() = when (this) {
    StationSortingType.DISTANCE -> StationsSortingTypeProto.DISTANCE
    StationSortingType.STATION_NAME -> StationsSortingTypeProto.STATION_NAME
}

private fun StationTypeProto.toModel() = when (this) {
    StationTypeProto.CLFS -> StationType.CLFS
    StationTypeProto.CNG -> StationType.CNG
    StationTypeProto.SERVICE -> StationType.SERVICE
}

private fun StationType.toProto() = when (this) {
    StationType.CLFS -> StationTypeProto.CLFS
    StationType.CNG -> StationTypeProto.CNG
    StationType.SERVICE -> StationTypeProto.SERVICE
}

internal fun String.asHomeContentItemOrNull(): HomeContentItem? = when (this) {
    "USER_LOCATION" -> HomeContentItem.USER_LOCATION
    "CALCULATORS" -> HomeContentItem.CALCULATORS
    "FAQ" -> HomeContentItem.FAQ
    "CAREER" -> HomeContentItem.CAREER
    else -> null
}
