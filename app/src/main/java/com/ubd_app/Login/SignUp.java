package com.ubd_app.Login;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.android.volley.*;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends Fragment implements OnBackPressedListener {
    private View view;

    private String str, message;

    private Button Confirm, Back;
    private EditText Email, Pw, PwCh, Name, Birth;
    private NumberPicker Weight;

    private String email, pw, name, birth;
    private int weight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_signup, container, false);

        set();

        return view;
    }

    public void set() {
        email = ""; pw = ""; name = ""; birth="";

        Email = view.findViewById(R.id.signup_email);
        Pw = view.findViewById(R.id.signup_pw);
        PwCh = view.findViewById(R.id.signup_pwCh);
        Name = view.findViewById(R.id.signup_name);
        Birth = view.findViewById(R.id.signup_birth);
        Weight = view.findViewById(R.id.signup_weight);
        Weight.setMinValue(0);
        Weight.setMaxValue(200);
        Confirm = view.findViewById(R.id.signup_Confirm);
        Back = view.findViewById(R.id.signup_Back);

        Email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !checkEmail(Email.getText().toString())) {
                    Email.setText("");
                    Toast.makeText(getActivity(), "이메일 형식으로 작성해주세요.", Toast.LENGTH_SHORT).show();
                } else if (!hasFocus && !Email.getText().toString().equals("")) {
                    JSONObject emailcheck = new JSONObject();
                    try {
                        emailcheck.put("email", Email.getText().toString());
                        registerRequest(emailcheck, 0);
                    } catch (JSONException e) {
                        Log.d("###", e.getMessage());
                    }
                }
            }
        });

        Pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && Pw.getText().length() < 6 && !Pw.getText().toString().equals("")) {
                    Pw.setText("");
                    Toast.makeText(getActivity(), "6자리 이상으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        PwCh.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !PwCh.getText().toString().equals(Pw.getText().toString())) {
                    PwCh.setText("");
                    Toast.makeText(getActivity(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else if (!hasFocus && !PwCh.getText().toString().equals(""))
                    pw = PwCh.getText().toString();
            }
        });

        Name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !Name.getText().toString().equals(""))
                    name = Name.getText().toString();
                else if (!hasFocus && !Back.performClick())
                    Toast.makeText(getActivity(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        Birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    Birth.callOnClick();
            }
        });

        Birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatePickerDialog myDatePickerDialog = new MyDatePickerDialog();
                myDatePickerDialog.setListener(listener);
                myDatePickerDialog.show(getChildFragmentManager(), "###");
            }
        });


        Weight.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                weight = newVal;
            }
        });



        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    confirm();
                } catch (JSONException e) {
                    Log.d("###", e.getMessage());
                }
            }
        });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Login) getActivity()).SignUp(false);
                resetText();
            }
        });
    }

    public void confirm() throws JSONException {
        JSONObject confirmCheck = new JSONObject();
        if (!email.equals("") && !pw.equals("") && !name.equals("") && !birth.equals("")) {
            Log.d("###", email + ", " + pw + ", " + name + ", " + birth + ", " + weight);
            confirmCheck.put("email", email);
            confirmCheck.put("password", pw);
            confirmCheck.put("name", name);
            confirmCheck.put("birth", birth);
            if (weight != 0) {
                confirmCheck.put("weight", weight);
                registerRequest(confirmCheck, 1);
            } else {
                registerRequest(confirmCheck, 1);
            }
        } else
            Toast.makeText(getActivity(), "빈칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
    }

    public void resetText() {
        Email.setText("");
        Pw.setText("");
        PwCh.setText("");
        Name.setText("");
        Birth.setText("");
        Weight.setValue(0);
    }

    public void registerRequest(JSONObject jsonObject, int num) {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://121.150.35.204:5000/api/users/";
        String emailCheck = "emailCheck", register = "register";

        if (num == 0)
            url += emailCheck;
        else
            url += register;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(num == 0) {
                                str = response.getString("loginCheck");
                                message = response.getString("message");
                            }
                            else
                                str = response.getString("registerSuccess");

                            Log.d("###", num + "");
                            Log.d("###", str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (num == 0) {
                            if (str.equals("true")) {
                                Email.setText("");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                email = Email.getText().toString();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (str.equals("true")) {
                                ((Login)getActivity()).SignUp(false);
                                resetText();
                                Toast.makeText(getActivity(), "가입 성공", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(getActivity(), "서버 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", "error => " + error.toString());
            }
        }
        );
        queue.add(jsonObjectRequest);
    }

    public boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Birth.setText(year + "-" + month + "-" + dayOfMonth);
            birth = Birth.getText().toString();
        }
    };

    @Override
    public void onBackPressed() {
        Back.performClick();
    }
}
