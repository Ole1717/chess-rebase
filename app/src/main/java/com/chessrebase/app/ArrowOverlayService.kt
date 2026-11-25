package com.chessrebase.app

import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class ArrowOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var arrowView: ArrowView? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        arrowView = ArrowView()
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START }
        windowManager?.addView(arrowView, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_NOT_STICKY
    override fun onDestroy() { arrowView?.let { windowManager?.removeView(it) }; arrowView = null; super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null

    private inner class ArrowView : View(this@ArrowOverlayService) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFF00D66B.toInt(); style = Paint.Style.STROKE; strokeWidth = 12f
            strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
        }
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            // Временная метка оверлея для проверки разрешения. Это НЕ шахматная подсказка.
            val x1 = width * .42f; val y1 = height * .55f
            val x2 = width * .58f; val y2 = height * .45f
            canvas.drawLine(x1, y1, x2, y2, paint)
            val a = kotlin.math.atan2((y2-y1).toDouble(), (x2-x1).toDouble()); val h = 28f
            val head = Path().apply {
                moveTo(x2, y2)
                lineTo((x2-h*kotlin.math.cos(a-.55)).toFloat(), (y2-h*kotlin.math.sin(a-.55)).toFloat())
                moveTo(x2, y2)
                lineTo((x2-h*kotlin.math.cos(a+.55)).toFloat(), (y2-h*kotlin.math.sin(a+.55)).toFloat())
            }
            canvas.drawPath(head, paint)
        }
    }
}
