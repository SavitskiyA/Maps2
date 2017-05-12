package savitskiy.com.osmandapp.support;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import savitskiy.com.osmandapp.R;
import savitskiy.com.osmandapp.support.AppConfig;
import savitskiy.com.osmandapp.models.Continent;
import savitskiy.com.osmandapp.models.Map;


/**
 * Created by Andrey on 07.05.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private List<Map> maps;
    private Context context;


    private CustomClickListner customClickListner;


    public static final Comparator<Map> sortByName = new Comparator<Map>() {
        @Override
        public int compare(Map o1, Map o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }

    };


    public RecyclerAdapter(List<Map> maps, Context context) {
        Collections.sort(maps, sortByName);
        this.maps = maps;
        this.customClickListner = (CustomClickListner) context;
        this.context = context;

    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.continent_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        Map map = maps.get(position);
        holder.textView.setText(MapParser.up(map.getName()));
        configureItemView(holder, maps.get(position));

    }

    private void configureItemView(RecyclerViewHolder holder, Map map) {
        holder.progressBar.setMax(map.getMaxSize());
        holder.progressBar.setProgress(map.getProgressSize());

        float scale = context.getResources().getDisplayMetrics().density;
        int dpAsPixels = (int) (6 * scale + 0.5f);



        if (map.getClass() == Continent.class) {
            holder.imageViewLeft.setBackgroundResource(R.drawable.ic_world_globe_dark);
        } else {
            if (map.getMaxSize() == -1) {
                holder.progressBar.setVisibility(View.GONE);
                holder.imageViewLeft.setBackgroundResource(R.drawable.ic_map);
                holder.textView.setPadding(0, 0, 0, 0);

            } else if (map.getMaxSize() == -2) {
                holder.progressBar.setVisibility(View.GONE);
                holder.imageViewLeft.setBackgroundResource(R.drawable.ic_map_g);
                holder.imageViewRight.setVisibility(View.GONE);
                holder.textView.setPadding(0, 0, 0, 0);

            } else {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.imageViewLeft.setBackgroundResource(R.drawable.ic_map);
                holder.textView.setPadding(0, 0, 0, dpAsPixels);
            }
        }


        if (map.getFileName() != null) {
            holder.imageViewRight.setVisibility(View.VISIBLE);
            if (map.getMaxSize() >= 0) {
                holder.imageViewRight.setBackgroundResource(R.drawable.ic_action_remove_dark);
            } else
                holder.imageViewRight.setBackgroundResource(R.drawable.ic_action_import);
        } else {
            holder.imageViewRight.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public interface CustomClickListner {
        public void toChildRegion(View view, int position);

        public void startItemDownload(int position);

        public void stopItemDownload(int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageViewLeft, imageViewRight;
        private TextView textView;
        private ProgressBar progressBar;
        private RelativeLayout relativeLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            imageViewLeft = (ImageView) itemView.findViewById(R.id.imageViewLeft);
            imageViewRight = (ImageView) itemView.findViewById(R.id.imageViewRight);
            textView = (TextView) itemView.findViewById(R.id.textView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
            relativeLayout.setOnClickListener(this);
            imageViewRight.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.relativeLayout:
                    if (maps.get(getPosition()).getChildRegions().size() != 0) {
                        customClickListner.toChildRegion(v, getPosition());
                    } else {
                        Toast.makeText(v.getContext(), "There are no regions", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.imageViewRight:
                    if (maps.get(getPosition()).getMaxSize() >= 0) {
                        customClickListner.stopItemDownload(getPosition());
                    } else {
                        customClickListner.startItemDownload(getPosition());
                    }
                    break;
            }

        }
    }
}
