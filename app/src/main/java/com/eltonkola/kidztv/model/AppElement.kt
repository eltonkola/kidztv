package com.eltonkola.kidztv.model

import android.graphics.drawable.Drawable
import com.eltonkola.kidztv.model.db.UserApp

data class AppElement(val packageName: String,
                      val title: String,
                      val icon: Drawable,
                      val dbModel :UserApp)