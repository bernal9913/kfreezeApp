package com.bernalgas.finalchaval;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class postLogin extends AppCompatActivity {
    //LoginResponse loginResponse;
    private static final int REQUEST_IMAGE_GET = 1;
    private static final int SELECT_IMAGE = 100;
    TextView u, e, n, b, ut, fn, ln;
    FloatingActionButton noti,logout,edit, delete, admin, menu, location;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Boolean flag = true;
    Boolean hasPicture = false;
    String USER;
    dbStopJumper db;
    private FusedLocationProviderClient mFusedLocationClient;
    private int MY_PERMISSION_READ_CONTACTS;
    ImageView dp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        // getting extras from the another activity
        Intent intent = getIntent();
        String usr = intent.getStringExtra("usr");
        System.out.println("postlogin");
        sharedPreferences = this.getSharedPreferences("sessions",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //System.out.println(usr);

        // parsing json string to user object
        User user = new Gson().fromJson(usr,User.class);
        System.out.println(user.getEmail());
        startEverything(user);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        USER = sharedPreferences.getString("username","");
        db = new dbStopJumper(this);
        hasPicture = db.checkPhoto(USER);
        System.out.println("check photo: " + hasPicture);
        if(hasPicture){
            Bitmap tmp = db.getPhoto(USER);
            dp.setImageBitmap(tmp);
        }
        //boolean flag = true;
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag){
                    menu.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_close));
                    flag = false;
                    if (user.getUserType().contains("Admin")){
                        noti.setVisibility(View.VISIBLE);
                        edit.setVisibility(View.VISIBLE);
                        delete.setVisibility(View.VISIBLE);
                        admin.setVisibility(View.VISIBLE);
                        location.setVisibility(View.VISIBLE);
                        logout.setVisibility(View.VISIBLE);
                    }else if(user.getUserType().contains("Regular")){
                        logout.setVisibility(View.VISIBLE);
                    }
                }else if(!flag){
                    menu.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_menu));
                    flag = true;
                    if (user.getUserType().contains("Admin")){
                        noti.setVisibility(View.INVISIBLE);
                        edit.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                        admin.setVisibility(View.INVISIBLE);
                        location.setVisibility(View.INVISIBLE);
                        logout.setVisibility(View.INVISIBLE);
                    }else if(user.getUserType().contains("Regular")){
                        logout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), DeleteUser.class);
                startActivity(i);
            }
        });
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MousekeTools.class);
                startActivity(i);
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ModUser.class);
                startActivity(i);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Adios " + user.getUsername() + ", fue un gusto verte";
                Toast.makeText(postLogin.this, msg, Toast.LENGTH_SHORT).show();

                editor.putString("username", "");
                editor.putString("email", "");
                editor.putString("password","");
                editor.putString("birthdate","");
                editor.putString("nationality","");
                editor.putString("userType", "");
                editor.putString("firstName", "");
                editor.putString("lastName", "");
                editor.putBoolean("CHECKED", false);
                editor.commit();
                finish();
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("boton miado");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }else{
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotification();
            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsChilo.class);
                startActivity(i);

            }
        });
    }

    private void startEverything(User user){

        u = findViewById(R.id.tv_pUsername);
        e = findViewById(R.id.tv_pEmail);
        n = findViewById(R.id.tv_pNationality);
        b = findViewById(R.id.tv_pBirthdate);
        ut = findViewById(R.id.tv_pUserType);
        fn = findViewById(R.id.tv_pFirst);
        ln = findViewById(R.id.tv_pLast);
        dp = findViewById(R.id.iv_dp);
        // floating action button
        menu = findViewById(R.id.fab_menu);
        noti = findViewById(R.id.fab_notification);
        logout = findViewById(R.id.fab_logout);
        edit = findViewById(R.id.fab_edit);
        delete = findViewById(R.id.fab_delete);
        admin = findViewById(R.id.fab_admin);
        location = findViewById(R.id.fab_location);

        u.setText(user.getUsername());
        e.setText(user.getEmail());
        n.setText(user.getNationality());
        b.setText(user.getBirthdate());
        ut.setText(user.getUserType());
        fn.setText(user.getFirstName());
        ln.setText(user.getLastName());
    }
    private void createNotification(){
        String id = "My_channel_id_01";
        Bitmap DONGOYO06 = ((BitmapDrawable)getDrawable(R.drawable.gosh_maquila)).getBitmap();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri NATA = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.nataea);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = manager.getNotificationChannel(id);
            if (channel == null){
                channel = new NotificationChannel(id,"Channel Tittle", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setBypassDnd(true);
                channel.setVibrationPattern(new long[]{100,1000,200,300});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.setSound(NATA,audioAttributes);
                manager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,id)
                .setSmallIcon(R.mipmap.ic_gosh_foreground)
                .setContentTitle("Notificacion Belica")
                .setContentText("Asi es, Don Gregorio 06 en tus notifaciones")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,300})
                .setAutoCancel(false)// true on notification menu dismised
                .setTicker("Notification")
                .setSound(NATA, AudioManager.STREAM_NOTIFICATION)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(DONGOYO06));
        NotificationManagerCompat m = NotificationManagerCompat.from(getApplicationContext());
        m.notify(1,builder.build());
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE && null != data){
            String user = sharedPreferences.getString("username","");
            Uri uri = data.getData();
            /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
            byte[] img = byteArray.toByteArray();
            boolean insr = dbStopJumper.insertData(user, img);
            if(insr){
                Toast.makeText(ChangeDP.this, "Foto cambiada", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ChangeDP.this, "Algo anda mal", Toast.LENGTH_SHORT).show();
            }
            profileImageView.setImageBitmap(dbStopJumper.getIMG(user));

             */
            dp.setImageURI(uri);
        }
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            //Bitmap thumbnail = data.getParcelable("data");
            if (data != null) {
                Uri fullPhotoUri = data.getData();
                //profileImageView.setImageURI(fullPhotoUri);
                Bitmap bitmap = null;

                Bitmap bmp = bitmap;
                try {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),fullPhotoUri));
                    dp.setImageBitmap(bitmap);
                    if(hasPicture){

                        Toast.makeText(this, "Modificando imagen", Toast.LENGTH_SHORT).show();
                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        //byte[] b = baos.toByteArray();
                        db.updatePhoto(USER,bitmap);
                    }else{
                        System.out.println("agregando img");
                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        //byte[] b = baos.toByteArray();

                        //boolean checku = dbHelper.checkPhoto(USER);

                        //String encodedImage = Base64.getEncoder().encodeToString(b);
                        //System.out.println(encodedImage);
                        System.out.println(USER);
                        db.addPhoto(USER, bitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this,getContentResolver(), fullPhotoUri);
            }
        }
    }
}