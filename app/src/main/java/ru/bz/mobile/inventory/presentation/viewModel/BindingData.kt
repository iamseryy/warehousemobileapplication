package ru.bz.mobile.inventory.presentation.viewModel

import android.view.Menu
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import ru.bz.mobile.inventory.presentation.FieldValidator

data class BindingData(
    val text: String,
    val error: String? = null,
    val observe: Boolean = true,
) {
    val isErrorEnabled: Boolean
        get() = !error.isNullOrEmpty()
}

data class BindingDataSwitch(var isChecked: Boolean)
data class BindingDataMenuItem(val menuItemIds: List<Int>, val isVisible: Boolean)
data class BindingDataExtendedFloatingActionButton(val isVisible: Boolean)

fun TextInputLayout.bindTextTwoWay(
    liveData: MutableLiveData<BindingData>,
    lifecycleOwner: LifecycleOwner,
    validator: FieldValidator? = null,
    onChange: ((String) -> Unit)? = null,
) {
    this.editText?.doOnTextChanged { s, start, count, after ->
        if (validator != null) {
            validator.validate(s).let { isValid ->
                liveData.postValue(
                    BindingData(
                        text = (if (isValid) s else s?.take(start)).toString(),
                        observe = false
                    )
                )
                onChange?.invoke((if (isValid) s else s?.take(start)).toString())
            }
            return@doOnTextChanged
        }
        liveData.postValue(
            BindingData(
                text = s.toString(),
                observe = false
            )
        )
        onChange?.invoke(s.toString())
    }

    liveData.observe(lifecycleOwner) { bindingData ->
        if (bindingData.observe) {
            editText?.setText(bindingData.text)
            editText?.setSelection(bindingData.text.length)
            error = bindingData.error
            isErrorEnabled = bindingData.isErrorEnabled
        }
    }
}


fun SwitchCompat.bindTwoWay(
    liveData: MutableLiveData<BindingDataSwitch>,
    lifecycleOwner: LifecycleOwner,
    onChange: ((Boolean) -> Unit)? = null,
) {
    setOnCheckedChangeListener { buttonView, isChecked ->
        liveData.postValue(liveData.value!!.apply {
            this.isChecked = isChecked
            onChange?.invoke(isChecked)
        })
    }
    liveData.observe(lifecycleOwner) { bindingData ->
        isChecked = bindingData?.isChecked ?: false
    }
}

fun LiveData<BindingDataMenuItem>.bindToMenuItem(
    menu: Menu,
    lifecycleOwner: LifecycleOwner
) {
    this.observe(lifecycleOwner) { bindingData ->
        bindingData.menuItemIds.forEach {
            menu.findItem(it)?.isVisible = bindingData.isVisible
        }
    }
}
fun LiveData<BindingDataExtendedFloatingActionButton>.bindTo(
    button: ExtendedFloatingActionButton,
    lifecycleOwner: LifecycleOwner
) {
    this.observe(lifecycleOwner) { bindingData ->

        if (bindingData.isVisible) {
            button.show()
        } else {
            button.hide()
        }
    }
}
