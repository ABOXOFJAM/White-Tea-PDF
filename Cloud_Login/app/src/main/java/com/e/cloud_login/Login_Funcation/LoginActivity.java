package com.e.cloud_login.Login_Funcation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.e.cloud_login.Data.JSON.RegisterJson;
import com.e.cloud_login.R;

public class LoginActivity extends AppCompatActivity {
    EditText met_username,met_password;
    Button mbtn_start;
    TextView mtv_register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        met_username = findViewById(R.id.et_username);
        met_password = findViewById(R.id.et_password);
        mbtn_start =findViewById(R.id.btn_start);
        mtv_register=findViewById(R.id.tv_register);
        mbtn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn_start();
            }
        });
        mtv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    /*****登入按钮*****/
    public void login_btn_start(){
        String uname =met_username.getText().toString();
        String pword =met_password.getText().toString();
        AccountLogin alogin = new AccountLogin();
        Intent intent = new Intent();
        Runnable runnable =new Runnable() {
            @Override
            public void run() {
                try {
                    alogin.accountLogin(uname, pword);
                    intent.putExtra("token",alogin.token);//将获得到的token传给别的activity
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        Thread thread =new Thread(runnable);
        try{//重新开一个进程,4秒内没反应就显示失败
        thread.start();
        thread.join(4000);
        Log.i(alogin.token,alogin.token+"————测试—————");
        if(alogin.status&&alogin.token!=null&&alogin.user!=null){
            Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
            //设置登入状态
            //将这个userinfo传入别的Activity
            SharedPreferences.Editor userinfo = this.getSharedPreferences("登入状态", Context.MODE_PRIVATE).edit();
            userinfo.putBoolean("STATE",true);
            userinfo.putString("ID",uname);
            userinfo.putString("PSWD",pword);
            userinfo.commit();
        }
        else    Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            };
    /****重写onActivityResult()以接收RegisterActivity的数据****/
    void onActivityResult(){

    }
    }
