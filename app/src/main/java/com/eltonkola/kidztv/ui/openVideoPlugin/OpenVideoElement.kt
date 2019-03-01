package com.eltonkola.kidztv.ui.openVideoPlugin

data class OpenVideoElement(
    val description: String,
    val sources: List<Source>,
    val subtitle: String,
    val thumb: String,
    val title: String,
    var alreadyDownloaded: Boolean = false
) {
    data class Source(
        val name: String,
        val url: String
    )

    fun getUrl(): String {
        return sources[0].url
    }

    fun getFileName(): String {
        val url = getUrl()
        return url.substring(url.lastIndexOf("/"), url.length)
    }

}