/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ngapp.metanmobile.ui

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import dev.icerock.moko.resources.StringResource
import kotlin.properties.ReadOnlyProperty

/**
 * moko's [StringResource] carries the underlying Android `@StringRes` id as [StringResource.
 * resourceId] — this used to take a plain `@StringRes Int` directly (master/pre-moko), but every
 * string in this app is a moko resource now, so this resolves through that id instead of taking
 * one.
 */
fun AndroidComposeTestRule<*, *>.stringResource(
    resource: StringResource,
): ReadOnlyProperty<Any, String> =
    ReadOnlyProperty { _, _ -> activity.getString(resource.resourceId) }
