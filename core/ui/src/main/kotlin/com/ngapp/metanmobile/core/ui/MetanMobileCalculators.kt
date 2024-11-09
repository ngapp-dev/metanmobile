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

package com.ngapp.metanmobile.core.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ngapp.metanmobile.core.designsystem.component.ButtonWithIcon
import com.ngapp.metanmobile.core.designsystem.component.MMTab
import com.ngapp.metanmobile.core.designsystem.component.MMTabRow
import com.ngapp.metanmobile.core.designsystem.icon.MMIcons
import com.ngapp.metanmobile.core.designsystem.theme.Blue
import com.ngapp.metanmobile.core.designsystem.theme.Gray400
import com.ngapp.metanmobile.core.designsystem.theme.MMShapes
import com.ngapp.metanmobile.core.designsystem.theme.MMTypography
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.ui.CalculatorTabs.MILEAGE
import com.ngapp.metanmobile.core.ui.CalculatorTabs.PAYBACK
import com.ngapp.metanmobile.core.ui.charts.HorizontalBarChartItem
import com.ngapp.metanmobile.core.ui.charts.SimpleHorizontalBarChartView
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class CalculatorTabs(val titleResId: Int) {
    MILEAGE(R.string.core_ui_title_calculate_milage),
    PAYBACK(R.string.core_ui_title_calculate_payback)
}

@Composable
fun MetanMobileCalculators(
    modifier: Modifier = Modifier,
    tabRowIndicatorColor: Color,
    tabNameColor: Color,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MILEAGE) }
    val pagerState = rememberPagerState(
        pageCount = { CalculatorTabs.entries.size },
        initialPage = selectedTab.ordinal
    )
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.animateContentSize()) {
        val tabsNames = rememberSaveable { CalculatorTabs.entries.map { it.titleResId } }
        MMTabRow(
            selectedTabIndex = pagerState.currentPage,
            tabRowIndicatorColor = tabRowIndicatorColor,
        ) {
            tabsNames.forEachIndexed { index, titleResId ->
                val tab = if (titleResId == MILEAGE.titleResId) MILEAGE else PAYBACK
                MMTab(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        selectedTab = tab
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            text = stringResource(titleResId),
                            style = MMTypography.displaySmall,
                            color = tabNameColor,
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                MILEAGE.ordinal -> CalculatorMileagePage()
                PAYBACK.ordinal -> CalculatorPaybackPage()
            }
        }
    }
}

@Composable
private fun CalculatorMileagePage() {
    var showResult by rememberSaveable { mutableStateOf(false) }
    var calculatedResult by rememberSaveable { mutableStateOf(Pair("", "")) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MMShapes.large)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp),
    ) {
        if (!showResult) {
            CalculatorMileageEnterValueView(
                onCalculate = { carType ->
                    calculatedResult = Pair(carType.first, carType.second)
                    showResult = true
                }
            )
        } else {
            CalculatorMileageResultView(
                carTypes = listOf(
                    stringResource(R.string.core_ui_text_car_personal),
                    stringResource(R.string.core_ui_text_car_van),
                    stringResource(R.string.core_ui_text_car_truck)
                ),
                selectedCarType = calculatedResult.first,
                inputAmount = calculatedResult.second.toDouble(),
                onCalculateAgain = {
                    calculatedResult = Pair("", "")
                    showResult = false
                }
            )
        }
    }
}

