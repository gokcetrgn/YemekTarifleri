package com.gokcenaztorgan.yemektarifleri.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gokcenaztorgan.yemektarifleri.model.Tarif
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface TarifDAO {

    @Query("SELECT * FROM TARIF")
    fun getAll() : Flowable<List<Tarif>>

    @Insert
    fun insert(tarif: Tarif) : Completable

    @Delete
    fun delete(tarif: Tarif) : Completable

    @Query("SELECT * FROM TARIF where id = :id")
    fun findByID(id : Int ) : Flowable<Tarif>

}