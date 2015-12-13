package com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
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

    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    InputStream inputStream;

    public void registerInterface(ActionDeviceAndPhone mActionInterface) {
        this.mActionInterface = mActionInterface;
    }

    /**
     * WRITE
     */
    public void transferDataToDevice(String mDeviceName, String data) {

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


        connect();
        writeData(data);
        disconnect();

    }

    /**
     * READ (FETCH)
     */
    public void fetchDataFromDevice() {
        connect();
        readeData();
        disconnect();
    }

    private void connect() {
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
        }
    }

    // Close the Socket
    private void disconnect() {

        try {
            mBluetoothSocket.close();
            Log.e(TAG, "===Close===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeData(String data) {
        // Write and Read the data by using OutputStreamWriter
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    mBluetoothSocket.getOutputStream());
            outputStreamWriter.write(data);     //////////////////////////////////////////data write
            outputStreamWriter.flush();
            Log.e(TAG, "===write===");
            mActionInterface.onSendSuccess();
        } catch (IOException e) {
            Log.e(TAG, "Unable to send message to the device");
            e.printStackTrace();
            mActionInterface.onSendError("Unable to send message to the device");
        }
    }

    private void readeData() {
        // Write and Read the data by using OutputStreamWriter
        try {
            inputStream = mBluetoothSocket.getInputStream();
            beginListenForData();
        } catch (IOException e) {
            Log.e(TAG, "Unable to fetch message to the device");
            e.printStackTrace();
            mActionInterface.onSendError("Cannot read message from device");
        }
    }


    private void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d("===FETCHED DATA===", data);
                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    /**
     * READ(FETCH) 이후 닫아줄 때 사용.
     * @throws IOException
     */
    public void stopRead() throws IOException {
        stopWorker = true;
        inputStream.close();
        mBluetoothSocket.close();
    }
}
