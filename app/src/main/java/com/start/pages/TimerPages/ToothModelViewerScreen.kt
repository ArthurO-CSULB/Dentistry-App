package com.start.pages.TimerPages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.InputStream

/*

References:

*https://developer.android.com/jetpack/androidx/releases/webkit#kts
*https://www.fab.com/listings/8734f504-34f5-4143-a81c-89e523aa909e
*https://developer.android.com/reference/android/webkit/WebView?authuser=2
*https://modelviewer.dev/
*ChatGPT

We have a composable that displays a 3d glb model inside a WebView. We do this by converting the
GLB model into a Base64-encoded string, embedding it inside an HTML string, and loading it into the
web view. The <model-viewer> web component is used to provide a JavaScript API to control, interact with, and
customize the behavior of the 3d viewer. We use the web rendering engine 'WebKit' which allows us render
web content in android, in this case the model is embedded in the HTML aka web content.
 */

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ToothModelViewerScreen() {
    // Get the current context.
    val context = LocalContext.current
    // Load and convert teeth_cartoon.glb from assets into a Base64 string.
    val base64Model = loadGlbAsBase64(context, "teeth_cartoon.glb")
    // Create a WebView inside jetpack compose using android view
    AndroidView(
        factory = {
            WebView(context).apply {
                // Configure the web view settings.

                // Enable javascript for the <model-viewer> component
                settings.javaScriptEnabled = true
                //settings.allowFileAccess = true
                //settings.allowContentAccess = true
                //loadUrl("file:///android_asset/tooth_model_viewer.html")
                // We have dynamically created our HTML which contains the <model-viewer> to render
                // the 3D model, which we will pass our Base64-encoded .glb model as the src.
                loadDataWithBaseURL(null, getHtmlContent(base64Model), "text/html", "UTF-8", null)
            }
        },
        // Wrap the content and have it in a circular shape
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
    )
}

/*

Initially had an error 'Fetch API cannot load file:///android_asset/teeth_cartoon.glb. URL scheme "file" is
not supported. TypeError: Failed to fetch. This occurred because WebView does not allow the Fetch API
to load file from file:///android_asset. Initially I had tried to load it like this:

loadUrl("file:///android_asset/tooth_model_viewer.html")

Now the new approach is loading the GLB file as a Base64-encoded string.
 */

// Method to encode the tooth model into a string of base64 which will be embedded in the html
// that the WebView will load to show the tooth model.
fun loadGlbAsBase64(context: Context, assetFileName: String): String {
    // Put the opened .glb file from our assets folder into input stream.
    val inputStream: InputStream = context.assets.open(assetFileName)
    // Read the file as bytes.
    val bytes = inputStream.readBytes()
    // Close the input stream.
    inputStream.close()
    // Convert the .glb model file into a Base64-encoded string, then format it as a data url
    // that <model-viewer> api can load.
    return "data:model/gltf-binary;base64," + Base64.encodeToString(bytes, Base64.DEFAULT)
}

// Utilize boiler-plate code from documentation to create the model-viewer that renders the model.
// Pass in the encoded Base64 which is our tooth model.
// Add methods within the <model-viewer> tags to adjust orientation, attributes, etc.
fun getHtmlContent(base64Model: String): String {
    // Adjust body style as needed to get a clean look at the tooth model.
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <script type="module"
                src="https://unpkg.com/@google/model-viewer@1.12.0/dist/model-viewer.min.js">
            </script>
        </head>
        <body style="margin: 0; overflow: hidden; padding: 0; display: flex; justify-content: center; align-items: center;">
            <model-viewer 
                id="viewer"
                src="$base64Model"
                camera-controls
                auto-rotate>
            </model-viewer>
        </body>
        </html>
    """.trimIndent()
}