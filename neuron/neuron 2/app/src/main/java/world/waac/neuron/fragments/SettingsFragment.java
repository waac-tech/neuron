package world.waac.neuron.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.waac.neuron.R;
import world.waac.neuron.activities.MainActivity;
import world.waac.neuron.adapters.DevicesRVAdapter;
import world.waac.neuron.models.DeviceDTO;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SettingsFragment extends Fragment {



    @BindView(R.id.rv_devices)
    RecyclerView rvDevices;

    DevicesRVAdapter adapter;

    private MainActivity mainActivity;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public SettingsFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public static SettingsFragment newInstance(MainActivity mainActivity) {
        SettingsFragment fragment = new SettingsFragment(mainActivity);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);


        ButterKnife.bind(this, rootView);

        return rootView;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // do all initial stuff here.

        adapter = new DevicesRVAdapter(this);

        rvDevices.setAdapter(adapter);
        rvDevices.setLayoutManager(new LinearLayoutManager(this.getContext()));
        rvDevices.setItemAnimator(new DefaultItemAnimator());

    }

    public DevicesRVAdapter getAdapter() {
        return adapter;
    }


    @OnClick(R.id.btn_discover)
    public void onClickBtnDiscover() {
        mainActivity.findPeers();
    }

    public void onClickSettingsItemConnect(final DeviceDTO deviceDTO) {
        mainActivity.onDeviceSelect(deviceDTO);
    }

    public void onClickedSettingsItemDisconnect(DeviceDTO deviceDTO) {
        mainActivity.onDeviceDisconnect(deviceDTO);
    }

    public void refreshDeviceList() {
        adapter.notifyDataSetChanged();
    }
}
