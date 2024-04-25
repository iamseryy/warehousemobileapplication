package ru.bz.mobile.inventory.presentation.viewModel.main

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.presentation.viewModel.BindingData
import ru.bz.mobile.inventory.presentation.viewModel.BindingDataExtendedFloatingActionButton
import ru.bz.mobile.inventory.presentation.viewModel.BindingDataMenuItem
import ru.bz.mobile.inventory.presentation.viewModel.BindingDataSwitch
import ru.bz.mobile.inventory.presentation.ClotBundle
import ru.bz.mobile.inventory.presentation.ClotLocaBundle
import ru.bz.mobile.inventory.presentation.CwarItemBundle
import ru.bz.mobile.inventory.presentation.CwarItemClotBundle
import ru.bz.mobile.inventory.model.main.DataMatrix
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.presentation.ResourcesProvider
import ru.bz.mobile.inventory.presentation.ResultProvider
import ru.bz.mobile.inventory.config.DataStoreManager
import ru.bz.mobile.inventory.presentation.controllers.BarcodeController
import ru.bz.mobile.inventory.model.DataStoreSave
import ru.bz.mobile.inventory.model.IOP
import ru.bz.mobile.inventory.model.main.MainModel
import ru.bz.mobile.inventory.model.main.ScanResultListener
import ru.bz.mobile.inventory.model.main.Validator
import ru.bz.mobile.inventory.data.room.MainRepository
import ru.bz.mobile.inventory.util.GsonSerializer
import ru.bz.mobile.inventory.util.YesNo
import ru.bz.mobile.inventory.presentation.view.main.MainActivity
import ru.bz.mobile.inventory.presentation.view.main.MainFragment
import java.util.Date

