package ru.bz.mobile.inventory.presentation.viewModel.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import ru.bz.mobile.inventory.presentation.viewModel.BindingData
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.data.local.resource.ResourcesRepository
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    private val resources: ResourcesRepository
) : ViewModel() {

    private val _actions: Channel<Action> = Channel(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    val password = MutableLiveData<BindingData>()

    fun login() {
        if (validatePassword()) {
            loginSuccess()
        }
    }

    private fun loginSuccess() {
        sendAction(Action.navigate)
    }

    private fun sendAction(action: Action) {
        _actions.trySend(action)
    }

    private fun validatePassword(): Boolean {

        with(password.value) {
            when {
                this?.text?.isEmpty() ?: true -> {
                    password.postValue(
                        BindingData(
                            "",
                            resources.getString(R.string.empty_password)
                        )
                    )
                    return false
                }

                this?.text != resources.getString(R.string.properties_password) -> {
                    password.postValue(
                        BindingData(
                            "",
                            resources.getString(R.string.wrong_password)
                        )
                    )
                    return false
                }

                else -> {}
            }
        }
        return true
    }

    override fun onCleared() {
        super.onCleared()
    }
}



class LoginViewModelFactory @Inject constructor(
    private val viewModel: LoginViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}