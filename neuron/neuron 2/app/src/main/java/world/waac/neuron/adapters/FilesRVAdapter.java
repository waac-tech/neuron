package world.waac.neuron.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.waac.neuron.R;
import world.waac.neuron.globals.Utility;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilesRVAdapter extends RecyclerView.Adapter<FilesRVAdapter.ViewHolder> {


    Context context;
    List<File> listData = null;

    public FilesRVAdapter(Context context, List<File> listData) {
        this.context = context;
        this.listData = listData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rv_files, parent, false);

        return new ViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File file = this.listData.get(position);
        holder.updateView(file);

        holder.itemView.setTag(file);
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

        public void updateView(File file) {
            textFilePath.setText(file.getAbsolutePath());

            imgViewTypeImage.setVisibility(View.GONE);
            imgViewTypePdf.setVisibility(View.GONE);
            imgViewTypeText.setVisibility(View.GONE);
            imgViewTypeOther.setVisibility(View.GONE);

            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(Utility.getFileExtFromPath(file.getAbsolutePath()));

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
    }
}
