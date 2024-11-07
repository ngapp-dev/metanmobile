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

package com.ngapp.metanmobile.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun ColorScheme.isLight() = this.background.luminance() > 0.5

val Black = Color(0xFF000000)
val White = Color(0xFFFFFFFF)
val Transparent = Color(0x00000000)
val Blue = Color(0xFF009CDE)
val LightBlue = Color(0xFFDCF5FF)
val BluePale = Color(0x16009CDE)
val BlueAlfaLight = Color(0xFFD3EDF7)
val DarkBlue = Color(0xFF252941)
val DarkGrayBlue = Color(0xFF30344B)
val ElevatedDarkBlue = Color(252941)
val BlueDark = Color(0xFF05060B)

val Red = Color(0xFFEB5757)
val Green = Color(0xFF09AF84)
val GreenPale = Color(0x1600AF85)

val Gray25 = Color(0xFFF8F8F8)
val Gray50 = Color(0xFFF1F1F1)
val Gray75 = Color(0xFFECECEC)
val Gray100 = Color(0xFFE1E1E1)
val Gray200 = Color(0xFFEEEEEE)
val Gray250 = Color(0xFFC8C8C8)
val Gray300 = Color(0xFFACACAC)
val Gray400 = Color(0xFF919191)
val Gray500 = Color(0xFF6E6E6E)
val Gray600 = Color(0xFF535353)
val Gray700 = Color(0xFF616161)
val Gray750 = Color(0xFF414141)
val Gray800 = Color(0xFF292929)
val Gray900 = Color(0xFF212121)
val Gray950 = Color(0xFF141414)

//val CardDark = Color(0xFF3B3E43)
val CardDark = DarkBlue
val CardLight = White

val SmallWidgetLight = Gray50

val BackgroundLight = Color(0xFFF5F2F5)
val BackgroundDark = Color(0xFF24292E)

val DividerLight = Color(0xFFE0E0E0)
val DividerDark = Color(0xFF6E6E6E)

val Red700 = Color(0xFFD32F2F)

val toolbarIconDark = White
val toolbarIconLight = Gray500

val textColorLight = White
val textColorDark = Black

val permissionButtonLight = Gray200
val permissionButtonDark = Gray800

val ColorScheme.textColor: Color
    @Composable get() = if (isLight()) textColorDark else textColorLight

val ColorScheme.toolbarIconColor: Color
    @Composable get() = if (isLight()) toolbarIconLight else toolbarIconDark

val ColorScheme.navigationBarIndicatorColor: Color
    @Composable get() = if (isLight()) BlueAlfaLight else Blue

val ColorScheme.dividerColor: Color
    @Composable get() = if (isLight()) DividerLight else DividerDark

val ColorScheme.cardBackgroundColor: Color
    @Composable get() = if (isLight()) CardLight else CardDark

val ColorScheme.elevatedCardBackgroundColor: Color
    @Composable get() = if (isLight()) White else DarkGrayBlue

val ColorScheme.smallWidgetBackgroundColor: Color
    @Composable get() = if (isLight()) SmallWidgetLight else ElevatedDarkBlue

val ColorScheme.permissionButtonColor: Color
    @Composable get() = if (isLight()) permissionButtonLight else permissionButtonDark


internal val Blue10 = Color(0xFF001F28)
internal val Blue20 = Color(0xFF003544)
internal val Blue30 = Color(0xFF004D61)
internal val Blue40 = Color(0xFF006780)
internal val Blue80 = Color(0xFF5DD5FC)
internal val Blue90 = Color(0xFFB8EAFF)
internal val DarkGreen10 = Color(0xFF0D1F12)
internal val DarkGreen20 = Color(0xFF223526)
internal val DarkGreen30 = Color(0xFF394B3C)
internal val DarkGreen40 = Color(0xFF4F6352)
internal val DarkGreen80 = Color(0xFFB7CCB8)
internal val DarkGreen90 = Color(0xFFD3E8D3)
internal val DarkGreenGray10 = Color(0xFF1A1C1A)
internal val DarkGreenGray20 = Color(0xFF2F312E)
internal val DarkGreenGray90 = Color(0xFFE2E3DE)
internal val DarkGreenGray95 = Color(0xFFF0F1EC)
internal val DarkGreenGray99 = Color(0xFFFBFDF7)
internal val DarkPurpleGray10 = Color(0xFF201A1B)
internal val DarkPurpleGray20 = Color(0xFF362F30)
internal val DarkPurpleGray90 = Color(0xFFECDFE0)
internal val DarkPurpleGray95 = Color(0xFFFAEEEF)
internal val DarkPurpleGray99 = Color(0xFFFCFCFC)
internal val Green10 = Color(0xFF00210B)
internal val Green20 = Color(0xFF003919)
internal val Green30 = Color(0xFF005227)
internal val Green40 = Color(0xFF006D36)
internal val Green80 = Color(0xFF0EE37C)
internal val Green90 = Color(0xFF5AFF9D)
internal val GreenGray30 = Color(0xFF414941)
internal val GreenGray50 = Color(0xFF727971)
internal val GreenGray60 = Color(0xFF8B938A)
internal val GreenGray80 = Color(0xFFC1C9BF)
internal val GreenGray90 = Color(0xFFDDE5DB)
internal val Orange10 = Color(0xFF380D00)
internal val Orange20 = Color(0xFF5B1A00)
internal val Orange30 = Color(0xFF812800)
internal val Orange40 = Color(0xFFA23F16)
internal val Orange80 = Color(0xFFFFB59B)
internal val Orange90 = Color(0xFFFFDBCF)
internal val Purple10 = Color(0xFF36003C)
internal val Purple20 = Color(0xFF560A5D)
internal val Purple30 = Color(0xFF702776)
internal val Purple40 = Color(0xFF8B418F)
internal val Purple80 = Color(0xFFFFA9FE)
internal val Purple90 = Color(0xFFFFD6FA)
internal val PurpleGray30 = Color(0xFF4D444C)
internal val PurpleGray50 = Color(0xFF7F747C)
internal val PurpleGray60 = Color(0xFF998D96)
internal val PurpleGray80 = Color(0xFFD0C3CC)
internal val PurpleGray90 = Color(0xFFEDDEE8)
internal val Red10 = Color(0xFF410002)
internal val Red20 = Color(0xFF690005)
internal val Red30 = Color(0xFF93000A)
internal val Red40 = Color(0xFFBA1A1A)
internal val Red80 = Color(0xFFFFB4AB)
internal val Red90 = Color(0xFFFFDAD6)
internal val Teal10 = Color(0xFF001F26)
internal val Teal20 = Color(0xFF02363F)
internal val Teal30 = Color(0xFF214D56)
internal val Teal40 = Color(0xFF3A656F)
internal val Teal80 = Color(0xFFA2CED9)
internal val Teal90 = Color(0xFFBEEAF6)
internal val lightGray = Color(0xFFE8EDF2)
internal val darkGray = Color(0xFF414669)