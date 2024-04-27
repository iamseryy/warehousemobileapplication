package ru.bz.mobile.inventory.data.local.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.bz.mobile.inventory.domain.model.DataStoreSave


const val USER_PREFERENCES_NAME = "user_preferences"
val Context.datastore : DataStore< Preferences> by  preferencesDataStore(name = USER_PREFERENCES_NAME)

class DataStoreRepository(
    private val context: Context
){
    fun load(): Flow<DataStoreSave> {
        return context.datastore.data.map { preferences ->
            DataStoreSave(
                isInventoryDataImported = preferences[IS_INVENTORY_DATA_IMPORTED],
                iopSerialized = preferences[IOP_SERIALIZED]
            )
        }
    }

    suspend fun save(data: DataStoreSave) = coroutineScope {
        context.datastore.edit { pref ->
            data.isInventoryDataImported?.let { pref[IS_INVENTORY_DATA_IMPORTED] = it }
            data.iopSerialized?.let { pref[IOP_SERIALIZED] = it }
        }
    }

    private  companion  object {
        val IS_INVENTORY_DATA_IMPORTED = booleanPreferencesKey("IS_INVENTORY_DATA_IMPORTED")
        val IOP_SERIALIZED = stringPreferencesKey("IOP")
    }
}