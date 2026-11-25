package com.chessrebase.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var status: TextView
    private val captureRequest = 4107

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 32, 28, 24)
        }
        root.addView(TextView(this).apply {
            text = "CHESS REBASE 0.2.0"
            textSize = 25f
            gravity = Gravity.CENTER
        })
        status = TextView(this).apply {
            text = "Захват экрана ещё не включён. Android запросит отдельное разрешение."
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        }
        root.addView(status)
        root.addView(Button(this).apply {
            text = "1. Разрешить плавающее окно"
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else status.text = "Разрешение плавающего окна уже выдано."
            }
        })
        root.addView(Button(this).apply {
            text = "2. Начать захват экрана"
            setOnClickListener {
                val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                startActivityForResult(manager.createScreenCaptureIntent(), captureRequest)
            }
        })
        root.addView(Button(this).apply {
            text = "Показать тестовую стрелку"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, ArrowOverlayService::class.java))
                } else status.text = "Сначала разреши отображение поверх окон."
            }
        })
        root.addView(Button(this).apply {
            text = "Остановить захват и стрелку"
            setOnClickListener {
                stopService(Intent(this@MainActivity, ScreenCaptureService::class.java))
                stopService(Intent(this@MainActivity, ArrowOverlayService::class.java))
                status.text = "Остановлено."
            }
        })
        root.addView(TextView(this).apply {
            text = "Захват работает только после системного подтверждения Android. В этой версии кадр сохраняется для проверки; распознавание фигур и выбор хода ещё не подключены."
            textSize = 13f
            setPadding(0, 22, 0, 0)
        })
        setContentView(root)
    }

    @Deprecated("Deprecated in Android API, kept for broad device compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == captureRequest && resultCode == RESULT_OK && data != null) {
            val service = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenCaptureService.EXTRA_DATA, data)
            }
            startForegroundService(service)
            status.text = "Захват экрана запущен. Переключись в шахматы; Android покажет индикатор захвата."
        } else if (requestCode == captureRequest) {
            status.text = "Разрешение на захват экрана не выдано."
        }
    }
}
