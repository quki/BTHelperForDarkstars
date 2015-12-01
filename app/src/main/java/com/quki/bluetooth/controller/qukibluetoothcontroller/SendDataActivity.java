package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.ActionDeviceAndPhone;
import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.BluetoothHelper;

public class SendDataActivity extends AppCompatActivity {

    private LinearLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("시간데이터전달");

        rootView = (LinearLayout) findViewById(R.id.rootView);

        Intent intent = getIntent();
        final String deviceName = intent.getStringExtra("deviceName");

        // Time Picker
        final NumberPicker hourePicker = (NumberPicker) findViewById(R.id.minutePicker);
        final NumberPicker minPicker = (NumberPicker) findViewById(R.id.secondPicker);

        // Picker 설정 부분 (Max,Min설정,글자색 white설정)
        hourePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        hourePicker.setMaxValue(23);
        hourePicker.setMinValue(0);
        minPicker.setMaxValue(59);
        minPicker.setMinValue(0);

        Button sendBtn = (Button) findViewById(R.id.sendBtn);

        sendBtn.setText("SEND TO " + "\'" + deviceName + "\'");
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // SettingFragment로 알람의 분,초 값 각각 전달
                int hour = hourePicker.getValue();
                int min = minPicker.getValue();
                int dataIntegerForm = hour*60+min;
                String data = Integer.valueOf(dataIntegerForm).toString();
                Toast.makeText(getApplicationContext(), hour +"시간"+ min+"분" + "\ndata : "+dataIntegerForm + "\ndevice name : "+deviceName, Toast.LENGTH_SHORT).show();


                /////////////////////////////데이터 전달 부분////////////////////////////
                 BluetoothHelper btHelper = new BluetoothHelper();
                 btHelper.registerInterface(getActionInterface());
                 btHelper.transferDataToDevice(deviceName, data);

            }
        });

    }

    public ActionDeviceAndPhone getActionInterface(){
        return new ActionDeviceAndPhone() {
            @Override
            public void onSendSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Toast.makeText(getApplicationContext(), "전달 성공", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onSendError(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(rootView, msg,Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

            }
        };
    }
}
