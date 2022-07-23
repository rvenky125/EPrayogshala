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
import com.famas.vlabschemistrymvgr.ui.screens.conductometric.ThirdScreen
import com.famas.vlabschemistrymvgr.ui.theme.VLabsChemistryMVGRTheme
import com.famas.vlabschemistrymvgr.util.*
import com.famas.vlabschemistrymvgr.util.Screens.*
import com.famas.vlabschemistrymvgr.util.pdftools.createPdf
import com.famas.vlabschemistrymvgr.util.titrationvalues.ConductancesCurveType
import com.famas.vlabschemistrymvgr.util.titrationvalues.getRandomConductances
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
class ConductometricActivity : ComponentActivity() {

    private var currentScreen by mutableStateOf(SCREEN1)
    private lateinit var storageRef: StorageReference
    private var showPhotoDialog by mutableStateOf(true)
    private var myPhotoBitmap: Bitmap? = null
    private var showExitDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        //getting id from dashboard
        val id = intent.getStringExtra("id")!!
        storageRef = Firebase.storage.reference

        setContent {
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

            //For third screen
            val list = remember {
                getRandomConductances(if (id == "0") ConductancesCurveType.PARABOLA else ConductancesCurveType.STRAIGHT_LINE)
            }
            val (totVolNaOH, setTotVol) = remember { mutableStateOf(0.0f) }

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
                        (if (id == "0") Constants.CONDUCTOMETRIC1 else Constants.CONDUCTOMETRIC2).mapFileNameToExperiment(),
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

                    if (showPhotoDialog) {
                        TakePhotoDialog(onGoBack = { finish() }) {
                            val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(photoIntent, 0)
                        }
                    }

                    //getting scaffold and coroutine scopes
                    val scaffoldState = rememberScaffoldState()
                    val coroutineScope = rememberCoroutineScope()

                    Scaffold(
                        scaffoldState = scaffoldState
                    ) {
                        if (!id.isNullOrBlank()) {
                            MyCrossfade(targetState = currentScreen) { screen ->
                                when (screen) {
                                    SCREEN1 -> FirstScreen(
                                        showSnack = {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    it
                                                )
                                            }
                                        },
                                        onSubClick = { currentScreen = SCREEN2 },
                                        w1 = w1,
                                        setW1 = setW1,
                                        w2 = w2,
                                        setW2 = setW2,
                                        m1 = m1,
                                        setM1 = setM1,
                                        subst = subst,
                                        setSubst = setSubst,
                                        expName = if (id == "0") Constants.CONDUCTOMETRIC1 else Constants.CONDUCTOMETRIC2,
                                        aimStr = if (id == "0") " Determination of concentration of Hydrochloric acid and Sodium hydroxide solution by Conductometric titration method" else " Determination of concentration of Acetic acid and Sodium hydroxide solution by Conductometric titration method",
                                        step1Str = "  Preparation of standard oxalic solution",
                                        w1Label = "Weight of the bottle with oxalic acid",
                                        w2Label = "Weight of the bottle without oxalic acid",
                                        m1Label = "Calculated concentration of oxalic acid"
                                    )

                                    SCREEN2 -> SecondScreen(
                                        m1 = m1,
                                        v1 = v1,
                                        setV1 = setV1,
                                        n1 = n1,
                                        setN1 = setN1,
                                        m2 = m2,
                                        setM2 = setM2,
                                        v2 = v2,
                                        n2 = n2,
                                        setN2 = setN2,
                                        showSnack = {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    it
                                                )
                                            }
                                        },
                                        onPrevClick = { currentScreen = SCREEN1 },
                                        onSubClick = { currentScreen = SCREEN3 }
                                    )
                                    SCREEN3 -> ThirdScreen(
                                        conductances = list,
                                        totVolNaOH = totVolNaOH,
                                        setTotVol = setTotVol,
                                        onSubClick = {
                                            if (myName.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myRollNo.isNotBlank())
                                                coroutineScope.launch {
                                                    if (myPhotoBitmap != null) {
                                                        createPdf(
                                                            stName = myName,
                                                            stImage = myPhotoBitmap,
                                                            rollNo = myRollNo,
                                                            branch = myBranch,
                                                            section = mySection,
                                                            expName = if (id == "0") Constants.CONDUCTOMETRIC1 else Constants.CONDUCTOMETRIC2,
                                                            labTeacher = lectName,
                                                            step1List = listOf(
                                                                "Preparation of standard oxalic solution",
                                                                "Molarity of oxalic acid(M1) = $m1"
                                                            ),
                                                            step2List = listOf(
                                                                "Standardization of sodium hydroxide solution supplied",
                                                                "Molarity of NaOH(M2) = $m2"
                                                            ),
                                                            step3List = listOf(
                                                                "Conductometric titration",
                                                                "Volume of NaOH added = $totVolNaOH",
                                                                "Initial conductance = ${list[0]}",
                                                                "Final conductance = ${list[1]}"
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
                                        },
                                        showSnack = {
                                            coroutineScope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    it
                                                )
                                            }
                                        },
                                        id = id
                                    )
                                }
                            }
                        } else showPhotoDialog = true
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

    //Handling back press
    override fun onBackPressed() {
        when (currentScreen) {
            SCREEN1 -> {
                showExitDialog = !showExitDialog
            }
            SCREEN2 -> {
                currentScreen = SCREEN1
            }
            SCREEN3 -> {
                currentScreen = SCREEN2
            }
        }
    }
}