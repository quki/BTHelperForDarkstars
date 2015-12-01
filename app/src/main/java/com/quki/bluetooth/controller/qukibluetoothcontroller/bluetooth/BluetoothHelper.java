package com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;
import java.util.UUID;

/**
 * Created by quki on 2015-11-29.
 */
public class BluetoothHelper {

    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    // UUID 설정 (SPP)
    private UUID SPP_UUID = java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice targetDevice = null;
    private BluetoothSocket mBluetoothSocket = null;

    private static final String TAG = "==BluetoothHelper==";
    private ActionDeviceAndPhone mActionInterface;

    public void registerInterface(ActionDeviceAndPhone mActionInterface){
        this.mActionInterface = mActionInterface;
    }

    /*
     * 해당 Device Name 을 가진 Device로 데이터 전달함
     * */
    public void transferDataToDevice(String mDeviceName,String data) {

        // 페어링 된 device를 target으로 저장
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(mDeviceName)) {
                targetDevice = pairedDevice;
                Log.e(TAG, targetDevice.toString());
                break;
            }
        }

        // If the device was not found, toast an error and return
        if (targetDevice == null) {
            Log.e(TAG, "target decvice is NULL");
            return;
        }

        // Create a connection to the device with the SPP UUID
        try {
            mBluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(SPP_UUID);
        } catch (IOException e) {
            e.printStackTrace();
            mActionInterface.onSendError(e.getMessage());
            return;
        }

        // Connect to the PC and Android
        try {
            mBluetoothSocket.connect();
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect with the device");
            e.printStackTrace();
            mActionInterface.onSendError("Unable to connect with the device");
            return;
        }

        // Write the data by using OutputStreamWriter
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write(data);     //////////////////////////////////////////data 전달
            outputStreamWriter.flush();
            Log.e(TAG, "===write===");
            mActionInterface.onSendSuccess();
        } catch (IOException e) {
            Log.e(TAG, "Unable to send message to the device");
            e.printStackTrace();
            mActionInterface.onSendError("Unable to send message to the device");
        }

        disconnect();

    }

    // Close the Socket
    private void disconnect(){

        try {
            mBluetoothSocket.close();
            Log.e(TAG, "===Close===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
