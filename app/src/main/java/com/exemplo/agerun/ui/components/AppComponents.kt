package com.exemplo.agerun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exemplo.agerun.R
import com.exemplo.agerun.model.BottomModule
import com.exemplo.agerun.model.WorkoutDay
import com.exemplo.agerun.ui.theme.CardPurple
import com.exemplo.agerun.ui.theme.DeepPurple
import com.exemplo.agerun.ui.theme.FieldPurple
import com.exemplo.agerun.ui.theme.Lime
import com.exemplo.agerun.ui.theme.LimeStrong
import com.exemplo.agerun.ui.theme.NightPurple
import com.exemplo.agerun.ui.theme.Olive
import com.exemplo.agerun.ui.theme.SoftPurple
import com.exemplo.agerun.ui.theme.StrokeSoft
import com.exemplo.agerun.ui.theme.TextMuted
import com.exemplo.agerun.ui.theme.TextPrimary

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NightPurple, NightPurple, DeepPurple),
                ),
            ),
    ) {
        content()
    }
}

@Composable
fun LogoHero(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.wrapContentHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(40.dp)),
            shape = RoundedCornerShape(40.dp),
            color = DeepPurple.copy(alpha = 0.68f),
            tonalElevation = 14.dp,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agerun_logo),
                    contentDescription = "Logo AgeRun",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }
    }
}

@Composable
fun DecorativeGlow(modifier: Modifier = Modifier, color: Color) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label, color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldPurple,
            unfocusedContainerColor = FieldPurple,
            focusedBorderColor = Lime,
            unfocusedBorderColor = StrokeSoft,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Lime,
        ),
    )
}

@Composable
fun MultilineField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        label = { Text(text = label, color = TextMuted) },
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldPurple,
            unfocusedContainerColor = FieldPurple,
            focusedBorderColor = Lime,
            unfocusedBorderColor = StrokeSoft,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = Lime,
        ),
    )
}

@Composable
fun SmallActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Lime,
            contentColor = NightPurple,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FilledActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Lime,
            contentColor = NightPurple,
        ),
    ) {
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun OutlineActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.4.dp, StrokeSoft, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun OutlinedActionButton(text: String) {
    OutlineActionButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        onClick = {},
    )
}

@Composable
fun StatusMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = CardPurple.copy(alpha = 0.92f),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)) {
            Text(
                text = value,
                color = Lime,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
fun HeroSection(
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = DeepPurple,
        tonalElevation = 10.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(DeepPurple, CardPurple, NightPurple),
                    ),
                )
                .padding(22.dp),
        ) {
            DecorativeGlow(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd),
                color = Lime.copy(alpha = 0.10f),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                )
                if (actionLabel != null && onActionClick != null) {
                    SmallActionButton(text = actionLabel, onClick = onActionClick)
                }
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = DeepPurple,
        tonalElevation = 8.dp,
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
fun WorkoutCard(day: WorkoutDay) {
    val cardBrush = if (day.highlight) {
        Brush.verticalGradient(colors = listOf(LimeStrong, Lime))
    } else {
        Brush.verticalGradient(colors = listOf(Olive, LimeStrong.copy(alpha = 0.75f)))
    }

    Box(
        modifier = Modifier
            .width(150.dp)
            .height(162.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBrush)
            .padding(16.dp),
    ) {
        Text(text = day.day, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(
            text = day.subtitle,
            modifier = Modifier.padding(top = 26.dp),
            color = NightPurple.copy(alpha = 0.82f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = day.value,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 2.dp),
            color = TextPrimary,
            fontSize = 54.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
fun SelectionChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Lime else CardPurple)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = if (selected) Color.Transparent else StrokeSoft,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Text(
            text = text,
            color = if (selected) NightPurple else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun TagChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Lime.copy(alpha = 0.14f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            color = Lime,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = if (value.isBlank()) "-" else value,
            color = TextPrimary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun FloatingActionIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(Lime)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = NightPurple,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
fun BottomModuleBar(
    selectedModule: BottomModule,
    onModuleSelected: (BottomModule) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(36.dp))
            .background(CardPurple)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ModulePill(
            label = BottomModule.Students.label,
            selected = selectedModule == BottomModule.Students,
            onClick = { onModuleSelected(BottomModule.Students) },
        )
        CenterModuleButton(
            selected = selectedModule == BottomModule.Home,
            onClick = { onModuleSelected(BottomModule.Home) },
        )
        ModulePill(
            label = BottomModule.Workouts.label,
            selected = selectedModule == BottomModule.Workouts,
            onClick = { onModuleSelected(BottomModule.Workouts) },
        )
    }
}

@Composable
private fun ModulePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .widthIn(min = 112.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) Lime else Color.Transparent)
            .border(
                width = if (selected) 0.dp else 1.2.dp,
                color = if (selected) Color.Transparent else StrokeSoft,
                shape = RoundedCornerShape(18.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) NightPurple else TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun CenterModuleButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(if (selected) Lime else Color.Transparent)
            .border(2.dp, if (selected) Lime else Color.White, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+",
            color = if (selected) NightPurple else Lime,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
