package com.hms.demo.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        AGConnectUser user = AGConnectAuth.getInstance().getCurrentUser();
        if(user!=null){
            //ProfileActivity
            AGConnectAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginJavaActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(MainActivity.this, LoginJavaActivity.class));
            finish();
        }
        handler.postDelayed(() -> {
            //silentSignIn();

        }
                , 1000);

    }

    private void HMSVersion() {
        try {
            PackageManager pm = getPackageManager();

            PackageInfo packageInfo = pm.getPackageInfo("com.huawei.hwid", 0);

            Log.e("HMS", "Current HMS Core version: " + packageInfo.versionName);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    public void silentSignIn(){
        ArrayList<Scope> scopes = new ArrayList<>();
        scopes.add(new Scope("email"));
        HuaweiIdAuthParams authParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setScopeList(scopes)
                .createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(MainActivity.this, authParams);
        Task<AuthHuaweiId> task = service.silentSignIn();
        task.addOnSuccessListener(authHuaweiId -> {//Ya habia un usuario registrado, se procede a usar las mismas credenciales
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("account", authHuaweiId);
            startActivity(intent);
            finish();

        });
        task.addOnFailureListener(exception -> {//No hay Huawei ID en esta app se lanza Login
            startActivity(new Intent(MainActivity.this, LoginJavaActivity.class));

            finish();
        });
    }
}