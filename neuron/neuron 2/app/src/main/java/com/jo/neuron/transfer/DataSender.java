package com.jo.neuron.transfer;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.jo.neuron.globals.ConnectionUtils;
import com.jo.neuron.globals.DeviceManager;
import com.jo.neuron.globals.Utility;
import com.jo.neuron.models.DeviceDTO;
import com.jo.neuron.models.FileRequestDTO;
import com.jo.neuron.models.SearchRequestDTO;
import com.jo.neuron.models.SearchResponseDTO;


public class DataSender {

    public static void sendData(Context context, String destIP, int destPort, ITransferable data) {
        Intent serviceIntent = new Intent(context,
                DataTransferService.class);
        serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
        serviceIntent.putExtra(
                DataTransferService.DEST_IP_ADDRESS, destIP);
        serviceIntent.putExtra(
                DataTransferService.DEST_PORT_NUMBER, destPort);

        serviceIntent.putExtra(DataTransferService.EXTRAS_SHARE_DATA, data);
        context.startService(serviceIntent);
    }

    public static void sendFile(Context context, String destIP, int destPort, Uri fileUri) {
        Intent serviceIntent = new Intent(context,
                DataTransferService.class);
        serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(
                DataTransferService.DEST_IP_ADDRESS, destIP);
        serviceIntent.putExtra(
                DataTransferService.DEST_PORT_NUMBER, destPort);
        serviceIntent.putExtra(
                DataTransferService.EXTRAS_FILE_PATH, fileUri.toString());

        context.startService(serviceIntent);
    }

    public static void sendCurrentDeviceData(Context context, String destIP, int destPort, boolean isRequest) {

        Log.d("eeee", "sendCurrentDeviceData" + "destIP: " + destIP + "destPort: " + destIP + "isRequest: " + isRequest);

        DeviceDTO currentDevice = DeviceManager.getInstance().getMyDevice();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));

        ITransferable transferData = null;
        if (!isRequest) {
            transferData = TransferModelGenerator.generateDeviceTransferModelResponse(currentDevice);
        } else {
            transferData = TransferModelGenerator.generateDeviceTransferModelRequest(currentDevice);
        }

        sendData(context, destIP, destPort, transferData);
    }

    public static void sendCurrentDeviceDataWD(Context context, String destIP, int destPort) {

        Log.d("eeee", "sendCurrentDeviceDataWD" + "destIP: " + destIP + "destPort: " + destIP);

        DeviceDTO currentDevice = DeviceManager.getInstance().getMyDevice();
        currentDevice.setPort(ConnectionUtils.getPort(context));
        currentDevice.setIp(Utility.getString(context, TransferConstants.KEY_MY_IP));

        ITransferable transferData = null;

        transferData = TransferModelGenerator.generateDeviceTransferModelRequestWD(currentDevice);

        sendData(context, destIP, destPort, transferData);
    }


    public static void sendSearchRequest(Context context, String destIP, int destPort, SearchRequestDTO searchRequestDTO) {

        ITransferable transferData = TransferModelGenerator.generateSearchRequestModel(searchRequestDTO);
        sendData(context, destIP, destPort, transferData);
    }

    public static void sendSearchResponse(Context context, String destIP, int destPort, SearchResponseDTO searchResponseDTO) {
        ITransferable transferData = TransferModelGenerator.generateSearchResponseModel(searchResponseDTO);
        sendData(context, destIP, destPort, transferData);

    }

    public static void sendFileRequest(Context context, String destIP, int destPort, FileRequestDTO fileRequestDTO) {
        ITransferable transferData = TransferModelGenerator.generateFileRequestModel(fileRequestDTO);
        sendData(context, destIP, destPort, transferData);
    }
}
