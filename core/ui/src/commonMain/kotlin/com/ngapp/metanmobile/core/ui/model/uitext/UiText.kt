package com.ngapp.metanmobile.core.ui.model.uitext

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

sealed interface UiText {
    data class DynamicString(val value: String) : UiText
    data class Resource(val resource: StringResource, val args: List<Any> = emptyList()) : UiText

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is Resource -> stringResource(resource, *args.toTypedArray())
    }
}
