package ru.bz.mobile.inventory.presentation.view.locas

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.App
import ru.bz.mobile.inventory.presentation.CwarItemClotBundle
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.presentation.ResourcesProvider
import ru.bz.mobile.inventory.databinding.FragmentLocasBinding
import ru.bz.mobile.inventory.util.GsonSerializer
import ru.bz.mobile.inventory.presentation.view.main.MainFragment
import ru.bz.mobile.inventory.presentation.viewModel.locas.LocasViewModel
import ru.bz.mobile.inventory.presentation.viewModel.locas.LocasViewModelFactory
import ru.bz.mobile.inventory.presentation.viewModel.locas.Action

class LocasFragment : Fragment() {

    private val viewModel: LocasViewModel by viewModels {
        LocasViewModelFactory(
            (requireActivity().application as App).locaRepo, ResourcesProvider(requireContext())
        )
    }

    private var _binding: FragmentLocasBinding? = null
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

        _binding = FragmentLocasBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModelAndBinding {
            initListeners()
            initObservers()
            initOther()
            viewModel.onViewCreated()
        }
    }

    private fun initViewModelAndBinding(after: () -> Unit) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        val manager = LinearLayoutManager(requireContext())
        binding.contentRecycler.recyclerView.layoutManager = manager
        binding.contentRecycler.recyclerView.adapter = viewModel.adapter

        binding.executePendingBindings()
        after()
    }

    private fun initListeners() {
        setFragmentResultListener(MainFragment.Companion.RequestKeys.REQUEST_CLOT_LOCA.requestKey) { requestKey, bundle ->
            val bundleStr =
                bundle.getString(MainFragment.Companion.RequestKeys.REQUEST_CLOT_LOCA.bundleKey)
                    ?: return@setFragmentResultListener
            viewModel.onBundleResult(
                MainFragment.Companion.RequestKeys.REQUEST_CLOT_LOCA.bundle(
                    bundleStr
                ) as CwarItemClotBundle
            )
        }
    }

    private fun initOther() {

    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(it) }
        }
    }


    private fun handleAction(action: Action) {
        when (action) {
            is Action.showMessage -> {}
            is Action.navigateMainFragment -> navigateMainFragment()
            else -> {}
        }
    }


    private fun navigateMainFragment() {
        val bundleStr = GsonSerializer.serializeObject(
            viewModel.getClotLocaBundle()
        )

        setFragmentResult(
            MainFragment.Companion.RequestKeys.REQUEST_MAIN_CLOT_LOCA.requestKey, bundleOf(
                MainFragment.Companion.RequestKeys.REQUEST_MAIN_CLOT_LOCA.bundleKey to bundleStr
            )
        )
        findNavController().navigate(R.id.action_nav_locas_to_nav_main)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}