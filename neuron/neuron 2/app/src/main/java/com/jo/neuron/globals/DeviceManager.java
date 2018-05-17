package com.jo.neuron.globals;

import android.net.wifi.p2p.WifiP2pDevice;
import android.telecom.DisconnectCause;

import com.jo.neuron.models.DeviceDTO;

import java.util.ArrayList;

import static com.jo.neuron.transfer.TransferConstants.DEVICE_DISCONNECTED;


public class DeviceManager {

    private DeviceDTO myDevice;

    private ArrayList<DeviceDTO> deviceDTOList;

    private static DeviceManager singleInstance;


    private DeviceManager() {
        myDevice = new DeviceDTO();
        myDevice.setStatus(DEVICE_DISCONNECTED);
        myDevice.setDeviceAddress("");
        myDevice.setDeviceName("");
        myDevice.setIp("");
        myDevice.setPort(-1);


        deviceDTOList = new ArrayList<>();
    }

    public static DeviceManager getInstance() {
        if (singleInstance == null) {
            synchronized (DeviceManager.class) {
                if (singleInstance == null) {
                    singleInstance = new DeviceManager();
                }
            }
        }
        return singleInstance;
    }

    public void addDevice(WifiP2pDevice wifiP2pDevice) {

        boolean exists = false;

        for (DeviceDTO device : deviceDTOList) {
            if (device.getDeviceAddress().equals(wifiP2pDevice.deviceAddress)) {
                exists = true;
            }
        }

        if (exists) {
            return;
        }

        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setIp("");
        deviceDTO.setDeviceName(wifiP2pDevice.deviceName);
        deviceDTO.setDeviceAddress(wifiP2pDevice.deviceAddress);
        deviceDTO.setPort(-1);
        deviceDTO.setStatus(DEVICE_DISCONNECTED);

        deviceDTOList.add(deviceDTO);

    }


    public ArrayList<DeviceDTO> getDeviceList() {

        return deviceDTOList;
    }

    public DeviceDTO getDevice(String ip) {

        for (DeviceDTO device : deviceDTOList) {
            if (device.getIp().equals(ip)) {
                return device;
            }
        }

        return null;

    }

    public void clearDatabase() {
        this.deviceDTOList.clear();
    }

    public void setAllDisconnected() {
        for (DeviceDTO device : deviceDTOList) {
            device.setStatus(DEVICE_DISCONNECTED);
        }
    }

    public DeviceDTO getMyDevice() {
        return myDevice;
    }

    public void setMyDevice(DeviceDTO myDevice) {
        this.myDevice = myDevice;
    }

    public void addOrUpdateDevice(DeviceDTO device) {
        if (device == null || device.getIp() == null || device.getPort() == 0) {
            return;
        }


        for (DeviceDTO deviceDTO : deviceDTOList) {
            if (deviceDTO.getDeviceAddress().equals(device.getDeviceAddress())) {
                deviceDTO.setIp(device.getIp());
                deviceDTO.setStatus(device.getStatus());
                deviceDTO.setDeviceName(device.getDeviceName());
                deviceDTO.setPort(device.getPort());
                return;
            }
        }

        deviceDTOList.add(device);
    }
}
