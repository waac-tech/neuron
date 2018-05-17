package world.waac.neuron.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.jo.neuron.R;
import world.waac.neuron.adapters.SearchResultRVAdapter;
import world.waac.neuron.globals.GlobalConstants;
import world.waac.neuron.models.DeviceDTO;
import world.waac.neuron.models.FileRequestDTO;
import world.waac.neuron.transfer.DataSender;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultActivity extends Activity {

    @BindView(R.id.rv_search_result)
    RecyclerView rvSearchResult;

    SearchResultRVAdapter adapter;
    DeviceDTO selectedDevice;

    List<String> filePaths;

    public static final String KEY_FILE_PATHS = "file_paths";
    public static final String KEY_SELECTED_DEVICE_TO_SEARCH_RESULT = "selected_device_to_search_result_activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        ButterKnife.bind(this);

        Intent intent = this.getIntent();

        this.filePaths = (List<String>) intent.getSerializableExtra(KEY_FILE_PATHS);
        this.selectedDevice = (DeviceDTO) intent.getSerializableExtra(KEY_SELECTED_DEVICE_TO_SEARCH_RESULT);

        adapter = new SearchResultRVAdapter(this, filePaths);

        for (String filePath : filePaths) {
            Log.d("eeee", filePath);
        }

        rvSearchResult.setAdapter(adapter);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResult.setItemAnimator(new DefaultItemAnimator());

    }

    public void onClickGetFile(String filePath) {

        if (GlobalConstants.copyingDialog != null && GlobalConstants.copyingDialog.isShowing()) {
            GlobalConstants.copyingDialog.dismiss();
        }
        GlobalConstants.copyingDialog = new ProgressDialog(this);
        GlobalConstants.copyingDialog.setMessage("copying");
        GlobalConstants.copyingDialog.setCancelable(true);
        GlobalConstants.copyingDialog.show();


        GlobalConstants.requestedFilePath = filePath;

        DataSender.sendFileRequest(this, selectedDevice.getIp(),
                selectedDevice.getPort(),
                new FileRequestDTO(filePath));
    }
}
