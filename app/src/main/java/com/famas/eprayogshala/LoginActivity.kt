package com.famas.eprayogshala

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.famas.eprayogshala.ui.theme.eprayogshalaTheme
import com.famas.eprayogshala.util.Constants
import com.famas.eprayogshala.util.MyCrossfade
import com.famas.eprayogshala.util.Screens.*
import com.famas.eprayogshala.util.rememberStringPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
class LoginActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore
    private var currentScreen by mutableStateOf(SCREEN1)
    private var loading by mutableStateOf(false)
    private lateinit var myName: MutableState<String>
    private lateinit var myRollNo: MutableState<String>
    private lateinit var myBranch: MutableState<String>
    private lateinit var mySection: MutableState<String>
    private lateinit var myUUID: MutableState<String>
    private lateinit var lectUUID: MutableState<String>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = Firebase.firestore

        //getting intent from splash screen
        val isDetailsEmpty = intent.getBooleanExtra("is_details_empty", false)

        mAuth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.FIREBASE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {

            myName = rememberStringPreference(keyName = Constants.MY_NAME_KEY, "", "")
            myRollNo = rememberStringPreference(keyName = Constants.MY_ROLL_NO_KEY, "", "")
            myBranch = rememberStringPreference(keyName = Constants.MY_BRANCH_KEY, "", "")
            mySection = rememberStringPreference(keyName = Constants.MY_SECTION_KEY, "", "")
            myUUID = rememberStringPreference(keyName = Constants.MY_UUID_KEY, "", "")

            eprayogshalaTheme {
                Surface(color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f)) {

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (!isDetailsEmpty) {
                            MyCrossfade(targetState = currentScreen) {
                                when (currentScreen) {
                                    SCREEN1 -> LoginWithGoogle()

                                    SCREEN2 -> EnterUserDetails()
                                    else -> {}
                                }
                            }
                        }
                        else EnterUserDetails()
                    }
                }
            }
        }
    }

    @Composable
    fun LoginWithGoogle() {
        Card(
            Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Hey, Welcome", style = MaterialTheme.typography.h5)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onClick = {
                        loading = true
                        val signInIntent = googleSignInClient.signInIntent
                        startActivityForResult(signInIntent, 0)
                    },
                    enabled = !loading
                ) {
                    AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }
                    if (!loading)
                        Text(text = "Login / Create Account")
                }
            }
        }
    }


    @Composable
    fun EnterUserDetails() {
        var name by remember { mutableStateOf("") }
        var rollNo by remember { mutableStateOf("") }
        var branch by remember { mutableStateOf("") }
        var section by remember { mutableStateOf("") }
        Card(modifier = Modifier.fillMaxWidth(0.9f))
        {
            Column(modifier = Modifier.padding(8.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = "Please enter your details", style = MaterialTheme.typography.h5)
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Enter your full name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text(text = "Enter your Branch") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = section,
                    onValueChange = { section = it },
                    label = { Text(text = "Enter your section here") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    value = rollNo,
                    onValueChange = { rollNo = it },
                    label = { Text(text = "Enter your roll no here") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = {
                        loading = true
                        myName.value = name
                        myBranch.value = branch
                        myRollNo.value = rollNo
                        mySection.value = section
                        val stData = hashMapOf(
                            "name" to name,
                            "branch" to branch,
                            "roll_no" to rollNo,
                            "section" to section
                        )
                        firestore.document("_students/${myUUID.value}").set(stData)
                            .addOnSuccessListener {
                                loading = false
                                Toast.makeText(applicationContext, "Successfully logged in", Toast.LENGTH_SHORT).show()
                                startActivity(
                                    Intent(applicationContext, DashBoardActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                loading = false
                                Toast.makeText(applicationContext, "Something went wrong, Try again", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = name.isNotBlank() && rollNo.isNotBlank() && branch.isNotBlank() && section.isNotBlank() && !loading
                ) {
                    AnimatedVisibility(
                        visible = loading,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    }
                    if (!loading)
                        Text(text = "Continue")
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        lifecycleScope.launch {
            if (requestCode == 0) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.await()
                    // Google Sign In was successful, authenticate with Firebase
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    loading = false
                    Toast.makeText(this@LoginActivity, "Failed..", Toast.LENGTH_SHORT).show()
                    Log.w("myTag", "Google sign in failed", e)
                }
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("myTag", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    user?.uid?.let { uuid ->
                        myUUID.value = uuid
                        firestore.collection("_students").get()
                            .addOnSuccessListener { snapshot ->
                                if ((snapshot.documents.filter { it.id == uuid }).isNotEmpty()) {
                                    val studentData = snapshot.single { it.id == uuid }
                                    saveUser(studentData)
                                    Toast.makeText(
                                        applicationContext,
                                        "User login success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(
                                        Intent(this, DashBoardActivity::class.java)
                                            .putExtra("lect_uuid", lectUUID.value)
                                    )
                                    finish()
                                } else {
                                    currentScreen = SCREEN2
                                }
                                loading = false
                            }
                            .addOnFailureListener {
                                loading = false
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to connect",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Toast.makeText(
                                    applicationContext,
                                    "Please check your network connection",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }

                } else {
                    loading = false
                    Toast.makeText(this, "Failed, try again..", Toast.LENGTH_SHORT).show()
                    Log.w("myTag", "signInWithCredential:failure", task.exception)
                }
            }
            .addOnFailureListener {
                if (it is FirebaseAuthInvalidUserException)
                    Toast.makeText(this, "Your account is disabled", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUser(studentData: QueryDocumentSnapshot?) {
        studentData?.let { data ->
            myName.value = data["name"].toString()
            myBranch.value = data["branch"].toString()
            myRollNo.value = data["roll_no"].toString()
            mySection.value = data["section"].toString()
        }
    }
}