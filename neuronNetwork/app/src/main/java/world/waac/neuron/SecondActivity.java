package world.waac.neuron;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        
        initialWork();
        
        exqListener();


    }

    private void exqListener() {
        btnOnOff.setOnClickListener(new View.OnClickListener() {
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

    }
}
