package com.github.panpf.sketch.sample.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.panpf.sketch.sample.Res
import com.github.panpf.sketch.sample.ic_arrow_right
import com.github.panpf.sketch.sample.ic_expand_more
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

val menuItemHeight = 50.dp

@Composable
fun DividerSettingItem(
    title: String? = null,
    enabledState: Flow<Boolean>? = null,
) {
    if (enabledState != null) {
        val enabled by enabledState.collectAsState(false)
        if (enabled) return
    }

    Column(Modifier.fillMaxWidth()) {
        if (title != null) {
            Text(
                text = title,
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 10.dp,
                    start = 20.dp,
                    end = 20.dp
                )
            )
        }
        HorizontalDivider(
            Modifier.fillMaxWidth()
                .height(0.5.dp)
                .padding(horizontal = 20.dp)
        )
    }
}

@Composable
fun SwitchSettingItem(
    title: String,
    state: MutableStateFlow<Boolean>,
    desc: String? = null,
    onClick: (suspend () -> Unit)? = null,
    onLongClick: (suspend () -> Unit)? = null,
    enabledState: Flow<Boolean>? = null,
) {
    if (enabledState != null) {
        val enabled by enabledState.collectAsState(false)
        if (enabled) return
    }

    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = menuItemHeight)
            .pointerInput(state) {
                detectTapGestures(
                    onTap = {
                        state.value = !state.value
                        coroutineScope.launch {
                            onClick?.invoke()
                        }
                    },
                    onLongPress = {
                        coroutineScope.launch {
                            onLongClick?.invoke()
                        }
                    },
                )
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
            )
            if (desc != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    lineHeight = 14.sp,
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        val checked by state.collectAsState()
        Switch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@Composable
fun <T> DropdownSettingItem(
    title: String,
    values: List<T>,
    state: MutableStateFlow<T>,
    desc: String? = null,
    enabledState: Flow<Boolean>? = null,
    onItemClick: (suspend (T) -> Unit)? = null,
) {
    if (enabledState != null) {
        val enabled by enabledState.collectAsState(false)
        if (enabled) return
    }

    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        var expanded by remember { mutableStateOf(false) }
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = menuItemHeight)
                .clickable { expanded = true }
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                )
                if (desc != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box {
                Row {
                    val value by state.collectAsState()
                    Text(text = value.toString(), fontSize = 10.sp)
                    Icon(
                        painter = painterResource(Res.drawable.ic_expand_more),
                        contentDescription = "more"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    values.forEachIndexed { index, value ->
                        if (index > 0) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp)
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(text = value.toString()) },
                            onClick = {
                                state.value = value
                                expanded = false
                                coroutineScope.launch {
                                    onItemClick?.invoke(value)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MultiChooseSettingItem(
    title: String,
    values: List<String>,
    checkedList: List<Boolean>,
    onSelected: suspend (which: Int, isChecked: Boolean) -> Unit,
    enabledState: Flow<Boolean>? = null,
) {
    if (enabledState != null) {
        val enabled by enabledState.collectAsState(false)
        if (enabled) return
    }

    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val checkedCount = remember(key1 = checkedList) {
        checkedList.count { it }.toString()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(menuItemHeight)
                .clickable {
                    expanded = !expanded
                }
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = checkedCount,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
            )
            Icon(
                painter = painterResource(Res.drawable.ic_expand_more),
                contentDescription = "more"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = !expanded
            },
        ) {
            values.forEachIndexed { index, value ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(text = value, modifier = Modifier.width(150.dp))
                    },
                    trailingIcon = {
                        Checkbox(checked = checkedList[index], onCheckedChange = {
//                            expanded = !expanded
                            coroutineScope.launch {
                                onSelected(index, !checkedList[index])
                            }
                        })
                    },
                    onClick = {
//                        expanded = !expanded
                        coroutineScope.launch {
                            onSelected(index, !checkedList[index])
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ClickableSettingItem(
    title: String,
    desc: String? = null,
    value: MutableStateFlow<String>,
    onClick: suspend () -> Unit,
    onLongClick: (suspend () -> Unit)? = null,
    enabledState: Flow<Boolean>? = null,
) {
    if (enabledState != null) {
        val enabled by enabledState.collectAsState(false)
        if (enabled) return
    }

    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = menuItemHeight)
            .clickable {
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        coroutineScope.launch {
                            onClick()
                        }
                    },
                    onLongPress = {
                        coroutineScope.launch {
                            onLongClick?.invoke()
                        }
                    },
                )
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            val value by value.collectAsState()
            val desc by remember { derivedStateOf { value.ifEmpty { desc ?: "" } } }
            Text(
                text = desc,
                fontSize = 12.sp,
                lineHeight = 14.sp,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = "more"
        )
    }
}