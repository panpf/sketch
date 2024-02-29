package com.github.panpf.sketch.compose.painter

// TODO support drawable res
//@Composable
//fun rememberIconPainter(
//    iconPath: String,
//    backgroundPath: String? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconPainter {
//    val icon = painterResource(iconPath)
//    val background = backgroundPath?.let { painterResource(it) }
//    return remember(iconPath, backgroundPath, iconSize, iconTint) {
//        IconPainter(icon, background, iconSize, iconTint)
//    }
//}
//
//@Composable
//fun rememberIconPainter(
//    iconPath: String,
//    background: Color? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconPainter {
//    val icon = painterResource(iconPath)
//    return remember(iconPath, background, iconSize, iconTint) {
//        val backgroundPainter = background?.let { ColorPainter(it) }
//        IconPainter(icon, backgroundPainter, iconSize, iconTint)
//    }
//}
//
//@Composable
//fun rememberIconAnimatablePainter(
//    iconPath: String,
//    backgroundPath: String? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconAnimatablePainter {
//    val icon = painterResource(iconPath)
//    val background = backgroundPath?.let { painterResource(it) }
//    return remember(iconPath, backgroundPath, iconSize, iconTint) {
//        IconAnimatablePainter(icon, background, iconSize, iconTint)
//    }
//}
//
//@Composable
//fun rememberIconAnimatablePainter(
//    iconPath: String,
//    background: Color? = null,
//    iconSize: Size? = null,
//    iconTint: Color? = null,
//): IconPainter {
//    val icon = painterResource(iconPath)
//    return remember(iconPath, background, iconSize, iconTint) {
//        val backgroundPainter = background?.let { ColorPainter(it) }
//        IconAnimatablePainter(icon, backgroundPainter, iconSize, iconTint)
//    }
//}