@Composable
private fun CalculatorPaybackPage() {
    var showResult by rememberSaveable { mutableStateOf(false) }
    var fuelPrice by rememberSaveable { mutableStateOf("") }
    var additionalCosts by rememberSaveable { mutableStateOf("") }
    var estimatedMileage by rememberSaveable { mutableStateOf("") }
    var fuelConsumption by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MMShapes.large)
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(16.dp),
    ) {
        if (!showResult) {
            CalculatorPaybackEnterValueView(
                onCalculateClick = { inputFuelPrice, inputAdditionalCosts, inputEstimatedMileage, inputFuelConsumption, showResultValue ->
                    fuelPrice = inputFuelPrice
                    additionalCosts = inputAdditionalCosts
                    estimatedMileage = inputEstimatedMileage
                    fuelConsumption = inputFuelConsumption
                    showResult = true
                }
            )
        } else {
            CalculatePaybackProfit(
                fuelPrice = fuelPrice.toDouble(),
                additionalCosts = additionalCosts.toDouble(),
                estimatedMileage = estimatedMileage.toDouble(),
                fuelConsumption = fuelConsumption.toDouble(),
                onCalculateAgain = {
                    fuelPrice = ""
                    additionalCosts = ""
                    estimatedMileage = ""
                    fuelConsumption = ""
                    showResult = false
                }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CalculatorMileageEnterValueView(onCalculate: (Triple<String, String, Boolean>) -> Unit) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val pattern = remember { Regex("[0-9]+(\\.[0-9]+)?\$") }

    val carTypes = listOf(
        Pair(stringResource(R.string.core_ui_text_car_personal), MMIcons.Personal),
        Pair(stringResource(R.string.core_ui_text_car_van), MMIcons.Van),
        Pair(stringResource(R.string.core_ui_text_car_truck), MMIcons.Truck)
    )

    val (selected, setSelected) = rememberSaveable { mutableStateOf(carTypes[0]) }
    var fuelAmount by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.core_ui_text_choose_car_type),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall,
        )

        CarTypesRadioGroup(
            items = carTypes,
            selected = selected.first,
            setSelected = setSelected,
        )

        Text(
            text = stringResource(R.string.core_ui_text_enter_refill_amount),
            style = MMTypography.displaySmall,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter fuel amount" },
            value = fuelAmount,
            onValueChange = { fuelAmount = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.core_ui_placeholder_number),
                    style = MMTypography.bodyLarge,
                )
            },
            shape = MMShapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Exit) }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Gray400,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray400,
            )
        )
        ButtonWithIcon(
            icon = MMIcons.Calculate,
            iconTint = White,
            textResId = R.string.core_ui_button_calculate,
            buttonBackgroundColor = Blue,
            fontColor = White,
            borderStrokeColor = Blue,
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                focusManager.clearFocus()
                if (fuelAmount.isNotEmpty() && fuelAmount.trim().matches(pattern)
                ) {
                    onCalculate(Triple(selected.first, fuelAmount.trim(), it))
                } else {
                    Toast.makeText(
                        context,
                        R.string.core_ui_text_values_error,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CalculatorPaybackEnterValueView(
    onCalculateClick: (String, String, String, String, Boolean) -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val pattern = remember { Regex("[0-9]+(\\.[0-9]+)?\$") }

    var fuelPrice by rememberSaveable { mutableStateOf("") }
    var additionalCosts by rememberSaveable { mutableStateOf("") }
    var estimatedMileage by rememberSaveable { mutableStateOf("") }
    var fuelConsumption by rememberSaveable { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.core_ui_text_fuel_price_by_vehicle),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter fuel price" },
            value = fuelPrice,
            onValueChange = { fuelPrice = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.core_ui_placeholder_number),
                    style = MMTypography.bodyLarge,
                )
            },
            shape = MMShapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Gray400,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray400,
            )
        )
        Text(
            text = stringResource(R.string.core_ui_text_additional_costs),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter additional costs" },
            value = additionalCosts,
            onValueChange = { additionalCosts = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.core_ui_placeholder_number),
                    style = MMTypography.bodyLarge,
                )
            },
            shape = MMShapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Gray400,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray400,
            )
        )
        Text(
            text = stringResource(R.string.core_ui_text_estimated_vehicle_milage),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter estimated mileage" },
            value = estimatedMileage,
            onValueChange = { estimatedMileage = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.core_ui_placeholder_number),
                    style = MMTypography.bodyLarge,
                )
            },
            shape = MMShapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Gray400,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray400,
            )
        )
        Text(
            text = stringResource(R.string.core_ui_text_vehicle_consumption),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Enter vehicle consumption" },
            value = fuelConsumption,
            onValueChange = { fuelConsumption = it },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.core_ui_placeholder_number),
                    style = MMTypography.bodyLarge,
                )
            },
            shape = MMShapes.large,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Exit) }),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Gray400,
                focusedBorderColor = Blue,
                unfocusedBorderColor = Gray400,
            )
        )
        Text(
            text = stringResource(R.string.core_ui_text_payback_calc_note),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.titleMedium,
        )
        ButtonWithIcon(
            icon = MMIcons.Calculate,
            iconTint = White,
            textResId = R.string.core_ui_button_calculate,
            buttonBackgroundColor = Blue,
            fontColor = White,
            borderStrokeColor = Blue,
            modifier = Modifier.padding(top = 8.dp),
            onClick = {
                focusManager.clearFocus()
                if (fuelPrice.isNotEmpty() && fuelPrice.trim().matches(pattern) &&
                    additionalCosts.isNotEmpty() && additionalCosts.trim().matches(pattern) &&
                    estimatedMileage.isNotEmpty() && estimatedMileage.trim().matches(pattern) &&
                    fuelConsumption.isNotEmpty() && fuelConsumption.trim().matches(pattern)
                ) {
                    onCalculateClick(
                        fuelPrice.trim(),
                        additionalCosts.trim(),
                        estimatedMileage.trim(),
                        fuelConsumption.trim(),
                        it
                    )
                } else {
                    Toast.makeText(
                        context,
                        R.string.core_ui_text_values_error,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        )
    }
}

@Composable
private fun CalculatorMileageResultView(
    carTypes: List<String>,
    selectedCarType: String,
    inputAmount: Double,
    onCalculateAgain: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.core_ui_text_car_milage_amount, inputAmount),
            overflow = TextOverflow.Ellipsis,
            style = MMTypography.displaySmall,
        )
        SimpleHorizontalBarChartView(
            data = getCalculatorMileageResult(context, carTypes, selectedCarType, inputAmount),
            height = 272.dp,
            topStartRadius = 12.dp,
            topEndRadius = 12.dp,
            bottomEndRadius = 12.dp,
            bottomStartRadius = 12.dp,
            modifier = Modifier.padding(top = 16.dp),
        )
        ButtonWithIcon(
            icon = MMIcons.Calculate,
            iconTint = White,
            textResId = R.string.core_ui_button_calculate_again,
            buttonBackgroundColor = Blue,
            fontColor = White,
            borderStrokeColor = Blue,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            onClick = { onCalculateAgain(!it) },
        )
    }
}

