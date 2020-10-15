package com.ubd_app.Login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import androidx.fragment.app.DialogFragment;
import com.ubd_app.OnBackPressedListener;
import com.ubd_app.R;

import java.util.Calendar;

public class MyDatePickerDialog extends DialogFragment implements OnBackPressedListener {

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    Button btnConfirm;
    Button btnCancel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.date_picker, null);

        btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        final NumberPicker yearPicker = (NumberPicker) dialog.findViewById(R.id.picker_year);
        final NumberPicker monthPicker = (NumberPicker) dialog.findViewById(R.id.picker_month);
        final NumberPicker dayPicker = (NumberPicker) dialog.findViewById(R.id.picker_day);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
                MyDatePickerDialog.this.getDialog().cancel();
            }
        });

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        yearPicker.setMinValue(cal.get(Calendar.YEAR)-100);
        yearPicker.setMaxValue(cal.get(Calendar.YEAR));
        yearPicker.setValue(cal.get(Calendar.YEAR));

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(cal.get(Calendar.DAY_OF_MONTH));

        builder.setView(dialog);

        return builder.create();
    }


    @Override
    public void onBackPressed() {
        btnCancel.performClick();
    }
}
