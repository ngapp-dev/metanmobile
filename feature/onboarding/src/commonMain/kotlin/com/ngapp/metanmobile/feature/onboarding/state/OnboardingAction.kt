package com.ngapp.metanmobile.feature.onboarding.state

sealed interface OnboardingAction {
    data object DismissOnboarding : OnboardingAction
}
