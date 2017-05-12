package savitskiy.com.osmandapp.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import savitskiy.com.osmandapp.R;
import savitskiy.com.osmandapp.models.Map;
import savitskiy.com.osmandapp.support.AppConfig;
import savitskiy.com.osmandapp.support.MapParser;
import savitskiy.com.osmandapp.support.RecyclerAdapter;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.CustomClickListner{

    private StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
    private List<Map> maps;
    private ProgressBar progressBar;
    private TextView textViewFreeMemorySize;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_download_maps_name);

        progressBar = (ProgressBar) findViewById(R.id.progressBarMemory);
        textViewFreeMemorySize = (TextView) findViewById(R.id.textViewFreeMemorySize);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewContinents);

        double totalSizeInGig = ((double) stat.getTotalBytes()) / 1024 / 1024 / 1024;
        double freeSizeInGig = ((double) stat.getFreeBytes()) / 1024 / 1024 / 1024;

        progressBar.setMax((int) totalSizeInGig);
        progressBar.setProgress((int) freeSizeInGig);

        textViewFreeMemorySize.setText(String.format("%.2f", freeSizeInGig));

        MapParser mapParser = new MapParser();

        InputStream is = null;
        try {
            is = getAssets().open("countries.xml");
            if(maps==null) {
                maps = mapParser.getContinents(is);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        adapter = new RecyclerAdapter(maps, this);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


    }


    @Override
    public void toChildRegion(View view, int position) {
        Intent intent = new Intent(this, RegionActivity.class);
        intent.putExtra(AppConfig.MAP_KEY, maps.get(position));
        startActivity(intent);
    }

    @Override
    public void startItemDownload(int position) {

    }

    @Override
    public void stopItemDownload(int position) {

    }
}
