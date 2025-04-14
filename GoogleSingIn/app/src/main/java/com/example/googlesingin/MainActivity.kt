package com.example.googlesingin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.googlesingin.ui.theme.Blue
import com.example.googlesingin.ui.theme.GoogleSingInTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleSingInTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GoogleDesign(modifier = Modifier.padding(innerPadding))

                }
            }
        }
    }
}


@Composable
fun GoogleDesign(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var emailField by remember { mutableStateOf("") }
    val brush = remember {
        Brush.linearGradient(
            colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Red)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.google_icon),
            contentDescription = "Google Logo",
            modifier = Modifier
                .padding(top = 32.dp)
                .size(60.dp)
        )

        Text(
            text = "Sign In", color = Color.Black, fontSize = 32.sp,
            fontFamily = FontFamily(
                Font(R.font.google_font, FontWeight.Bold)
            ),
            modifier = Modifier.padding(top = 32.dp)

        )

        Text(
            text = "Use your Google Account",
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 16.dp)
        )

        OutlinedTextField(
            value = emailField,
            onValueChange = { emailField = it },
            label = { Text("Email or Phone") },
            textStyle = TextStyle(brush = brush),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Text(
            text = "Forgot Password?",
            color = Blue,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        val annotatedString = buildAnnotatedString {
            append("Not your computer? Use Private Browsing windows to sign in.")

            withStyle(
                style = SpanStyle(
                    color = Blue, fontWeight = FontWeight.Bold
                )
            ) {
                pushStringAnnotation(
                    tag = "url",
                    annotation = "https://support.google.com/chrome/answer/6130773?hl=en&co=GENIE.Platform%3DAndroid#:~:text=In%20Guest%20mode%2C%20you%20won,or%20borrowing%20someone%20else's%20computer."
                )
                append(" Learn more about using Guest mode.")
                pop()
            }
        }

        ClickableText(
            text = annotatedString, onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "url", start = offset, end = offset)
                    .firstOrNull()?.let {
                        val i = Intent(Intent.ACTION_VIEW, it.item.toUri())
                        context.startActivity(i)
                    }
            }, modifier = Modifier.padding(top = 32.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)

        ) {
            Text(
                text = "Create account", color = Blue, fontWeight = FontWeight.Bold
            )

            Button(
                {}, colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Next")

            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun GoogleDesignPreview() {
    GoogleDesign()
}