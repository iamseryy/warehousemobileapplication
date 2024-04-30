package ru.bz.mobile.inventory.presentation.controllers

import android.content.Context
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.ScannerNotClaimedException
import com.honeywell.aidc.ScannerUnavailableException
import com.honeywell.aidc.TriggerStateChangeEvent
import com.honeywell.aidc.UnsupportedPropertyException
import ru.bz.mobile.inventory.domain.model.scanner.ScanResultListener

import java.nio.charset.Charset



class BarcodeController(
    private val context: Context,
) {
    private var resultListener: ScanResultListener? = null
    private var barcodeReader: BarcodeReader? = null
    private var manager: AidcManager? = null
    private val triggerListener = object : BarcodeReader.TriggerListener {
        override fun onTriggerEvent(event: TriggerStateChangeEvent) {
            try {
                barcodeReader?.aim(event.getState())
                barcodeReader?.light(event.getState())
                barcodeReader?.decode(event.getState())
            } catch (e: ScannerNotClaimedException) {
                e.printStackTrace()
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
            }
        }
    }
    private val barcodeListener = object : BarcodeReader.BarcodeListener {
        override fun onBarcodeEvent(event: BarcodeReadEvent) {
            resultListener?.onResult(event.barcodeData.toUTF8(event.charset).trim())
        }

        override fun onFailureEvent(event: BarcodeFailureEvent) {
            resultListener?.onFailure(event.toString())
        }

    }

    private fun setBarcodeSettings() {
        if (barcodeReader != null) {

            // register bar code event listener
            barcodeReader?.addBarcodeListener(barcodeListener)

            // set the trigger mode to client control
            try {
                barcodeReader?.setProperty(
                    BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                    BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL
                )
            } catch (e: UnsupportedPropertyException) {

            }
            // register trigger state change listener
            barcodeReader?.addTriggerListener(
                triggerListener
            )
            val properties: MutableMap<String, Any> = HashMap()
            // Set Symbologies On/Off
            properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_GS1_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = true
            properties[BarcodeReader.PROPERTY_DATAMATRIX_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = true
            properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = false
            properties[BarcodeReader.PROPERTY_AZTEC_ENABLED] = false
            properties[BarcodeReader.PROPERTY_CODABAR_ENABLED] = false
            properties[BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED] = false
            properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = false
            // Set Max Code 39 barcode length
            properties[BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH] = 10
            // Turn on center decoding
            properties[BarcodeReader.PROPERTY_CENTER_DECODE] = true
            // Disable bad read response, handle in onFailureEvent
            properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] = false
            // Apply the settings
            barcodeReader?.setProperties(
                properties
            )
        }
    }
    fun addListener(resultListener: ScanResultListener) {
        this.resultListener = resultListener
    }
    fun removeListener() {
        this.resultListener = null
    }
    fun create() {
        AidcManager.create(
            context
        ) { aidcManager ->
            manager = aidcManager
            barcodeReader = manager?.createBarcodeReader()
            setBarcodeSettings()
            barcodeReader?.claim()
        }
    }
    fun pause() {
        if (barcodeReader != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            barcodeReader?.release()
        }

    }
    fun resume() {
        if (barcodeReader != null) {
            try {
                barcodeReader?.claim()
            } catch (e: ScannerUnavailableException) {
                e.printStackTrace()
            }
        }
    }
    fun destroy() {
        if (barcodeReader != null) {
            // unregister barcode event listener
            barcodeReader?.removeBarcodeListener(
                barcodeListener
            )

            // unregister trigger state change listener
            barcodeReader?.removeTriggerListener(
                triggerListener
            )
            barcodeReader!!.close()
        }
        if (manager != null) {
            // close AidcManager to disconnect from the scanner service.
            // once closed, the object can no longer be used.
            manager?.close()
        }
    }

    private fun String.toUTF8(charset: Charset): String =
        this.toByteArray(charset).toString(Charsets.UTF_8)

}
