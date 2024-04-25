package ru.bz.mobile.inventory.config

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
import ru.bz.mobile.inventory.model.DataStoreSave

class DataStoreManager(private val context: Context) {
    companion  object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "appPreferences")
        val IS_INVENTORY_DATA_IMPORTED = booleanPreferencesKey("IS_INVENTORY_DATA_IMPORTED")
        val IOP_SERIALIZED = stringPreferencesKey("IOP")
    }
    fun load(): Flow<DataStoreSave> {
        return context.dataStore.data.map { preferences ->
            DataStoreSave(
                isInventoryDataImported = preferences[IS_INVENTORY_DATA_IMPORTED],
                iopSerialized = preferences[IOP_SERIALIZED]
            )
        }
    }
    suspend fun save(data: DataStoreSave) = coroutineScope {
        context.dataStore.edit { pref ->
            data.isInventoryDataImported?.let { pref[IS_INVENTORY_DATA_IMPORTED] = it }
            data.iopSerialized?.let { pref[IOP_SERIALIZED] = it }
        }
    }
}