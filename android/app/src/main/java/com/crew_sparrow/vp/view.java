package com.crew_sparrow.vp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class view extends AppCompatActivity {
    SQLiteDatabase db,dp;
    TextView texte,ue;
    ImageView imge;
    Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //texte=findViewById(R.id.caption);
        //imge=findViewById(R.id.imgg);
        //ue=findViewById(R.id.us);
        byte[] im;
        Bitmap bitmap;
        LinearLayout mainmenu = (LinearLayout)findViewById(R.id.main_menu);
        //LinearLayout submenu = (LinearLayout)findViewById(R.id.imggrp);
        db = openOrCreateDatabase("vp", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists img(user varchar(20),imgpath blob,caption varchar(200));");

        dp = this.openOrCreateDatabase("test.dp", Context.MODE_PRIVATE, null);
        dp.execSQL("create table if not exists tb (a blob)");


        c=db.rawQuery("select * from img",null);
        Toast.makeText(view.this, Integer.toString(c.getCount()), Toast.LENGTH_LONG).show();
        if(c.getCount()>0){

            for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() )
            {
                try
                {
                    LinearLayout ll = new LinearLayout(this);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    TextView ue = new TextView(this);
                    ue.setText(c.getString(0));
                    ll.addView(ue);
                    im=c.getBlob(1);
                    bitmap=BitmapFactory.decodeByteArray(im,0,im.length);
                    TextView texte = new TextView(this);
                    texte.setText(c.getString(2));
                    ImageView imge= new ImageView(this);
                    imge.setImageBitmap(bitmap);
                    ll.addView(imge);
                    ll.addView(texte);
                    mainmenu.addView(ll);
                    //Toast.makeText(view.this, Integer.toString(c.getCount()), Toast.LENGTH_LONG).show();
                }
                catch (IllegalStateException e)
                {
                    //Do Nothing
                }
                catch (NullPointerException e)
                {
                    //Do Nothing
                }
            }

        }
        else{
            Toast.makeText(view.this, "No data available", Toast.LENGTH_LONG).show();
        }


        /*findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c.getCount()>0){
                    if(c.moveToFirst()){
                        do
                        {
                            try
                            {
                                byte[] im=c.getBlob(1);
                                Bitmap bitmap=BitmapFactory.decodeByteArray(im,0,im.length);
                                ue.setText(c.getString(0));
                                texte.setText(c.getString(2));
                                imge.setImageBitmap(bitmap);
                                Toast.makeText(view.this, Integer.toString(c.getCount()), Toast.LENGTH_LONG).show();
                            }
                            catch (IllegalStateException e)
                            {
                                //Do Nothing
                            }
                            catch (NullPointerException e)
                            {
                                //Do Nothing
                            }
                        }
                        while(c.moveToNext());

                    }
                }
            }
        });
        findViewById(R.id.pre).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(c.getCount()>0){
                    if(c.moveToFirst()){
                        do
                        {
                            try
                            {
                                byte[] im=c.getBlob(1);
                                Bitmap bitmap=BitmapFactory.decodeByteArray(im,0,im.length);
                                ue.setText(c.getString(0));
                                texte.setText(c.getString(2));
                                imge.setImageBitmap(bitmap);
                                Toast.makeText(view.this, Integer.toString(c.getCount()), Toast.LENGTH_LONG).show();
                            }
                            catch (IllegalStateException e)
                            {
                                //Do Nothing
                            }
                            catch (NullPointerException e)
                            {
                                //Do Nothing
                            }
                        }
                        while(c.moveToPrevious());

                    }
                }
            }
        });*/

    }
}
