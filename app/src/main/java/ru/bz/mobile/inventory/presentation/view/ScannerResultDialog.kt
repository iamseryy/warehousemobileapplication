package ru.bz.mobile.inventory.presentation.view

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.bz.mobile.inventory.databinding.FragmentScannerResultDialogListDialogBinding

const val ARG_SCANNING_RESULT = "scanning_result"
const val ARG_SCANNING_HINT= "scanning_hint"

class ScannerResultDialog(private val listener: DialogDismissListener) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentScannerResultDialogListDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScannerResultDialogListDialogBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        val hint  = arguments?.getString(ARG_SCANNING_HINT)
        binding.edtResult.setText(scannedResult)
        binding.textHint.setHint(hint)
        binding.btnNext.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }


    companion object {

        fun newInstance(scanningResult: String, hint:String, listener: DialogDismissListener): ScannerResultDialog =
            ScannerResultDialog(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                    putString(ARG_SCANNING_HINT, hint)
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