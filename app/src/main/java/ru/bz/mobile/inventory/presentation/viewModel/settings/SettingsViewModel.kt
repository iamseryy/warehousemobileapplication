package ru.bz.mobile.inventory.presentation.viewModel.settings

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.presentation.ResourcesProvider
import ru.bz.mobile.inventory.config.DataStoreManager
import ru.bz.mobile.inventory.model.DataStoreSave
import ru.bz.mobile.inventory.model.settings.SettingsModel
import ru.bz.mobile.inventory.data.room.SettingsRepository
import ru.bz.mobile.inventory.util.FileUtils
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class SettingsViewModel(
    private val repo: SettingsRepository,
    private val resources: ResourcesProvider,
    private val dataStore: DataStoreManager
) : ViewModel() {
    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    private val model = SettingsModel()
    fun setPermissions(permissions: Boolean) {
        model.hasPermissions = permissions
    }

    fun tryImport() {
        if (model.hasPermissions) {
            sendAction(Action.openFilePicker)
        } else {
            sendAction(Action.requestPermissions)
        }
    }

    fun tryExport() {
        if (model.hasPermissions) {
            export()
        } else {
            sendAction(Action.requestPermissions)
        }
    }

    fun import(inputStream: InputStream?) {
        viewModelScope.launch {
            repo.deleteAll()
            val encoding =
                Charset.forName(resources.getString(R.string.properties_file_import_encoding))
            if (inputStream == null) {
                sendAction(Action.showMessage(R.string.import_file_is_empty))
                return@launch
            }
            val iops = FileUtils.importCsv(inputStream, encoding)
            if (iops.isEmpty()) {
                sendAction(Action.showMessage(R.string.import_file_data_is_empty))
                return@launch
            }
            dataStore.save(DataStoreSave(isInventoryDataImported = true))
            repo.insertAll(iops)
            sendAction(Action.showMessage(R.string.inventory_data_imported))
        }
    }

    private fun export() {
        viewModelScope.launch {
            val iops = repo.getAllNotUTCZeroSync()
            val file = getFullPathFile()
            val encoding =
                Charset.forName(resources.getString(R.string.properties_file_export_encoding))
            FileUtils.exportCsv(file, iops, encoding)
            sendAction(Action.showMessage(R.string.inventory_data_exported))
        }
    }

    private fun sendAction(action: Action) {
        _actions.trySend(action)
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun getFullPathFile(): OutputStream {
        val fileName = resources.getString(R.string.properties_file_export)
        return Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOWNLOADS}/$fileName")
            .outputStream()
    }
}

class SettingsViewModelFactory(
    private val repo: SettingsRepository,
    private val resourcesProvider: ResourcesProvider,
    private val dataStore: DataStoreManager
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repo, resourcesProvider, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}