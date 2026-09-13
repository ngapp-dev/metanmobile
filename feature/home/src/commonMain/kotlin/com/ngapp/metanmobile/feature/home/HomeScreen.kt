@file:OptIn(ExperimentalMaterial3Api::class)

package com.ngapp.metanmobile.feature.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ngapp.metanmobile.SharedRes
import com.ngapp.metanmobile.core.designsystem.component.MMHomeTopAppBar
import com.ngapp.metanmobile.core.designsystem.component.MMLinearWavyProgressIndicator
import com.ngapp.metanmobile.core.designsystem.theme.Green
import com.ngapp.metanmobile.core.designsystem.theme.White
import com.ngapp.metanmobile.core.model.home.HomeContentItem
import com.ngapp.metanmobile.core.ui.TrackScreenViewEvent
import com.ngapp.metanmobile.core.ui.animation.EmptyView
import com.ngapp.metanmobile.core.ui.util.LocalPermissionsState
import com.ngapp.metanmobile.feature.home.state.HomeAction
import com.ngapp.metanmobile.feature.home.state.HomeUiState
import com.ngapp.metanmobile.feature.home.ui.HomeContent
import com.ngapp.metanmobile.feature.stationdetail.ui.StationDetailBottomSheet
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeRoute(
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onFaqClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onMenuClick: () -> Unit,
    onShowBottomBar: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val permissionsState = LocalPermissionsState.current
    LaunchedEffect(permissionsState.hasLocationPermissions) {
        if (permissionsState.hasLocationPermissions) {
            viewModel.triggerAction(HomeAction.UpdateLocation(true))
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val reorderableList by viewModel.reorderableList.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncFailed by viewModel.syncFailed.collectAsStateWithLifecycle()
    val isEditingUi by viewModel.isEditing.collectAsStateWithLifecycle()
    val isLastNewsExpended by viewModel.isLastNewsExpanded.collectAsStateWithLifecycle()

    HomeScreen(
        modifier = modifier,
        uiState = uiState,
        reorderableList = reorderableList,
        isSyncing = isSyncing,
        syncFailed = syncFailed,
        isEditingUi = isEditingUi,
        isLastNewsExpended = isLastNewsExpended,
        onNewsClick = onNewsClick,
        onNewsDetailClick = onNewsDetailClick,
        onFaqListClick = onFaqClick,
        onCareersClick = onCareersClick,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onMenuClick,
        onShowBottomBar = onShowBottomBar,
        onAction = viewModel::triggerAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    reorderableList: List<HomeContentItem>,
    isSyncing: Boolean,
    syncFailed: Boolean,
    isEditingUi: Boolean,
    isLastNewsExpended: Boolean,
    onNewsClick: () -> Unit,
    onNewsDetailClick: (String) -> Unit,
    onFaqListClick: () -> Unit,
    onCareersClick: () -> Unit,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onShowBottomBar: (Boolean) -> Unit,
    onAction: (HomeAction) -> Unit,
) {
    val isLoading = uiState is HomeUiState.Loading
    // Room's local flows emit as soon as they're subscribed, even when the very first sync
    // hasn't reached the network yet - so uiState can turn Success with everything still empty
    // well before there's anything real to show. Keep showing only the spinner above through
    // that in-between moment, rather than flashing the static, always-rendered widgets (the
    // calculators tile, the user-location header) with nothing behind them.
    val isContentPending = isLoading ||
        (uiState is HomeUiState.Success && uiState.isEmpty() && isSyncing && !syncFailed)
    var showTopAppBar by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false,
            initialValue = SheetValue.Hidden,
        )
    )

    HomeHeader(
        modifier = Modifier,
        isEditingUi = isEditingUi,
        showTopAppBar = showTopAppBar,
        onCabinetClick = onCabinetClick,
        onSettingsClick = onSettingsClick,
        onAction = onAction,
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = isSyncing || isLoading,
                enter = slideInVertically(initialOffsetY = { fullHeight -> -fullHeight }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }) + fadeOut(),
            ) {
                MMLinearWavyProgressIndicator()
            }
            if (!isContentPending) {
                when (uiState) {
                    HomeUiState.Loading -> Unit
                    is HomeUiState.Success -> {
                        if (uiState.isEmpty() && syncFailed) {
                            EmptyView(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                message = stringResource(SharedRes.strings.feature_home_text_empty),
                                canRetry = true,
                                onClickRetry = { onAction(HomeAction.RetrySync) },
                            )
                        } else {
                            StationDetailBottomSheet(
                                stationCode = uiState.nearestStation?.code,
                                bottomSheetState = bottomSheetScaffoldState,
                                onShowTopAppBar = { showTopAppBar = it },
                                onShowBottomBar = onShowBottomBar,
                                onNewsDetailClick = onNewsDetailClick,
                            ) {
                                HomeContent(
                                    isEditingUi = isEditingUi,
                                    isLastNewsExpended = isLastNewsExpended,
                                    reorderableList = reorderableList,
                                    pinnedNewsList = uiState.pinnedNewsList,
                                    lastNewsList = uiState.lastNewsList,
                                    nearestStation = uiState.nearestStation,
                                    cngPrice = uiState.cngPrice,
                                    pinnedFaqList = uiState.pinnedFaqList,
                                    career = uiState.career,
                                    onShowAllNewsClick = onNewsClick,
                                    onSeeAllFaqClick = onFaqListClick,
                                    onSeeAllCareersClick = onCareersClick,
                                    onNewsDetailClick = onNewsDetailClick,
                                    onStationDetailClick = {
                                        showTopAppBar = false
                                        onShowBottomBar(false)
                                        coroutineScope.launch { bottomSheetScaffoldState.bottomSheetState.expand() }
                                    },
                                    onAction = onAction,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    TrackScreenViewEvent(screenName = "HomeScreen")
}

private fun HomeUiState.Success.isEmpty(): Boolean =
    pinnedNewsList.isEmpty() &&
        lastNewsList.isEmpty() &&
        cngPrice == null &&
        nearestStation == null &&
        pinnedFaqList.isEmpty() &&
        career == null

@Composable
private fun HomeHeader(
    modifier: Modifier,
    isEditingUi: Boolean,
    showTopAppBar: Boolean,
    onCabinetClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAction: (HomeAction) -> Unit,
    pageContent: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            AnimatedVisibility(
                visible = showTopAppBar,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            ) {
                MMHomeTopAppBar(
                    onEditClicked = { onAction(HomeAction.EditUi(!isEditingUi)) },
                    onMenuClicked = onSettingsClick,
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isEditingUi,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { onAction(HomeAction.SaveUi) },
                    containerColor = Green,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    shape = CircleShape,
                ) {
                    Text(
                        text = stringResource(SharedRes.strings.core_ui_button_save_changes),
                        style = MaterialTheme.typography.titleLarge,
                        color = White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = pageContent
    )
}
