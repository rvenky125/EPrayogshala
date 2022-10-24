package com.famas.eprayogshala.ui.activities

import android.content.Intent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.famas.eprayogshala.ui.components.ExitDialog
import com.famas.eprayogshala.ui.components.TakePhotoDialog
import com.famas.eprayogshala.ui.screens.conductometric.FirstScreen
import com.famas.eprayogshala.ui.screens.potentiometric.FourthScreen
import com.famas.eprayogshala.ui.theme.eprayogshalaTheme
import com.famas.eprayogshala.util.*
import com.famas.eprayogshala.util.Screens.SCREEN1
import com.famas.eprayogshala.util.Screens.SCREEN2
import com.famas.eprayogshala.util.pdftools.createPdf
import com.famas.eprayogshala.util.titrationvalues.DoubleTitrationType
import com.famas.eprayogshala.util.titrationvalues.getRandomPotentials
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class PotentiometricActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(SCREEN1)
    private lateinit var storageRef: StorageReference
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

        storageRef = Firebase.storage.reference

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()

            //for screen1
            val (w1, setW1) = remember { mutableStateOf("") }
            val (w2, setW2) = remember { mutableStateOf("") }
            val (m1, setM1) = remember { mutableStateOf("") }
            val (subst, setSubst) = remember { mutableStateOf(0f) }

            //For screen2
            val (totVolK2Cr2O7, setTotVol) = remember { mutableStateOf(0.0.toBigDecimal()) }
            val pilotList = remember { getRandomPotentials(DoubleTitrationType.PILOT) }
            val accurateList = remember { getRandomPotentials(DoubleTitrationType.ACCURATE) }

            //student details
            val myName by rememberStringPreference(keyName = Constants.MY_NAME_KEY, "", "")
            val myRollNo by rememberStringPreference(keyName = Constants.MY_ROLL_NO_KEY, "", "")
            val myBranch by rememberStringPreference(keyName = Constants.MY_BRANCH_KEY, "", "")
            val mySection by rememberStringPreference(keyName = Constants.MY_SECTION_KEY, "", "")
            val lectName by rememberStringPreference(keyName = Constants.LECT_NAME_KEY, "", "")
            val lectUUID by rememberStringPreference(keyName = Constants.LECT_ID_KEY, "", "")

            //checking wether the experiment expired for every second
            LaunchedEffect(key1 = lectUUID, block = {
                if (lectUUID.isNotBlank()) {
                    checkForExperimentSwitch(
                        lectUUID,
                        Constants.POTENTIOMETRIC.mapFileNameToExperiment(),
                        lifecycleScope
                    ) {
                        finish()
                    }
                }
            })

            eprayogshalaTheme {

                if (showExitDialog) ExitDialog(onCancel = { showExitDialog = false }) {
                    finish()
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    if (showPhotoDialog) {
                        TakePhotoDialog(onGoBack = { finish() }) {
                            val file = File(cacheDir, "myPhoto.jpeg")
                            if (!file.exists()) {
                                file.createNewFile()
                            }
                            getImage.launch(file.toUri())
                        }
                    }

                    Scaffold(scaffoldState = scaffoldState) {
                        MyCrossfade(currentScreen, modifier = Modifier.padding(it)) { screen ->
                            when (screen) {

                                SCREEN1 -> FirstScreen(
                                    showSnack = {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(it)
                                        }
                                    },
                                    onSubClick = {
                                        currentScreen = SCREEN2
                                    },
                                    w1 = w1,
                                    w2 = w2,
                                    m1 = m1,
                                    subst = subst,
                                    expName = "Potentiometric titration of ferrous iron using Potassium dichromate",
                                    aimStr = "Determination of concentration of Ferrous iron using Potassium dichromate",
                                    step1Str = " Preparation of standard Potassium dichromate solution",
                                    w1Label = "Weight of the bottle with Potassium dichromate",
                                    w2Label = "Weight of the bottle without Potassium dichromate",
                                    m1Label = "Concentration of Potassium dichromate",
                                    gmw = 294.185f,
                                    setW1 = setW1,
                                    setW2 = setW2,
                                    setM1 = setM1,
                                    setSubst = setSubst
                                )

                                SCREEN2 -> {
                                    FourthScreen(
                                        listPilot = pilotList,
                                        listAccurate = accurateList,
                                        totVolAddingSol = totVolK2Cr2O7,
                                        setTotVol = setTotVol,
                                        showSnack = {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    it
                                                )
                                            }
                                        }
                                    ) {
                                        if (myPhotoBitmap != null) {
                                            if (myName.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myRollNo.isNotBlank())
                                                coroutineScope.launch {
                                                    createPdf(
                                                        stName = myName,
                                                        stImage = myPhotoBitmap!!,
                                                        rollNo = myRollNo,
                                                        branch = myBranch,
                                                        section = mySection,
                                                        expName = Constants.POTENTIOMETRIC,
                                                        labTeacher = lectName,
                                                        step1List = listOf(
                                                            "Preparation of standard Potassium dichromate solution",
                                                            "Concentration of Potassium dichromate = $m1"
                                                        ),
                                                        step2List = listOf(
                                                            "Pilot titration",
                                                            "Total volume of K2Cr2O7 added: 6.0 ml"
                                                        ),
                                                        step3List = listOf(
                                                            "Accurate titration",
                                                            "Total volume of K2Cr2O7 added: $totVolK2Cr2O7 ml"
                                                        ),
                                                        filesDir = filesDir
                                                    ) {
                                                        Toast.makeText(
                                                            applicationContext,
                                                            "saved in your reports",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        finish()
                                                    }
                                                }
                                            else coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    "Sorry your details are empty"
                                                )
                                            }
                                        } else showPhotoDialog = true
                                    }
                                }
                                else -> {}
                            }
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

    //Handling back press
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when (currentScreen) {
            SCREEN1 -> {
                showExitDialog = !showExitDialog
            }
            SCREEN2 -> {
                currentScreen = SCREEN1
            }
            else -> super.onBackPressed()
        }
    }
}
