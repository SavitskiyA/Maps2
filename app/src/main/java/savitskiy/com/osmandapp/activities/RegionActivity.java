package savitskiy.com.osmandapp.activities;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import savitskiy.com.osmandapp.R;

import savitskiy.com.osmandapp.support.PermissionHelper;
import savitskiy.com.osmandapp.support.RecyclerAdapter;
import savitskiy.com.osmandapp.support.AppConfig;
import savitskiy.com.osmandapp.support.MapParser;
import savitskiy.com.osmandapp.models.Map;


public class RegionActivity extends AppCompatActivity implements RecyclerAdapter.CustomClickListner {
    private static final int REQUEST_PERMISSION_STORAGE = 1;
    private static int permissionSavePos;
    private List<Map> maps;
    private Queue<Map> mapsQueue = new ArrayDeque<>();


    private ProgressBar progressBarDownloading;
    private TextView textViewDownloading, textViewDownloadedSize;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayout linearLayout;

    private DownloadingDialog downloadingDialog;

    private final static String URL = "http://download.osmand.net/download.php?standard=yes&file=";
    private static int curDownloadPos;
    private static boolean continueDownloading = false;
    private static boolean isRunning = false;

    private Handler handler;

    private CustomProgressListener customProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_region);
        Intent intent = getIntent();

        Map map = (Map) intent.getSerializableExtra(AppConfig.MAP_KEY);
        maps = map.getChildRegions();

        setTitle(MapParser.up(map.getName()));

        progressBarDownloading = (ProgressBar) findViewById(R.id.progressBarDownloading);
        textViewDownloading = (TextView) findViewById(R.id.textViewDownloadingRegion);
        textViewDownloadedSize = (TextView) findViewById(R.id.textViewDownloadedSize);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        adapter = new RecyclerAdapter(maps, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        handler = new Handler(Looper.getMainLooper());


        progressBarDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadingDialog = new DownloadingDialog();
                Bundle bundle = new Bundle();
                bundle.putSerializable(AppConfig.MAP_KEY, maps.get(curDownloadPos));
                downloadingDialog.setArguments(bundle);
                downloadingDialog.show(getSupportFragmentManager(), "TAG");
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mapsQueue.size() != 0) {
                showDialog(getResources().getString(R.string.alertDialogTitle), getResources().getString(R.string.alertDialogMsg));
            } else {
                onBackPressed();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showDialog(String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(RegionActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        stopItemDownload(curDownloadPos);
                        mapsQueue.clear();
                        dialog.dismiss();
                        onBackPressed();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void startDownload(int position){
        maps.get(position).setDownloadPosition(position);
        maps.get(position).setMaxSize(0);
        adapter.notifyDataSetChanged();
        linearLayout.setVisibility(View.VISIBLE);
        mapsQueue.add(maps.get(position));
        if (!isRunning) {
            DownloadRunnable downloadRunnable = new DownloadRunnable();
            Thread downloadThread = new Thread(downloadRunnable);
            downloadThread.start();
        }
    }


    @Override
    public void toChildRegion(View view, int position) {
        Intent intent = new Intent(this, RegionActivity.class);
        intent.putExtra(AppConfig.MAP_KEY, maps.get(position));
        startActivity(intent);
    }

    @Override
    public void startItemDownload(int position) {
        permissionSavePos = position;
        PermissionHelper.requestPermissions(this,
                REQUEST_PERMISSION_STORAGE,
                new PermissionHelper.PermissionsChecker() {
                    @Override
                    public void allPermissionsGranted() {
                        startDownload(permissionSavePos);
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }

    @Override
    public void stopItemDownload(int position) {
        if (position == curDownloadPos) {
            continueDownloading = false;
        } else {
            mapsQueue.remove(maps.get(position));
        }
        maps.get(position).setMaxSize(-1);
        maps.get(position).setProgressSize(-1);
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allPermissionsGranted = PermissionHelper.allPermissionsGranted(grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (allPermissionsGranted) {
                startDownload(permissionSavePos);
            } else {
                Toast.makeText(this, "Please provide us a permission to write external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadRunnable implements Runnable {

        @Override
        public void run() {
            isRunning = true;
            while (mapsQueue.size() != 0) {
                startDownloadMap(mapsQueue.peek());
            }
            isRunning = false;
        }


        private void startDownloadMap(final Map map) {
            curDownloadPos = map.getDownloadPosition();
            continueDownloading = true;
            textViewDownloading.post(new Runnable() {
                @Override
                public void run() {
                    textViewDownloading.setText(MapParser.up(map.getName()));
                }
            });

            String fileName = map.getFileName();
            String stringURL = URL + fileName;
            HttpURLConnection connection = null;
            InputStream inputstream = null;
            OutputStream outputStream = null;
            URL url;
            try {
                url = new URL(stringURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    toast(handler, "Connection error: " + connection.getResponseCode());
                }

                final int fileLength = connection.getContentLength();


                progressBarDownloading.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBarDownloading.setMax(fileLength);
                        map.setMaxSize(fileLength);
                        adapter.notifyDataSetChanged();
                    }
                });


                inputstream = connection.getInputStream();

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                if (!file.exists()) {
                    file.mkdirs();
                }

                File file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/" + fileName);
                outputStream = new FileOutputStream(file1);

                byte data[] = new byte[1024];
                long total = 0;
                long nTotalPercents = 0;
                int count;
                while ((count = inputstream.read(data)) != -1) {
                    if (continueDownloading) {
                        total += count;
                        final long mTotal = total;
                        final int totalPercents = (int) ((total * 100) / fileLength);


                        outputStream.write(data, 0, count);

                        if (totalPercents > nTotalPercents) {
                            nTotalPercents = totalPercents;
                            progressBarDownloading.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBarDownloading.setProgress((int) mTotal);
                                    map.setProgressSize((int) mTotal);
                                    adapter.notifyDataSetChanged();
                                    textViewDownloadedSize.setText(String.valueOf(totalPercents));
                                    if (customProgressListener != null)
                                        customProgressListener.setProgress((int) mTotal / 1024 / 1024);
                                }
                            });


                        }
                    } else {
                        toast(handler, "Downloading of " + MapParser.up(map.getName()) + " has been aborted");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                progressBarDownloading.setProgress(0);
                                textViewDownloadedSize.setText("0");
                            }
                        });
                        return;
                    }
                }
                toast(handler, MapParser.up(map.getName()) + " has been successfully downloaded");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.setMaxSize(-2);
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
                toast(handler, e.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.setMaxSize(-1);
                        adapter.notifyDataSetChanged();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                toast(handler, e.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.setMaxSize(-1);
                        adapter.notifyDataSetChanged();
                    }
                });
            } catch (Exception e) {
                toast(handler, e.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        map.setMaxSize(-1);
                        adapter.notifyDataSetChanged();
                    }
                });
            } finally {
                try {
                    if (outputStream != null)
                    outputStream.close();
                    if (inputstream != null)
                        inputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    toast(handler, e.toString());
                }

                if (connection != null)
                    connection.disconnect();

                mapsQueue.remove(map);
                if (mapsQueue.size() == 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            linearLayout.setVisibility(View.GONE);
                        }
                    });
                }
            }

        }
    }


    private void toast(Handler handler, final String msg) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegionActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof DownloadingDialog) {
            customProgressListener = (CustomProgressListener) fragment;
            ((DownloadingDialog) fragment).setCustomClickListner(this);
        } else {
            Toast.makeText(this, fragment.toString()
                    + " must implement CustomProgressListener", Toast.LENGTH_SHORT).show();
            throw new ClassCastException(fragment.toString()
                    + " must implement CustomProgressListener");
        }
        super.onAttachFragment(fragment);
    }

    public interface CustomProgressListener {
        public void setMax(int max);
        public void setProgress(int progress);
    }
}
