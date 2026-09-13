package com.ngapp.metanmobile.composeapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.domain.repository.user.UserDataRepository
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CALCULATORS
import com.ngapp.metanmobile.core.model.home.HomeContentItem.CAREER
import com.ngapp.metanmobile.core.model.home.HomeContentItem.FAQ
import com.ngapp.metanmobile.core.model.home.HomeContentItem.USER_LOCATION
import com.ngapp.metanmobile.core.model.station.StationType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Ported from master's `MainActivityViewModel.onInitUserData()` — this is app-lifetime,
 * first-run seeding, not specific to Android's Activity, so it lives at the composeApp root
 * (shared by Android and iOS) rather than in `:app`.
 *
 * This was lost entirely in the initial KMP migration (the whole `MainActivityViewModel.kt` file
 * was deleted, not ported) — without it, a fresh install's `homeReorderableList` stays
 * permanently empty (proto3 repeated fields default to empty, nothing else ever populates it),
 * so Home renders its header/news and then nothing else. Consent-triggering and usage-time/
 * review-prompt tracking from the same master file are deliberately NOT ported here yet — they
 * touch the ads SDK and haven't been verified against this KMP ads setup, so porting them
 * blind risks trading one bug for another.
 */
class MainViewModel(
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    init {
        onInitUserData()
    }

    private fun onInitUserData() = viewModelScope.launch {
        userDataRepository.userData.collectLatest { userData ->
            if (userData.homeReorderableList.isEmpty()) {
                viewModelScope.launch {
                    userDataRepository.setHomeLastNewsExpanded(true)
                }
                viewModelScope.launch {
                    userDataRepository.setHomeReorderableList(
                        listOf(USER_LOCATION, CALCULATORS, FAQ, CAREER)
                    )
                }
            }
            viewModelScope.launch {
                userDataRepository.setStationSortingConfig(
                    userData.stationSortingConfig.copy(
                        activeStationTypes = listOf(StationType.CNG, StationType.CLFS)
                    )
                )
            }
        }
    }
}