class MainViewModel(
    private val repo: MainRepository,
    private val barcodeController: BarcodeController,
    private val resources: ResourcesProvider,
    private val dataStore: DataStoreManager
) :
    ViewModel() {
    companion object {
        val TAG: String =
            MainActivity::class.java.simpleName + " " +
                    MainFragment::class.java.simpleName + " " +
                    MainViewModel::class.java.simpleName + " "
    }

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    private val validator = Validator(repo)
    private val model = MainModel()

    val cwar = MutableLiveData<BindingData>()
    val loca = MutableLiveData<BindingData>()
    val item = MutableLiveData<BindingData>()
    val clot = MutableLiveData<BindingData>()

    val stkr = MutableLiveData<BindingDataSwitch>()
    val lock = MutableLiveData<BindingDataSwitch>()
    val illiquid = MutableLiveData<BindingDataSwitch>()
    val predictedAction = MutableLiveData<String?>()


    var canEdit = MutableLiveData<BindingDataMenuItem>()
    var edited = MutableLiveData<Boolean>()
    var canNavigateClots = MutableLiveData<Int>() //0-visible 2-gone
    var canSave = MutableLiveData<BindingDataExtendedFloatingActionButton>()

    private val onResult = object : ScanResultListener {
        override fun onResult(result: String) {
            if (!model.edited)
                this@MainViewModel.onResult(result)
        }

        override fun onFailure(message: String) {

        }
    }

    private fun onResult(result: String) {
        createDataMatrix(result).run {
            if (isNotEmpty()) {
                onDataMatrix(this)
                return
            }
        }

        if (checkIsCwar(result)) {
            onCwar(result, ResultProvider.Scanner)
            return
        }
        if (checkIsLoca(result)) {
            onLoca(result, ResultProvider.Scanner)
            return
        }

        when (getScanRequest()) {
            ScanRequest.CWAR -> {
                onCwar(result, ResultProvider.Scanner); return
            }

            ScanRequest.LOCA -> {
                onLoca(result, ResultProvider.Scanner); return
            }

            ScanRequest.ITEM -> {
                onItem(result, ResultProvider.Scanner); return
            }

            ScanRequest.CLOT -> {
                onClot(result, ResultProvider.Scanner); return
            }

            ScanRequest.NONE -> {}
        }
    }

    private fun createDataMatrix(result: String): DataMatrix {
        return result.split(resources.DATAMATRIX_DELIMITER).filter { it.contains("=") }.associate {
            val (left, right) = it.split("=")
            left.lowercase() to right
        }.let { map ->
            DataMatrix(
                cwar = map["cwar"], loca = map["loca"], item = map["item"], clot = map["clot"]
            )
        }
    }


    private fun getScanRequest(): ScanRequest {
        return when {
            model.iopIsEmpty -> ScanRequest.CWAR

            model.cwar.isEmpty() || !model.cwarError.isNullOrEmpty() -> ScanRequest.CWAR
            model.loca.isEmpty() || !model.locaError.isNullOrEmpty() -> ScanRequest.LOCA
            model.item.isEmpty() || !model.itemError.isNullOrEmpty() -> ScanRequest.ITEM
            model.clot.isEmpty() || !model.clotError.isNullOrEmpty() -> ScanRequest.CLOT
            else -> ScanRequest.NONE
        }
    }

    enum class ScanRequest {
        NONE,
        CWAR,
        LOCA,
        ITEM,
        CLOT
    }

    private fun checkIsCwar(result: String): Boolean {
        return repo.findCwarSync(result.uppercase())
    }

    private fun checkIsLoca(result: String): Boolean {
        return repo.findLocaByCwarSync(cwar = model.cwar, result)
    }

    private fun onDataMatrix(dataMatrix: DataMatrix) {
        dataMatrix.clot?.let { validateClotScanner(it) }
        dataMatrix.item?.let { validateItemScanner(it) }
        dataMatrix.loca?.let { validateLocaScanner(it) }
        dataMatrix.cwar?.let { validateCwarScanner(it) }
        onModelChanged()
    }

    private fun onCwar(cwar: String, resultProvider: ResultProvider = ResultProvider.Input) {

        when (resultProvider) {
            ResultProvider.Scanner -> {
                validateCwarScanner(cwar)
            }

            ResultProvider.Input -> {
                validateCwarInput(cwar)
            }
        }
        onModelChanged()
    }

    private fun onLoca(loca: String, resultProvider: ResultProvider = ResultProvider.Input) {
        when (resultProvider) {
            ResultProvider.Scanner -> {
                validateLocaScanner(loca)
            }

            ResultProvider.Input -> {
                validateLocaInput(loca)
            }
        }
        onModelChanged()
    }

    private fun onItem(item: String, resultProvider: ResultProvider = ResultProvider.Input) {
        when (resultProvider) {
            ResultProvider.Scanner -> {
                validateItemScanner(item)
            }

            ResultProvider.Input -> {
                validateItemInput(item)
            }
        }
        onModelChanged()
    }

    private fun onClot(clot: String, resultProvider: ResultProvider = ResultProvider.Input) {
        when (resultProvider) {
            ResultProvider.Scanner -> {
                validateClotScanner(clot)
            }

            ResultProvider.Input -> {
                validateClotInput(clot)
            }
        }
        onModelChanged()
    }

    fun onViewCreated() {
        loadDataStoreData { validateAll(skipLoca = true); updateView() }
        barcodeController.create()
        barcodeAddListeners()
    }

    fun onPause() {
        barcodeController.pause()
    }

    fun onResume() {
        barcodeController.resume()
    }

    fun onStop() {
        saveDataStoreData()
    }

    fun onDestroyView() {
        barcodeRemoveListeners()
        barcodeController.pause()
    }

    fun onDestroy() {
        barcodeRemoveListeners()
        barcodeController.destroy()
    }

    fun beginEdit() {
        prepareForm()
        sendAction(Action.beginEdit)
    }

    fun endEdit() {
        commitForm()
        sendAction(Action.endEdit)
    }

    fun undoEdit() {
        undoForm()
        sendAction(Action.undoEdit)
    }

    fun getCwarItemBundle(): CwarItemBundle {
        return CwarItemBundle(cwar = model.cwar, item = model.item)
    }

    fun getCwarItemClotBundle(): CwarItemClotBundle {
        return CwarItemClotBundle(cwar = model.cwar, item = model.item, clot = model.clot)
    }

    fun delete() {
        sendAction(Action.showDeleteDialog(positiveAction = {
            clearModel(
                clearCwar = false,
                clearLoca = false
            )
        }))
    }

    private fun onModelChanged() {
        saveDataStoreData()
        updateView()
        if (canSave())
            save()
    }

    private fun clearModel(clearCwar: Boolean = true, clearLoca: Boolean = true) {
        val cwar = model.iop.cwar
        val loca = model.iop.loca
        model.iop.clear()
        if (!clearCwar) model.iop.cwar = cwar
        if (!clearLoca) model.iop.loca = loca
        clearErrors()
        onModelChanged()
        sendAction(Action.showMessage(R.string.delete_success))
    }

    private fun clearErrors() {
        model.cwarError = null
        model.locaError = null
        model.itemError = null
        model.clotError = null

        cwar.postValue(BindingData(model.cwar, error = model.cwarError))
        loca.postValue(BindingData(model.loca, error = model.locaError))
        item.postValue(BindingData(model.item, error = model.itemError))
        clot.postValue(BindingData(model.clot, error = model.clotError))
    }

    fun onBundleResult(bundle: ClotBundle?) {
        bundle?.let { bundle ->
            val (clot) = bundle
            model.clot = clot
            onModelChanged()
        }
    }

    fun onBundleResult(bundle: ClotLocaBundle?) {
        bundle?.let { bundle ->
            val (clot, loca) = bundle
            model.clot = clot
            model.loca = loca
            onModelChanged()
        }
    }

    private fun validateAll(
        skipCwar: Boolean = false,
        skipClot: Boolean = false,
        skipItem: Boolean = false,
        skipLoca: Boolean = false
    ) {
        validateClotInput(model.clot, skipClot)
        validateItemInput(model.item, skipItem)
        validateLocaInput(model.loca, skipLoca)
        validateCwarInput(model.cwar, skipCwar)
    }

    private fun loadDataStoreData(after: (() -> Unit)? = null) = viewModelScope.launch {
        dataStore.load().collect() { dataStore ->
            dataStore.isInventoryDataImported?.let { model.isInventoryDataImported = it }
            dataStore.iopSerialized?.let { serialized ->
                GsonSerializer.deserializeObject<IOP>(serialized)?.let { iop ->
                    model.iop = iop
                }
            }
            after?.invoke()
            this.cancel()
        }

        Log.d(TAG, "loadDataStoreData# IOP: $dataStore.iopSerialized")
    }

    private fun barcodeAddListeners() {
        barcodeController.addListener(onResult)
    }

    private fun barcodeRemoveListeners() {
        barcodeController.removeListener()
    }

    private fun saveDataStoreData(callback: (() -> Unit)? = null) = viewModelScope.launch {
        dataStore.save(DataStoreSave(iopSerialized = model.iop.serialized()))
        callback?.invoke()
    }

    private fun prepareForm() {
        model.makeBackup()
        clearErrors()
        model.edited = true
        onModelChanged()
    }

    private fun commitForm() {
        validateAll(skipLoca = true)
        model.clearBackup()
        model.edited = false
        onModelChanged()
        sendAction(Action.clearFocus)
    }

    private fun undoForm() {
        model.loadFromBackup()
        edited.postValue(false)
        onModelChanged()
        sendAction(Action.clearFocus)
    }

    private fun validateCwarInput(cwar: String, skip: Boolean = false) {
        model.cwar = model.cwar.uppercase()
        if (skip)
            return

        if (cwar.isEmpty()) {
            clearCwar()
            return
        }
        validator.cwarIsCwar(cwar).let { isValid ->
            if (isValid) {
                if (cwar.uppercase() != model.cwar.uppercase()) {
                    clearLoca()
                    clearItem()
                    clearClot()
                }
                model.cwar = cwar.uppercase()
                model.cwarError = null

            } else {
                model.cwar = cwar.uppercase()
                model.cwarError = resources.getString(R.string.cwar_not_found)
                clearLoca()
                clearItem()
                clearClot()
            }
        }
    }

    private fun validateCwarScanner(cwar: String, skip: Boolean = false) {
        model.cwar = model.cwar.uppercase()
        if (skip)
            return

        if (cwar.isEmpty()) {
            sendAction(Action.showMessage(R.string.cwar_not_found))
            return
        }
        validator.cwarIsCwar(cwar).let { isValid ->
            if (isValid) {
                if (cwar.uppercase() != model.cwar.uppercase()) {
                    clearLoca()
                    clearItem()
                    clearClot()
                }
                model.cwar = cwar.uppercase()
                model.cwarError = null

            } else {
                sendAction(Action.showMessage(R.string.cwar_not_found))
            }
        }
    }

    private fun validateLocaInput(loca: String, skip: Boolean = false) {
        model.loca = model.loca.uppercase()
        if (skip)
            return

        if (loca.isEmpty()) {
            clearLoca()
            return
        }
        validator.locaIsLoca(model.cwar, loca).let { isValid ->
            if (isValid) {
                if (loca.uppercase() != model.loca.uppercase()) {
                    clearItem()
                    clearClot()
                }
                model.loca = loca.uppercase()
                model.locaError = null

            } else {
                model.loca = loca.uppercase()
                model.locaError = resources.getString(R.string.loca_not_found)
                clearItem()
                clearClot()
            }
        }
    }

    private fun validateLocaScanner(loca: String, skip: Boolean = false) {
        model.loca = model.loca.uppercase()
        if (skip)
            return

        if (loca.isEmpty()) {
            sendAction(Action.showMessage(R.string.loca_not_found))
            return
        }
        validator.locaIsLoca(model.cwar, loca).let { isValid ->
            if (isValid) {
                if (loca.uppercase() != model.loca.uppercase()) {
                    clearItem()
                    clearClot()
                }
                model.loca = loca.uppercase()
                model.locaError = null

            } else {
                sendAction(Action.showMessage(R.string.loca_not_found))
            }
        }
    }

    private fun validateItemInput(item: String, skip: Boolean = false) {
        model.item = model.item.uppercase()
        if (skip)
            return

        if (item.isEmpty()) {
            clearItem()
            return
        }
        validator.itemIsValid(item).let { isValid ->
            if (isValid) {
                model.item = item.uppercase()
                model.itemError = null
            } else {
                model.item = item.uppercase()
                model.itemError = resources.getString(R.string.wrong_item)
                clearClot()
            }
        }
    }

    private fun validateItemScanner(item: String, skip: Boolean = false) {
        model.item = model.item.uppercase()
        if (skip)
            return

        if (item.isEmpty()) {
            sendAction(Action.showMessage(R.string.wrong_item))
            return
        }
        validator.itemIsValid(item).let { isValid ->
            if (isValid) {
                model.item = item.uppercase()
                model.itemError = null
            } else {
                sendAction(Action.showMessage(R.string.wrong_item))
            }
        }
    }

    private fun validateClotInput(clot: String, skip: Boolean = false) {
        model.clot = model.clot.uppercase()
        if (skip)
            return

        if (clot.isEmpty()) {
            clearClot()
            return
        }
        validator.clotIsValid(clot).let { isValid ->
            if (isValid) {
                model.clot = clot.uppercase()
                model.clotError = null
            } else {
                model.clot = clot.uppercase()
                model.clotError = resources.getString(R.string.wrong_clot)
            }
        }
    }

    private fun validateClotScanner(clot: String, skip: Boolean = false) {
        model.clot = model.clot.uppercase()
        if (skip)
            return

        if (clot.isEmpty()) {
            sendAction(Action.showMessage(R.string.wrong_clot))
            return
        }
        validator.clotIsValid(clot).let { isValid ->
            if (isValid) {
                model.clot = clot.uppercase()
                model.clotError = null
            } else {
                sendAction(Action.showMessage(R.string.wrong_clot))
            }
        }
    }

    private fun clearCwar() {
        model.cwar = ""
        model.cwarError = null
        clearLoca()
        clearClot()
        clearItem()
    }

    private fun clearLoca() {
        model.loca = ""
        model.locaError = null
        clearClot()
        clearItem()
    }

    private fun clearItem() {
        model.item = ""
        model.itemError = null
        clearClot()
    }

    private fun clearClot() {
        model.clot = ""
        model.clotError = null
    }

    private fun updateView() {
        cwar.postValue(BindingData(model.cwar, error = model.cwarError))
        loca.postValue(BindingData(model.loca, error = model.locaError))
        item.postValue(BindingData(model.item, error = model.itemError))
        clot.postValue(BindingData(model.clot, error = model.clotError))
        stkr.postValue(BindingDataSwitch(model.iop.stkr.flag))
        lock.postValue(BindingDataSwitch(model.iop.lock.flag))
        illiquid.postValue(BindingDataSwitch(model.iop.illiquid.flag))
        canSave.postValue(BindingDataExtendedFloatingActionButton(isVisible = canSave()))
        canNavigateClots.postValue(if (canNavigateClotsVisibility()) View.VISIBLE else ViewGroup.INVISIBLE)
        edited.postValue(model.edited)
        predictedAction.postValue(getPredictedActionText())

        canEdit.postValue(
            BindingDataMenuItem(
                listOf(R.id.beginEdit, R.id.delete),
                isVisible = canEdit()
            )
        )

    }

    private fun getPredictedActionText(): String? {
        return when (getScanRequest()) {
            ScanRequest.CWAR -> resources.getString(R.string.scanCwar)
            ScanRequest.LOCA -> resources.getString(R.string.scanLoca)
            ScanRequest.ITEM -> resources.getString(R.string.scanItem)
            ScanRequest.CLOT -> resources.getString(R.string.scanClot)
            ScanRequest.NONE -> resources.getString(R.string.enterQnty)
        }
    }

    private fun canSave(): Boolean {
        return !model.edited && model.noErrors && model.iopIsFull
    }

    private fun canNavigateClotsVisibility(): Boolean {
        return (!model.edited && model.cwar.isNotEmpty() && model.loca.isNotEmpty() && model.item.isNotEmpty())
    }

    private fun canEdit(): Boolean {
        return model.cwar.isNotEmpty()
    }

    fun save() {
        if (!model.isInventoryDataImported) {
            sendAction(Action.showMessage(R.string.inventory_data_not_imported))
            return
        }
        if (canSave()) {
            getIOPByIndex()?.let { dto ->
                sendAction(
                    Action.showSaveDialog(
                        loca = model.loca,
                        inRepo = true,
                        dto = dto,
                        unit = dto.unit,
                        positiveAction = { qnty ->
                            updateModelDB(
                                qnty,
                                dto
                            ); clearModel(clearCwar = false, clearLoca = false)
                        })
                )
                return
            } // если запись существует в базе, то перезаписываем

            getIOPListByCwarItemClotSync().let { locas ->
                when {
                    locas.isEmpty() -> {
                        sendAction(
                            Action.showClotsDialog(
                                positiveAction = {
                                    sendAction(Action.navigateClotsFragment)
                                },
                                negativeAction = {
                                    val unit = repo.getUnitByItem(model.item)
                                    sendAction(
                                        Action.showSaveDialog(
                                            loca = model.loca,
                                            inRepo = false,
                                            dto = null,
                                            unit = unit,
                                            positiveAction = { qnty ->
                                                insertModelDB(
                                                    qnty,
                                                    dto = null,
                                                    unit = unit
                                                ); clearModel(clearCwar = false, clearLoca = false)
                                            })
                                    )
                                }
                            )
                        )
                        return
                    }

                    locas.size == 1 -> {

                        sendAction(
                            Action.showLocaDialog(
                                loca = locas.first().loca,
                                positiveAction = {
                                    replaceModelDb(locas.first())
                                },
                                negativeAction = {
                                    val dto = locas.first()
                                    sendAction(
                                        Action.showSaveDialog(
                                            loca = model.loca,
                                            inRepo = false,
                                            dto = dto,
                                            unit = dto.unit,
                                            positiveAction = { qnty ->
                                                insertModelDB(
                                                    qnty,
                                                    dto = dto,
                                                    unit = dto.unit
                                                ); clearModel(clearCwar = false, clearLoca = false)
                                            })
                                    )
                                }
                            )
                        ); return
                    }

                    else -> {
                        sendAction(
                            Action.showLocasDialog(locas = locas.joinToString(limit = 2) { it.loca },
                                positiveAction = { sendAction(Action.navigateLocasFragment) },
                                negativeAction = {
                                    val dto = locas.first()
                                    sendAction(
                                        Action.showSaveDialog(
                                            loca = model.loca,
                                            inRepo = false,
                                            dto = dto,
                                            unit = dto.unit,
                                            positiveAction = { qnty ->
                                                insertModelDB(
                                                    qnty,
                                                    dto = dto,
                                                    unit = dto.unit
                                                ); clearModel(clearCwar = false, clearLoca = false)
                                            })
                                    )
                                })
                        ); return
                    }
                }
            }
        }
    }

    private fun replaceModelDb(dto: IOP.Dto) = viewModelScope.launch {
        model.iop.toDTO().copyPartial(dto).apply {
            this.sloca = dto.loca
            this.utcDate = Date().time
        }.let { newDto ->
            repo.replace(newDto = newDto, oldDto = dto)
        }
        save()
    }

    private fun insertModelDB(qnty: Double, unit:String?, dto: IOP.Dto?) = viewModelScope.launch {
        model.iop.toDTO().copyPartial(dto).apply {
            this.qnty = qnty
            this.utcDate = Date().time
            this.qstr = 0.0
            this.unit = unit ?: ""
        }.let { repo.insert(it) }
        sendAction(Action.showMessage(R.string.save_success))
    }

    private fun updateModelDB(qnty: Double, dto: IOP.Dto) = viewModelScope.launch {
        model.iop.toDTO().copyPartial(dto).apply {
            this.qnty = qnty
            this.utcDate = Date().time
        }.let { repo.update(it) }
        sendAction(Action.showMessage(R.string.save_success))
    }

    private fun getIOPByIndex(): IOP.Dto? {
        return repo.getIOPByIndexSync(
            cwar = model.cwar,
            loca = model.loca,
            item = model.item,
            clot = model.clot
        )
    }

    private fun getIOPListByCwarItemClotSync(): List<IOP.Dto> {
        return repo.getIOPListByCwarItemClotSync(
            cwar = model.cwar,
            item = model.item,
            clot = model.clot,
            excludeLoca = model.loca,
        )
    }

    private fun sendAction(action: Action) {
        _actions.trySend(action)
    }

    fun setLoca(loca: String) {
        model.loca = loca
    }

    fun setItem(item: String) {
        model.item = item
    }

    fun setClot(clot: String) {
        model.clot = clot
    }

    fun setStkr(isChecked: Boolean) {
        model.iop.stkr = YesNo.getYesNo(isChecked)
//        stkr.postValue(BindingDataSwitch(isChecked))
    }

    fun setLock(isChecked: Boolean) {
        model.iop.lock = YesNo.getYesNo(isChecked)
//        lock.postValue(BindingDataSwitch(isChecked))
    }

    fun setIlliquid(isChecked: Boolean) {
        model.iop.illiquid = YesNo.getYesNo(isChecked)
//        illiquid.postValue(BindingDataSwitch(isChecked))
    }
    // endregion

    override fun onCleared() {
        super.onCleared()
    }
}

class MainViewModelFactory(
    private val repo: MainRepository,
    private val barcodeController: BarcodeController,
    private val resourcesProvider: ResourcesProvider,
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo, barcodeController, resourcesProvider, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
