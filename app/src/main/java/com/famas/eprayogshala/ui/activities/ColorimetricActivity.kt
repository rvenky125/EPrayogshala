package com.famas.eprayogshala.ui.activities

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.famas.eprayogshala.ui.components.ExitDialog
import com.famas.eprayogshala.ui.components.TakePhotoDialog
import com.famas.eprayogshala.ui.screens.colorimetric.Colorimetry
import com.famas.eprayogshala.ui.theme.eprayogshalaTheme
import com.famas.eprayogshala.util.Constants
import com.famas.eprayogshala.util.checkForExperimentSwitch
import com.famas.eprayogshala.util.mapFileNameToExperiment
import com.famas.eprayogshala.util.pdftools.createPdf
import com.famas.eprayogshala.util.rememberStringPreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class ColorimetricActivity : ComponentActivity() {

    private var showPhotoDialog by mutableStateOf(true)
    private var myPhotoBitmap: Bitmap? = null
private var uri: Uri? = null
    private var showExitDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()

            eprayogshalaTheme {

                if (showExitDialog) ExitDialog(onCancel = { showExitDialog = false }) {
                    finish()
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    if (showPhotoDialog) {
                        TakePhotoDialog(onGoBack = { finish() }) {
                            val values = ContentValues()
                            values.put(MediaStore.Images.Media.TITLE, "Profile")
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                            uri = contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                            getImage.launch(uri)
                        }
                    }

                    //student details
                    val myName by rememberStringPreference(keyName = Constants.MY_NAME_KEY, "", "")
                    val myRollNo by rememberStringPreference(
                        keyName = Constants.MY_ROLL_NO_KEY,
                        "",
                        ""
                    )
                    val myBranch by rememberStringPreference(
                        keyName = Constants.MY_BRANCH_KEY,
                        "",
                        ""
                    )
                    val mySection by rememberStringPreference(
                        keyName = Constants.MY_SECTION_KEY,
                        "",
                        ""
                    )
                    val lectName by rememberStringPreference(
                        keyName = Constants.LECT_NAME_KEY,
                        "",
                        ""
                    )
                    val lectUUID by rememberStringPreference(
                        keyName = Constants.LECT_ID_KEY,
                        "",
                        ""
                    )

                    //checking wether the experiment expired for every second
                    LaunchedEffect(key1 = lectUUID, block = {
                        if (lectUUID.isNotBlank()) {
                            checkForExperimentSwitch(
                                lectUUID,
                                Constants.COLORIMETRIC.mapFileNameToExperiment(),
                                lifecycleScope
                            ) {
                                finish()
                            }
                        }
                    })

                    Scaffold(scaffoldState = scaffoldState) {
                        Colorimetry(
                            modifier = Modifier.padding(horizontal = 10.dp).padding(it)
                        ) { finalM, ukValue ->
                            if (myPhotoBitmap != null) {
                                if (myName.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myRollNo.isNotBlank()) {
                                    coroutineScope.launch {
                                        createPdf(
                                            stName = myName,
                                            stImage = myPhotoBitmap,
                                            rollNo = myRollNo,
                                            branch = myBranch,
                                            section = mySection,
                                            expName = Constants.COLORIMETRIC,
                                            labTeacher = lectName,
                                            step1List = listOf(
                                                "Preparation of 0.01m stock solution of ferric salt",
                                                "Molarity of Fe(III) stock solution = 0.01m",
                                                "Molarity of final solution = $finalM"
                                            ),
                                            step3List = listOf(
                                                "Determination of ferric iron in the given sample",
                                                "Optical density of unknown cement sample: $ukValue"
                                            ),
                                            filesDir = filesDir
                                        ) {
                                            Toast.makeText(
                                                applicationContext,
                                                "saved in your reports",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            lifecycleScope.launch {
                                                delay(200)
                                                finish()
                                            }
                                        }
                                    }
                                }
                                else coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        "Sorry your details are empty"
                                    )
                                }
                            }
                            else showPhotoDialog = true
                        }
                    }
                }
            }
        }
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            showPhotoDialog = false
            val ins = uri?.let { it1 -> contentResolver.openInputStream(it1) }
            myPhotoBitmap = BitmapFactory.decodeStream(ins)
        } else {
            showPhotoDialog = false
            Toast.makeText(this, "You should take photo", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onBackPressed() {
        showExitDialog = !showExitDialog
    }
}