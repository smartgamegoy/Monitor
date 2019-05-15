package com.jetec.Monitor.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.print.PrintManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import com.jetec.Monitor.R;
import com.jetec.Monitor.SupportFunction.PDF.MyPrintPdfAdapter;

import java.io.File;
import java.util.Objects;

public class PDFView extends AppCompatActivity {

    private Vibrator vibrator;
    private File file;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdfreadview);
        vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        com.github.barteksc.pdfviewer.PDFView pdfView = findViewById(R.id.pdfView);

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        // SD卡位置getApplicationContext().getFilesDir().getAbsolutePath();
        // 系統位置android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "JetecBLE" + ".pdf";
        filePath = baseDir + File.separator + fileName;
        file = new File(filePath);

        pdfView.fromFile(file)
                .enableAnnotationRendering(true)
                .load();

    }

    private void PDFshare(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("text/*");
        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
    }

    private void doPdfPrint(String filePath) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        MyPrintPdfAdapter myPrintAdapter = new MyPrintPdfAdapter(filePath);
        assert printManager != null;
        printManager.print("JetecRemote", myPrintAdapter, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pdfmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //toolbar menu item
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sharepdf) {
            vibrator.vibrate(100);
            PDFshare();
            return true;
        }
        if (id == R.id.action_settings) {
            vibrator.vibrate(100);
            doPdfPrint(filePath);
            return true;
        }
        return true;
    }

    public boolean onKeyDown(int key, KeyEvent event) {
        switch (key) {
            case KeyEvent.KEYCODE_SEARCH:
                break;
            case KeyEvent.KEYCODE_BACK: {
                vibrator.vibrate(100);
                Intent intent = new Intent(PDFView.this, RecordActivity.class);
                startActivity(intent);
                finish();
            }
            break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                break;
            default:
                return false;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // land do nothing is ok
            //show_device();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // port do nothing is ok
            //show_device();
        }
    }
}
