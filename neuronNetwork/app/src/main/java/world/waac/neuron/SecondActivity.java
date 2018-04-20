package world.waac.neuron;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    Button btnOnOff, btnDiscover,btnSend;
    ListView   listView;
    TextView read_msg_box, connectiomStatus;
    EditText writeMsg;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        
        initialWork();
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

    }
}
