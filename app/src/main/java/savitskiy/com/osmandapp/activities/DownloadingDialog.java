package savitskiy.com.osmandapp.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import savitskiy.com.osmandapp.R;
import savitskiy.com.osmandapp.models.Map;
import savitskiy.com.osmandapp.support.AppConfig;
import savitskiy.com.osmandapp.support.MapParser;
import savitskiy.com.osmandapp.support.RecyclerAdapter;

/**
 * Created by Andrey on 12.05.2017.
 */

public class DownloadingDialog extends DialogFragment implements RegionActivity.CustomProgressListener {
    private TextView textViewRegion, textViewDownloaded, textViewFileSize, textViewClose;
    private ProgressBar progressBarDownloading;
    private ImageView imageViewCancel;


    private RecyclerAdapter.CustomClickListner customClickListner;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private Map map;


    public DownloadingDialog() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.activity_dialog, null);
        textViewRegion = (TextView) v.findViewById(R.id.textViewRegion);
        textViewDownloaded = (TextView) v.findViewById(R.id.textViewDownloaded);
        textViewFileSize = (TextView) v.findViewById(R.id.textViewFileSize);
        textViewClose = (TextView) v.findViewById(R.id.textViewClose);
        imageViewCancel = (ImageView) v.findViewById(R.id.imageViewCancel);
        progressBarDownloading = (ProgressBar) v.findViewById(R.id.progressBarDownloadedSize);

        Bundle bundle = this.getArguments();
        this.map = (Map) bundle.getSerializable(AppConfig.MAP_KEY);
        setMax(map.getMaxSize() / 1024 / 1024);
        setProgress(0);
        textViewRegion.setText(MapParser.up(map.getName()));

        imageViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customClickListner.stopItemDownload(map.getDownloadPosition());
                alertDialog.dismiss();
            }
        });

        textViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setCancelable(true);
        alertDialog = builder.create();
        return alertDialog;
    }


    @Override
    public void setMax(int max) {
        progressBarDownloading.setMax(max);
        textViewFileSize.setText(String.valueOf(max));
    }

    @Override
    public void setProgress(int progress) {
        progressBarDownloading.setProgress(progress);
        textViewDownloaded.setText(String.valueOf(progress));
        if (progress == map.getMaxSize() / 1024 / 1024) {
            alertDialog.dismiss();
        }
    }


    public void setCustomClickListner(RecyclerAdapter.CustomClickListner customClickListner) {
        this.customClickListner = customClickListner;
    }
}
