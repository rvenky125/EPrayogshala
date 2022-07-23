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
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.*
import com.famas.vlabschemistrymvgr.util.pdftools.createPdf
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
@ExperimentalFoundationApi
@ExperimentalAnimationApi
class AcidBaseTitration : ComponentActivity() {

    private var currentScreen by mutableStateOf(Screens.SCREEN1)
    private var showExitDialog by mutableStateOf(false)
    private lateinit var storageRef: StorageReference
    private var showPhotoDialog by mutableStateOf(true)
    private var myPhotoBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        //getting id from dashboard
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
                        Constants.ACID_BASE_TIT.mapFileNameToExperiment(),
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

                if (showPhotoDialog) {
                    TakePhotoDialog(onGoBack = { finish() }) {
                        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(photoIntent, 0)
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(scaffoldState = scaffoldState) {
                        MyCrossfade(targetState = currentScreen) { screen ->
                            if (screen == Screens.SCREEN1) FirstScreen(
                                w1 = w1,
                                w2 = w2,
                                m1 = m1,
                                subst = subst,
                                expName = "Acid Base Titration",
                                aimStr = "Determination of concentration of HCl in a given solution of volume 100ml",
                                step1Str = "Preparation of standard sodium carbonate solution",
                                w1Label = "Weight of bottle with Sodium Carbonate",
                                w2Label = "Weight of bottle without Sodium Carbonate",
                                m1Label = "Molarity of Sodium Carbonate",
                                setW1 = setW1,
                                setW2 = setW2,
                                setM1 = setM1,
                                setSubst = setSubst,
                                gmw = 106f,
                                picText = "Sodium carbonate solution",
                                m1MulFactor = 4,
                                showSnack = {
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(it)
                                    }
                                }
                            ) {
                                currentScreen = Screens.SCREEN2
                            }
                            else {
                                SecondScreen(
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
                                    step2String = "Determination of concentration of HCl in test sample",
                                    v1Label = "Volume of sodium carbonate solution",
                                    n1Label = "number of moles of Sodium carbonate",
                                    m2Label = "Molarity of HCl solution",
                                    addSolText = "HCl",
                                    m1Label = "Molarity of Sodium Carbonate",
                                    showSnack = {
                                        coroutineScope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(it)
                                        }
                                    },
                                    onPrevClick = { /*TODO*/ },
                                ) {
                                    if (myName.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myRollNo.isNotBlank())
                                        coroutineScope.launch {
                                            if (myPhotoBitmap != null) {
                                                createPdf(
                                                    stName = myName,
                                                    stImage = myPhotoBitmap,
                                                    rollNo = myRollNo,
                                                    branch = myBranch,
                                                    section = mySection,
                                                    expName = Constants.ACID_BASE_TIT,
                                                    labTeacher = lectName,
                                                    step1List = listOf(
                                                        "Preparation of standard Sodium Carbonate solution",
                                                        "Molarity of Sodium carbonate acid(M1) = $m1"
                                                    ),
                                                    step2List = listOf(
                                                        "Standardization of sodium hydroxide solution supplied",
                                                        "Molarity of HCl(M2) = $m2"
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
                                            } else showPhotoDialog = true
                                        }
                                    else coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Sorry your details are empty"
                                        )
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
        } else {
            showPhotoDialog = false
            Toast.makeText(this, "You should take photo", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onBackPressed() {
        if (currentScreen == Screens.SCREEN1) showExitDialog = !showExitDialog
        else currentScreen = Screens.SCREEN1
    }
}
