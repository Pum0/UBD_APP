package com.ubd_app.bottomMenu.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ubd_app.AppDatabase.userData;
import com.ubd_app.Login.Login;
import com.ubd_app.Main;
import com.ubd_app.R;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ectSetting extends BottomSheetDialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Main main;

    private View view;

    //설정내의 버튼
    private Button weight, searchRemove, logout;

    private EditText editText;

    public userData ud;

    public ectSetting() {

    }

    @Override
    public void onCreate(@androidx.annotation.Nullable @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = (Main) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_etc_setting, container, false);

        setButton();

        CompoundButton setting2 = view.findViewById(R.id.setting_switch2);

        main.UserDataViewModel.getAll().observe(this, new Observer<List<userData>>() {
            @Override
            public void onChanged(List<userData> user) {
                ud = user.get(0);
                int sysc = ud.getSyncCheck();
                int s = ud.getUserWeight();
                editText.setText(String.valueOf(s));
                Log.d("!!!!", user.get(0).toString());
                if (sysc == 1) {
                    setting2.setChecked(true);
                    main.SysC = true;
                } else {
                    setting2.setChecked(false);
                    main.SysC = false;
                }
            }
        });
        setting2.setOnCheckedChangeListener(this);

        return view;
    }

    private void setButton() {
        editText = (EditText) view.findViewById(R.id.ecteditText);
        weight = (Button) view.findViewById(R.id.weightButton);
        weight.setOnClickListener(this);
        searchRemove = (Button) view.findViewById(R.id.searchRemove);
        searchRemove.setOnClickListener(this);
        logout = (Button) view.findViewById(R.id.logout);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.weightButton):
                int weight = Integer.parseInt(String.valueOf(editText.getText()));
                if (1 < weight && weight < 150) {
                    String id = ud.getUserID();
                    String pw = ud.getPassWord();
                    int sysc = ud.getSyncCheck();
                    main.UserDataViewModel.update(new userData(1, id, pw, weight, sysc));

                    main.getIMM().hideSoftInputFromWindow(editText.getWindowToken(), 0);

                    Toast.makeText(getContext(), "몸무게 저장", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "다시 입력해 주세요 (몸무게 : 1~150)", Toast.LENGTH_SHORT).show();
                }

                break;
            case (R.id.searchRemove):
                main.wordViewModel.deleteAll();
                Toast.makeText(getContext(), "검색기록이 삭제 되었습니다", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.logout):
//                main.UserDataViewModel.deleteAll();
//                Toast.makeText(getContext(), "로그아웃 메소드 삽입요망, 디비에서 삭제 까지완료", Toast.LENGTH_SHORT).show();
                logout();
                break;
            default:
                break;
        }

    }

    public void logout() {
        Intent intent = new Intent(getActivity(), Login.class);
        intent.putExtra("loginCheck", true);
        startActivity(intent);

        main.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        main.finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            dbUpLoad(1, true);
        } else {
            dbUpLoad(0, false);
        }
    }

    private void dbUpLoad(int i, boolean bln) {
        ud.setSyncCheck(i);
        main.UserDataViewModel.update(ud);
        main.SysC = bln;

    }
}

