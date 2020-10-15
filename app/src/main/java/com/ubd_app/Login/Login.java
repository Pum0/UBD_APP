package com.ubd_app.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ubd_app.KeyValues;
import com.ubd_app.Main;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Login extends AppCompatActivity implements KeyValues {


    private EditText id, pw;
    private Button signUP;
    private ImageView logo;

    private SignUp signUp = new SignUp();

    private ProgressDialog progressDialog;

    private SharedPreferences pref;

    private long backKeyPressedTime = 0;
    private Toast toast;

    //프래그먼트 매니저
    public androidx.fragment.app.FragmentManager FragmentManager = getSupportFragmentManager();
    public FragmentTransaction Transaction;

    public Login() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        findViewById(R.id.login_box).setVisibility(View.INVISIBLE);
        findViewById(R.id.logo_text).setVisibility(View.INVISIBLE);

        Animation ani = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        Animation alp = AnimationUtils.loadAnimation(this, R.anim.fadein);
        logo = findViewById(R.id.Logo);
        logo.startAnimation(ani);

        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.logo_text).startAnimation(alp);
                findViewById(R.id.logo_text).setVisibility(View.VISIBLE);
                findViewById(R.id.login_box).startAnimation(alp);
                findViewById(R.id.login_box).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pref = getSharedPreferences("loginData", MODE_PRIVATE);

        if(getIntent().getBooleanExtra("loginCheck", false)) {
            Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show();
            Logout();
        }

        if(!(pref.getString("text", "")).isEmpty()) {
            Login(true,  "자동 로그인 성공");
        }

        set();
        setprogressDialog();
    }

    public void setprogressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login Check...");
        progressDialog.setCancelable(true);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
    }

    public void set() {
        id = findViewById(R.id.idBox);
        pw = findViewById(R.id.pwBox);

        signUP = findViewById(R.id.signup);
        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp(true);
            }
        });

        Button signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.getText().toString().equals("") || pw.getText().toString().equals(""))
                    Toast.makeText(getBaseContext(), "아이디나 비밀번호가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        progressDialog.show();

                        LoginRequest(id.getText().toString(), pw.getText().toString());

                    } catch (JSONException e) {

                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String loginSuccess, message, userID;

    public void LoginRequest(String id, String pw) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Server+"/api/users/login";

        JSONObject loginTest = new JSONObject();
        loginTest.put("email", id);
        loginTest.put("password", pw);


        Log.d("###", loginTest.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginTest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            loginSuccess = response.getString("loginSuccess");
                            message = response.getString("message");


                            if (loginSuccess.equals("true")) {
                                userID = response.getString("userID");

                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("text", userID);
                                editor.commit();
                            }

                            Log.d("###", loginSuccess);
                            Log.d("###", message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (loginSuccess.equals("true")) {
                            progressDialog.dismiss();

                            Login(true, "로그인 성공");
                        } else {
                            progressDialog.dismiss();
                            Login(false, message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "error => " + error.toString());
                progressDialog.dismiss();
                Login(false, "인터넷 연결을 확인해주세요.");
            }
        }
        );
        queue.add(jsonObjectRequest);
    }


    // 로그인 버튼
    public void Login(boolean res, String message) {
        if (res) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Main.class);
            intent.putExtra("userID", pref.getString("text", ""));
            startActivity(intent);

            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            id.setText("");
            pw.setText("");
        }
    }

    public void Logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("text", "");
        editor.commit();
    }

    // 회원가입 버튼
    public void SignUp(boolean values) {
        if (values) {
            Transaction = FragmentManager.beginTransaction();
            Transaction.replace(R.id.SignUp_Frame, signUp).commitAllowingStateLoss();
        } else {
            Transaction = FragmentManager.beginTransaction();
            Transaction.remove(signUp).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList.size() != 0) {
            //TODO: Perform your logic to pass back press here
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof OnBackPressedListener) {
                    ((OnBackPressedListener) fragment).onBackPressed();
                }
            }
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
                toast.show();
                return;
            }

            if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
                finish();
                toast.cancel();
                toast = Toast.makeText(this, "이용해 주셔서 감사합니다.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
