package com.quki.bluetooth.controller.qukibluetoothcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.lang.reflect.Method;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    private SearchFragment fragment = null;
    private RippleBackground rippleBackground;
    private BroadcastReceiver mBroadcastReceiver;
    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("주변기기 검색");

        rippleBackground = (RippleBackground) findViewById(R.id.content);
        ImageView centerIcon = (ImageView) findViewById(R.id.centerImage);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        centerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothAdapter.isDiscovering()) {

                    mBluetoothAdapter.cancelDiscovery();
                    rippleBackground.stopRippleAnimation();
                    Toast.makeText(SearchActivity.this, "검색중지", Toast.LENGTH_SHORT).show();
                } else {

                    // Fragment 초기화
                    fragment = new SearchFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_body, fragment)
                            .commit();


                    // 애니매이션 효과
                    rippleBackground.startRippleAnimation();
                    Toast.makeText(SearchActivity.this, "검색을 시작합니다", Toast.LENGTH_SHORT).show();
                    mBluetoothAdapter.startDiscovery();
                }
            }
        });
    }

    public void myOnclick(View v){

        final BluetoothDevice deviceFound =  (BluetoothDevice)v.getTag();
        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(SearchActivity.this);
        mAlertBuilder.setTitle(deviceFound.getName()+" ("+deviceFound.getAddress()+")")
                .setMessage("블루투스 페어링을 요청하시겠어요?\n상대 기기의 승인 이후 페어링이 완료 됩니다.")
                .setCancelable(false)
                .setPositiveButton("연결요청", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mBluetoothAdapter.cancelDiscovery();
                        rippleBackground.stopRippleAnimation();

                        try {

                            Method method = deviceFound.getClass().getMethod("createBond", (Class[]) null);
                            method.invoke(deviceFound, (Object[]) null);

                        } catch (Exception e) {
                            Toast.makeText(SearchActivity.this
                                    , "요청오류", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog mDialog = mAlertBuilder.create();
        mDialog.show();


    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();// retrieve : 검색하다.

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // 새로운 기기를 찾았을 때...
                    BluetoothDevice devicesFound= intent
                            .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // 패어링 안되있는 기기 만 찾아서 아이콘을 띄움
                    if(!isPaired(devicesFound)){
                        fragment.setDeviceIconFound(devicesFound);
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {

                    // 검색이 완료되었을 때...
                    Toast.makeText(SearchActivity.this,"주변 기기 검색이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    rippleBackground.stopRippleAnimation();

                } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                    //broadcast intent 에는 현재 스캔모드와 이전 스캔 모드가 엑스트라로 포함된다.
                    int preScanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, -1);
                    int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                } else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){


                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        Toast.makeText(SearchActivity.this, "연결 완료", Toast.LENGTH_SHORT).show();
                        finish();

                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                        Toast.makeText(SearchActivity.this, "UnPaired", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        };
        // IntentFilter 이벤트를 모니터링 할 수 있다.
        IntentFilter deviceFoundFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND); // 새로운 기기를 찾았을 때
        IntentFilter deviveDiscoveryFinishedFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // 탐색을 끝냈을때
        IntentFilter scanModechanged = new IntentFilter(
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        IntentFilter bondStateChanged = new IntentFilter(
                BluetoothDevice.ACTION_BOND_STATE_CHANGED);

        // 리시버와 intentfilter를 등록한다. 이는 해당 이벤트를 BroadcastReceiver로 통보하도록 요구!
        registerReceiver(mBroadcastReceiver, deviceFoundFilter);
        registerReceiver(mBroadcastReceiver, deviveDiscoveryFinishedFilter);
        registerReceiver(mBroadcastReceiver, scanModechanged);
        registerReceiver(mBroadcastReceiver, bondStateChanged);

        super.onPostCreate(savedInstanceState);
    }
    @Override
    protected void onDestroy() {

        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();

    }
    // 이미 기기가 Pairing 되어있는지 확인...
    private boolean isPaired(BluetoothDevice deviceFound){

        boolean isPaired= false;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice pariedDevice : pairedDevices){

            if(pariedDevice.equals(deviceFound)){
                isPaired = true;
            }
        }
        return isPaired;
    }

}
