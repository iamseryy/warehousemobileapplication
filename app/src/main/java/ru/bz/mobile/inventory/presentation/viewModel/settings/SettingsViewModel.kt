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
import ru.bz.mobile.inventory.domain.model.DataStoreSave
import ru.bz.mobile.inventory.domain.model.settings.SettingsModel
import ru.bz.mobile.inventory.domain.usecase.DataStoreUseCase
import ru.bz.mobile.inventory.domain.usecase.FileUseCase
import ru.bz.mobile.inventory.domain.usecase.ResourcesUseCase
import ru.bz.mobile.inventory.domain.usecase.SettingsUseCase
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsUseCase: SettingsUseCase,
    private val resourcesUseCase: ResourcesUseCase,
    private val dataStoreUseCase: DataStoreUseCase,
    private val fileUseCase: FileUseCase
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
            settingsUseCase.deleteAll()
            val encoding =
                Charset.forName(resourcesUseCase.getString(R.string.properties_file_import_encoding))
            if (inputStream == null) {
                sendAction(Action.showMessage(R.string.import_file_is_empty))
                return@launch
            }
            val iops = fileUseCase.importCsv(inputStream, encoding)
            if (iops.isEmpty()) {
                sendAction(Action.showMessage(R.string.import_file_data_is_empty))
                return@launch
            }
            dataStoreUseCase.save(DataStoreSave(isInventoryDataImported = true))
            settingsUseCase.insertAll(iops)
            sendAction(Action.showMessage(R.string.inventory_data_imported))
        }
    }

    private fun export() {
        viewModelScope.launch {
            val iops = settingsUseCase.getAllNotUTCZeroSync()
            val file = getFullPathFile()
            val encoding =
                Charset.forName(resourcesUseCase.getString(R.string.properties_file_export_encoding))
            fileUseCase.exportCsv(file, iops, encoding)
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
        val fileName = resourcesUseCase.getString(R.string.properties_file_export)
        return Environment.getExternalStoragePublicDirectory("${Environment.DIRECTORY_DOWNLOADS}/$fileName")
            .outputStream()
    }
}

class SettingsViewModelFactory @Inject constructor(
    private val viewModel: SettingsViewModel
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}