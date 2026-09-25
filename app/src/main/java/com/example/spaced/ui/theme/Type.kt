package com.example.spaced.ui.theme

import android.graphics.pdf.models.ListItem
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.unit.sp
import com.example.spaced.R

val NormalFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex
    )
)

// Track: Weight 500, ROND 100, Width 110
val TrackFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(110f),
            FontVariation.Setting("ROND", 100f),
            FontVariation.opticalSizing(16.sp)
        )
    )
)

// Month Header: Weight 445, ROND 65, Width 110
val MonthHeaderFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(445),
            FontVariation.width(110f),
            FontVariation.Setting("ROND", 65f),
            FontVariation.opticalSizing(12.sp)
        )
    )
)

// Calendar Short Texts: Weight 400, ROND 47
val CalendarShortFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(400),
            FontVariation.Setting("ROND", 47f)
        )
    )
)

// TextStyle Definitions
val TrackTextStyle = TextStyle(
    fontFamily = TrackFontFamily,
    fontSize = 17.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.15.sp
)

val MonthHeaderTextStyle = TextStyle(
    fontFamily = MonthHeaderFontFamily,
    fontSize = 13.sp
)


val CalendarShortTextStyle = TextStyle(
    fontFamily = CalendarShortFontFamily
)

// Month Header: Weight 545, ROND 65, Width 110
val MonthHeadeExpandedrFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(545),
            FontVariation.width(110f),
            FontVariation.Setting("ROND", 65f)
        )
    )
)

val MonthHeaderExpandedTextStyle = TextStyle(
    fontFamily = MonthHeadeExpandedrFontFamily,
    fontSize = 16.sp
)

// Top Date Header: Weight 560, ROND 100, Width 120, OPSZ 24, Size 24sp
val TopDateHeaderFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(560),
            FontVariation.width(120f),
            FontVariation.Setting("ROND", 100f),
            FontVariation.Setting("opsz", 24f)
        )
    )
)

val TopDateHeaderTextStyle = TextStyle(
    fontFamily = TopDateHeaderFontFamily,
    fontSize = 24.sp
)

// Task Tab Pill Font: Weight 400, Width 114, Size 16sp

val TaskTabFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(450),
            FontVariation.width(114f)
        )
    )
)

val TaskTabTextStyle = TextStyle(
    fontFamily = TaskTabFontFamily,
    fontSize = 16.sp
)

val TextFieldTitleFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(445),
            FontVariation.width(105f),
            FontVariation.Setting("GRAD", 50f),
            FontVariation.Setting("ROND", 50f),
            FontVariation.Setting("opsz", 14f)
        )
    )
)

val TextFieldTitleTextStyle = TextStyle(
    fontFamily = TextFieldTitleFontFamily,
    fontSize = 14.sp
)
val MajorButtonFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.width(110f),
            FontVariation.Setting("ROND", 100f),
            FontVariation.Setting("opsz", 14f)
        )
    )
)

val MajorButtonTextStyle = TextStyle(
    fontFamily = MajorButtonFontFamily,
    letterSpacing = 0.5.sp,
    lineHeight = 24.sp
)

val HighlightedLabelFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(100f),
            FontVariation.Setting("ROND", 65f),
            FontVariation.Setting("opsz", 8f)
        )
    )
)

val HighlightedLabelTextStyle = TextStyle(
    fontFamily = HighlightedLabelFontFamily,
    letterSpacing = 0.1.sp,
    fontSize = 14.sp
)

val TagAndChapFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(450),
            FontVariation.width(104f),
            FontVariation.Setting("opsz", 12f)
        )
    )
)

val TagAndChapTextStyle = TextStyle(
    fontFamily = TagAndChapFontFamily,
    fontSize = 12.sp
)

val TaskTitleFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(545),
            FontVariation.width(105f),
            FontVariation.Setting("opsz", 28f),
            FontVariation.Setting("GRAD", 70f),
            FontVariation.Setting("ROND", 70f)
        )
    )
)

val TaskTitleTextStyle = TextStyle(
    fontFamily = TaskTitleFontFamily,
    fontSize = 16.sp
)

val TimerFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(700),
            FontVariation.width(110f),
            FontVariation.Setting("opsz", 20f),
            FontVariation.Setting("ROND", 100f)
        )
    )
)

val TimerTextStyle = TextStyle(
    fontFamily = TimerFontFamily,
    fontSize = 24.sp
)

val ListItemFontFamily = FontFamily(
    Font(
        resId = R.font.google_sans_flex,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(500),
            FontVariation.width(105f),
            FontVariation.Setting("GRAD", 50f),
            FontVariation.Setting("ROND", 100f)
        )
    )
)

val ListItemTextStyle = TextStyle(
    fontFamily = ListItemFontFamily,
    fontSize = 16.sp
)


// App Theme Typography Default
val SpacedTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = CalendarShortFontFamily,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CalendarShortFontFamily,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CalendarShortFontFamily,
        fontSize = 12.sp
    )
)