private fun getCalculatorMileageResult(
    context: Context,
    carTypes: List<String>,
    selectedCarType: String,
    inputAmount: Double,
): List<HorizontalBarChartItem> {
    val fuelPrices = listOf(
        Pair(context.getString(R.string.core_ui_text_cng), 1.06),
        Pair(context.getString(R.string.core_ui_text_dt), 2.46),
        Pair(context.getString(R.string.core_ui_text_ai95), 2.46),
        Pair(context.getString(R.string.core_ui_text_ai92), 2.36),
        Pair(context.getString(R.string.core_ui_text_lpg), 1.32),
    )
    val fuelRates = when (selectedCarType) {
        carTypes[0] -> listOf(
            Triple(
                context.getString(R.string.core_ui_text_cng),
                8.7,
                context.getString(R.string.core_ui_text_on_cng)
            ),
            Triple(
                context.getString(R.string.core_ui_text_dt),
                5.7,
                context.getString(R.string.core_ui_text_on_dt)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai95),
                8.1,
                context.getString(R.string.core_ui_text_on_ai95)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai92),
                8.1,
                context.getString(R.string.core_ui_text_on_ai92)
            ),
            Triple(
                context.getString(R.string.core_ui_text_lpg),
                10.4,
                context.getString(R.string.core_ui_text_on_lpg)
            )
        )

        carTypes[1] -> listOf(
            Triple(
                context.getString(R.string.core_ui_text_cng),
                18.0,
                context.getString(R.string.core_ui_text_on_cng)
            ),
            Triple(
                context.getString(R.string.core_ui_text_dt),
                11.5,
                context.getString(R.string.core_ui_text_on_dt)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai95),
                16.0,
                context.getString(R.string.core_ui_text_on_ai95)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai92),
                16.0,
                context.getString(R.string.core_ui_text_on_ai92)
            ),
            Triple(
                context.getString(R.string.core_ui_text_lpg),
                19.2,
                context.getString(R.string.core_ui_text_on_lpg)
            )
        )

        carTypes[2] -> listOf(
            Triple(
                context.getString(R.string.core_ui_text_cng),
                32.0,
                context.getString(R.string.core_ui_text_on_cng)
            ),
            Triple(
                context.getString(R.string.core_ui_text_dt),
                25.0,
                context.getString(R.string.core_ui_text_on_dt)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai95),
                31.0,
                context.getString(R.string.core_ui_text_on_ai95)
            ),
            Triple(
                context.getString(R.string.core_ui_text_ai92),
                31.0,
                context.getString(R.string.core_ui_text_on_ai92)
            ),
            Triple(
                context.getString(R.string.core_ui_text_lpg),
                42.0,
                context.getString(R.string.core_ui_text_on_lpg)
            )
        )

        else -> emptyList()
    }
    return fuelRates.mapIndexed { index, element ->
        val formula = (inputAmount * 100) / (element.second * fuelPrices[index].second)
        HorizontalBarChartItem(
            id = index,
            active = element.first == context.getString(R.string.core_ui_text_cng),
            title = element.third,
            value = formula.toFloat(),
        )
    }
}

