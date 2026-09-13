package com.ngapp.metanmobile.feature.privacypolicy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ngapp.metanmobile.core.ui.ads.ConsentHelper
import com.ngapp.metanmobile.feature.privacypolicy.state.PrivacyPolicyAction
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class PrivacyPolicyViewModel(
    private val consentHelper: ConsentHelper,
) : ViewModel() {

    val isPrivacyOptionsRequired: StateFlow<Boolean> = flow {
        emit(consentHelper.isPrivacyOptionsRequired())
    }.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(5_000),
        initialValue = false
    )

    fun triggerAction(action: PrivacyPolicyAction) {
        when (action) {
            is PrivacyPolicyAction.UpdateConsent -> onUpdateConsent()
        }
    }

    private fun onUpdateConsent() {
        if (isPrivacyOptionsRequired.value) {
            consentHelper.updateConsent()
        }
    }
}
