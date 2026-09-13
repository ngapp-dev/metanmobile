package com.ngapp.metanmobile.feature.contacts.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ngapp.metanmobile.feature.contacts.ContactsRoute
import kotlinx.serialization.Serializable

@Serializable data object ContactsNavigation
fun NavController.navigateToContacts(options: NavOptionsBuilder.() -> Unit = {}) = navigate(ContactsNavigation, options)
fun NavGraphBuilder.contactsScreen(onBackClick: () -> Unit) = composable<ContactsNavigation> { ContactsRoute(onBackClick) }
