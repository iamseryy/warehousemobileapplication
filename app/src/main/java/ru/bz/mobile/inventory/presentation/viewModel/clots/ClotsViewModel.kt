package ru.bz.mobile.inventory.presentation.viewModel.clots

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.bz.mobile.inventory.presentation.ClotBundle
import ru.bz.mobile.inventory.presentation.CwarItemBundle
import ru.bz.mobile.inventory.presentation.CwarItemClotBundle
import ru.bz.mobile.inventory.presentation.ResourcesProvider
import ru.bz.mobile.inventory.presentation.adapter.ClotActionListener
import ru.bz.mobile.inventory.presentation.adapter.ClotAdapter
import ru.bz.mobile.inventory.model.clots.Clot
import ru.bz.mobile.inventory.model.clots.ClotModel
import ru.bz.mobile.inventory.data.room.ClotRepository

typealias ClotListener = (clots: List<Clot>) -> Unit

class ClotsViewModel(
    private val repo: ClotRepository,
    private val resources: ResourcesProvider
) : ViewModel() {

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    val adapter = ClotAdapter(object : ClotActionListener {

        override fun onClick(clot: Clot) {
            check(clot)
        }

        override fun onLongClick(clot: Clot) {
            setClot(clot)
            navigateLocasFragment()
        }

    })
    private val model = ClotModel()

    fun onViewCreated() {
        loadFromRepo()
        checkPrevious()
        updateView()
    }

    fun onStop() {

    }

    private fun updateView() {
        fabVisibility.postValue(if (clot.value == null) View.INVISIBLE else View.VISIBLE)
        noData.postValue(if (clots.size == 0) View.VISIBLE else View.INVISIBLE)
    }

    private fun sendAction(action: Action) {
        _actions.trySend(action)
    }

    private var clots = mutableListOf<Clot>()
    val clot = MutableLiveData<Clot?>()
    var fabVisibility = MutableLiveData<Int>() // visible - 0 invisible - 1 gone - 2
    private var previousClotIndex: Int = -1

    val noData = MutableLiveData<Int>() // visible - 0 invisible - 1 gone - 2

    private var listeners = mutableListOf<ClotListener>()

    private fun loadFromRepo() {
        if (model.cwar.isEmpty() || model.item.isEmpty())
            return
        removeListeners()
        clots = getClotsFromRepo(model.cwar, model.item).toMutableList()
        addListener { adapter.data = it }
    }

    fun onBundleResult(bundle: CwarItemBundle?) {
        bundle?.let { bundle ->
            val (cwar, item) = bundle
            model.cwar = cwar
            model.item = item
            onViewCreated()
        }
    }

    fun getCwarItemClotBundle(): CwarItemClotBundle {

        return CwarItemClotBundle(cwar = model.cwar, item = model.item, clot = model.clot!!.clot)
    }
    fun getClotBundle(): ClotBundle {

        return ClotBundle(clot = model.clot!!.clot)
    }

    fun check(clot: Clot) {
        val index = clots.indexOfFirst { it.id == clot.id }
        if (index == -1) return

        clots = ArrayList(clots)


        val isChecked = !clots[index].isChecked ?: false

        if (previousClotIndex != index) {

            if (previousClotIndex > -1) clots[previousClotIndex] =
                clots[previousClotIndex].copy(isChecked = false)
            clots[index] = clots[index].copy(isChecked = isChecked)
            previousClotIndex = if (isChecked) index else -1
        } else {
            clots[index] = clots[index].copy(isChecked = isChecked)
            previousClotIndex = if (isChecked) index else -1
        }
        setClot(if (isChecked) clots[index] else null)
        notifyChanges()
    }

    private fun checkPrevious() {
        if (previousClotIndex == -1) return
        if (clots.size == 0) return
        check(clots[previousClotIndex])
    }

    fun setClot(clot: Clot?) {
        if (clot != null) {
            previousClotIndex = clots.indexOfFirst { it.id == clot.id }
        }
        model.clot = clot
        this.clot.postValue(clot)
        fabVisibility.postValue(if (clot == null) View.INVISIBLE else View.VISIBLE)
    }

    fun navigateLocasFragment() {
        sendAction(Action.navigateLocasFragment)
    }
    fun navigateMainFragment() {
        sendAction(Action.navigateMainFragment)
    }

    fun addListener(listener: ClotListener) {
        listeners.add(listener)
        listener.invoke(clots)
    }

    fun removeListener(listener: ClotListener) {
        listeners.remove(listener)
        listener.invoke(clots)
    }

    fun removeListeners() {
        listeners.clear()
    }

    private fun notifyChanges() = listeners.forEach { it.invoke(clots) }
    override fun onCleared() {
        super.onCleared()
    }

    private fun getClotsFromRepo(cwar: String, item: String): List<Clot> {
        val clotsRepo = repo.getClotsListGroupedByClotLocaUnitPornSync(cwar, item)
        return clotsRepo.mapIndexed { id, item -> item.toClot(id = id) }
    }

    private fun getFakeClots(): List<Clot> {
        return listOf(
            Clot(
                id = 1,
                isEnabled = false,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "Fсновное",
                locaSize = 1,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 2,
                clot = "059999-MIG-600",
                isEnabled = false,
                porn = "C01000224",
                loca = "Fсновное",
                locaSize = 1,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 3,
                isEnabled = false,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "Fсновное",
                locaSize = 1,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 4,
                isEnabled = false,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "Fсновное",
                locaSize = 1,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 5,
                isEnabled = false,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "Fсновное",
                locaSize = 1,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 6,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 7,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 8,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 9,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 10,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
            Clot(
                id = 11,
                clot = "059999-MIG-600",
                porn = "C01000224",
                loca = "",
                locaSize = 2,
                qstrSum = 15.0,
                qntySum = 1.0,
                unit = "fake",
            ),
        )
    }
}

class ClotsViewModelFactory(
    private val repo: ClotRepository,
    private val resourcesProvider: ResourcesProvider
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClotsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClotsViewModel(repo, resourcesProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}