package com.start.pages.TimerPages

import android.annotation.SuppressLint
import android.content.Context
import android.util.Base64
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.start.viewmodels.TimerModelState
import com.start.viewmodels.TimerViewModel
import kotlinx.coroutines.delay
import java.io.InputStream

/*

References:

*https://developer.android.com/jetpack/androidx/releases/webkit#kts , web kit engine
*https://www.fab.com/listings/8734f504-34f5-4143-a81c-89e523aa909e , where model is from
*https://developer.android.com/reference/android/webkit/WebView?authuser=2 , web view to render the model from html
*https://modelviewer.dev/ , component where the glb file is embedded in.
*https://registry.khronos.org/glTF/specs/2.0/glTF-2.0.html ,
*ChatGPT

We have a composable that displays a 3d glb model inside a WebView. We do this by converting the
GLB model into a Base64-encoded string, embedding it inside an HTML string, and loading it into the
web view. The <model-viewer> web component is used to provide a JavaScript API to control, interact with, and
customize the behavior of the 3d viewer. We use the web rendering engine 'WebKit' which allows us render
web content in android, in this case the model is embedded in the HTML aka web content.
 */

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ToothModelViewerScreen(context: Context, timerViewModel: TimerViewModel) {
    // Load and convert teeth_cartoon.glb from assets into a Base64 string.
    val base64Model = loadGlbAsBase64(context, "teeth_cartoon_edited.glb")
    // Create a WebView inside jetpack compose using android view that will persist across recompositions.
    val webView = remember {mutableStateOf(WebView(context))}
    // Boolean to store whether the model has been fully loaded that will persist across recompositions.
    val modelLoaded = remember {mutableStateOf(false)}

    // We create an android view which will be a web view to display the model.
    AndroidView(
        factory = {
            // We configure the web view.
            webView.value.apply {
                // Configure the web view settings.

                // Enable javascript for the <model-viewer> component
                settings.javaScriptEnabled = true
                //settings.allowFileAccess = true
                //settings.allowContentAccess = true
                //loadUrl("file:///android_asset/tooth_model_viewer.html")

                // Web client to override functions that execute on the lifecycle of the web view.
                webViewClient = object : WebViewClient() {
                    // When page is finished loading, we set modelLoaded value to true.
                    override fun onPageFinished(view: WebView, url: String) {
                        modelLoaded.value = true
                    }
                }
            }
        },
        // Wrap the content and have it in a circular shape
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape),
    )

    // Store the state of the timer.
    val timerModelState = timerViewModel.timerModelState.collectAsState()

    // Launched effect to check the model state.
    LaunchedEffect(timerModelState.value) {

        // If the model is not loaded we load the model.
        if (!modelLoaded.value) {
            // Print that the model is not loaded and is currently loading.
            println("Model not loaded. Loading the model.")
            // We have dynamically created our HTML which contains the <model-viewer> to render
            // the 3D model, which we will pass our Base64-encoded .glb model as the src.
            webView.value.loadDataWithBaseURL(null, getHtmlContent(base64Model), "text/html", "UTF-8", null)
            // Wait 1.5 seconds to ensure that everything is loaded so that we can call the
            // javascript functions that manipulate the teeth properly
            delay(1500)
            println("Model has been loaded.")
        }
        // Depending on the model state...
        when (timerModelState.value) {
            // When upper, highlight upper teeth.
            TimerModelState.Upper -> webView.value.evaluateJavascript("highlightUpperTeeth()", null)
            // When lower highlight lower teeth.
            TimerModelState.Lower -> webView.value.evaluateJavascript("highlightLowerTeeth()", null)
            // When tongue, highlight tongue.
            TimerModelState.Tongue -> webView.value.evaluateJavascript("highlightTongue()", null)
            // Else do nothing.
            else -> Unit
        }
    }
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
// "https://unpkg.com/@google/model-viewer@1.12.0/dist/model-viewer.min.js" other link
// "https://ajax.googleapis.com/ajax/libs/model-viewer/4.0.0/model-viewer.min.js"
fun getHtmlContent(base64Model: String): String {
    // Adjust body style as needed to get a clean look at the tooth model.
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script type="module"
                src="https://unpkg.com/@google/model-viewer@1.12.0/dist/model-viewer.min.js">
            </script>
        </head>
        <body style="margin: 0; overflow: hidden; padding: 0; display: flex; justify-content: center; align-items: center;">
            <model-viewer 
                id="viewer"
                src="$base64Model"
                camera-controls
                auto-rotate
                auto-rotate-delay="0"
                rotation-per-second="45deg">
            </model-viewer>
            
            <script>
                // Declare viewer globally
                //let viewer = document.querySelector("#viewer");
                
                // Store new materials for the upper and lower teeth.
                let teethMaterials = {}
                
                // Stores the original baseColorFactor for the teeth.
                let originalBaseColorTeeth = [1, 1, 1, 1]; // Stores original baseColorFactor of teeth
                let originalBaseColorTongue = []; // Stores original baseColorFactor of tongue
                
                // Store the highlight baseColor for teeth/tongue.
                let highlightColor = [0, 0, 1, 1];

                // We declare the function to highlight the tongue.
                function highlightTongue() {
                    viewer.model.materials[0].pbrMetallicRoughness.setBaseColorFactor(originalBaseColorTeeth);
                    viewer.model.materials[3].pbrMetallicRoughness.setBaseColorFactor(originalBaseColorTeeth);
                    viewer.model.materials[2].pbrMetallicRoughness.setBaseColorFactor(highlightColor);
                }
                
                // Function to highlight the lower teeth.
                function highlightLowerTeeth() {
                    viewer.model.materials[0].pbrMetallicRoughness.setBaseColorFactor(originalBaseColorTeeth);
                    viewer.model.materials[3].pbrMetallicRoughness.setBaseColorFactor(highlightColor);
                }
                
                // Function to highlight the upper teeth.
                function highlightUpperTeeth() {
                    viewer.model.materials[0].pbrMetallicRoughness.setBaseColorFactor(highlightColor);
                    viewer.model.materials[3].pbrMetallicRoughness.setBaseColorFactor(originalBaseColorTeeth);
                }
                
                // Code to run when the model is loaded.
                document.getElementById("viewer").addEventListener("scene-graph-ready", () => {
                    console.log("Model is ready to interact with.");
                    
                    // Store the original baseColorFactor for the teeth and tongue.
                    originalBaseColorTongue = viewer.model.materials[2].pbrMetallicRoughness.baseColorFactor;
                  
                });
            </script>
        </body>
        </html>
    """.trimIndent()
}