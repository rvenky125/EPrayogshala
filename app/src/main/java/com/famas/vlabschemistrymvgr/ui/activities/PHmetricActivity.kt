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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import com.famas.vlabschemistrymvgr.ui.components.ExitDialog
import com.famas.vlabschemistrymvgr.ui.components.TakePhotoDialog
import com.famas.vlabschemistrymvgr.ui.screens.conductometric.FirstScreen
import com.famas.vlabschemistrymvgr.ui.screens.conductometric.SecondScreen
import com.famas.vlabschemistrymvgr.ui.screens.potentiometric.FourthScreen
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.*
import com.famas.vlabschemistrymvgr.util.Screens.*
import com.famas.vlabschemistrymvgr.util.pdftools.createPdf
import com.famas.vlabschemistrymvgr.util.titrationvalues.DoubleTitrationType
import com.famas.vlabschemistrymvgr.util.titrationvalues.getRandomPhValues
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
class PHmetricActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(SCREEN1)
    private lateinit var storageRef: StorageReference
    private var showPhotoDialog by mutableStateOf(true)
    private var myPhotoBitmap: Bitmap? = null
    private var showExitDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        storageRef = Firebase.storage.reference

        setContent {
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()

            //states for first screen
            val (w1, setW1) = remember { mutableStateOf("") }
            val (w2, setW2) = remember { mutableStateOf("") }
            val (m1, setM1) = remember { mutableStateOf("") }
            val (subst, setSubst) = remember { mutableStateOf(0f) }

            //states for second screen
            val (v1, setV1) = remember { mutableStateOf("") }
            val (n1, setN1) = remember { mutableStateOf("") }
            val (m2, setM2) = remember { mutableStateOf("") }
            val v2 =
                remember { listOf(9.0, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 9.8, 9.9, 10.0).random() }
            val (n2, setN2) = remember { mutableStateOf("") }

            //for Third screen
            val (totVolOfAddSol, setTotVol) = remember { mutableStateOf(0.0.toBigDecimal()) }
            val pilotPhValues = remember { getRandomPhValues(DoubleTitrationType.PILOT) }
            val accuratePhValues = remember { getRandomPhValues(DoubleTitrationType.ACCURATE) }

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
                        Constants.PHMETRIC.mapFileNameToExperiment(),
                        lifecycleScope
                    ) {
                        finish()
                    }
                }
            })

            VLabsChemistryMVGRTheme {

                if (showExitDialog) ExitDialog(onCancel = { showExitDialog = false }) {
                    finish()
                }

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    if(showPhotoDialog) {
                        TakePhotoDialog(onGoBack = { finish() }) {
                            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(photoIntent, 0)
                        }
                    }

                    Scaffold(scaffoldState = scaffoldState) {
                        MyCrossfade(targetState = currentScreen) {
                            when (currentScreen) {

                                SCREEN1 -> FirstScreen(
                                    w1 = w1,
                                    w2 = w2,
                                    m1 = m1,
                                    subst = subst,
                                    expName = "pH metric titration of Strong Acid and Strong Base",
                                    aimStr = "Determination of concentration of HCL using NaOH by pH metric titration method",
                                    step1Str = "Preparation of Standard Oxalic solution",
                                    w1Label = "Weight of the bottle with oxalic acid",
                                    w2Label = "Weight of the bottle without oxalic acid",
                                    m1Label = "Calculated concentration of oxalic acid",
                                    setW1 = setW1,
                                    setW2 = setW2,
                                    setM1 = setM1,
                                    setSubst = setSubst,
                                    showSnack = {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(it)
                                        }
                                    }
                                ) { currentScreen = SCREEN2 }

                                SCREEN2 -> SecondScreen(
                                    m1 = m1,
                                    v1 = v1,
                                    n1 = n1,
                                    m2 = m2,
                                    v2 = v2,
                                    n2 = n2,
                                    setV1 = setV1,
                                    setN1 = setN1,
                                    setM2 = setM2,
                                    setN2 = setN2,
                                    showSnack = {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(it)
                                        }
                                    },
                                    onPrevClick = { /*TODO*/ }
                                ) { currentScreen = SCREEN3 }

                                SCREEN3 -> FourthScreen(
                                    isPhmetric = true,
                                    listPilot = pilotPhValues,
                                    listAccurate = accuratePhValues,
                                    totVolAddingSol = totVolOfAddSol,
                                    setTotVol = setTotVol,
                                    showSnack = {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(it)
                                        }
                                    },
                                    onSubClick = {
                                        if(myPhotoBitmap != null) {
                                            if (myName.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myRollNo.isNotBlank())
                                                coroutineScope.launch {
                                                    createPdf(
                                                        stName = myName,
                                                        stImage = myPhotoBitmap!!,
                                                        rollNo = myRollNo,
                                                        branch = myBranch,
                                                        section = mySection,
                                                        expName = Constants.PHMETRIC,
                                                        labTeacher = lectName,
                                                        step1List = listOf(
                                                            "Preparation of standard oxalic solution",
                                                            "Molarity of oxalic acid(M1) = $m1"
                                                        ),
                                                        step2List = listOf(
                                                            "Standardization of sodium hydroxide supplide",
                                                            "Molarity of NaOH = $m2"
                                                        ),
                                                        step3List = listOf(
                                                            "pH metric titration :-",
                                                            "Pilot tiration:",
                                                            "Volume of NaOH added  = 3 ml",
                                                            "Accurate tiration:",
                                                            "Volume of NaOH added  = 2.5 ml",
                                                        ),
                                                        filesDir = filesDir
                                                    ) {
                                                        Toast.makeText(applicationContext, "saved in your reports", Toast.LENGTH_SHORT).show()
                                                        finish()
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
                                )
                            }
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
        }
        else {
            showPhotoDialog = false
            Toast.makeText(this, "You should take photo", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onBackPressed() {
        when(currentScreen) {
            SCREEN1 -> showExitDialog = !showExitDialog

            SCREEN2 -> currentScreen = SCREEN1

            SCREEN3 -> currentScreen = SCREEN2
        }
    }
}