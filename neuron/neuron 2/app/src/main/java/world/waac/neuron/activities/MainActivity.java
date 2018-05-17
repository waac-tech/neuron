package world.waac.neuron.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.waac.neuron.R;
import world.waac.neuron.globals.DeviceManager;
import world.waac.neuron.fragments.FilesFragment;
import world.waac.neuron.fragments.HomeFragment;
import world.waac.neuron.fragments.SettingsFragment;
import world.waac.neuron.globals.AppController;
import world.waac.neuron.globals.ConnectionUtils;
import world.waac.neuron.globals.GlobalConstants;
import world.waac.neuron.globals.NotificationToast;
import world.waac.neuron.globals.Utility;
import world.waac.neuron.models.DeviceDTO;
import world.waac.neuron.models.FileRequestDTO;
import world.waac.neuron.models.SearchRequestDTO;
import world.waac.neuron.models.SearchResponseDTO;
import world.waac.neuron.receivers.WiFiDirectBroadcastReceiver;
import world.waac.neuron.transfer.DataHandler;
import world.waac.neuron.transfer.DataSender;
import world.waac.neuron.transfer.TransferConstants;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static world.waac.neuron.transfer.DataHandler.KEY_SELECTED_DEVICE;
import static world.waac.neuron.transfer.DataHandler.SELECTED_DEVICE_CONFIRMED;
import static world.waac.neuron.transfer.TransferConstants.DEVICE_CONNECTED;
import static world.waac.neuron.transfer.TransferConstants.DEVICE_CONNECTING;
import static world.waac.neuron.transfer.TransferConstants.DEVICE_DISCONNECTED;

public class MainActivity extends FragmentActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener {

    @BindView(R.id.pager)
    ViewPager viewPager;

    @BindView(R.id.pager_tab_strip)
    PagerTabStrip pagerTabStrip;

    HomeFragment homeFragment = HomeFragment.newInstance(this);
    SettingsFragment settingsFragment = SettingsFragment.newInstance(this);
    FilesFragment filesFragment = FilesFragment.newInstance(this);


    public static final String FIRST_DEVICE_CONNECTED = "first_device_connected";
    public static final String KEY_FIRST_DEVICE_IP = "first_device_ip";


    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifip2pChannel;
    WiFiDirectBroadcastReceiver wiFiDirectBroadcastReceiver;

    private boolean isWDConnected = false;

    private AppController appController;

    boolean isConnectionInfoSent = false;
    private DeviceDTO selectedDevice = null;

    private ProgressDialog discoverProgressDialog = null;
    private ProgressDialog connectingDialog = null;


    private BroadcastReceiver localDashReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case FIRST_DEVICE_CONNECTED:

                    Log.d("eeee", "FIRST_DEVICE_CONNECTED");

                    appController.restartConnectionListenerWith(ConnectionUtils.getPort(MainActivity.this));

                    String senderIP = intent.getStringExtra(KEY_FIRST_DEVICE_IP);
                    int port = DeviceManager.getInstance().getDevice(senderIP).getPort();
                    DataSender.sendCurrentDeviceData(MainActivity.this, senderIP, port, true);

                    break;
                case SELECTED_DEVICE_CONFIRMED:

                    isWDConnected = true;

                    if (connectingDialog != null && connectingDialog.isShowing()) {
                        connectingDialog.dismiss();
                    }

                    DeviceDTO _selectedDevice = (DeviceDTO) intent.getSerializableExtra(KEY_SELECTED_DEVICE);

                    Log.d("eeee", "SELECTED_DEVICE_CONFIRMED" + _selectedDevice.toString());

                    _selectedDevice.setStatus(DEVICE_CONNECTED);


                    DeviceManager.getInstance().addOrUpdateDevice(_selectedDevice);

                    MainActivity.this.setSelectedDevice(_selectedDevice);

                    settingsFragment.refreshDeviceList();

                    break;
                case DataHandler.DEVICE_LIST_CHANGED:
                    settingsFragment.refreshDeviceList();
//                    setToolBarTitle(peerCount);
                    break;
                case DataHandler.SEARCH_REQUEST_RECEIVED:
                    SearchRequestDTO searchRequestDTO = (SearchRequestDTO) intent.getSerializableExtra(DataHandler.KEY_SEARCH_REQUEST);

