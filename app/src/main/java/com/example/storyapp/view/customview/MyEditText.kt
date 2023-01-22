package com.example.storyapp.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyapp.R
import com.example.storyapp.view.login.LoginActivity
import com.example.storyapp.view.register.RegisterActivity


class MyEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                LoginActivity.isErrorPassword(false)
                RegisterActivity.isErrorPassword(false)

                if (p0.toString().length < 8) {
                    error = context.getString(R.string.password_min_length)
                    LoginActivity.isErrorPassword(true)
                    RegisterActivity.isErrorPassword(true)
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}