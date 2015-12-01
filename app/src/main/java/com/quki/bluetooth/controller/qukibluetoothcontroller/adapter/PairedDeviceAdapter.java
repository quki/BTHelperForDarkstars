package com.quki.bluetooth.controller.qukibluetoothcontroller.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quki.bluetooth.controller.qukibluetoothcontroller.bluetooth.BluetoothConfig;
import com.quki.bluetooth.controller.qukibluetoothcontroller.R;

import java.util.ArrayList;

/**
 * Created by quki on 2015-11-28.
 */
public class PairedDeviceAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<PairedDeviceData> pairedDevicesList;
    private Activity activity;


    public PairedDeviceAdapter(Activity activity, ArrayList<PairedDeviceData> pairedDevicesList) {
        this.activity = activity;
        this.pairedDevicesList = pairedDevicesList;
    }


    @Override
    public int getCount() {
        return pairedDevicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return pairedDevicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_paired, null);

        TextView textViewPairedName = (TextView) convertView.findViewById(R.id.pairedDeviceName);
        TextView textViewPairedAdress = (TextView) convertView.findViewById(R.id.textViewPairedAdress);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        textViewPairedName.setText(pairedDevicesList.get(position).name);
        textViewPairedAdress.setText(String.valueOf(pairedDevicesList.get(position).adress));

        switch (pairedDevicesList.get(position).type) {
            case BluetoothConfig.COMPUTER:
                image.setImageResource(R.drawable.ic_pc);
                break;
            case BluetoothConfig.WEARABLE:
                image.setImageResource(R.drawable.ic_watch);
                break;
            case BluetoothConfig.PHONE:
                image.setImageResource(R.drawable.ic_phone);
                break;
            case BluetoothConfig.AUDIO_VIDEO:
                image.setImageResource(R.drawable.ic_headphone);
                break;
            case BluetoothConfig.UNCATEGORIZED:
                image.setImageResource(R.drawable.ic_phone);
                break;
            default:
                image.setImageResource(R.drawable.ic_phone);
                break;
        }

        return convertView;
    }
}
