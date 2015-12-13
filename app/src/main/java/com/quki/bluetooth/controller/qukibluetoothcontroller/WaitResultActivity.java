package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.BluetoothHelper;

public class WaitResultActivity extends AppCompatActivity {


    BluetoothHelper mBluetoothHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_result);

        final Button go_result = (Button) findViewById(R.id.go_result);
        final Button start_read = (Button) findViewById(R.id.start_read);
        final Button stop_read = (Button) findViewById(R.id.stop_read);
        stop_read.setEnabled(false);
        go_result.setEnabled(false);
        mBluetoothHelper = new BluetoothHelper();

        /**
         * 읽기 시작
         */
        start_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop_read.setEnabled(true);
                mBluetoothHelper.fetchDataFromDevice();
            }
        });

        /**
         * 읽기 중지
         */
        stop_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go_result.setEnabled(true);
                try {
                    mBluetoothHelper.stopRead();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        /**
         * 결과 확인
         */
        go_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WaitResultActivity.this, ResultActivity.class));
            }
        });
    }
}
