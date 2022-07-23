package com.famas.vlabschemistrymvgr.ui.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.famas.vlabschemistrymvgr.ui.components.ExitDialog
import com.famas.vlabschemistrymvgr.ui.components.TakePhotoDialog
import com.famas.vlabschemistrymvgr.ui.screens.colorimetric.Colorimetry
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.Constants
import com.famas.vlabschemistrymvgr.util.checkForExperimentSwitch
import com.famas.vlabschemistrymvgr.util.mapFileNameToExperiment
import com.famas.vlabschemistrymvgr.util.pdftools.createPdf
import com.famas.vlabschemistrymvgr.util.rememberStringPreference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class ColorimetricActivity : ComponentActivity() {

    private var showPhotoDialog by mutableStateOf(true)
    private var myPhotoBitmap: Bitmap? = null
    private var showExitDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()

            VLabsChemistryMVGRTheme {

                if (showExitDialog) ExitDialog(onCancel = { showExitDialog = false }) {
                    finish()
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    if (showPhotoDialog) {
                        TakePhotoDialog(onGoBack = { finish() }) {
                            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(photoIntent, 0)
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
                    val lectUUID by rememberStringPreference(keyName = Constants.LECT_ID_KEY, "", "")

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
                            modifier = Modifier.padding(horizontal = 10.dp)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 0) {
            showPhotoDialog = false
            lifecycleScope.launch(Dispatchers.IO) {
                val uri = data?.extras?.get("data") as Bitmap
                myPhotoBitmap = uri
            }
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