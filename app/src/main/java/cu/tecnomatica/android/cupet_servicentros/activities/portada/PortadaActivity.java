package cu.tecnomatica.android.cupet_servicentros.activities.portada;

import android.Manifest;
import android.annotation.SuppressLint;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.greenrobot.greendao.database.Database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import cu.tecnomatica.android.cupet_servicentros.R;
import cu.tecnomatica.android.cupet_servicentros.activities.mapa.MapaActivity;
import cu.tecnomatica.android.cupet_servicentros.database.Combustible;
import cu.tecnomatica.android.cupet_servicentros.database.DaoMaster;
import cu.tecnomatica.android.cupet_servicentros.database.DaoSession;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PortadaActivity extends AppCompatActivity
{
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    //private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            /*mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        }
    };
    //private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private final int duracion = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_portada);
        AssetManager assetManager = getAssets();

        try
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
            }
            else
            {
                File dir = new File(Environment.getExternalStorageDirectory() + "/CUPET/");

                if (!dir.exists())
                {
                    dir.mkdir();
                }

                String filemapname = "cuba.map";
                String filedbname = "servi.db";

                File destinationfile = new File(Environment.getExternalStorageDirectory() + "/CUPET/" + filemapname);
                File destinationdbfile = new File(Environment.getExternalStorageDirectory() + "/CUPET/" + filedbname);


                if (!destinationfile.exists())
                {
                    InputStream inputStream = assetManager.open("cuba.map");
                    FileOutputStream fileOutputStream = new FileOutputStream(destinationfile);
                    byte[] buffer = new byte[1024];

                    int len1 = 0;

                    while ((len1 = inputStream.read(buffer)) > 0)
                    {
                        fileOutputStream.write(buffer, 0, len1);
                    }
                    fileOutputStream.close();
                }
                if (!destinationdbfile.exists())
                {
                    InputStream inputStreamdb = assetManager.open("servi.db");
                    FileOutputStream fileOutputStreamdb = new FileOutputStream(destinationdbfile);
                    byte[] bufferdb = new byte[1024];

                    int len1 = 0;

                    while ((len1 = inputStreamdb.read(bufferdb)) > 0)
                    {
                        fileOutputStreamdb.write(bufferdb, 0, len1);
                    }
                    fileOutputStreamdb.close();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        Intent intent = new Intent(PortadaActivity.this, MapaActivity.class);
                        startActivity(intent);
                        finish();
                    };
                }, duracion);
            }
        }
        catch (Exception exc)
        {
            Log.e("Error de Copia", exc.getMessage());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        //mControlsView.setVisibility(View.GONE);
        //mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        //mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        //mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        AssetManager assetManager = getAssets();

        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    try
                    {
                        File dir = new File(Environment.getExternalStorageDirectory() + "/CUPET/");

                        if (!dir.exists())
                        {
                            dir.mkdir();
                        }

                        String filemapname = "cuba.map";
                        String filedbname = "servi.db";

                        File destinationfile = new File(Environment.getExternalStorageDirectory() + "/CUPET/" + filemapname);
                        File destinationdbfile = new File(Environment.getExternalStorageDirectory() + "/CUPET/" + filedbname);


                        if (!destinationfile.exists())
                        {
                            InputStream inputStream = assetManager.open("cuba.map");
                            FileOutputStream fileOutputStream = new FileOutputStream(destinationfile);
                            byte[] buffer = new byte[1024];

                            int len1 = 0;

                            while ((len1 = inputStream.read(buffer)) > 0)
                            {
                                fileOutputStream.write(buffer, 0, len1);
                            }
                            fileOutputStream.close();
                        }
                        if (!destinationdbfile.exists())
                        {
                            InputStream inputStreamdb = assetManager.open("servi.db");
                            FileOutputStream fileOutputStreamdb = new FileOutputStream(destinationdbfile);
                            byte[] bufferdb = new byte[1024];

                            int len1 = 0;

                            while ((len1 = inputStreamdb.read(bufferdb)) > 0)
                            {
                                fileOutputStreamdb.write(bufferdb, 0, len1);
                            }
                            fileOutputStreamdb.close();
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run()
                            {
                                Intent intent = new Intent(PortadaActivity.this, MapaActivity.class);
                                startActivity(intent);
                                finish();
                            };
                        }, duracion);
                    }
                    catch (Exception e)
                    {
                    }
                }
                else
                {
                    finish();
                }
            }
            return;
        }
    }
}
