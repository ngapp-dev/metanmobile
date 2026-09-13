package com.ngapp.metanmobile.feature.onboarding.state

sealed interface OnboardingUiState {
    data object Loading : OnboardingUiState
    data object NotShown : OnboardingUiState
    data object Shown : OnboardingUiState
}
