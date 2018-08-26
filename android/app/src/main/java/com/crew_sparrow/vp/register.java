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

public class register extends AppCompatActivity {
    SQLiteDatabase db;
    EditText namee,username,password,cpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        namee=findViewById(R.id.name);
        username=findViewById(R.id.user);
        password=findViewById(R.id.pass);
        cpass=findViewById(R.id.cpass);

        db = openOrCreateDatabase("vp", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists user(name varchar(20),user varchar(20),pass varchar(20));");

        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n=namee.getText().toString();
                String d=username.getText().toString();
                String p=password.getText().toString();
                String cp=cpass.getText().toString();

                Cursor c = db.rawQuery("select * from user where user='"+d+"'", null);
                c.moveToFirst();

                if(n.length()==0 || d.length()==0 || p.length()==0 || cp.length()==0){
                    Toast.makeText(register.this, "Empty field", Toast.LENGTH_SHORT).show();
                }
                else if(c.getCount()!=0)
                {
                    Toast.makeText(register.this,"User name is always present",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(cp.equals(p)){
                    db.execSQL("insert into user(name,user,pass) values('" + n + "','" + d + "','"+p+"');");
                    System.out.print("inserted");
                    Toast.makeText(register.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(register.this,login.class);
                    startActivity(intent);
                }
                else{
                    System.out.print("nope");
                    Toast.makeText(register.this, "password not matched", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
