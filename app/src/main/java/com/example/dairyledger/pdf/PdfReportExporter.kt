package com.example.dairyledger.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.example.dairyledger.views.ReportRowItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.atomic.AtomicBoolean
import android.graphics.Color as AndroidColor
import androidx.core.graphics.toColorInt


/**
 * Generates a PDF table from a list of ReportRowItem and opens it with the
 * device's default PDF viewer.
 *
 * Usage from Compose:
 *
 *   val scope = rememberCoroutineScope()
 *   Button(onClick = {
 *       scope.launch {
 *           PdfReportExporter.exportAndOpen(context, rows, title = "Delivery Report")
 *       }
 *   }) { Text("Export PDF") }
 */
object PdfReportExporter {

    // --- Concurrency guard: blocks a second export while one is running ---
    private val isExporting = AtomicBoolean(false)

    private const val PAGE_WIDTH = 595   // A4 @ 72dpi
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 32f
    private const val HEADER_ROW_HEIGHT = 36f
    private const val ROW_HEIGHT = 32f
    private const val AVATAR_RADIUS = 10f

    // Column width ratios — must sum to 1f: [name(+avatar), liters, price/L, total]
    private val columnWeights = floatArrayOf(0.42f, 0.18f, 0.20f, 0.20f)
    private val headers = arrayOf("Name", "Liters", "Price/L", "Total")

    /**
     * Builds the PDF on a background thread, saves it, then launches a
     * viewer intent on the main thread.
     *
     * @return true if export+open started successfully, false if an export
     *         was already in progress or generation/opening failed.
     */
    suspend fun exportAndOpen(
        context: Context,
        rows: List<ReportRowItem>,
        title: String = "Report",
        fileName: String = "report_${System.currentTimeMillis()}.pdf",
    ): Boolean {
        // Atomically flip false -> true; if it was already true, bail out.
        if (!isExporting.compareAndSet(false, true)) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Export already in progress…", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        return try {
            val file = withContext(Dispatchers.IO) {
                generatePdf(context, rows, title, fileName)
            }
            openPdf(context, file)
            true
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to export PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
            false
        } finally {
            isExporting.set(false) // always release the guard
        }
    }

    // ---------------------------------------------------------------------
    // PDF generation (runs on Dispatchers.IO)
    // ---------------------------------------------------------------------

    private fun generatePdf(
        context: Context,
        rows: List<ReportRowItem>,
        title: String,
        fileName: String,
    ): File {
        val document = PdfDocument()
        val contentWidth = PAGE_WIDTH - MARGIN * 2
        val colWidths = columnWeights.map { it * contentWidth }

        val titlePaint = Paint().apply {
            color = AndroidColor.BLACK
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val headerBgPaint = Paint().apply { color = "#2D2D2D".toColorInt() }
        val headerTextPaint = Paint().apply {
            color = AndroidColor.WHITE
            textSize = 12f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val cellTextPaint = Paint().apply {
            color = "#222222".toColorInt()
            textSize = 12f
            isAntiAlias = true
        }
        val rowBgEvenPaint = Paint().apply { color = "#F7F7F7".toColorInt() }
        val borderPaint = Paint().apply {
            color = "#DDDDDD".toColorInt()
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val avatarTextPaint = Paint().apply {
            color = AndroidColor.WHITE
            textSize = 9f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

        var pageNumber = 1
        var page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
        var canvas = page.canvas
        var y = MARGIN

        fun drawTitle() {
            canvas.drawText(title, MARGIN, y + 18f, titlePaint)
            y += 34f
        }

        fun drawHeaderRow() {
            var x = MARGIN
            canvas.drawRect(MARGIN, y, MARGIN + contentWidth, y + HEADER_ROW_HEIGHT, headerBgPaint)
            headers.forEachIndexed { i, label ->
                canvas.drawText(label, x + 8f, y + HEADER_ROW_HEIGHT / 2 + 4f, headerTextPaint)
                x += colWidths[i]
            }
            y += HEADER_ROW_HEIGHT
        }

        fun startNewPage() {
            document.finishPage(page)
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
            canvas = page.canvas
            y = MARGIN
            drawHeaderRow()
        }

        drawTitle()
        drawHeaderRow()

        rows.forEachIndexed { index, item ->
            // Paginate if this row won't fit above the bottom margin.
            if (y + ROW_HEIGHT > PAGE_HEIGHT - MARGIN) {
                startNewPage()
            }

            if (index % 2 == 0) {
                canvas.drawRect(MARGIN, y, MARGIN + contentWidth, y + ROW_HEIGHT, rowBgEvenPaint)
            }

            var x = MARGIN
            val midY = y + ROW_HEIGHT / 2 + 4f

            // Column 0: avatar circle + initials + name
            val avatarCx = x + 8f + AVATAR_RADIUS
            val avatarCy = y + ROW_HEIGHT / 2
            val avatarPaint = Paint().apply {
                color = item.avatarBgColor.toArgb()
                isAntiAlias = true
            }
            canvas.drawCircle(avatarCx, avatarCy, AVATAR_RADIUS, avatarPaint)
            canvas.drawText(item.initials, avatarCx, avatarCy + 3f, avatarTextPaint)
            canvas.drawText(item.name, avatarCx + AVATAR_RADIUS + 8f, midY, cellTextPaint)
            x += colWidths[0]

            // Column 1: liters
            canvas.drawText(item.liters, x + 8f, midY, cellTextPaint)
            x += colWidths[1]

            // Column 2: price per liter
            canvas.drawText(item.pricePerL, x + 8f, midY, cellTextPaint)
            x += colWidths[2]

            // Column 3: total
            canvas.drawText(item.total, x + 8f, midY, cellTextPaint)

            // Row border
            canvas.drawRect(MARGIN, y, MARGIN + contentWidth, y + ROW_HEIGHT, borderPaint)
            y += ROW_HEIGHT
        }

        document.finishPage(page)

        // Scoped storage-friendly location — no WRITE_EXTERNAL_STORAGE permission needed.
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { document.writeTo(it) }
        document.close()
        return file
    }

    // ---------------------------------------------------------------------
    // Open with the device's default PDF viewer
    // ---------------------------------------------------------------------

    private suspend fun openPdf(context: Context, file: File) {
        withContext(Dispatchers.Main) {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(context, "No app found to open PDF files.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
