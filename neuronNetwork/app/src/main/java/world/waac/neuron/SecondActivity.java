package world.waac.neuron;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    Button btnOnOff, btnDiscover,btnSend;
    ListView   listView;
    TextView read_msg_box, connectiomStatus;
    EditText writeMsg;

    WifiManager wifiManager;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;

    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    //for getting the list of available decice
    List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        
        initialWork();
        
        exqListener();


    }

    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("on");
                } else {
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("off");
                }
            }
        });

        // Discovery method
        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        connectiomStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        connectiomStatus.setText("Discovery Failed");
                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override //check the long it was 1 and I change it to l to fix error tempprary
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final WifiP2pDevice device = deviceArray[i];
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;

                mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Connected to"+device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(getApplicationContext(),"Not Connected",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void initialWork() {


        //initial Button
        btnOnOff = (Button) findViewById(R.id.onOff);
        btnDiscover = (Button) findViewById(R.id.discover);
        btnSend = (Button) findViewById(R.id.sendButton);

        //initial list of peer
        listView = (ListView) findViewById(R.id.peerListView);

        //initial TextView
        read_msg_box = (TextView) findViewById(R.id.readMsg);
        connectiomStatus = (TextView) findViewById(R.id.connectionStatus);

        //initial EditText
        writeMsg = (EditText) findViewById(R.id.writeMsg);

        //wifi managing
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        //make object from WifiDirectBroudcastReceiver
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {

                if (!peerList.getDeviceList().equals(peers)){
                        peers.clear();
                        peers.addAll(peerList.getDeviceList());

                        deviceNameArray = new String[peerList.getDeviceList().size()];
                        deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];

                        int index = 0;

                        for (WifiP2pDevice device : peerList.getDeviceList()){

                            deviceNameArray[index] = device.deviceName;
                            deviceArray[index] = device;
                            index++;

                        }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, deviceNameArray);
                    listView.setAdapter(adapter);

                }

                if(peers.size() == 0){

                    Toast.makeText(getApplicationContext(), "No Peer Finded", Toast.LENGTH_SHORT).show();
                    return;

                }

            }
        };


        WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;

                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){

                    connectiomStatus.setText("HOST");

                } else if (wifiP2pInfo.groupFormed){

                    connectiomStatus.setText("CLIENT");

                }
            }
        };


        @Override
        protected void onResume(){
            super.onResume();
            registerReceiver(mReceiver, mIntentFilter);
        }

        @Override
        protected void onPause(){
            super.onPause();
            unregisterReceiver(mReceiver);
        }

        // inner class for socket codes
        public class ServerClass extends Thread {

            Socket socket;
            ServerSocket serverSocket;

            @Override
            public void run(){
                try {
                    serverSocket = new ServerSocket(88888);
                    socket = serverSocket.accept();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

        }

        public class ClientClass extends Thread {

            Socket socket;
            String hostAdd;

            public ClientClass(InetAddress hostAddress){

                hostAdd = hostAddress.getHostAddress();
                socket = new Socket();

            }

            @Override
            public void run(){
                try {
                    socket.connect(new InetSocketAddress(hostAdd, 8888),500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
