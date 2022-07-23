package com.famas.vlabschemistrymvgr.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toFile
import androidx.lifecycle.lifecycleScope
import com.famas.vlabschemistrymvgr.ui.screens.ViewPdf
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.NetworkListener
import com.famas.vlabschemistrymvgr.util.mapFileNameToExperiment
import com.famas.vlabschemistrymvgr.util.pdftools.addBitmapToPdf
import com.famas.vlabschemistrymvgr.util.pdftools.addFileToPdf
import com.famas.vlabschemistrymvgr.util.pdftools.removeLastPage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.util.*

@ExperimentalAnimationApi
class PdfViewerActivity : ComponentActivity() {

    private lateinit var file: File
    private var loading by mutableStateOf(false)
    private var experimentEnabled by mutableStateOf(false)
    private var lectUUID = ""
    private var myUUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        lectUUID = intent.getStringExtra("lectUUID")!!
        myUUID = intent.getStringExtra("myUUID")!!

        val filePath = intent.getStringExtra("path")!!
        file = File(filePath)
        val isSubmitted = file.name.contains("_sub") || file.name.contains("_rev")
        val isReviewed = file.name.contains("_rev")
        val experiment = file.name.mapFileNameToExperiment()


        if (!isReviewed && !isSubmitted) {
            Firebase.firestore.document("_lecturers/$lectUUID/exp_timings/$experiment")
                .get()
                .addOnSuccessListener { snapshot ->
                    val enable = (snapshot?.get("enable") as Timestamp?)?.toDate()
                    val disable = (snapshot.get("disable") as Timestamp?)?.toDate()
                    lifecycleScope.launch(Dispatchers.Default) {
                        repeat(Int.MAX_VALUE) {
                            val time = Calendar.getInstance().time
                            if (enable != null && disable != null) {
                                experimentEnabled = time.after(enable) && time.before(disable)
                                Log.d("myTag", "$it $experimentEnabled")
                            }
                            delay(30L * 1000L)
                        }
                    }
                }
        }

        setContent {

            val isNetAvailable by NetworkListener.checkNetworkAvailability(this).collectAsState()
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()

            VLabsChemistryMVGRTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        floatingActionButton = {
                            if (!isSubmitted) PdfFab()
                        }
                    ) {
                        if (loading) Dialog(onDismissRequest = { /*TODO*/ }) {
                            CircularProgressIndicator()
                        }

                        ViewPdf(
                            file = file,
                            isSubmitted = isSubmitted,
                            isReviewed = isReviewed,
                            isNetAvailable = isNetAvailable,
                            lectUUID = lectUUID,
                            myUUID = myUUID,
                            experimentEnabled = experimentEnabled,
                            showSnack = {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(it)
                                }
                            },
                            removePic = {
                                loading = true
                                lifecycleScope.launch {
                                    file.removeLastPage(
                                        showToast = {
                                            loading = false
                                            Toast.makeText(
                                                applicationContext,
                                                "There are no added pics to remove",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    ) {
                                        loading = false
                                        Toast.makeText(
                                            applicationContext,
                                            "Removed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                        startActivity(
                                            Intent(
                                                this@PdfViewerActivity,
                                                PdfViewerActivity::class.java
                                            )
                                                .putExtra("path", file.absolutePath)
                                                .putExtra("lectUUID", lectUUID)
                                                .putExtra("myUUID", myUUID)
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun PdfFab() {
        var showFabs by remember { mutableStateOf(false) }
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = showFabs,
                enter = expandIn(),
                exit = shrinkOut()
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        loading = true
                        requestPhoto.launch("image/*")
                    }
                ) {
                    Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Add photo")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = showFabs,
                enter = expandIn(),
                exit = shrinkOut()
            ) {
                FloatingActionButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        loading = true
                        requestPdf.launch("application/pdf")
                    }
                ) {
                    Icon(imageVector = Icons.Default.AttachFile, contentDescription = "Add file")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            FloatingActionButton(onClick = { showFabs = !showFabs }) {
                Icon(imageVector = if (!showFabs) Icons.Default.Add else Icons.Default.KeyboardArrowDown, contentDescription = "Add pics")
            }
        }
    }

    private val requestPdf = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri == null) Toast.makeText(this, "Pdf not received", Toast.LENGTH_SHORT).show()
        uri?.let { uri1 ->
            val stream = contentResolver.openInputStream(uri1)
            val newFile = File.createTempFile("sample", ".pdf")
            newFile.outputStream().use {
                stream?.copyTo(it)
            }
            file.addFileToPdf(fromFile = newFile) {
                loading = false
                finish()
                startActivity(
                    Intent(
                        this@PdfViewerActivity,
                        PdfViewerActivity::class.java
                    )
                        .putExtra("path", file.absolutePath)
                        .putExtra("lectUUID", lectUUID)
                        .putExtra("myUUID", myUUID)
                )
            }
        }
    }

    private val requestPhoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val ins = contentResolver.openInputStream(uri)
                ins?.let {
                    file.addBitmapToPdf(it) {
                        loading = false
                        finish()
                        startActivity(
                            Intent(
                                this@PdfViewerActivity,
                                PdfViewerActivity::class.java
                            )
                                .putExtra("path", file.absolutePath)
                                .putExtra("lectUUID", lectUUID)
                                .putExtra("myUUID", myUUID)
                        )
                    }
                }
            }
        }
        else Toast.makeText(this, "Photo not received", Toast.LENGTH_SHORT).show()
    }
}