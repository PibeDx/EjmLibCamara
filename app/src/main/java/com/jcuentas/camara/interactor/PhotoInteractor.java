package com.jcuentas.camara.interactor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Jose Cuentas Turpo on 26/06/2015 - 02:53 PM.
 * E-mail: jcuentast@gmail.com
 */
public interface PhotoInteractor {

    void mensajeLog(String mensaje);

    String gerRealPathFromUri(String contentURI);

    Bitmap tramadoImagen(Bitmap bitmap);

    Paint[] paintConfiguracion(float sizeTextBasic, float sizeTextPrincipal);

    float calcularTamanoSepacion(int tamanoImagen, int numeroTextos);

    Bitmap modificarOrientacion(String filePath, Bitmap scaledBitmap);

    void crearCarpeta(String path);

    Canvas insertHead(Bitmap bmpHeader, Canvas canvasBase);

    Canvas insertPie(Bitmap bmpCuerpo,Bitmap bmpFooter, Canvas canvasBase);

    void compressImage(String imageURI);

}
