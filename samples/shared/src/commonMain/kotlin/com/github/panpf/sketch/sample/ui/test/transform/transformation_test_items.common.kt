@file:OptIn(ExperimentalLayoutApi::class)

package com.github.panpf.sketch.sample.ui.test.transform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt

@Composable
fun singleChoiceListItem(
    title: String,
    values: ImmutableList<String>,
    state: MutableState<String>
) {
    Column(Modifier.fillMaxWidth()) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            values.forEach { value ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { state.value = value }
                ) {
                    RadioButton(
                        selected = state.value == value,
                        onClick = null,
                    )
                    Spacer(Modifier.size(4.dp))
                    Text(text = value, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun sliderListItem(
    title: String,
    state: MutableState<Int>,
    minValue: Int = 0,
    maxValue: Int = 100,
) {
    Column(Modifier.fillMaxWidth()) {
        Row {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            Text(text = state.value.toString(), fontSize = 12.sp)
        }

        Slider(
            value = (state.value / maxValue.toFloat()),
            onValueChange = {
                state.value = (it * maxValue).roundToInt().coerceIn(minValue, maxValue)
            },
            modifier = Modifier.height(30.dp)
        )
    }
}