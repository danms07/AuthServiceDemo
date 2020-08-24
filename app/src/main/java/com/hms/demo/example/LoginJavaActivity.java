package com.hms.demo.example;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.entity.hwid.HwIDConstant;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;

import java.util.ArrayList;
import java.util.Map;

public class LoginJavaActivity extends AppCompatActivity implements View.OnClickListener {
    private HuaweiIdAuthButton hwid;
    private ArrayList<Scope> scopes;
    private final static int HUAWEI_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_java);
        hwid = findViewById(R.id.hwid);

        hwid.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        scopes = new ArrayList<>();
        scopes.add(new Scope("email"));
        scopes.add(new Scope(HwIDConstant.SCOPE.ACCOUNT_BASEPROFILE));
        HuaweiIdAuthParams params = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setScopeList(scopes)
                .setAccessToken()
                .createParams();
        HuaweiIdAuthService service = HuaweiIdAuthManager.getService(getApplicationContext(), params);

        startActivityForResult(service.getSignInIntent(), HUAWEI_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HUAWEI_ID) {
            Task<AuthHuaweiId> task = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (task.isSuccessful()) {
                AuthHuaweiId huaweiAccount = task.getResult();
                String token = huaweiAccount.getAccessToken();
                Log.e("Token",token);
                String displayName = huaweiAccount.getDisplayName();
                String email = huaweiAccount.getEmail();

                AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(token);
                AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(signInResult -> {
                   //Registro exitoso
                    Intent intent=new Intent(LoginJavaActivity.this,ProfileActivity.class);
                    intent.putExtra("mail",email);
                    intent.putExtra("foto",signInResult.getUser().getPhotoUrl());
                    intent.putExtra("name",displayName);
                    intent.putExtra("uid",signInResult.getUser().getUid());
                    Log.e("mail",email);
                    Log.e("foto",signInResult.getUser().getPhotoUrl());
                    Log.e("uid",signInResult.getUser().getUid());
                    Log.e("displayName",signInResult.getUser().getDisplayName());
                    Log.e("Provider",signInResult.getUser().getProviderId());


                    //startActivity(intent);
                    //TODO pedir datos adicionales
                    //TODO insertar registro en BD
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Fail",e.toString());
                    }
                });


                //Intent intent=new Intent(this,ProfileActivity.class);
                //intent.putExtra("account",huaweiAccount);
            }
        }

    }
}