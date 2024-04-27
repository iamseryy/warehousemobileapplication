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
import ru.bz.mobile.inventory.presentation.adapter.ClotActionListener
import ru.bz.mobile.inventory.presentation.adapter.ClotAdapter
import ru.bz.mobile.inventory.domain.model.clots.Clot
import ru.bz.mobile.inventory.domain.model.clots.ClotModel
import ru.bz.mobile.inventory.domain.usecase.ClotUseCase
import javax.inject.Inject

typealias ClotListener = (clots: List<Clot>) -> Unit

class ClotsViewModel @Inject constructor(
    private val useCase: ClotUseCase
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
        val clotsRepo = useCase.getClotsListGroupedByClotLocaUnitPornSync(cwar, item)
        return clotsRepo.mapIndexed { id, item -> item.toClot(id = id) }
    }

}

class ClotsViewModelFactory @Inject constructor(
    private val viewModel: ClotsViewModel
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClotsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}