package ru.bz.mobile.inventory.presentation.view.login

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.databinding.FragmentLoginBinding
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.presentation.viewModel.bindTextTwoWay
import ru.bz.mobile.inventory.presentation.viewModel.login.LoginViewModel
import ru.bz.mobile.inventory.presentation.viewModel.login.LoginViewModelFactory
import ru.bz.mobile.inventory.presentation.viewModel.login.Action
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: LoginViewModelFactory
    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModelAndBinding {
            initListeners()
            initObservers()
        }
    }

    private fun initViewModelAndBinding(after: () -> Unit) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.passwordText.bindTextTwoWay(
            liveData = viewModel.password,
            lifecycleOwner = viewLifecycleOwner
        )
        binding.executePendingBindings()
        after()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(it) }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.showMessage -> {}
            is Action.navigate -> goSettingsFragment()
            else -> {}
        }
    }

    private fun initListeners() {
    }

    private fun goSettingsFragment() {
        findNavController().navigate(R.id.action_nav_login_to_nav_settings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}