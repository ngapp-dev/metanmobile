package com.ngapp.metanmobile.core.ui.util

/**
 * Opens the platform's open-source-licenses screen. Android shows the real generated list
 * (`OssLicensesMenuActivity`, via the `com.google.android.gms.oss-licenses-plugin` gradle plugin
 * applied at `:app`); iOS has no equivalent screen wired up yet, so this is a no-op there.
 */
expect fun openOssLicenses()
