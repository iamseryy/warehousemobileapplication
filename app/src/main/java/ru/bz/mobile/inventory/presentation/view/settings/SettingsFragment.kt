package ru.bz.mobile.inventory.presentation.view.settings

import android.Manifest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.databinding.FragmentSettingsBinding
import ru.bz.mobile.inventory.presentation.view.main.MainActivity
import ru.bz.mobile.inventory.presentation.viewModel.settings.Action
import ru.bz.mobile.inventory.presentation.viewModel.settings.SettingsViewModel
import ru.bz.mobile.inventory.presentation.viewModel.settings.SettingsViewModelFactory
import java.io.InputStream
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: SettingsViewModelFactory
    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentSettingsBinding? = null
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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(view.id)
        initViewModelAndBinding {
            initListeners()
            initObservers()
            viewModel.setPermissions(hasPermissions())
        }
    }

    private fun initViewModelAndBinding(after: () -> Unit) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        after()
    }

    private fun initListeners() {
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewModel.actions.collect { handleAction(it) }
        }
    }

    private fun handleAction(action: Action) {
        when (action) {
            is Action.showMessage -> showSnackbar(action.messageId)
            is Action.openFilePicker -> openFilePicker()
            is Action.requestPermissions -> requestPermissions()
            else -> {}
        }
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun openFilePicker() {
        val intent = Intent()
            .setType("text/*") //("text/csv") -не работает))))
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.select_an_import_file)
            ), FILE_PICKER
        )
    }

    fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            true
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", requireActivity().packageName))
                startActivityForResult(intent, PERMISSION_STORAGE)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, PERMISSION_STORAGE)
            }
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_STORAGE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.setPermissions(true)
                    showSnackbar(R.string.permission_granted)
                } else {
                    viewModel.setPermissions(false)
                    showSnackbar(R.string.permission_not_granted)
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (hasPermissions()) {
                    viewModel.setPermissions(true)
                    showSnackbar(R.string.permission_granted)
                } else {
                    viewModel.setPermissions(false)
                    showSnackbar(R.string.permission_not_granted)
                }
            }
        }

        if (requestCode == FILE_PICKER && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            viewModel.import(getInputStreamFromURI(uri = data.data))
        }
    }

    fun getInputStreamFromURI(uri: Uri?): InputStream? {
        if (uri == null)
            return null
        return requireContext().contentResolver.openInputStream(uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val TAG: String =
            MainActivity::class.java.simpleName + " " + SettingsFragment::class.java.simpleName

        private val PERMISSION_STORAGE = 101
        private val FILE_PICKER = 100

    }

}