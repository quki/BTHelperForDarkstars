package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.ActionBluetoothRead;
import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.ActionDeviceAndPhone;
import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.BluetoothHelper;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class WaitResultActivity extends AppCompatActivity {

    private LinearLayout rootView;
    private BluetoothHelper mBluetoothHelper;
    String deviceName = "FB755v1.2.6";
    private TextView checkForRead;

    private String myArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_result);

        final Button go_result = (Button) findViewById(R.id.go_result);
        final Button start_read = (Button) findViewById(R.id.start_read);
        final Button stop_read = (Button) findViewById(R.id.stop_read);
        checkForRead = (TextView) findViewById(R.id.checkForRead);
        rootView = (LinearLayout) findViewById(R.id.rootView);
        stop_read.setEnabled(false);
        go_result.setEnabled(false);

        mBluetoothHelper = new BluetoothHelper();
        mBluetoothHelper.registerInterface(getActionInterface());
        mBluetoothHelper.registerInterfaceForRead(getActionForRead());
        mBluetoothHelper.transferDataToDevice(deviceName, "asd");

        /**
         * 읽기 시작
         */
        start_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop_read.setEnabled(true);
                //mBluetoothHelper.fetchDataFromDevice();
                mBluetoothHelper.fetchDataFromDeviceTest();
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

               ArrayList<Integer> al = parsingStringToArray(myArray);
                startActivity(new Intent(WaitResultActivity.this, ResultActivity.class).putIntegerArrayListExtra("KEYKEY", al));
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
                        //  finish();
                        Toast.makeText(getApplicationContext(), "전달 성공", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onSendError(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }
                });

            }
        };
    }


    public ActionBluetoothRead getActionForRead(){
        return new ActionBluetoothRead() {
            @Override
            public void getDataArray(final String data) {

                myArray = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkForRead.append(myArray);
                    }
                });
            }
        };
    }

    /**
     * 파싱
     * @param data (String)
     * @return ArrayList
     */
    private ArrayList<Integer>  parsingStringToArray(String data){
        ArrayList<Integer> al = new ArrayList<>();
        try {
            JSONArray jArray = new JSONArray(data);
            for(int i=0;i<jArray.length();i++){
                al.add(jArray.getInt(i));
            }
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
        return al;
    }
}
