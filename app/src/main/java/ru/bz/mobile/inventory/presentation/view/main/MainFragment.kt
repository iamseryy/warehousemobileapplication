package ru.bz.mobile.inventory.presentation.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.presentation.ClotBundle
import ru.bz.mobile.inventory.presentation.CwarItemBundle
import ru.bz.mobile.inventory.presentation.CwarItemClotBundle
import ru.bz.mobile.inventory.presentation.ClotLocaBundle
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.presentation.Validators
import ru.bz.mobile.inventory.presentation.viewModel.bindTextTwoWay
import ru.bz.mobile.inventory.presentation.viewModel.bindTo
import ru.bz.mobile.inventory.presentation.viewModel.bindToMenuItem
import ru.bz.mobile.inventory.presentation.viewModel.bindTwoWay
import ru.bz.mobile.inventory.databinding.FragmentMainBinding
import ru.bz.mobile.inventory.domain.model.IOP
import ru.bz.mobile.inventory.presentation.view.BottomSheetMenuDialog
import ru.bz.mobile.inventory.presentation.viewModel.main.Action
import ru.bz.mobile.inventory.presentation.viewModel.main.MainViewModel
import ru.bz.mobile.inventory.presentation.viewModel.main.MainViewModelFactory
import ru.bz.mobile.inventory.util.GsonSerializer
import javax.inject.Inject


