package com.shivisuper.alachat_mobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static com.shivisuper.alachat_mobile.CameraActivity.ARG_FROM_CAMERA;

public class AlacodeActivity extends AppCompatActivity implements View.OnClickListener {

    String QRcode;
    public final static int WIDTH=500;
    String passedArg;
    @Bind(R.id.img_qr_code_image) ImageView qrCodeImageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alacode);
        ButterKnife.bind(this);
        passedArg = getIntent().getExtras().getString(ARG_FROM_CAMERA);
        initializeListeners();
        Toast.makeText(this, passedArg, Toast.LENGTH_SHORT).show();
        getID();
        // create thread to avoid ANR Exception
        Thread t = new Thread(new Runnable() {
            public void run() {
                // this is the msg which will be encode in QRcode
                QRcode = passedArg;
                try {
                    synchronized (this) {
                        wait(5000);
                        //runOnUiThread method used to do UI task in main thread.
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap bitmap;
                                    bitmap = encodeAsBitmap(QRcode);
                                    qrCodeImageview.setImageBitmap(bitmap);
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                } // end of catch block
                            } // end of run method
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void getID() {
        qrCodeImageview=(ImageView) findViewById(R.id.img_qr_code_image);
    }

    // this is method call from on create and return bitmap image of QRCode.
    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y)
                        ? getResources().getColor(R.color.black)
                        :getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    } // end of this method

    public void initializeListeners() {
        qrCodeImageview.setOnClickListener(this);
    }

    public void gotoScanner() {
        Intent scanner = new Intent(getApplicationContext(), ScannerActivity.class);
        startActivity(scanner);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.img_qr_code_image) {
            gotoScanner();
        }
    }
}