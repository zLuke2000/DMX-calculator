package it.uninsubria.dmx_calculator.customView

import android.content.Context
import android.graphics.*
import android.graphics.Path.FillType
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import it.uninsubria.dmx_calculator.R

class PhotometricsChart(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val minZoomPath = Path()
    private val maxZoomPath = Path()
    private val persZoomPath = Path()
    private var maxZoomRadius = 10F
    private var minZoomRadius = 5F
    private var persZoomRadius = 0F

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.isAntiAlias = true

        paint.strokeWidth = 4F
        paint.style = Paint.Style.STROKE
        val myHeigth = height/20F
        // disegno triangolo (maxZoom)
        paint.color = ContextCompat.getColor(context, R.color.maxZoom)

        minZoomPath.fillType = FillType.EVEN_ODD
        minZoomPath.moveTo(0F, (height/2).toFloat())
        minZoomPath.lineTo(width.toFloat(), (height/2) + (maxZoomRadius*myHeigth))
        minZoomPath.lineTo(width.toFloat(), (height/2) - (maxZoomRadius*myHeigth))
        minZoomPath.close()
        canvas.drawPath(minZoomPath, paint)

        // disegno triangolo (minZoom)
        paint.color = ContextCompat.getColor(context, R.color.minZoom)

        maxZoomPath.fillType = FillType.EVEN_ODD
        maxZoomPath.moveTo(0F, (height/2).toFloat())
        maxZoomPath.lineTo(width.toFloat(), (height/2) + (minZoomRadius*myHeigth))
        maxZoomPath.lineTo(width.toFloat(), (height/2) - (minZoomRadius*myHeigth))
        maxZoomPath.close()
        canvas.drawPath(maxZoomPath, paint)

        // disegno triangolo (personalizzato)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = ContextCompat.getColor(context, R.color.selectedZoom)
        persZoomPath.fillType = FillType.EVEN_ODD
        persZoomPath.reset()
        persZoomPath.moveTo(0F, (height/2).toFloat())
        persZoomPath.lineTo(width.toFloat(), (height/2) + (persZoomRadius*myHeigth))
        persZoomPath.lineTo(width.toFloat(), (height/2) - (persZoomRadius*myHeigth))
        persZoomPath.close()
        canvas.drawPath(persZoomPath, paint)

        // 9 linee orizzontali
        paint.strokeWidth = 2F
        paint.color = Color.GRAY
        for(i in 1..9) {
            canvas.drawLine(0F, (i*height/10).toFloat(), width.toFloat(), (i*height/10).toFloat(), paint)
        }

        // 19 linee verticali
        for(i in 1..19) {
            canvas.drawLine((i*width/20).toFloat(), 0F, (i*width/20).toFloat(), height.toFloat(), paint)
        }

        // Contorno
        paint.strokeWidth = 5F
        paint.color = ContextCompat.getColor(context, R.color.grid_border)
        canvas.drawLine(0F, paint.strokeWidth/2, width.toFloat(), paint.strokeWidth/2, paint)                               // TOP
        canvas.drawLine(width.toFloat() - paint.strokeWidth/2, height.toFloat(), width - paint.strokeWidth/2, 0F, paint)    // DX
        canvas.drawLine(0F, height - paint.strokeWidth/2, width.toFloat(), height - paint.strokeWidth/2, paint)             // BOT
        canvas.drawLine(paint.strokeWidth/2, height.toFloat(), paint.strokeWidth/2, 0F, paint)                              // SX
    }

    fun setZoom(minZoomRadius: Float, maxZoomRadius: Float) {
        this.minZoomRadius = minZoomRadius
        this.maxZoomRadius = maxZoomRadius
        invalidate()
    }

    fun setPersonalizedZoom(persZoomRadius: Float) {
        this.persZoomRadius = persZoomRadius
        invalidate()
    }

}