package ru.bz.mobile.inventory.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.bz.mobile.inventory.domain.model.IOP



@Dao
interface IOPDao {
    //  sync
    @Query("SELECT * FROM $WHINH501")
    fun getAllSync(): List<IOP.Dto>

    @Insert
    fun insertSync(dto: IOP.Dto)

    @Delete
    fun deleteSync(dto: IOP.Dto)

    @Query("SELECT * FROM $WHINH501 WHERE utcDate > 0")
    fun getAllNotUTCZeroSync(): List<IOP.Dto>



    @Query("SELECT * FROM $WHINH501 LIMIT 1")
    fun getFirstSync(): IOP.Dto?

    @Query(
        """
        SELECT cwar 
        FROM $WHINH501 
        WHERE cwar = :cwar
        LIMIT 1
        
        """
    )
    fun getCwarSync(cwar: String): String?

    @Query(
        """
        SELECT loca 
        FROM $WHINH501 
        WHERE 
            cwar = :cwar 
        AND loca = :loca
        LIMIT 1
        
        """
    )
    fun getLocaByCwarSync(cwar: String, loca: String): String?

    @Query(
        """
        SELECT loca 
        FROM $WHINH501 
        WHERE 
            cwar = :cwar 
        AND item = :item
        AND clot = :clot
        LIMIT 1
        
        """
    )
    fun getLocaByCwarItemClotSync(cwar: String, item: String, clot: String): String?

    @Query(
        """
        SELECT * 
        FROM $WHINH501 
        WHERE 
            cwar = :cwar
        AND loca != :excludeLoca
        AND item = :item
        AND clot = :clot
        
        """
    )
    fun getIOPListByCwarItemClotSync(
        cwar: String,
        item: String,
        clot: String,
        excludeLoca: String,
    ): List<IOP.Dto>

    @Query(
        """
        SELECT * 
        FROM $WHINH501 
        WHERE 
            cwar = :cwar 
        AND loca = :loca 
        AND item = :item 
        AND clot = :clot
        
        """
    )

    fun getIOPByIndexSync(cwar: String, loca: String, item: String, clot: String): IOP.Dto?
    @Query(
        """
        SELECT unit 
        FROM $WHINH501 
        WHERE item = :item
        LIMIT 1
        """
    )
    fun getUnitByItem(item: String): String?

    //  async
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dto: IOP.Dto)

    @Delete
    suspend fun delete(dto: IOP.Dto)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(dtos: List<IOP.Dto>)

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(dto: IOP.Dto)

    @Query("DELETE FROM $WHINH501")
    suspend fun deleteAll()

    @Transaction
    suspend fun replace(newDto: IOP.Dto, oldDto: IOP.Dto) {
        delete(oldDto)
        insert(newDto)
    }
}
