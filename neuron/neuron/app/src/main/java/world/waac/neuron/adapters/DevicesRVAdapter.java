package world.waac.neuron.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jo.neuron.R;
import world.waac.neuron.fragments.SettingsFragment;
import world.waac.neuron.globals.DeviceManager;
import world.waac.neuron.models.DeviceDTO;
import world.waac.neuron.transfer.TransferConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

            if (deviceDTO.getStatus() == TransferConstants.DEVICE_CONNECTED) {
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
