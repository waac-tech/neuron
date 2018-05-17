package world.waac.neuron.transfer;


public interface TransferConstants {

    int INITIAL_DEFAULT_PORT = 8124;

    int CLIENT_DATA = 3001;
    int CLIENT_DATA_WD = 3003;
    int SEARCH_REQUEST = 4000;
    int SEARCH_RESPONSE = 4001;
    int FILE_REQUEST = 4002;

    int DEVICE_DISCONNECTED = 1000;
    int DEVICE_CONNECTING = 1001;
    int DEVICE_CONNECTED = 1002;

    String TYPE_REQUEST = "request";
    String TYPE_RESPONSE = "response";

    String KEY_MY_IP = "myip";
//    String KEY_USER_NAME = "username";
}