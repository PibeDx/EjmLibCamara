package com.jcuentas.camara;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jcuentas.camara.interactor.PhotoInteractor;
import com.jcuentas.camara.interactor.impl.PhotoInteractorImpl;
import com.jcuentas.camara.util.UtilPhoto;

import java.io.File;

/**
 * Created by Jose Cuentas Turpo on 26/06/2015 - 05:05 PM.
 * E-mail: jcuentast@gmail.com
 */
public class TestActivity extends Activity {
    public static final String TAG = "MainActivity";
    String PATH_IMG="";
    Button btnTomarFoto;
    PhotoInteractor photoInteractor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoInteractor = new PhotoInteractorImpl(this, this);
        btnTomarFoto= (Button)findViewById(R.id.btnTomarFoto);
        btnTomarFoto.setOnClickListener(new btnFotoOnClickListener());
    }

    private class btnFotoOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intentCamara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            startActivityForResult(intentCamara, 102);
        }
    }
    public Uri setImageUri() {
        String nombre= "Fotografia" + "_" + System.currentTimeMillis() + ".png";
        File file = new File(Environment.getExternalStorageDirectory() + UtilPhoto.PATH+"/", nombre);
        Uri imgUri = Uri.fromFile(file);
        PATH_IMG=file.getAbsolutePath();
        Log.d(TAG, "setImageUri - file.getAbsolutePath(): " + file.getAbsolutePath());
        return imgUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 102) {
                Log.d(TAG, "onActivityResult - requestCode == 102: "+PATH_IMG);
                photoInteractor.compressImage(PATH_IMG);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PATH_IMG",PATH_IMG);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PATH_IMG=savedInstanceState.getString("PATH_IMG");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
