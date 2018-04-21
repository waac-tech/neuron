package world.waac.neuron;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
}
