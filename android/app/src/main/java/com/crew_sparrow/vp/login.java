package com.crew_sparrow.vp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class login extends AppCompatActivity {

    SQLiteDatabase db;
    EditText name,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = openOrCreateDatabase("vp", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists user(name varchar(20),user varchar(20),pass varchar(20));");

        name=findViewById(R.id.name);
        pass=findViewById(R.id.pass);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u=name.getText().toString();
                String p=pass.getText().toString();
                Cursor c = db.rawQuery("select * from user where user='"+u+"'", null);
                c.moveToFirst();
                if(c.getCount()==0)
                {
                    Toast.makeText(login.this,"Database is Empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if (c.getString(2).equals(p)) {
                        Toast.makeText(login.this, "LOG IN", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(login.this,index.class);
                        intent.putExtra("user_name",u);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(login.this, "PASSWORD NOT MATCH", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,register.class);
                startActivity(intent);
            }
        });
    }
}
