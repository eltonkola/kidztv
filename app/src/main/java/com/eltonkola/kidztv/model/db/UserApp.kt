package com.eltonkola.kidztv.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserApp(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "packageName") var packageName: String?,
    @ColumnInfo(name = "enabled") var enabled: Boolean?
)