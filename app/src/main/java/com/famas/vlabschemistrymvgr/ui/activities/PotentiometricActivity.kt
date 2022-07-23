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
import com.famas.vlabschemistrymvgr.ui.screens.potentiometric.FourthScreen
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.*
import com.famas.vlabschemistrymvgr.util.Screens.SCREEN1
import com.famas.vlabschemistrymvgr.util.Screens.SCREEN2
import com.famas.vlabschemistrymvgr.util.pdftools.createPdf
import com.famas.vlabschemistrymvgr.util.titrationvalues.DoubleTitrationType
import com.famas.vlabschemistrymvgr.util.titrationvalues.getRandomPotentials
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
class PotentiometricActivity : ComponentActivity() {

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
                        MyCrossfade(currentScreen) { screen ->
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
                                        }
                                        else showPhotoDialog = true
                                    }
                                }
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

    //Handling back press
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
