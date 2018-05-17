package world.waac.neuron.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.jo.neuron.R;
import world.waac.neuron.activities.SearchResultActivity;
import world.waac.neuron.globals.Utility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchResultRVAdapter extends RecyclerView.Adapter<SearchResultRVAdapter.ViewHolder> {

    SearchResultActivity activity;
    List<String> listData = null;

    public SearchResultRVAdapter(SearchResultActivity activity, List<String> listData) {
        this.activity = activity;
        this.listData = listData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_search_result, parent, false);

        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String item = this.listData.get(position);
        holder.updateView(item);

        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_file_path)
        TextView textFilePath;

        @BindView(R.id.img_type_image)
        ImageView imgViewTypeImage;

        @BindView(R.id.img_type_text)
        ImageView imgViewTypeText;

        @BindView(R.id.img_type_pdf)
        ImageView imgViewTypePdf;

        @BindView(R.id.img_type_other)
        ImageView imgViewTypeOther;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void updateView(String item) {
            textFilePath.setText(item);

            imgViewTypeImage.setVisibility(View.GONE);
            imgViewTypePdf.setVisibility(View.GONE);
            imgViewTypeText.setVisibility(View.GONE);
            imgViewTypeOther.setVisibility(View.GONE);

            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(Utility.getFileExtFromPath(item));

            if (mimeType != null) {
                if (mimeType.toLowerCase().contains("image")) {
                    imgViewTypeImage.setVisibility(View.VISIBLE);
                } else if (mimeType.toLowerCase().contains("pdf")) {
                    imgViewTypePdf.setVisibility(View.VISIBLE);
                } else if (mimeType.toLowerCase().contains("text")) {
                    imgViewTypeText.setVisibility(View.VISIBLE);
                } else {
                    imgViewTypeOther.setVisibility(View.VISIBLE);
                }
            } else {
                imgViewTypeOther.setVisibility(View.VISIBLE);
            }

        }

        @OnClick(R.id.btn_get_file)
        public void onClickBtnGetFile() {
            activity.onClickGetFile(SearchResultRVAdapter.this.listData.get(this.getLayoutPosition()));
        }

    }
}
