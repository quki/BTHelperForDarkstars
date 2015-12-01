package com.quki.bluetooth.controller.qukibluetoothcontroller.adapter;

/**
 * Created by quki on 2015-11-28.
 */
public class PairedDeviceData {

    public int type;
    public String name;
    public String adress;

    public PairedDeviceData() {
    }

    public PairedDeviceData(int type, String name,String adress) {
        this.type = type;
        this.name = name;
        this.adress = adress;
    }
}
