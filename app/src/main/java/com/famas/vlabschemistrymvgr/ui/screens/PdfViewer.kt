package com.famas.vlabschemistrymvgr.ui.screens

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.famas.vlabschemistrymvgr.ui.components.AskForSubmit
import com.famas.vlabschemistrymvgr.ui.components.LinearProgressLoader
import com.famas.vlabschemistrymvgr.util.generateFloatPercent
import com.famas.vlabschemistrymvgr.util.pdftools.renderPdf
import com.famas.vlabschemistrymvgr.util.rememberStringPreference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalAnimationApi
@Composable
fun ViewPdf(
    file: File,
    isSubmitted: Boolean = false,
    isReviewed: Boolean = false,
    isNetAvailable: Boolean,
    lectUUID: String,
    myUUID: String,
    experimentEnabled: Boolean,
    showSnack: (String) -> Unit,
    removePic: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    var loading by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var pdfList by remember { mutableStateOf<List<Bitmap>>(listOf()) }
    var showSubmit by rememberSaveable { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0.0f) }
    val grade by rememberStringPreference(
        keyName = "${
            file.name.removeSuffix(".pdf").removePrefix("_rev")
        }_grade", "loading..", "not provided"
    )
    val comment by rememberStringPreference(
        keyName = "${
            file.name.removeSuffix(".pdf").removePrefix("_rev")
        }_comment", "loading..", "not provided"
    )
    val height =
        with(LocalDensity.current) { LocalContext.current.resources.displayMetrics.heightPixels.toDp() }
    val width = with(LocalDensity.current) {
        LocalContext.current.resources.displayMetrics.widthPixels.toDp()
    }

    LaunchedEffect(key1 = Unit) {
        pdfList = renderPdf(file) {
            showSnack(it)
        }
    }

    if (loading) LinearProgressLoader(progress, message = "Submitting")

    if (showSubmit) AskForSubmit(onCancel = { showSubmit = false }) {
        if (lectUUID.isNotBlank() && myUUID.isNotBlank()) {
            loading = true
            coroutineScope.launch {
                submitReport(
                    file,
                    myUUID,
                    lectUUID,
                    onCompleted = {
                        loading = false
                        try {
                            file.renameTo(File(context.filesDir, "_sub${file.name}"))
                            showSnack("submitted successfully")
                            Log.d("myTag", "submitted successfully")
                            showSubmit = false
                        } catch (e: Exception) {
                            Log.w("myTag", "Failed to upload", e)
                        }
                    },
                    onFailed = {
                        loading = false
                        showSnack(it)
                        showSubmit = false
                    },
                    percent = { progress = it }
                )
            }
        } else showSnack("lecturer id is blank")
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (assessed_dls, pdfView, chipLt) = createRefs()

        //Showing the reviewed details
        if (isReviewed) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(MaterialTheme.shapes.small)
                    .constrainAs(assessed_dls) {
                        top.linkTo(parent.top)
                    },
                color = MaterialTheme.colors.primary.copy(alpha = 0.2f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Grade: $grade", color = MaterialTheme.colors.primaryVariant)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Comment: $comment",
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(pdfView) {
                    top.linkTo(if (!isReviewed) parent.top else assessed_dls.bottom)
                }
                .background(color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.3f)),
            state = scrollState
        ) {
            items(pdfList) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    bitmap = it.asImageBitmap(),
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }

        //Extra gestures for student to modify or submit pdf
        AnimatedVisibility(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(chipLt) {
                    bottom.linkTo(parent.bottom, height / 8)
                },
            visible = !scrollState.isScrollInProgress && !isSubmitted,
            enter = slideInVertically(
                initialOffsetY = { -it + height.value.toInt() / 3 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { it + height.value.toInt() / 3 }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                PdfChip(
                    text = "Remove added pic",
                    chipColor = Color(0xFFFFCDD2),
                    textColor = Color(0xFFB71C1C),
                    modifier = Modifier
                        .width(width / 3)
                        .height(40.dp)
                ) {
                    removePic()
                }

                PdfChip(
                    text = "Submit Report",
                    chipColor = Color(0xFFE8F5E9),
                    textColor = Color(0xFF2E7D32),
                    modifier = Modifier
                        .width(width / 3)
                        .height(40.dp)
                ) {
                    if (isNetAvailable) {
                        if (experimentEnabled) showSubmit = true
                        else showSnack("Experiment expired")
                    } else showSnack("No internet connection")
                }
            }
        }
    }
}


//Submitting report
fun submitReport(
    file: File,
    myUUID: String,
    lectUUID: String,
    percent: (Float) -> Unit,
    onCompleted: () -> Unit,
    onFailed: (String) -> Unit
) {
    Firebase.storage.getReference("$myUUID/${file.name}")
        .putStream(file.inputStream())
        .addOnProgressListener {
            Log.d("myTag", " ${it.bytesTransferred} ${file.length()} ")
            percent(generateFloatPercent((it.bytesTransferred * 100 / file.length()).toInt()))
        }
        .addOnSuccessListener { snapshot ->
            snapshot.storage.downloadUrl
                .addOnSuccessListener { uri ->
                    Firebase.firestore.document("_lecturers/$lectUUID/_sub_reports/$myUUID")
                        .set(
                            mapOf(file.name.removeSuffix(".pdf") to uri.toString()),
                            SetOptions.merge()
                        )
                        .addOnSuccessListener {
                            onCompleted()
                            Log.d("myTag", "Upload succeeded")
                        }
                        .addOnFailureListener {
                            it.message?.let { it1 -> onFailed(it1) }
                            Log.w("myTag", "exception at uploading", it)
                        }
                }
                .addOnFailureListener { e ->
                    e.message?.let { onFailed(it) }
                    Log.w("myTag", "exception at uploading", e)
                }
        }
        .addOnFailureListener {
            it.message?.let { it1 -> onFailed(it1) }
            Log.w("myTag", "exception at uploading", it)
        }
}


//Pdf chip used in extra gestures
@Composable
fun PdfChip(
    modifier: Modifier,
    text: String,
    chipColor: Color = Color(0xFFFFF8E1),
    textColor: Color = Color(0xFFFF8F00),
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() },
        contentColor = textColor,
        backgroundColor = chipColor,
        elevation = 6.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, style = MaterialTheme.typography.button)
        }
    }
}