@Composable
private fun CalculatePaybackProfit(
    fuelPrice: Double,
    additionalCosts: Double,
    estimatedMileage: Double,
    fuelConsumption: Double,
    onCalculateAgain: (Boolean) -> Unit,
) {

    val left = (fuelConsumption * estimatedMileage * fuelPrice / 100)
    val right = (fuelConsumption * estimatedMileage * 1.06 / 100)

    val paybackTime = ((additionalCosts / (left - right)) * 12).roundToInt()

    val paybackMileage =
        ((additionalCosts * 100) / ((fuelConsumption * fuelPrice) - (fuelConsumption * 1.06))).roundToInt()

    CalculatorPaybackResultView(
        paybackTime = paybackTime,
        paybackMileage = paybackMileage,
        onCalculateAgain = onCalculateAgain
    )
}

@Composable
private fun CalculatorPaybackResultView(
    paybackTime: Int,
    paybackMileage: Int,
    onCalculateAgain: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val paybackTimeMonth = when (paybackTime) {
            1 -> R.string.core_ui_text_value_month1
            in 2..3 -> R.string.core_ui_text_value_month2_4
            else -> R.string.core_ui_text_value_month
        }
        Text(
            text = stringResource(paybackTimeMonth, paybackTime.toString()),
            style = MMTypography.displaySmall
        )
        Text(
            text = stringResource(R.string.core_ui_text_or),
            style = MMTypography.displaySmall
        )
        Text(
            text = stringResource(
                id = R.string.core_ui_text_value_km,
                paybackMileage.toString()
            ),
            style = MMTypography.displaySmall
        )
        ButtonWithIcon(
            icon = MMIcons.Calculate,
            iconTint = White,
            textResId = R.string.core_ui_button_calculate_again,
            buttonBackgroundColor = Blue,
            fontColor = White,
            borderStrokeColor = Blue,
            modifier = Modifier.padding(vertical = 8.dp),
            onClick = { onCalculateAgain(it) }
        )
    }
}