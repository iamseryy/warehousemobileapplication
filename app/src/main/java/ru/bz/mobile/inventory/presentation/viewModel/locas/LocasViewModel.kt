package ru.bz.mobile.inventory.presentation.viewModel.locas

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.bz.mobile.inventory.presentation.ClotLocaBundle
import ru.bz.mobile.inventory.presentation.CwarItemClotBundle
import ru.bz.mobile.inventory.presentation.adapter.LocaActionListener
import ru.bz.mobile.inventory.presentation.adapter.LocaAdapter
import ru.bz.mobile.inventory.domain.model.locas.Loca
import ru.bz.mobile.inventory.domain.model.locas.LocaModel
import ru.bz.mobile.inventory.domain.usecase.LocaUseCase
import javax.inject.Inject

typealias LocaListener = (locas: List<Loca>) -> Unit

class LocasViewModel @Inject constructor(
    private val useCase: LocaUseCase
) : ViewModel() {

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()
    val noData = MutableLiveData<Int>() // 0 - visible 1 - invisible

    private val model = LocaModel()
    private var locas = mutableListOf<Loca>()
    val loca = MutableLiveData<Loca?>()
    var fabVisibility = MutableLiveData<Int>() // visible - 0 invisible - 1 gone - 2
    private var listeners = mutableListOf<LocaListener>()
    private var previousLocaIndex: Int = -1

    val adapter = LocaAdapter(object : LocaActionListener {

        override fun onClick(loca: Loca) {
            check(loca)
        }

        override fun onLongClick(loca: Loca) {
            //no need
        }

    })
    fun onViewCreated() {
        loadFromRepo()
        updateView()
    }
    private fun updateView() {
        fabVisibility.postValue(if (loca.value == null) View.INVISIBLE else View.VISIBLE)
        noData.postValue(if (locas.size == 0) View.VISIBLE else View.INVISIBLE)
    }
    fun onStop() {

    }


    private fun sendAction(action: Action) {
        _actions.trySend(action)
    }


    fun onBundleResult(bundle: CwarItemClotBundle?) {
        bundle?.let { bundle ->
            val (cwar, item, clot) = bundle
            model.cwar = cwar
            model.item = item
            model.clot = clot
            onViewCreated()
        }
    }

    private fun loadFromRepo() {
        if (model.cwar.isEmpty() || model.item.isEmpty() || model.clot.isEmpty())
            return
        removeListeners()
        locas = getLocasFromRepo(model.cwar, model.item, model.clot).toMutableList()
        addListener { adapter.data = it }
    }
    fun getClotLocaBundle(): ClotLocaBundle {
        return ClotLocaBundle(clot = model.clot, loca = model.loca!!.loca)
    }
    private fun getLocasFromRepo(cwar: String, item: String, clot: String): List<Loca> {
        val repo = useCase.getLocasListByCwarItemClotSync(cwar = cwar, item = item, clot = clot)
        return repo.mapIndexed { i, locaDTO -> locaDTO.toLoca(id = i) }
    }
    fun check(loca: Loca) {
        val index = locas.indexOfFirst { it.id == loca.id }
        if (index == -1) return

        locas = ArrayList(locas)


        val isChecked = !locas[index].isChecked

        if (previousLocaIndex != index) {

            if (previousLocaIndex > -1) locas[previousLocaIndex] =
                locas[previousLocaIndex].copy(isChecked = false)
            locas[index] = locas[index].copy(isChecked = isChecked)
            previousLocaIndex = if (isChecked) index else -1
        } else {
            locas[index] = locas[index].copy(isChecked = isChecked)
            previousLocaIndex = if (isChecked) index else -1
        }
        setLoca(if (isChecked) locas[index] else null)
        notifyChanges()
    }
    fun setLoca(loca: Loca?) {
        model.loca = loca
        this.loca.postValue(loca)
        fabVisibility.postValue(if (loca == null) View.INVISIBLE else View.VISIBLE)
    }
    fun navigateMainFragment() {
        sendAction(Action.navigateMainFragment)
    }

    fun addListener(listener: LocaListener) {
        listeners.add(listener)
        listener.invoke(locas)
    }

    fun removeListener(listener: LocaListener) {
        listeners.remove(listener)
        listener.invoke(locas)
    }
    fun removeListeners() {
        listeners.clear()
    }
    private fun notifyChanges() = listeners.forEach { it.invoke(locas) }

    override fun onCleared() {
        super.onCleared()
    }

}

class LocasViewModelFactory @Inject constructor(
    private val viewModel: LocasViewModel

) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}