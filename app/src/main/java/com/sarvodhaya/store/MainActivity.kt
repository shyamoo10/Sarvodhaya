package com.sarvodhaya.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sarvodhaya.store.core.ui.theme.SarvodhayaTheme
import com.sarvodhaya.store.ui.SarvodhayaRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as SarvodhayaApp).container
        setContent {
            SarvodhayaTheme {
                SarvodhayaRoot(container = container)
            }
        }
    }
}
