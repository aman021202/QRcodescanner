package code.aman.qrcodescanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.tech.NfcBarcode
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.WorkSource
import android.util.SparseArray
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.util.concurrent.Flow
import com.google.android.gms.vision.CameraSource as CameraSource1

class MainActivity : AppCompatActivity() {
    private val requestCodeCameraPermission = 1001
    private lateinit var cameraSource: CameraSource1
    private lateinit var detector: BarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this@MainActivity,Manifest.permission.CAMERA) !=PackageManager.PERMISSION_GRANTED
            ){
            askForCameraPermission()
        }else
        {
            setupControls()
        }
    }

    private fun setupControls() {
           detector = BarcodeDetector . Builder (this@MainActivity).build()
        cameraSource = CameraSource1.Builder(this@MainActivity, detector)
            .setAutoFocusEnabled(true)
            .build()
       var cameraSurfaceView = findViewById<SurfaceView>(R.id.cameraSurfaceView)
        cameraSurfaceView.holder.addCallback(surfaceCallBack)
        detector.setProcessor(processor)

    }

    private fun askForCameraPermission() {
    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CAMERA),
        requestCodeCameraPermission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty())
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                setupControls()
            }else {
                Toast.makeText(applicationContext,"Permission Denied", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private val surfaceCallBack = object : SurfaceHolder.Callback{
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {

        try {
            cameraSource.start(surfaceHolder)

        }catch (exception: Exception){
            Toast.makeText(applicationContext, "Somethings went wrong", Toast.LENGTH_SHORT).show()
        }

        }


        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(p0: SurfaceHolder) {
          cameraSource.stop()
        }
    }
    private val processor = object : Detector.Processor<Barcode>{
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>) {
            if (detections != null && detections.detectedItems.isNotEmpty())
            {
                 val qrCodes : SparseArray<Barcode> = detections.detectedItems
                val code = qrCodes.valueAt(0)
               var textScanResult = findViewById<TextView>(R.id.textScanResult)
                textScanResult.text = code.displayValue
            }else {
                var textScanResult = findViewById<TextView>(R.id.textScanResult)
                textScanResult.text = ""
            }
        }
    }
}