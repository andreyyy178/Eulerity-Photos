package com.example.eulerityphotos.ui.screens

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.drawToBitmap
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.eulerityphotos.R
import com.example.eulerityphotos.model.Photo
import com.example.eulerityphotos.model.UploadUrl
import com.example.eulerityphotos.network.UploadApiService
import com.example.eulerityphotos.ui.theme.EulerityPhotosTheme
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.ByteArrayOutputStream

@Composable
fun EditScreen(
    navController: NavController,
    photosUiState: PhotosUiState,
    retryAction: () -> Unit,
    photoId: String
) {
    when (photosUiState) {
        is PhotosUiState.Success -> EditBody(photoId, photosUiState.photos)
        is PhotosUiState.Loading -> LoadingScreen()
        is PhotosUiState.Error -> ErrorScreen(retryAction = retryAction)
    }
}

@Composable
fun EditBody(photoId: String, photos: List<Photo>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = modifier
                .fillMaxHeight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            EditableImage(photoId = photoId, photos = photos, modifier = modifier)
        }
    }
}

@Composable
fun EditableImage(photoId: String, photos: List<Photo>, modifier: Modifier) {
    var colorFilter by remember { mutableStateOf<ColorFilter?>(null) }
    var textCaption by remember { mutableStateOf("") }
    var blurRadius by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    // -expensive operations can be optimized.

    // -better to keep a map of UUID:array index instead of searching the whole list.

    // -would've liked to implement local storage and get photo from there instead of
    //  hitting the server every time.

    // -struggled with integrating chain get and pull request into existing
    //  view model with kotlin coroutines. ended up with an extra non lazily
    //  initialized retrofit client and messy call backs for the image upload.

    // -very giant function needs to be decoupled
    val photo = photos.find { it.id == photoId }
    val baseURL = "https://eulerity-hackathon.appspot.com/"
    val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseURL)
        .build()
    val uploadApiService = retrofit.create(UploadApiService::class.java)

    if (photo != null) {
        Column {
            /**
             * Image with caption
             **/
            val snapShot = CaptureBitmap {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(photo.url)
                            .crossfade(true)
                            .allowHardware(false)
                            .build(),
                        error = painterResource(R.drawable.ic_broken_image),
                        contentDescription = stringResource(R.string.photo),
                        contentScale = ContentScale.Fit,
                        colorFilter = colorFilter,
                        modifier = modifier
                            .blur(
                                radiusX = blurRadius.dp,
                                radiusY = blurRadius.dp
                            )
                    )

                    Text(
                        text = textCaption,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.h2.copy(
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 2f
                            )
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Clip,
                        modifier = modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = modifier.height(4.dp))

            SectionText("Image caption", modifier = modifier)

            TextField(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                value = textCaption,
                onValueChange = { textCaption = it },
                label = { Text("Enter text") },
            )

            Spacer(modifier = modifier.height(12.dp))

            SectionText("Image color filters", modifier = modifier)

            Card(
                modifier = modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(corner = CornerSize(16.dp)),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ColorFilterButton(Color.Green) {
                        colorFilter = ColorFilter.tint(Color.Green, blendMode = BlendMode.Darken)
                    }
                    ColorFilterButton(Color.Red) {
                        colorFilter = ColorFilter.tint(Color.Red, blendMode = BlendMode.Darken)
                    }
                    ColorFilterButton(Color.Yellow) {
                        colorFilter = ColorFilter.tint(Color.Yellow, blendMode = BlendMode.Darken)
                    }
                    ColorFilterButton(Color.Blue) {
                        colorFilter = ColorFilter.tint(Color.Blue, blendMode = BlendMode.Darken)
                    }
                    ColorFilterButton(Color.Magenta) {
                        colorFilter = ColorFilter.tint(Color.Magenta, blendMode = BlendMode.Darken)
                    }
                    ColorFilterButton(Color.Cyan) {
                        colorFilter = ColorFilter.tint(Color.Cyan, blendMode = BlendMode.Darken)
                    }
                }
            }


            Spacer(modifier = modifier.height(12.dp))
            SectionText("Image blur", modifier = modifier)
            Slider(
                modifier = modifier.padding(horizontal = 8.dp),
                value = blurRadius,
                onValueChange = { blurRadius = it },
                valueRange = 0f..10f
            )

            Spacer(modifier = modifier.height(30.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row {
                    Button(
                        onClick = {
                            colorFilter = null
                            textCaption = ""
                            blurRadius = 0f
                        },
                        modifier = modifier.width(180.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
                    ) {
                        Text(text = "Revert to original")
                    }
                    Spacer(modifier = modifier.padding(8.dp))
                    //Upload click
                    val loadingState = remember { mutableStateOf<Boolean?>(false) }
                    Button(
                        onClick = {
                            MainScope().launch {
                                val bitmap = snapShot.invoke()
                                val stream = ByteArrayOutputStream()
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray = stream.toByteArray()
                                val requestFile =
                                    RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
                                val multipartBody = MultipartBody.Part.createFormData(
                                    "image",
                                    "image.jpg",
                                    requestFile
                                )
                                val email = RequestBody.create(
                                    "text/plain".toMediaTypeOrNull(),
                                    "andreyverh@icloud.com"
                                )
                                val originalImageUrl =
                                    RequestBody.create("text/plain".toMediaTypeOrNull(), photo.url)

                                loadingState.value = true
                                uploadApiService.getUploadUrl()
                                    .enqueue(object : Callback<UploadUrl> {
                                        override fun onResponse(
                                            call: Call<UploadUrl>,
                                            response: Response<UploadUrl>
                                        ) {
                                            loadingState.value = false
                                            val body = response.body()
                                            if (body != null) {
                                                uploadApiService.uploadImage(
                                                    fullUrl = body.url,
                                                    appid = email,
                                                    original = originalImageUrl,
                                                    file = multipartBody
                                                ).enqueue(object : Callback<ResponseBody>{
                                                    override fun onResponse(
                                                        call: Call<ResponseBody>,
                                                        response: Response<ResponseBody>
                                                    ) {
                                                        Log.i("onClick", "Success")
                                                        loadingState.value = false
                                                        val text = "Image Uploaded"
                                                        val duration = Toast.LENGTH_SHORT
                                                        val toast = Toast.makeText(context, text, duration)
                                                        toast.show()
                                                    }

                                                    override fun onFailure(
                                                        call: Call<ResponseBody>,
                                                        t: Throwable
                                                    ) {
                                                        loadingState.value = false
                                                        Log.i("onClick", "$t")
                                                        val text = "Failed to Upload"
                                                        val duration = Toast.LENGTH_SHORT
                                                        val toast = Toast.makeText(context, text, duration)
                                                        toast.show()
                                                    }
                                                })
                                            }
                                            else{
                                                val text = "Failed to Upload"
                                                val duration = Toast.LENGTH_SHORT
                                                val toast = Toast.makeText(context, text, duration)
                                                toast.show()
                                            }
                                        }
                                        override fun onFailure(
                                            call: Call<UploadUrl>,
                                            t: Throwable
                                        ) {
                                            loadingState.value = false
                                            val text = "Failed to Upload"
                                            val duration = Toast.LENGTH_SHORT
                                            val toast = Toast.makeText(context, text, duration)
                                            toast.show()
                                        }
                                    })
                            }
                        },
                        modifier = modifier.width(180.dp),
                        enabled = loadingState.value != true
                    ) {
                        if (loadingState.value == true) {
                            Text("Uploading...")
                        } else {
                            Text("Upload")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorFilterButton(
    color: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(50.dp),
        content = {
            Icon(
                painterResource(id = R.drawable.circle_fill1_wght400_grad0_opsz48),
                contentDescription = null,
                tint = color
            )
        }
    )
}

@Composable
fun SectionText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        style = MaterialTheme.typography.subtitle2,
        modifier = modifier.padding(horizontal = 8.dp)
    )
}

@Composable
fun CaptureBitmap(
    content: @Composable () -> Unit,
): () -> Bitmap {

    val context = LocalContext.current

    /**
     * ComposeView that would take composable as its content
     * Kept in remember so recomposition doesn't re-initialize it
     **/
    val composeView = remember { ComposeView(context) }

    /**
     * Callback function which could get latest image bitmap
     **/
    fun captureBitmap(): Bitmap {
        return composeView.drawToBitmap()
    }

    /** Use Native View inside Composable **/
    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content.invoke()
                }
            }
        }
    )

    /** returning callback to bitmap **/
    return ::captureBitmap
}

@Composable
@Preview fun buttonPreview(){

}

@Composable
@Preview(showBackground = true)
fun ColorButtonPreview() {
    Surface {
        EulerityPhotosTheme() {
            Column() {
                Text(
                    "Color filters",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                        //.clip(CircleShape)
                    ) {
                        ColorFilterButton(Color.Green) {}
                        ColorFilterButton(Color.Red) {}
                        ColorFilterButton(Color.Yellow) {}
                        ColorFilterButton(Color.Blue) {}
                    }
                }
            }
        }
    }
}