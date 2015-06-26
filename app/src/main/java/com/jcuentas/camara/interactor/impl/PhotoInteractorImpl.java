package com.jcuentas.camara.interactor.impl;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.jcuentas.camara.R;
import com.jcuentas.camara.interactor.PhotoInteractor;
import com.jcuentas.camara.util.ImageLoadingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jose Cuentas Turpo on 26/06/2015 - 02:53 PM.
 * E-mail: jcuentast@gmail.com
 */
public class PhotoInteractorImpl implements PhotoInteractor {

    public static final String TAG = "PhotoInteractorImpl";
    public static final String PATH = "/FOTO_SATELITE_GPS";
    //TODO: inicializar
    private Activity mActivity;


    ImageLoadingUtils utils;

    public PhotoInteractorImpl(Activity mActivity, Context c) {
        this.mActivity = mActivity;
        this.utils = new ImageLoadingUtils(c);
    }

    @Override
    public float calcularTamanoSepacion(int tamanoImagen, int numeroTextos) {
        float tamano = 0f;
        tamano = tamanoImagen / numeroTextos;
        if (tamano <= 15) {
            mensajeLog("calcularTamanoSepacion: el tamano de separacion es menor a 15");
            return 0f;
        } else {
            mensajeLog("calcularTamanoSepacion: el tamano de separacion es " + tamano);
            return tamano;
        }
    }

    @Override
    public void mensajeLog(String mensaje) {
        Log.d("PhotoInteractorImpl", mensaje);

    }

    @Override
    public String gerRealPathFromUri(String contentURI) {
        //Obtener Path
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mActivity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }




    @Override
    public Canvas insertPie(Bitmap bmpCuerpo,Bitmap bmpFooter, Canvas canvasBase) {
        canvasBase.drawBitmap(bmpFooter, 0, bmpCuerpo.getHeight() - bmpFooter.getHeight(), null);
        return canvasBase;
    }

    @Override
    public Canvas insertHead(Bitmap bmpHeader, Canvas canvasBase) {
        canvasBase.drawBitmap(bmpHeader, 0, 0, null);
        return canvasBase;
    }

    public Bitmap crearBitmapDeRecurso(int recurso){
        Bitmap bmpRecurso = BitmapFactory.decodeResource(mActivity.getResources(), recurso);
        return bmpRecurso;
    }

    @Override
    public Bitmap tramadoImagen(Bitmap bitmap) {
        //TODO: input
        String [] strArrayOptions = {"Longitud: -12.515456445", "Latitud: -74.12564545665", "Altitud: 152.121", "UBIGEO: LIMA/LIMA/LIMA", "DNI: 70893065"};
        //TODO: input
        int pBasico=64, pPrincipal=70; //Porcentaje

        Canvas canvas = new Canvas(bitmap);
        Bitmap bmpFooter = crearBitmapDeRecurso(R.drawable.img_footer_foto);
        int bmpFooterWidth = bmpFooter.getWidth();
        int bmpFooterHeight = bmpFooter.getHeight();
        Bitmap bmpHeader= crearBitmapDeRecurso(R.drawable.img_head_foto);
        int bmpHeaderWidth = bmpHeader.getWidth();
        int bmpHeaderHeight = bmpHeader.getHeight();


        int sizePadLeft = 25;
        int sizeInvalidateFooterHeigtht = 14;
        int sizeRealFooterHeight = bmpFooterHeight - sizeInvalidateFooterHeigtht;

        canvas = insertHead(bmpHeader, canvas);
        canvas = insertPie(bitmap, bmpFooter, canvas);

        int sizeArrayOptions = strArrayOptions.length;
        float tamanoSeparacionPorTexto = calcularTamanoSepacion(sizeRealFooterHeight, sizeArrayOptions);

        float sizeTextBasic, sizeTextPrincipal;
        sizeTextBasic = (tamanoSeparacionPorTexto*pBasico)/100;
        sizeTextPrincipal = (tamanoSeparacionPorTexto*pPrincipal)/100;
        Paint paintBasic=  paintConfiguracionOne(1,sizeTextBasic);
        Paint paintPrincipal=  paintConfiguracionOne(2,sizeTextPrincipal);
        float initFooterText = bitmap.getHeight() - sizeRealFooterHeight + (tamanoSeparacionPorTexto/2) + (sizeTextPrincipal/2);

        for (int i = 0; i < sizeArrayOptions; i++) {
            if (i!=sizeArrayOptions-1) {

                canvas.drawText(strArrayOptions[i], sizePadLeft, initFooterText, paintBasic);
                initFooterText += tamanoSeparacionPorTexto;
            }else{
                canvas.drawText(strArrayOptions[i], sizePadLeft, initFooterText, paintPrincipal);
                initFooterText += tamanoSeparacionPorTexto;
            }
        }
        return bitmap;
    }

    @Override
    public Paint[] paintConfiguracion(float sizeTextBasic, float sizeTextPrincipal) {
        //Configuracion de Paint
        //Basico
        Paint paintBasic = new Paint();
        paintBasic.setColor(Color.BLACK);
        //paintBasic.setTextSize(18);
        paintBasic.setTextSize(sizeTextBasic);
        paintBasic.setTextAlign(Paint.Align.LEFT);
        //Principal
        Paint paintPrincipal = new Paint();
        paintPrincipal.setColor(Color.BLACK);
        //paintPrincipal.setTextSize(20);
        paintPrincipal.setTextSize(sizeTextPrincipal);
        paintPrincipal.setTextAlign(Paint.Align.LEFT);
        paintPrincipal.setStyle(Paint.Style.FILL_AND_STROKE);
        paintPrincipal.setStrokeWidth(1f);
        return new Paint[]{paintPrincipal,paintBasic};
    }

    public Paint paintConfiguracionOne(int tipoText, float sizeText){
        Paint paint = new Paint();
        switch (tipoText) {
        	case 1: //Basic
                paint.setColor(Color.BLACK);
                paint.setTextSize(sizeText);
                paint.setTextAlign(Paint.Align.LEFT);
        		break;
            case 2: //Principal
                paint.setColor(Color.BLACK);
                paint.setTextSize(sizeText);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(1f);
                break;
        	default:
                mensajeLog("paintConfiguracionOne: tipoText ingresado incorrecto");
        		break;
        }
        return paint;
    }

    @Override
    public Bitmap modificarOrientacion(String filePath, Bitmap scaledBitmap) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    @Override
    public void crearCarpeta(String path) {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard.getAbsolutePath() + path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void compressImage(String imageURI){
        crearCarpeta(PATH);
        String filePath = gerRealPathFromUri(imageURI);

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
//        float maxHeight = 1280.0f;
//        float maxWidth = 720.0f;

        float maxHeight =720.0f ;
        float maxWidth = 1280.0f;

        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16*1024];
        try{
            bmp = BitmapFactory.decodeFile(filePath,options);
        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }
        try{
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        }
        catch(OutOfMemoryError exception){
            exception.printStackTrace();
        }
        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float)options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //Configuracion de Orientacion
        scaledBitmap = modificarOrientacion(filePath,scaledBitmap);
        //Tramado
        scaledBitmap = tramadoImagen(scaledBitmap);

        //Guardar imagen
        GuardarImage(filePath, scaledBitmap);
    }

    void GuardarImage(String pathRuta, Bitmap bmpImage) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pathRuta);
            bmpImage.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
