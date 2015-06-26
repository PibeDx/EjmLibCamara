package com.jcuentas.camara.util;

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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;





/**
 * Created by Jose Cuentas Turpo on 23/06/2015 - 10:24 AM.
 * E-mail: jcuentast@gmail.com
 */
public class UtilPhoto {
    public static final String TAG = "UtilPhoto";
    public static final String PATH = "/FOTO_SATELITE_GPS";
    static ImageLoadingUtils utils;
    static Activity mActivity;
    public UtilPhoto(Context context,Activity activity) {
        this.utils  = new ImageLoadingUtils(context);
        mActivity = activity;
    }

    public String compressImage(String imageUri, double latitud, double longitud, double altitud) {

		File sdCard = Environment.getExternalStorageDirectory();
		File file = new File(sdCard.getAbsolutePath()+ PATH);
        //directory.mkdirs();
		//File file = new File(Environment.getExternalStorageDirectory().getPath(), "FOTO_SATELITE_GPS/");
        if (!file.exists()) {
            file.mkdirs();
        }



        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath,options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
//		float maxHeight = 816.0f;
//		float maxWidth = 612.0f;
//		float maxHeight = 1280.0f;
//		float maxWidth = 720.0f;

        float maxHeight = 720.0f;
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
        canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

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
			/*jcuentas*/
            // Valores
            Canvas canvasText = new Canvas(scaledBitmap);
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(18);
            paint.setTextAlign(Paint.Align.LEFT);
            Paint secondPaint = new Paint();
            secondPaint.setColor(Color.BLACK);
            secondPaint.setTextSize(20);
            secondPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            secondPaint.setStrokeWidth(1f);
            secondPaint.setTextAlign(Paint.Align.LEFT);
            String latituds = "Latitud: " + latitud;
            String longituds = "Longitud: " + longitud;
            String alturas = "Altura: "  + altitud;

//            String ipress = coIpress + " - " + noIpress;
//            Bitmap imgFooter = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.img_gooter_foto);
//            canvasText.drawBitmap(imgFooter, 0, scaledBitmap.getHeight()-imgFooter.getHeight(), null); // 0:0
//            canvasText.drawText(latituds, 25, scaledBitmap.getHeight() - 135, paint);
//            canvasText.drawText(longituds, 25, scaledBitmap.getHeight() - 110, paint);
//            canvasText.drawText(alturas, 25, scaledBitmap.getHeight() - 85, paint);
////			canvasText.drawText(user, 600, scaledBitmap.getHeight() - 130, secondPaint);
//            canvasText.drawText(ipress, 25, scaledBitmap.getHeight() - 60, paint);
//            canvasText.drawText(deUbigeo, 25, scaledBitmap.getHeight() - 35, paint);
//            canvasText.drawText(user, 25, scaledBitmap.getHeight() - 10, secondPaint);
//
//            //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_20_por);
//            Bitmap imgHead = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.img_head_foto);
//            canvasText.drawBitmap(imgHead, 0, 0, null); // 0:0
            //canvas.drawBitmap(scaledBitmap, 50, scaledBitmap.getHeight() - 30,paint );
            //canvasText.drawBitmap(largeIcon, scaledBitmap.getWidth() -largeIcon.getWidth() - 10 , 10, null); // Superior
            //canvasText.drawBitmap(largeIcon, scaledBitmap.getWidth() -largeIcon.getWidth() - 10 , scaledBitmap.getHeight() - 10, null); // Inferior

        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
//		String filename = getFilename();
        String filename = filePath;

        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }
    private static String getRealPathFromURI(String contentURI) {
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

    private static Bitmap tramadoSusalud(Bitmap bitmapcanvas){

        //Creacion de canvas

        Canvas canvas = new Canvas(bitmapcanvas);


//        String latituds = "Latitud: " + latitud;
//        String longituds = "Longitud: " + longitud;
//        String alturas = "Altura: "  + altitud;
//        String ipress = coIpress + " - " + noIpress;

        Bitmap imgFooter = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.img_footer_foto);
        canvas.drawBitmap(imgFooter, 0, bitmapcanvas.getHeight() - imgFooter.getHeight(), null);

        int SIZE_PLEFT = 25;
        int SIZE_INVALT_FOOTER = 14;

        int SIZE_FOOTER = imgFooter.getHeight()-SIZE_INVALT_FOOTER;
        String[] strArrayDescripcion = {"Longitud: -12.515456445", "Latitud: -74.12564545665", "Altitud: 152.121", "UBIGEO: LIMA/LIMA/LIMA", "DNI: 70893065"};
        float SIZE_INTERV = calcularTamanoSeparacion(SIZE_FOOTER, strArrayDescripcion.length);
        float sizeTextBc=(SIZE_INTERV*64)/100;
        float sizeTextPn=(SIZE_INTERV*70)/100;
        //float INIT_FOOTER = bitmapcanvas.getHeight() - imgFooter.getHeight()+SIZE_INVALT_FOOTER+/*18+*/(SIZE_INTERV/2)+9;
        float INIT_FOOTER = bitmapcanvas.getHeight() - imgFooter.getHeight()+SIZE_INVALT_FOOTER+/*18+*/(SIZE_INTERV/2)+(sizeTextPn/2);
        //Configuracion de Paint
        Paint paintPrincipal= paintConfiguracion(sizeTextBc,sizeTextPn)[0];
        Paint paintBasic=  paintConfiguracion(sizeTextBc,sizeTextPn)[1];
        for (int i = 0; i < strArrayDescripcion.length; i++) {
            if (i!=strArrayDescripcion.length-1) {
                canvas.drawText(strArrayDescripcion[i], SIZE_PLEFT, INIT_FOOTER, paintBasic);
                INIT_FOOTER += SIZE_INTERV;
            }else{
                canvas.drawText(strArrayDescripcion[i], SIZE_PLEFT, INIT_FOOTER, paintPrincipal);
                INIT_FOOTER += SIZE_INTERV;
            }
        }
//        canvas.drawText(latituds, 25, scaledBitmap.getHeight() - 135, paintBasic);
//        canvas.drawText(longituds, 25, scaledBitmap.getHeight() - 110, paintBasic);
//        canvas.drawText(alturas, 25, scaledBitmap.getHeight() - 85, paintBasic);
//        canvas.drawText(ipress, 25, scaledBitmap.getHeight() - 60, paintBasic);
//        canvas.drawText(deUbigeo, 25, scaledBitmap.getHeight() - 35, paintBasic);
//        canvas.drawText(user, 25, scaledBitmap.getHeight() - 10, paintPrincipal);

        Bitmap imgHead = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.img_head_foto);
        canvas.drawBitmap(imgHead, 0, 0, null); // 0:0
      return bitmapcanvas;
    }
    private static Paint[] paintConfiguracion(float sizeTextBasic, float sizeTextPrincipal){
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

    private static float calcularTamanoSeparacion(int tamanoImagen, int tamanoTextos){
        float tamano = 0f;
        tamano = tamanoImagen / tamanoTextos;
        if (tamano <= 15) {
            Log.e(TAG,"calcularTamano: el tamano de separacion es menor a 15");
            return 0f;
        } else {
            Log.e(TAG,"calcularTamano: el tamano de separacion es " + tamano);
            return tamano;
        }
    }

    private static Bitmap modificarOrientacion(String filePath, Bitmap scaledBitmap){
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

    public static void compressImage(String imageUri) {
        //Crear Carpeta
        crearCarpeta(PATH);

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
		float maxHeight = 1280.0f;
		float maxWidth = 720.0f;

//        float maxHeight = 720.0f;
//        float maxWidth = 1280.0f;
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
        scaledBitmap = tramadoSusalud(scaledBitmap);

        //Guardar imagen
        FileOutputStream out = null;
        String filename = filePath;
        try {
            out = new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void crearCarpeta(String path) {
        File sdCard = Environment.getExternalStorageDirectory();
        File file = new File(sdCard.getAbsolutePath() + path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
