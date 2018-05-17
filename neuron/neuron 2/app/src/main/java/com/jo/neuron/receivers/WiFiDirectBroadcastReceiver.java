package com.jo.neuron.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import com.jo.neuron.activities.MainActivity;
import com.jo.neuron.globals.DeviceManager;
import com.jo.neuron.globals.Utility;
import com.jo.neuron.models.DeviceDTO;

import static android.net.wifi.p2p.WifiP2pDevice.CONNECTED;
import static com.jo.neuron.transfer.TransferConstants.DEVICE_CONNECTED;
import static com.jo.neuron.transfer.TransferConstants.DEVICE_DISCONNECTED;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel channel;
    private MainActivity activity;

    private static final String TAG = "WiFiDirectReceiver";

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
            } else {
            }
            Log.d(TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            Log.d(TAG, "P2P peers changed");

            if (manager != null) {
                manager.requestPeers(channel, activity);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo p2pInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);

            if (p2pInfo != null && p2pInfo.groupOwnerAddress != null) {
                String goAddress = Utility.getDottedDecimalIP(p2pInfo.groupOwnerAddress
                        .getAddress());
                boolean isGroupOwner = p2pInfo.isGroupOwner;
            }
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                manager.requestConnectionInfo(channel, activity);
            } else {
                DeviceManager.getInstance().setAllDisconnected();
                activity.setDisconnected();
                // It's a disconnect
                // activity.resetData();
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            DeviceDTO deviceDTO = new DeviceDTO();
            deviceDTO.setPort(-1);
            deviceDTO.setDeviceName(device.deviceName);
            deviceDTO.setStatus(device.status == CONNECTED ? DEVICE_CONNECTED : DEVICE_DISCONNECTED);
            deviceDTO.setDeviceAddress(device.deviceAddress);
            deviceDTO.setIp("");

            DeviceManager.getInstance().setMyDevice(deviceDTO);
        }
    }
}
