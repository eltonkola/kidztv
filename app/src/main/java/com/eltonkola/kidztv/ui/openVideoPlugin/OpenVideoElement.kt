package com.eltonkola.kidztv.ui.openVideoPlugin

data class OpenVideoElement(
    val description: String,
    val sources: List<Source>,
    val subtitle: String,
    val thumb: String,
    val title: String
) {
    data class Source(
        val name: String,
        val url: String
    )
}