package com.hms.demo.example

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.huawei.hms.support.hwid.result.AuthHuaweiId
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG="ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val huaweiId=intent.extras?.getParcelable<AuthHuaweiId>("account")
        Log.e(TAG,"Huawei Id ${huaweiId?.familyName} ${huaweiId?.displayName} ${huaweiId?.avatarUriString} ${huaweiId?.email}")

        name.text=huaweiId?.displayName
        mail.text=huaweiId?.email
        loadProfilePic(huaweiId?.avatarUriString)
        logout.setOnClickListener(this)
        revoke.setOnClickListener(this)
    }

    private fun loadProfilePic(avatarUriString: String?) {
        CoroutineScope(IO).launch {
            val bitmap=getBitmap(avatarUriString)
            if(bitmap!=null){
                val resizedBitmap=getResizedBitmap(bitmap,480,480)
                runOnUiThread { profilePic.setImageBitmap(resizedBitmap) }
            }
        }
    }

    private suspend fun getBitmap(avatarUriString: String?): Bitmap?{
        try {
            val url= URL(avatarUriString)
            val connection=url.openConnection()
            connection.doInput=true
            connection.connect()
            val input: InputStream = connection.getInputStream()
            return BitmapFactory.decodeStream(input)
        }catch (e: Exception){
            return null
        }
    }

    private suspend fun getResizedBitmap(bitmap:Bitmap, newHeight:Int,newWidth:Int):Bitmap{
        val width: Int = bitmap.width
        val height: Int = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat()  / height
        // CREATE A MATRIX FOR THE MANIPULATION
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP

        // "RECREATE" THE NEW BITMAP

        return Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, false)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            logout.id ->{
                val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
                val service = HuaweiIdAuthManager.getService(this, authParams)
                val signOutTask = service.signOut()
                signOutTask.addOnCompleteListener{
                    startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
                }
            }

            revoke.id ->{
                val authParams = HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams()
                val service = HuaweiIdAuthManager.getService(this, authParams)
                service.cancelAuthorization().addOnCompleteListener{
                    if (it.isSuccessful) {
                        // Processing after a successful authorization revoking.
                        startActivity(Intent(this@ProfileActivity,LoginActivity::class.java))
                    } else {
                        // Handle the exception.
                        val exception = it.exception
                        if (exception is ApiException) {
                            val statusCode = exception.statusCode
                            Log.e(TAG, "onFailure: $statusCode")
                        }
                    }
                }
            }
        }
    }
}