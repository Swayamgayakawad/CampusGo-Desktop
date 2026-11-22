package org.example.project

import androidx.compose.ui.graphics.ImageBitmap

import androidx.compose.ui.graphics.toComposeImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter

fun generateQRCode(data: String): ImageBitmap {

    val bitMatrix = MultiFormatWriter().encode(
        data,
        BarcodeFormat.QR_CODE,
        400,
        400
    )

    val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)

    return bufferedImage.toComposeImageBitmap()
}
