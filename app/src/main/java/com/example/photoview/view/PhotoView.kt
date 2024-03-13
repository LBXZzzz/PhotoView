package com.example.photoview.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.photoview.R

class PhotoView : View {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private lateinit var paint: Paint
    private lateinit var bitmap: Bitmap

    private var originalOffsetX = 0f
    private var originalOffsetY = 0f

    private var smallSale = 0f
    private var bigScale = 0f
    private var currentScale = 0f

    private val OVER_SCALE_FACTOR = 1

    //是否已经放大
    private var isEnlarge = false

    //手势
    private var gestureDetector: GestureDetector? = null

    //属性动画
    private var objectAnimator: ObjectAnimator? = null

    private fun init(context: Context) {
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
        paint = Paint()
        gestureDetector = GestureDetector(context, PhotoGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            gestureDetector?.onTouchEvent(it) ?: super.onTouchEvent(it)
        } ?: super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.scale(currentScale, currentScale, width / 2f, height / 2f)
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint)
    }

    /**
     * 在onMeasure调用之后就会调用到onSizeChanged
     *
     * 每次改变尺寸时也会调用
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        originalOffsetX = (width - bitmap.width) / 2f
        originalOffsetY = (height - bitmap.height) / 2f

        //说明图片是横向的
        if (bitmap.width / bitmap.height > width / height) {
            smallSale = width / bitmap.width.toFloat()
            bigScale = height / bitmap.height.toFloat() * OVER_SCALE_FACTOR
        } else {
            smallSale = height / bitmap.height.toFloat()
            bigScale = width / bitmap.width.toFloat() * OVER_SCALE_FACTOR
        }
        currentScale = smallSale
    }

    private fun getObjectAnimator(): ObjectAnimator {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0f)
        }
        //属性动画的变化区间
        objectAnimator!!.setFloatValues(smallSale,bigScale)
        return objectAnimator!!
    }

    private fun setCurrentScale(currentScale: Float) {
        this.currentScale = currentScale
        invalidate()
    }

    inner class PhotoGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        /**
         * 延时触发100ms,为了点击效果，比如水波纹
         */
        override fun onShowPress(e: MotionEvent) {
            super.onShowPress(e)
        }

        /**
         * Up时触发，双击的时候只会在第二次up的时候触发
         */
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return super.onSingleTapUp(e)
        }


        /**
         * 相当于MOVE事件
         */
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }


        /**
         * 长按
         */
        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
        }

        /**
         *  抛掷，惯性
         */
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        /**
         * 单击按下时触发，双击不触发
         */
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return super.onSingleTapConfirmed(e)
        }

        /**
         *   双击，第二按下的时候触发 40ms(抖动)-300ms
         */
        override fun onDoubleTap(e: MotionEvent): Boolean {
            isEnlarge = !isEnlarge
           /* currentScale = if (isEnlarge) {
                bigScale
            } else {
                smallSale;
            }*/

            if (isEnlarge) {
                //启动属性动画，由大变小
               getObjectAnimator().start()
            } else {
                getObjectAnimator().reverse()
            }
            //刷新
            invalidate()
            return super.onDoubleTap(e)
        }

        /**
         * 双击触发，DOWN,MOVE,UP都会触发
         */
        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            return super.onDoubleTapEvent(e)
        }
    }
}