package com.famas.eprayogshala

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import com.famas.eprayogshala.ui.theme.eprayogshalaTheme
import com.famas.eprayogshala.util.Constants
import com.famas.eprayogshala.util.loadPicture
import com.famas.eprayogshala.util.rememberStringPreference
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
class SplashScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Firebase.auth.currentUser

        setContent {

            val myName by rememberStringPreference(keyName = Constants.MY_NAME_KEY, "", "")
            val myRollNo by rememberStringPreference(keyName = Constants.MY_ROLL_NO_KEY, "", "")
            val myBranch by rememberStringPreference(keyName = Constants.MY_BRANCH_KEY, "", "")
            val mySection by rememberStringPreference(keyName = Constants.MY_SECTION_KEY, "", "")
            val myUUID by rememberStringPreference(keyName = Constants.MY_UUID_KEY, "", "")

            eprayogshalaTheme {

                LaunchedEffect(true) {
                    delay(1000)
                    if (user != null) {
                        if (myName.isNotBlank() && myRollNo.isNotBlank() && myBranch.isNotBlank() && mySection.isNotBlank() && myUUID.isNotBlank()) {
                            startActivity(
                                Intent(
                                    this@SplashScreen,
                                    DashBoardActivity::class.java
                                )
                            )
                            finish()
                        } else {
                            startActivity(
                                Intent(
                                    this@SplashScreen,
                                    LoginActivity::class.java
                                ).putExtra("is_details_empty", true)
                            )
                            finish()
                        }
                    } else {
                        startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
                        finish()
                    }
                }

                Surface(color = MaterialTheme.colors.primary) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        loadPicture(imageRes = R.drawable.eprayogshalalogo).value?.let {
                            Image(
                                modifier = Modifier.fillMaxWidth(0.9f),
                                bitmap = it.asImageBitmap(),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }

}