package com.eltonkola.kidztv.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class UserApp(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "packageName") var packageName: String?,
    @ColumnInfo(name = "enabled_date") var enabledDate: Date?
)