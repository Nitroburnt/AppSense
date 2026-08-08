package com.example.appsense.domain.model

enum class SortOrder(val label: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    USAGE_HIGH("Most used"),
    USAGE_LOW("Least used"),
    INSTALL_NEW("Newest"),
    INSTALL_OLD("Oldest"),
    SIZE_LARGE("Largest"),
    SIZE_SMALL("Smallest")
}
