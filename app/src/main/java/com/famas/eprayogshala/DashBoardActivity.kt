package com.famas.eprayogshala

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.famas.eprayogshala.ui.activities.*
import com.famas.eprayogshala.ui.components.ExperimentItemCard
import com.famas.eprayogshala.ui.components.LinearProgressLoader
import com.famas.eprayogshala.ui.screens.ReportsScreen
import com.famas.eprayogshala.ui.theme.eprayogshalaTheme
import com.famas.eprayogshala.util.*
import com.famas.eprayogshala.util.BottomNav.*
import com.famas.eprayogshala.util.Constants.ROUTE_EXPERIMENTS
import com.famas.eprayogshala.util.experiments.Experiment.*
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
class DashBoardActivity : ComponentActivity() {

    private lateinit var navController: NavHostController
    private lateinit var firestore: FirebaseFirestore
    private lateinit var expTimings: MutableMap<String, Map<String, Timestamp>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reAuthUser()

        //checking for camera permission
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), 1)
        }

        Firebase.app
        firestore = Firebase.firestore

        val ls = listOf(AcidBase, Conductometric1, Conductometric2, Potentiometric, Colorimetric, PHmetric, HardnessOfWater)

        setContent {
            val (downloading, setDownloading) = remember { mutableStateOf(false) }
            var progress by remember { mutableStateOf(0.0f) }

            val myUUID by rememberStringPreference(
                keyName = Constants.MY_UUID_KEY,
                initialValue = "",
                defaultValue = ""
            )

            val lectUUID by rememberStringPreference(keyName = Constants.LECT_ID_KEY, "", "")

//            expTimings = produceState(initialValue = mutableMapOf<String, Map<String, Timestamp>>(), producer = {
//                firestore.collection("_lecturers/$lectUUID/exp_timings")
//                    .addSnapshotListener { snapshot, _ ->
//                        snapshot?.documents?.forEach {
//                            val map = mutableMapOf(
//                                "enable" to it["enable"] as Timestamp,
//                                "disable" to it["disable"] as Timestamp
//                            )
//                            this.value = this.value.apply {
//                                set(it.id, map)
//                            }
//                        }
//                    }
//            }).value

            LaunchedEffect(key1 = Unit, block = {
                delay(3000L)
                callUpdate()
            })

            eprayogshalaTheme {
                val scaffoldState = rememberScaffoldState()
                val coroutineScope = rememberCoroutineScope()
                navController = rememberNavController()

                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    if (downloading || progress > 0.0f && progress < 1.0f) {
                        LinearProgressLoader(
                            progress = progress,
                            message = "Downloading.. it will take some time"
                        )
                    }

                    Scaffold(
                        scaffoldState = scaffoldState,
                        bottomBar = { BottomNavView(navController = navController) }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = ROUTE_EXPERIMENTS,
                            modifier = Modifier.padding(it)
                        ) {
                            composable(ROUTE_EXPERIMENTS) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp)
                                )
                                {
                                    items(ls) { experiment ->
                                        ExperimentItemCard(experiment)
                                        {
                                            when (experiment) {
                                                Conductometric1 -> {
                                                    validateAndGotoTarget(ConductometricActivity::class.java, "0")
                                                }

                                                Conductometric2 -> {
                                                    validateAndGotoTarget(ConductometricActivity::class.java, "1")
                                                }

                                                Potentiometric -> {
                                                    validateAndGotoTarget(PotentiometricActivity::class.java)
                                                }

                                                Colorimetric -> {
                                                    validateAndGotoTarget(ColorimetricActivity::class.java)
                                                }

                                                PHmetric -> {
                                                    validateAndGotoTarget(PHmetricActivity::class.java)
                                                }

                                                AcidBase -> {
                                                    validateAndGotoTarget(AcidBaseTitration::class.java)
                                                }

                                                HardnessOfWater -> {
                                                    startActivity(
                                                        Intent(
                                                            this@DashBoardActivity,
                                                            HardnessWaterActivity::class.java
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    item {
                                        Spacer(modifier = Modifier.height(90.dp))
                                    }
                                }
                            }

                            composable(Constants.ROUTE_REPORTS) {
                                filesDir?.let { file ->
                                    ReportsScreen(
                                        filesDir = file,
                                        callForReviewedFiles = {
                                            checkForReviewedFiles(
                                                myUUID = myUUID,
                                                progress = { progress = it },
                                                onCompleted = {
                                                    coroutineScope.launch {
                                                        scaffoldState.snackbarHostState.showSnackbar(
                                                            "Please check reviewed screen"
                                                        )
                                                    }
                                                    navController.navigate(Constants.ROUTE_REPORTS) {
                                                        popUpTo(navController.graph.startDestinationId)
                                                    }
                                                },
                                                setDownloading = setDownloading,
                                                lectUUID = lectUUID
                                            )
                                        }) {
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                PdfViewerActivity::class.java
                                            )
                                                .putExtra("path", it.absolutePath)
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

    private fun callUpdate() {
        val updateManager = AppUpdateManagerFactory.create(this)
        updateManager.appUpdateInfo.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && updateInfo.isUpdateTypeAllowed(
                    AppUpdateType.FLEXIBLE
                )
            ) {
                try {
                    updateManager.startUpdateFlowForResult(
                        updateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        3
                    )
                } catch (e: Exception) {
                    e.message?.let { Log.d("myTag", it) }
                }
            }
        }
    }

    var register =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.data != null && activityResult.resultCode == RESULT_OK) {
                Toast.makeText(this, "Downloading start", Toast.LENGTH_SHORT).show()
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) != PermissionChecker.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Camera permission is necessary", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun checkForReviewedFiles(
        myUUID: String,
        progress: (Float) -> Unit,
        onCompleted: () -> Unit,
        lectUUID: String,
        setDownloading: (Boolean) -> Unit
    ) {
        val files = filesDir.list { _, name -> name.contains("_rev") }?.map {
            it.removeSuffix(".pdf").removePrefix("_rev")
        }!!

        files.forEach {
            Log.d("myTag", "saved: $it")
        }

        //Syncing data to know whether the the documents are reviewed by lecturer
        try {
            firestore.document("_lecturers/$lectUUID/_rev_reports/$myUUID").get()
                .addOnSuccessListener { snapshot ->
                    val reports = snapshot.data

                    reports?.forEach { (key, value) ->
                        try {
                            val map = value as Map<*, *>?
                            if (!files.contains(key.toString())) {
                                setDownloading(true)
                                map?.get("link")?.let { l ->
                                    downloadFile(
                                        link = l.toString(),
                                        fName = key.toString(),
                                        progress = progress,
                                        onCompleted = {
                                            setDownloading(false)
                                            onCompleted()
                                        })

                                    lifecycleScope.launch {
                                        dataStore.edit {
                                            it[stringPreferencesKey("${key}_grade")] =
                                                map["grade"].toString()
                                            it[stringPreferencesKey("${key}_comment")] =
                                                map["comment"].toString()
                                        }
                                    }
                                }
                            }
                        }
                        catch (e: Exception) {
                            Log.d("myTag", "exception at downloading", e)
                        }
                    }
                }
                .addOnFailureListener { }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun downloadFile(
        link: String,
        fName: String,
        progress: (Float) -> Unit,
        onCompleted: () -> Unit
    ) {
        if (link.isNotBlank())
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val url = URL(link)

                    val urlConn = url.openConnection()
                    val length = urlConn.contentLength
                    val input = urlConn.getInputStream()

                    val file = File(filesDir, "_rev$fName.pdf")
                    if (!file.exists()) {
                        file.createNewFile()
                        Log.d("myTag", file.absolutePath)
                    }

                    val out = FileOutputStream(file)
                    val byteArray = ByteArray(1024 * 1024)

                    var readData: Int
                    var total = 0
                    while (input.read(byteArray).also { readData = it } != -1) {
                        total += readData
                        progress(generateFloatPercent(total*100/length))
                        out.write(byteArray, 0, readData)
                    }
                    withContext(Dispatchers.Main) {
                        onCompleted()
                    }
                } catch (e: Exception) {
                    e.message?.let {
                        Log.d("myTag", it)
                    }
                }
            }
        else Toast.makeText(applicationContext, "empty url", Toast.LENGTH_SHORT).show()
    }


    @Composable
    fun BottomNavView(navController: NavController) {

        val bottomNavList = remember { listOf(Experiments, Reports) }
        var currentRoute by remember { mutableStateOf("") }
        navController.addOnDestinationChangedListener { _, navDestination, _ ->
            currentRoute = navDestination.route.toString()
        }
        BottomNavigation {
            bottomNavList.forEach { navItem ->
                BottomNavigationItem(
                    selected = navItem.route == currentRoute,
                    onClick = {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    label = {
                        Text(text = navItem.label)
                    },
                    alwaysShowLabel = navItem.route == currentRoute,
                    icon = {
                        Icon(
                            imageVector = navItem.icon(navItem.route == currentRoute),
                            contentDescription = navItem.label
                        )
                    }
                )
            }
        }
    }

    private fun reAuthUser() {
        Firebase.auth.currentUser?.let { usr ->
            usr.reload()
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    if ( it is FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                            this,
                            "Your account disabled by the developer. Please contact the developer",
                            Toast.LENGTH_LONG
                        ).show()
                        Firebase.auth.signOut()
                        startActivity(Intent(this, SplashScreen::class.java))
                        finish()
                    }
                }
        }
    }

    private fun validateAndGotoTarget(target: Class<*>, extra: String? = null) {
        startActivity(
            Intent(
                this@DashBoardActivity,
                target
            ).putExtra("id", extra)
        )
        /*val enable = expTimings["Acid Base Titration"]?.get("enable")
        val disable = expTimings["Acid Base Titration"]?.get("disable")
        val time = Calendar.getInstance().time

        if (enable != null && disable != null) {
            if (time.after(enable.toDate()) && time.before(disable.toDate())) {
                startActivity(
                    Intent(
                        this@DashBoardActivity,
                        target
                    ).putExtra("id", extra)
                )
            }
            else Toast.makeText(this@DashBoardActivity, "experiment not yet enabled", Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this@DashBoardActivity, "experiment not yet enabled", Toast.LENGTH_SHORT).show()*/
    }
}