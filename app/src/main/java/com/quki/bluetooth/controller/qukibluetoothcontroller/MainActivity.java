package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quki.bluetooth.controller.qukibluetoothcontroller.adapter.PairedDeviceAdapter;
import com.quki.bluetooth.controller.qukibluetoothcontroller.adapter.PairedDeviceData;
import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.BluetoothConfig;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ListView listViewPaired;
    private BluetoothAdapter mBluetoothAdapter;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert getSupportActionBar() != null;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("패어링된 기기");
        listViewPaired = (ListView)findViewById(R.id.listViewPaired);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        initBluetoothAdapter();
        listPairedDevices();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, SearchActivity.class));

            }
        });
    }

    private void initBluetoothAdapter(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothConfig.REQUEST_ENABLE_BT);
        }
    }

    private void listPairedDevices() {

        // 패어링된 기기들을 Set화 시킴
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<PairedDeviceData> pairedDeviceArrayList = new ArrayList<>();

        // 패어링된 device를 list
        for (BluetoothDevice pairedDevice : pairedDevices) {

            // 패어링된 기기의 type, name, adress를 data객체에 초기화
            int type = pairedDevice.getBluetoothClass().getMajorDeviceClass();
                PairedDeviceData data = new PairedDeviceData(type, pairedDevice.getName(),pairedDevice.getAddress());
                pairedDeviceArrayList.add(data);
        }

        PairedDeviceAdapter adapter = new PairedDeviceAdapter(this,pairedDeviceArrayList);

        listViewPaired.setAdapter(adapter);
        listViewPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String mDeviceName = ((TextView)view.findViewById(R.id.pairedDeviceName)).getText().toString();

                // AlertDialog
                AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(
                        MainActivity.this);
                mAlertBuilder.setTitle(mDeviceName)
                        .setMessage("신호를 전달할 디바이스 이름이 "+mDeviceName+"이(가) 맞습니까?")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                Intent sendDeviceName = new Intent(MainActivity.this,SendDataActivity.class);
                                sendDeviceName.putExtra("deviceName",mDeviceName);
                                startActivity(sendDeviceName);

                            }
                        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = mAlertBuilder.create();
                dialog.show();

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == BluetoothConfig.REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                listPairedDevices();
                Snackbar.make(coordinatorLayout, "블루투스를 켰습니다.", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }else{
                showCustomToast("블루투스를 꼭 켜주세요",Toast.LENGTH_SHORT);
                finish();
            }

        }
    }

    public void showCustomToast(String msg, int duration){

        //Retrieve the layout inflator
        LayoutInflater inflater = getLayoutInflater();
        //Assign the custom layout to view
        //Parameter 1 - Custom layout XML
        //Parameter 2 - Custom layout ID present in linearlayout tag of XML
        View layout = inflater.inflate(R.layout.layout_custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        TextView msgView = (TextView)layout.findViewById(R.id.toastMessage);
        msgView.setText(msg);
        //Return the application context
        Toast toast = new Toast(getApplicationContext());
        //Set toast gravity to bottom
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 100);
        //Set toast duration
        toast.setDuration(duration);
        //Set the custom layout to Toast
        toast.setView(layout);
        //Display toast
        toast.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listPairedDevices();
    }
}
