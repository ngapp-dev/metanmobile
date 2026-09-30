package com.ngapp.metanmobile.feature.privacypolicy.state

sealed interface PrivacyPolicyAction {
    data object UpdateConsent : PrivacyPolicyAction
}
