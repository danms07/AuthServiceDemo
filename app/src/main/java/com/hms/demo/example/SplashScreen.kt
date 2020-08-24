package com.hms.demo.example

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val handler= Handler()
        handler.postDelayed({
            val scopes= listOf(Scope("email"))
            val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                    .setScopeList(scopes)
                    .createParams()
            val service = HuaweiIdAuthManager.getService(this@SplashScreen, authParams)
            val task = service.silentSignIn()
            task.addOnSuccessListener {
                val intent= Intent(this@SplashScreen,ProfileActivity::class.java)
                intent.putExtra("account",it)
                startActivity(intent)
                finish()
            }
            task.addOnFailureListener{
                startActivity(Intent(this@SplashScreen,LoginActivity::class.java))
                finish()
            }
        },1000)
    }
}