                    List<String> filePaths = new ArrayList<>();
                    String keyword = searchRequestDTO.getKeyword();

                    for (File file : GlobalConstants.files) {
                        if (file.getAbsolutePath().contains(keyword)) {
                            filePaths.add(file.getAbsolutePath());
                        }
                    }

                    DataSender.sendSearchResponse(MainActivity.this, searchRequestDTO.getFromIP(), searchRequestDTO.getPort(), new SearchResponseDTO(filePaths));
                    break;
                case DataHandler.SEARCH_RESPONSE_RECEIVED:
                    SearchResponseDTO searchResponseDTO = (SearchResponseDTO) intent.getSerializableExtra(DataHandler.KEY_SEARCH_RESPONSE);

                    List<String> responseFilePaths = searchResponseDTO.getFilePaths();

                    gotoSearchResultActivity(responseFilePaths, selectedDevice);

                    break;
                case DataHandler.FILE_REQUEST_RECEIVED:

                    FileRequestDTO fileRequestDTO = (FileRequestDTO) intent.getSerializableExtra(DataHandler.KEY_FILE_REQUEST);
                    String filePath = fileRequestDTO.getFilePath();

                    DataSender.sendFile(MainActivity.this, fileRequestDTO.getFromIp(), selectedDevice.getPort(), Uri.fromFile(new File(filePath)));

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    return homeFragment;
                }
                if (position == 1) {
                    return settingsFragment;
                }
                return filesFragment;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "Home";
                }
                if (position == 1) {
                    return "Settings";
                }
                return "Files";
            }
        });

        initialize();


    }


    private void initialize() {

        String myIP = Utility.getWiFiIPAddress(MainActivity.this);
        Utility.saveString(MainActivity.this, TransferConstants.KEY_MY_IP, myIP);


        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        wifip2pChannel = wifiP2pManager.initialize(this, getMainLooper(), null);

        // Starting connection listener with default port for now
        appController = (AppController) getApplicationContext();
        appController.startConnectionListener(TransferConstants.INITIAL_DEFAULT_PORT);

    }

    @Override
    protected void onPause() {
//        if (mNsdHelper != null) {
//            mNsdHelper.stopDiscovery();
//        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localDashReceiver);
        unregisterReceiver(wiFiDirectBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter localFilter = new IntentFilter();
        localFilter.addAction(DataHandler.DEVICE_LIST_CHANGED);
        localFilter.addAction(FIRST_DEVICE_CONNECTED);
        localFilter.addAction(DataHandler.SEARCH_REQUEST_RECEIVED);
        localFilter.addAction(DataHandler.FILE_REQUEST_RECEIVED);
        localFilter.addAction(DataHandler.SEARCH_RESPONSE_RECEIVED);
        localFilter.addAction(SELECTED_DEVICE_CONFIRMED);
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(localDashReceiver,
                localFilter);

        IntentFilter wifip2pFilter = new IntentFilter();
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifip2pFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wiFiDirectBroadcastReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager,
                wifip2pChannel, this);
        registerReceiver(wiFiDirectBroadcastReceiver, wifip2pFilter);

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DataHandler.DEVICE_LIST_CHANGED));
    }

    @Override
    protected void onDestroy() {
//        mNsdHelper.tearDown();
//        connListener.tearDown();
        appController.stopConnectionListener();
        Utility.clearPreferences(MainActivity.this);
        Utility.deletePersistentGroups(wifiP2pManager, wifip2pChannel);
        DeviceManager.getInstance().clearDatabase();
        wifiP2pManager.removeGroup(wifip2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        super.onDestroy();
    }

    public void gotoSearchResultActivity(List<String> filePaths, DeviceDTO selectedDevice) {

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.KEY_FILE_PATHS, (Serializable) filePaths);
        intent.putExtra(SearchResultActivity.KEY_SELECTED_DEVICE_TO_SEARCH_RESULT, selectedDevice);
        startActivity(intent);

    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        if (discoverProgressDialog != null && discoverProgressDialog.isShowing()) {
            discoverProgressDialog.dismiss();
        }

        List<WifiP2pDevice> devices = (new ArrayList<>());
        devices.addAll(peerList.getDeviceList());
        for (WifiP2pDevice device : devices) {
            DeviceManager.getInstance().addDevice(device);
        }

        settingsFragment.refreshDeviceList();
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
        Log.d("eeee", "onConnectionInfoAvailable");
        if (wifiP2pInfo.groupFormed && !wifiP2pInfo.isGroupOwner && !isConnectionInfoSent) {


            appController.restartConnectionListenerWith(ConnectionUtils.getPort(MainActivity.this));

            String groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();

            DataSender.sendCurrentDeviceDataWD(MainActivity.this, groupOwnerAddress, TransferConstants.INITIAL_DEFAULT_PORT);
            isConnectionInfoSent = true;
        }
    }

    public void findPeers() {
        Log.d("eeee", "findPeers, isWDConnected: " + isWDConnected);
        if (!isWDConnected) {

            if (discoverProgressDialog != null && discoverProgressDialog.isShowing()) {
                discoverProgressDialog.dismiss();
            }
            discoverProgressDialog = new ProgressDialog(this);
            discoverProgressDialog.setMessage("discovering peers");
            discoverProgressDialog.setCancelable(true);
            discoverProgressDialog.show();

            wifiP2pManager.discoverPeers(wifip2pChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    NotificationToast.showToast(MainActivity.this, "Peer discovery started");
                }

                @Override
                public void onFailure(int reasonCode) {
                    NotificationToast.showToast(MainActivity.this, "Peer discovery failure: " + reasonCode);
                }
            });
        }
    }


    public void onDeviceSelect(DeviceDTO deviceDTO) {
        if (!isWDConnected) {

            if (connectingDialog != null && connectingDialog.isShowing()) {
                connectingDialog.dismiss();
            }
            connectingDialog = new ProgressDialog(this);
            connectingDialog.setMessage("connecting to " + deviceDTO.getDeviceName());
            connectingDialog.setCancelable(true);
            connectingDialog.show();

            DeviceManager.getInstance().getMyDevice().setStatus(DEVICE_CONNECTING);
            deviceDTO.setStatus(DEVICE_CONNECTING);

            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = deviceDTO.getDeviceAddress();
            config.wps.setup = WpsInfo.PBC;
            config.groupOwnerIntent = 4;
            wifiP2pManager.connect(wifip2pChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // Connection request succeeded. No code needed here
                }

                @Override
                public void onFailure(int reasonCode) {
                    NotificationToast.showToast(MainActivity.this, "Connection failed. try" + " again: reason: " + reasonCode);
                }
            });
        }
    }

    public void onDeviceDisconnect(final DeviceDTO deviceDTO) {

        wifiP2pManager.removeGroup(wifip2pChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                deviceDTO.setStatus(DEVICE_DISCONNECTED);
                DeviceManager.getInstance().getMyDevice().setStatus(DEVICE_DISCONNECTED);
                DeviceManager.getInstance().addOrUpdateDevice(deviceDTO);
                isWDConnected = false;
                selectedDevice = null;
                DeviceManager.getInstance().clearDatabase();
                settingsFragment.refreshDeviceList();
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    public void onSearch(String query) {

        if (selectedDevice == null) {
            NotificationToast.showToast(this, "not ready to search yet....");
            return;
        }

        DataSender.sendSearchRequest(
                this,
                selectedDevice.getIp(),
                selectedDevice.getPort(),
                new SearchRequestDTO(
                        query,
                        Utility.getString(this, "myip"),
                        ConnectionUtils.getPort(this)
                )
        );
    }

    public void setSelectedDevice(DeviceDTO selectedDevice) {
        this.selectedDevice = selectedDevice;
    }

    public void setDisconnected() {
        isWDConnected = false;
        selectedDevice = null;
        DeviceManager.getInstance().getMyDevice().setStatus(DEVICE_DISCONNECTED);
    }
}
