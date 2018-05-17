package world.waac.neuron.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import world.waac.neuron.activities.MainActivity;
import world.waac.neuron.globals.DeviceManager;
import world.waac.neuron.models.DeviceDTO;
import world.waac.neuron.models.FileRequestDTO;
import world.waac.neuron.models.SearchRequestDTO;
import world.waac.neuron.models.SearchResponseDTO;


public class DataHandler {

    public static final String DEVICE_LIST_CHANGED = "device_list_updated";
    public static final String SELECTED_DEVICE_CONFIRMED = "selected_device_confirmed";

    public static final String SEARCH_REQUEST_RECEIVED = "search_request_received";
    public static final String FILE_REQUEST_RECEIVED = "file_request_received";
    public static final String SEARCH_RESPONSE_RECEIVED = "search_response_received";
    public static final String KEY_SEARCH_REQUEST = "key_search_request";
    public static final String KEY_SEARCH_RESPONSE = "key_search_response";
    public static final String KEY_SELECTED_DEVICE = "key_selected_device";
    public static final String KEY_FILE_REQUEST = "key_file_request";


    private ITransferable data;
    private Context mContext;
    private String senderIP;
    private LocalBroadcastManager broadcaster;
    private DeviceManager deviceManager = null;

    DataHandler(Context context, String senderIP, ITransferable data) {
        this.mContext = context;
        this.data = data;
        this.senderIP = senderIP;
        this.deviceManager = DeviceManager.getInstance();
        this.broadcaster = LocalBroadcastManager.getInstance(mContext);
    }

    public void process() {

        Log.d("eeee", "Data Handler process, requestCode: " + data.getRequestCode() + "\ndata: " + data.getData());

        if (data.getRequestType().equalsIgnoreCase(TransferConstants.TYPE_REQUEST)) {
            processRequest();
        } else {
            processResponse();
        }
    }

    private void processRequest() {
        switch (data.getRequestCode()) {
            case TransferConstants.CLIENT_DATA: {
                processPeerDeviceInfo();

                String deviceJSON = data.getData();
                DeviceDTO device = DeviceDTO.fromJSON(deviceJSON);
                device.setIp(senderIP);

                Intent intent = new Intent(SELECTED_DEVICE_CONFIRMED);
                intent.putExtra(KEY_SELECTED_DEVICE, device);
                broadcaster.sendBroadcast(intent);


                DataSender.sendCurrentDeviceData(mContext, senderIP, deviceManager.getDevice(senderIP).getPort(), false);
                break;
            }
            case TransferConstants.CLIENT_DATA_WD: {
                processPeerDeviceInfo();
                Intent intent = new Intent(MainActivity.FIRST_DEVICE_CONNECTED);
                intent.putExtra(MainActivity.KEY_FIRST_DEVICE_IP, senderIP);
                broadcaster.sendBroadcast(intent);
                break;
            }
            case TransferConstants.SEARCH_REQUEST: {
                processSearchRequest();
                break;
            }
            case TransferConstants.FILE_REQUEST: {

                processFileRequest();

                break;
            }

            default:
                break;
        }
    }

    private void processFileRequest() {

        String fileRequestJSON = data.getData();
        FileRequestDTO fileRequestDTO = FileRequestDTO.fromJSON(fileRequestJSON);
        fileRequestDTO.setFromIp(senderIP);
        Intent intent = new Intent(FILE_REQUEST_RECEIVED);
        intent.putExtra(KEY_FILE_REQUEST, fileRequestDTO);
        broadcaster.sendBroadcast(intent);
    }


    private void processResponse() {
        switch (data.getRequestCode()) {
            case TransferConstants.CLIENT_DATA:
                processPeerDeviceInfo();

                String deviceJSON = data.getData();
                DeviceDTO device = DeviceDTO.fromJSON(deviceJSON);
                device.setIp(senderIP);

                Intent intent = new Intent(SELECTED_DEVICE_CONFIRMED);
                intent.putExtra(KEY_SELECTED_DEVICE, device);
                broadcaster.sendBroadcast(intent);


                break;
            case TransferConstants.CLIENT_DATA_WD:
                processPeerDeviceInfo();
                break;
            case TransferConstants.SEARCH_RESPONSE:
                processSearchResponse();
            default:
                break;
        }
    }

    private void processSearchRequest() {
        String searchRequestJSON = data.getData();
        SearchRequestDTO searchRequestDTO = SearchRequestDTO.fromJSON(searchRequestJSON);
        searchRequestDTO.setFromIP(senderIP);
        Intent intent = new Intent(SEARCH_REQUEST_RECEIVED);
        intent.putExtra(KEY_SEARCH_REQUEST, searchRequestDTO);
        broadcaster.sendBroadcast(intent);
    }

    private void processSearchResponse() {
        String searchResponseJSON = data.getData();
        SearchResponseDTO searchResponseDTO = SearchResponseDTO.fromJSON(searchResponseJSON);

        Intent intent = new Intent(SEARCH_RESPONSE_RECEIVED);
        intent.putExtra(KEY_SEARCH_RESPONSE, searchResponseDTO);
        broadcaster.sendBroadcast(intent);
    }

    private void processPeerDeviceInfo() {
        String deviceJSON = data.getData();
        DeviceDTO device = DeviceDTO.fromJSON(deviceJSON);
        device.setIp(senderIP);

        deviceManager.addOrUpdateDevice(device);


        Log.d("DXDX", Build.MANUFACTURER + " received: " + deviceJSON);

        Intent intent = new Intent(DEVICE_LIST_CHANGED);
        broadcaster.sendBroadcast(intent);
    }

}
