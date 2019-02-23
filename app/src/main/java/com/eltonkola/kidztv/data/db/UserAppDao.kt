package com.eltonkola.kidztv.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.eltonkola.kidztv.model.db.UserApp
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface UserAppDao {
    @Query("SELECT * FROM userapp")
    fun getAll(): List<UserApp>


    @Query("SELECT * FROM userapp")
    fun getAllRx(): Flowable<List<UserApp>>

//
//    @Query("SELECT * FROM userapp WHERE uid IN (:userappIds)")
//    fun loadAllByIds(userappIds: IntArray): List<UserApp>
//
//    @Query(
//        "SELECT * FROM userapp WHERE packageName LIKE :packageName AND " +
//                "enabled LIKE :enabled LIMIT 1"
//    )
//    fun findByValues(packageName: String, enabled: Boolean): UserApp

//    @Insert
//    fun insertAll(vararg userapps: UserApp)

    @Insert
    fun insert(app: UserApp) : Completable

    @Delete
    fun delete(app: UserApp) : Completable
}