@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: MainViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModelAndBinding {
            initListeners()
            initObservers()
        }
        viewModel.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        viewModel.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun initViewModelAndBinding(after: () -> Unit) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.cwarText.bindTextTwoWay(
            liveData = viewModel.cwar,
            lifecycleOwner = viewLifecycleOwner
        )
        binding.locaText.bindTextTwoWay(
            liveData = viewModel.loca,
            lifecycleOwner = viewLifecycleOwner

        ) {
            viewModel.setLoca(it)
        }
        binding.itemText.bindTextTwoWay(
            liveData = viewModel.item,
            lifecycleOwner = viewLifecycleOwner,
            validator = Validators.itemValidator
        ) {
            viewModel.setItem(it)
        }
        binding.clotText.bindTextTwoWay(
            liveData = viewModel.clot,
            lifecycleOwner = viewLifecycleOwner,
            validator = Validators.clotValidator
        ) {
            viewModel.setClot(it)
        }
        binding.stkrSwitch.bindTwoWay(
            liveData = viewModel.stkr,
            lifecycleOwner = viewLifecycleOwner
        ) {
            viewModel.setStkr(it)
        }
        binding.lockSwitch.bindTwoWay(
            liveData = viewModel.lock,
            lifecycleOwner = viewLifecycleOwner
        ) {
            viewModel.setLock(it)
        }
        binding.illiquidSwitch.bindTwoWay(
            liveData = viewModel.illiquid,
            lifecycleOwner = viewLifecycleOwner
        ) {
            viewModel.setIlliquid(it)
        }
        viewModel.canEdit.bindToMenuItem(binding.bottomAppBar.menu, viewLifecycleOwner)
        viewModel.canSave.bindTo(binding.save, viewLifecycleOwner)

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
            is Action.showMessage -> showSnackbar(action.messageId)
            is Action.beginEdit -> beginEdit()
            is Action.undoEdit -> endEdit()
            is Action.endEdit -> endEdit()
            is Action.navigateClotsFragment -> navigateClotsFragment()
            is Action.navigateLocasFragment -> navigateLocasFragment()
            is Action.clearFocus -> clearFocus()
            is Action.showDeleteDialog -> showDeleteDialog(
                positiveAction = action.positiveAction,
                negativeAction = action.negativeAction
            )

            is Action.showSaveDialog -> showSaveDialog(
                loca = action.loca,
                inRepo = action.inRepo,
                dto = action.dto,
                unit = action.unit,
                positiveAction = action.positiveAction,
                negativeAction = action.negativeAction
            )

            is Action.showClotsDialog -> showClotsDialog(
                positiveAction = action.positiveAction,
                negativeAction = action.negativeAction
            )

            is Action.showLocaDialog -> showLocaDialog(
                loca = action.loca,
                positiveAction = action.positiveAction,
                negativeAction = action.negativeAction
            )

            is Action.showLocasDialog -> showLocasDialog(
                locas = action.locas,
                positiveAction = action.positiveAction,
                negativeAction = action.negativeAction
            )

            else -> {}
        }
    }

    private fun initListeners() {
        setFragmentResultListener(RequestKeys.REQUEST_MAIN_CLOT.requestKey) { requestKey, bundle ->
            val bundleStr =
                bundle.getString(RequestKeys.REQUEST_MAIN_CLOT.bundleKey)
                    ?: return@setFragmentResultListener

            viewModel.onBundleResult(
                RequestKeys.REQUEST_MAIN_CLOT.bundle(
                    bundleStr
                ) as ClotBundle
            )
        }
        setFragmentResultListener(RequestKeys.REQUEST_MAIN_CLOT_LOCA.requestKey) { requestKey, bundle ->
            val bundleStr =
                bundle.getString(RequestKeys.REQUEST_MAIN_CLOT_LOCA.bundleKey)
                    ?: return@setFragmentResultListener

            viewModel.onBundleResult(
                RequestKeys.REQUEST_MAIN_CLOT_LOCA.bundle(
                    bundleStr
                ) as ClotLocaBundle
            )
        }

        binding.bottomAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.beginEdit -> {
                    viewModel.beginEdit()
                    true
                }

                R.id.undoEdit -> {
                    viewModel.undoEdit()
                    true
                }

                R.id.delete -> {
                    viewModel.delete()
                    true
                }

                else -> false
            }
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            openBottomSheetMenu()
        }
        binding.navigateClots.setOnClickListener(View.OnClickListener {
            navigateClotsFragment()
        })
    }

    private fun clearFocus() {
        binding.cwarText.editText?.clearFocus()
        binding.locaText.editText?.clearFocus()
        binding.itemText.editText?.clearFocus()
        binding.clotText.editText?.clearFocus()
    }

    private fun beginEdit() {
        binding.bottomAppBar.setNavigationIcon(null)
        binding.bottomAppBar.title = null

        binding.bottomAppBar.replaceMenu(R.menu.bottom_app_bar_edit)
        binding.endEdit.show()
        binding.bottomAppBar.performShow()
    }

    private fun endEdit() {
        binding.bottomAppBar.setNavigationIcon(R.drawable.ic_menu_24dp)
        binding.bottomAppBar.replaceMenu(R.menu.bottom_app_bar)
        binding.endEdit.hide()
        binding.bottomAppBar.performShow()
    }

    //
    private fun navigateClotsFragment() {

        val bundleStr = GsonSerializer.serializeObject(
            viewModel.getCwarItemBundle()
        )

        setFragmentResult(
            RequestKeys.REQUEST_CLOT.requestKey,
            bundleOf(RequestKeys.REQUEST_CLOT.bundleKey to bundleStr)
        )
        findNavController().navigate(
            R.id.action_nav_main_to_nav_clots
        )

    }
    private fun navigateLocasFragment() {
        val bundleStr = GsonSerializer.serializeObject(
            viewModel.getCwarItemClotBundle()
        )

        setFragmentResult(
            RequestKeys.REQUEST_CLOT_LOCA.requestKey, bundleOf(
                RequestKeys.REQUEST_CLOT_LOCA.bundleKey to bundleStr
            )
        )
        findNavController().navigate(R.id.action_nav_main_to_nav_locas)
    }


    //region Dialogs
    private var isDialogOpen = false
    private fun showDeleteDialog(
        positiveAction: (() -> Unit)?,
        negativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen)
            return

        val title = resources.getString(R.string.deleteTitle)
        val message = resources.getString(R.string.deleteMessage)

        createDialog(
            title = title,
            message = message,
            positiveAction = positiveAction,
            negativeAction = negativeAction,
            positiveText = resources.getString(R.string.delete)
        ).show()
        isDialogOpen = true
    }

    private fun showSaveDialog(
        loca: String,
        inRepo: Boolean,
        dto: IOP.Dto?,
        unit: String?,
        positiveAction: ((Double) -> Unit)?,
        negativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen)
            return

        val title = resources.run {
            if (inRepo && dto!!.qstr > 0) {
                getString(R.string.qstrTitle)
                    .replace("%1s1", dto.qstr.toString())
            } else {
                getString(R.string.qntyTitle)
            }
        }.replace("%1s2", if (unit != null) "(${unit})" else "")
        val message = resources.getString(R.string.qnty_message)
            .replace("%1s1", loca)
        val view = layoutInflater.inflate(R.layout.save_input, null)



        val dialog = createDialog(
            title = title,
            message = message,
            view = view,
            positiveAction = {
                positiveAction?.invoke(
                    view.findViewById<TextInputLayout>(R.id.qntyText)?.editText?.text.toString()
                        .toDouble()
                )
            },
            negativeAction = negativeAction

        )

        view.findViewById<TextInputLayout>(R.id.qntyText)?.editText?.let {
            if(inRepo && dto!!.qnty > 0) {
                it.setText(dto!!.qnty.toString())
            }
            it.setOnEditorActionListener(TextView.OnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    positiveAction?.invoke(
                        view.findViewById<TextInputLayout>(R.id.qntyText)?.editText?.text.toString()
                            .toDouble()
                    )
                    dialog.dismiss()
                }
                false
            })
        }
        dialog.show()
        isDialogOpen = true
    }

    private fun showClotsDialog(
        positiveAction: (() -> Unit)?,
        negativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen)
            return

        val title: String = resources.getString(R.string.no_iop_in_repo_title)
        val message: String = resources.getString(R.string.no_iop_in_repo_message)

        createDialog(
            title = title,
            message = message,
            positiveAction = positiveAction,
            negativeAction = negativeAction,
            positiveText = resources.getString(R.string.select),
            negativeText = resources.getString(R.string.no)
        ).show()
        isDialogOpen = true
    }

    private fun showLocaDialog(
        loca: String,
        positiveAction: (() -> Unit)?,
        negativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen)
            return

        val title: String = resources.getString(R.string.clot_in_loca_title)
        val message: String = resources.getString(R.string.clot_in_loca_message)
            .replace("%1s1", loca)

        createDialog(
            title = title,
            message = message,
            positiveAction = positiveAction,
            negativeAction = negativeAction,
            positiveText = resources.getString(R.string.edit),
            negativeText = resources.getString(R.string.no)
        ).show()
        isDialogOpen = true
    }

    private fun showLocasDialog(
        locas: String,
        positiveAction: (() -> Unit)?,
        negativeAction: (() -> Unit)? = null
    ) {
        if (isDialogOpen)
            return
        val title: String = resources.getString(R.string.clot_in_loca_list_title)
        val message: String =
            resources.getString(R.string.clot_in_loca_list_message).replace("%1s1", locas)

        createDialog(
            title = title,
            message = message,

            positiveAction = positiveAction,
            negativeAction = negativeAction,
            positiveText = resources.getString(R.string.select),
            negativeText = resources.getString(R.string.no)
        ).show()
        isDialogOpen = true
    }

    private fun createDialog(
        title: String,
        message: String,
        positiveText: String = resources.getString(R.string.save),
        negativeText: String = resources.getString(R.string.cancel),
        view: View? = null,
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ): AlertDialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setOnDismissListener { isDialogOpen = false }
            .setPositiveButton(positiveText) { dialog, which ->
                isDialogOpen = false
                positiveAction?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton(negativeText) { dialog, which ->
                isDialogOpen = false
                negativeAction?.invoke()
            }
        if (view != null)
            builder.setView(view)
        return builder.create()
    }

    private fun showSnackbar(messageId: Int) {
        Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG).show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private var menuOpened = false
    private fun openBottomSheetMenu() {
        if (!menuOpened) {
            BottomSheetMenuDialog.newInstance(

                object : BottomSheetMenuDialog.DialogDismissListener {
                    override fun onDismiss() {

                    }
                })
                .show(requireFragmentManager(), BottomSheetMenuDialog::class.java.simpleName)
        }
    }

    companion object {
        val TAG: String =
            MainActivity::class.java.simpleName + " " + MainFragment::class.java.simpleName

        enum class RequestKeys(
            val requestKey: String,
            val bundleKey: String,
            val bundle: (String) -> Any?
        ) {
            REQUEST_CLOT(
                requestKey = "$TAG:REQUEST_CLOT",
                bundleKey = "$TAG:REQUEST_CLOT",
                bundle = { bundleStr -> GsonSerializer.deserializeObject<CwarItemBundle>(bundleStr) }),
            REQUEST_CLOT_LOCA(
                requestKey = "$TAG:REQUEST_CLOT_LOCA",
                bundleKey = "$TAG:REQUEST_CLOT_LOCA",
                bundle = { bundleStr ->
                    GsonSerializer.deserializeObject<CwarItemClotBundle>(
                        bundleStr
                    )
                }),
            REQUEST_MAIN_CLOT(
                requestKey = "$TAG:REQUEST_MAIN_CLOT",
                bundleKey = "$TAG:REQUEST_MAIN_CLOT",
                bundle = { bundleStr -> GsonSerializer.deserializeObject<ClotBundle>(bundleStr) }),
            REQUEST_MAIN_CLOT_LOCA(
                requestKey = "$TAG:REQUEST_MAIN_CLOT_LOCA",
                bundleKey = "$TAG:REQUEST_MAIN_CLOT_LOCA",
                bundle = { bundleStr -> GsonSerializer.deserializeObject<ClotLocaBundle>(bundleStr) }),
        }
    }
}