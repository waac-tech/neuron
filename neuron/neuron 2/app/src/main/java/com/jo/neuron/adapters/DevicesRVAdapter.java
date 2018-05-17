package com.jo.neuron.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jo.neuron.R;
import com.jo.neuron.fragments.SettingsFragment;
import com.jo.neuron.globals.DeviceManager;
import com.jo.neuron.models.DeviceDTO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jo.neuron.transfer.TransferConstants.DEVICE_CONNECTED;

public class DevicesRVAdapter extends RecyclerView.Adapter<DevicesRVAdapter.ViewHolder> {


    SettingsFragment settingsFragment;

    public DevicesRVAdapter(SettingsFragment settingsFragment) {
        this.settingsFragment = settingsFragment;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_devices, parent, false);

        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setViewData(DeviceManager.getInstance().getDeviceList().get(position));
    }

    @Override
    public int getItemCount() {
        return DeviceManager.getInstance().getDeviceList().size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_device_name)
        TextView textDeviceName;

        @BindView(R.id.ll_row)
        LinearLayout llRow;

        @BindView(R.id.btn_connect)
        Button btnConnect;

        @BindView(R.id.img_btn_cancel)
        ImageButton imgBtnCancel;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.btn_connect)
        public void onClickBtnConnect() {
            settingsFragment.onClickSettingsItemConnect(DeviceManager.getInstance().getDeviceList().get(this.getLayoutPosition()));
        }

        @OnClick(R.id.img_btn_cancel)
        public void onClickImgBtnCancel() {
            settingsFragment.onClickedSettingsItemDisconnect(DeviceManager.getInstance().getDeviceList().get(this.getLayoutPosition()));
        }

        public void setViewData(DeviceDTO deviceDTO) {
            textDeviceName.setText(deviceDTO.getDeviceName());

            if (deviceDTO.getStatus() == DEVICE_CONNECTED) {
                llRow.setBackgroundColor(settingsFragment.getContext().getResources().getColor(R.color.colorPrimary));
                btnConnect.setVisibility(View.GONE);
                imgBtnCancel.setVisibility(View.VISIBLE);
            } else {
                llRow.setBackgroundColor(settingsFragment.getContext().getResources().getColor(R.color.colorMain));
                btnConnect.setVisibility(View.VISIBLE);
                imgBtnCancel.setVisibility(View.GONE);
            }
        }
    }
}
