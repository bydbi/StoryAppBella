package com.bella.storyappbella.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.bella.storyappbella.R

class MyEditText: AppCompatEditText, View.OnTouchListener {

    private lateinit var errorImage : Drawable
    private lateinit var passImage : Drawable
    private lateinit var passImageFocus : Drawable
    private val limit  = 8

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    private fun init() {
        errorImage = ContextCompat.getDrawable(context, R.drawable.baseline_error_24) as Drawable
        passImage = ContextCompat.getDrawable(context, R.drawable.baseline_key_24) as Drawable
        passImageFocus = ContextCompat.getDrawable(context, R.drawable.baseline_key_grey_24) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                showPassIcon()
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty() && s.toString().length < limit) {
                    showErrorIcon()
                } else {
                    showPassIconFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun showErrorIcon() {
        setIconDrawables(startOfTheText = passImageFocus, endOfTheText = errorImage)
    }

    private fun showPassIcon() {
        setIconDrawables(startOfTheText = passImage)
    }

    private fun showPassIconFocus() {
        setIconDrawables(startOfTheText = passImageFocus)
    }

    private fun setIconDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }
}