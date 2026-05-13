package no.uio.ifi.in2000.team20.team20app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoBox( // InfoBox is just a placeholder before the actual results, doubling as instruction for new users
    modifier: Modifier = Modifier,
    sizeRatio: Dp, // Infobox is square
    text: String = "Text",
    textSize: TextUnit = 20.sp,
    background: Color = Color.LightGray,
    borderColor: Color = Color.Black
){
    Box(
        modifier = modifier.background(background).sizeIn(minWidth = sizeRatio, minHeight = sizeRatio).drawBehind{
            drawRoundRect(
                color = borderColor,
                style = Stroke(
                    width = 10.0f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f), 1f)
                ),
                cornerRadius = CornerRadius(20f, 20f)
            )
        },
        contentAlignment = Alignment.Center
    ){
        Text(
            text = text,
            fontSize = textSize
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InfoBoxPreview(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        InfoBox(sizeRatio = 200.dp)
    }
}