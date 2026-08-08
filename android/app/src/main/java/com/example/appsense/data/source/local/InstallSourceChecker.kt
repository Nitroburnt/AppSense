package com.example.appsense.data.source.local

import javax.inject.Inject

class InstallSourceChecker @Inject constructor() {

    fun isSideloaded(installSourcePackage: String?): Boolean =
        installSourcePackage == null || installSourcePackage !in KNOWN_STORE_PACKAGES

    fun isFromKnownStore(installSourcePackage: String?): Boolean =
        installSourcePackage in KNOWN_STORE_PACKAGES

    companion object {
        private val KNOWN_STORE_PACKAGES = setOf(
            "com.android.vending",
            "org.fdroid.fdroid",
            "com.aurora.store",
            "com.sec.android.app.samsungapps",
            "com.amazon.venezia",
            "com.huawei.appmarket"
        )
    }
}
