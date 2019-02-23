package com.eltonkola.kidztv.model.settings

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment

class SettingsMenuItem(title: String, val external: Boolean, val icon: Drawable?, val fragment: Fragment? = null, val intent: Intent? = null) :
    BaseMenuItem(title)
