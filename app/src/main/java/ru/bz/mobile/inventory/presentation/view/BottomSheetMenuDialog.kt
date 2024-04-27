package ru.bz.mobile.inventory.presentation.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.bz.mobile.inventory.R
import ru.bz.mobile.inventory.databinding.FragmentBottomSheetMenuDialogBinding


class BottomSheetMenuDialog(private val listener: DialogDismissListener) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetMenuDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetMenuDialogBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }
    private val buildVersionName : String
        get()=  requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName?:"no_version"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        dismissAllowingStateLoss()
        binding.versionText.setText("${resources.getString(R.string.version)} $buildVersionName")
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    openSettings()
                    true
                }

                else -> false
            }
        }
    }
    private fun openSettings() {
        findNavController().navigate(R.id.action_nav_main_to_nav_login)
        dismiss()
    }
    companion object {

        fun newInstance(listener: DialogDismissListener): BottomSheetMenuDialog =
            BottomSheetMenuDialog(listener).apply {
                arguments = Bundle().apply {

                }
            }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }
}
