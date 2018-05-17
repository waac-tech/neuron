package world.waac.neuron.models;

import com.google.gson.Gson;

import java.io.Serializable;

public class DeviceDTO implements Serializable {

    private String deviceName;
    private String deviceAddress;
    private String ip;
    private int port;
    private int status;


    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        String stringRep = (new Gson()).toJson(this);
        return stringRep;
    }

    public static DeviceDTO fromJSON(String jsonRep) {
        Gson gson = new Gson();
        DeviceDTO deviceDTO = gson.fromJson(jsonRep, DeviceDTO.class);
        return deviceDTO;
    }
}
