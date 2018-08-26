package com.crew_sparrow.vp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class upload extends AppCompatActivity {
    SQLiteDatabase db,dp;
    ImageView image;
    Button upl;
    private  static final int pick_img=100;
    Uri imageuri;
    EditText cap;
    Bitmap bitmap;
    String usern="TK",capt,imagepath;
    private int s=1;
    boolean createSuccessful;
    StringBuilder builder;
    Cursor c;
    int[] imgintarr=new int[64*64];
    float[] imgfltarr=new float[64*64];
    private static final String MODEL_FILE = "file:///android_asset/optimized_tfdroid.pb";
    private static final String INPUT_NODE = "x";
    private static final String OUTPUT_NODE[] = {"prediction"};
    private TensorFlowInferenceInterface inferenceInterface;
    float[] result = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        usern = intent.getStringExtra("user_name");

        db = this.openOrCreateDatabase("vp", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists img(user varchar(20),imgpath blob,caption varchar(200));");
        cap=findViewById(R.id.caption);
        image=findViewById(R.id.img);
        upl=findViewById(R.id.upload);
        inferenceInterface = new TensorFlowInferenceInterface(getAssets(),MODEL_FILE);

        upl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opengallery();
            }
        });
        findViewById(R.id.uploaddb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                capt=cap.getText().toString();
                if (ContextCompat.checkSelfPermission(upload.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(upload.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            new AlertDialog.Builder(upload.this).setTitle("permission need").setMessage("The permission needed to save image in file").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(upload.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            s);
                                }
                            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    }
                    else{
                        ActivityCompat.requestPermissions(upload.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                s);
                    }
                }
                else {

                    try {
                        FileInputStream file = new FileInputStream(imagepath);
                        byte[] imgg = new byte[file.available()];
                        file.read(imgg);
                        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagepath), 64, 64, true);
                        bitmap=toGrayscale(bitmap);
                        bitmap.getPixels(imgintarr,0,64,0,0,64,64);
                        for (int i=0;i<4096;i++)
                            imgfltarr[i]=(float)imgintarr[0];
                        inferenceInterface.feed(INPUT_NODE,imgfltarr,1,4096);
                        inferenceInterface.run(OUTPUT_NODE);
                        inferenceInterface.fetch(OUTPUT_NODE[0], result);
                        ContentValues values = new ContentValues();
                        values.put("imgpath", imgg);
                        values.put("user", usern);
                        values.put("caption", capt);
                        createSuccessful = db.insert("img", null, values) > 0;
                        //c=db.rawQuery("select * from img",null);
                        Toast.makeText(upload.this, Float.toString(result[0])+' '+Float.toString(result[1]), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(upload.this, "Insert : " +String.valueOf(createSuccessful), Toast.LENGTH_LONG).show();
                        //Toast.makeText(upload.this,Integer.toString(c.getCount()) , Toast.LENGTH_LONG).show();
                        /*upload.this.finish();
                        Intent intent = new Intent(upload.this, upload.class);
                        startActivity(intent);*/
                    } catch (Exception e) {
                        Toast.makeText(upload.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                }





            }
        });
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {

        Bitmap bmpGrayscale = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    private  void opengallery(){

         Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
         startActivityForResult(gallery,pick_img);
    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode,Intent data){
        super.onActivityResult(requestcode,resultcode,data);
        if(resultcode==RESULT_OK && requestcode==pick_img && data!=null) {


            imageuri = data.getData();


            String[] filepath={MediaStore.Images.Media.DATA};

            Cursor cursor=getContentResolver().query(imageuri,filepath,null,null,null);
            cursor.moveToFirst();

            imagepath=cursor.getString(cursor.getColumnIndex(filepath[0]));
            cursor.close();
            //Toast.makeText(upload.this, imagepath, Toast.LENGTH_LONG).show();

           image.setImageURI(imageuri);
            }

        }
    }
