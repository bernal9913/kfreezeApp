package com.bernalgas.finalchaval;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompatExtras;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

public class postLogin extends AppCompatActivity {
    //LoginResponse loginResponse;
    TextView u, e, n, b;
    FloatingActionButton noti,logout;
    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);

        // getting extras from the another activity
        Intent intent = getIntent();
        String usr = intent.getStringExtra("usr");
        System.out.println("postlogin");
        //System.out.println(usr);

        // parsing json string to user object
        User user = new Gson().fromJson(usr,User.class);
        System.out.println(user.getEmail());

        u = findViewById(R.id.tv_pUsername);
        e = findViewById(R.id.tv_pEmail);
        n = findViewById(R.id.tv_pNationality);
        b = findViewById(R.id.tv_pBirthdate);

        // floating action button
        noti = findViewById(R.id.fab_notification);
        logout = findViewById(R.id.fab_logout);

        u.setText(user.getUsername());
        e.setText(user.getEmail());
        n.setText(user.getNationality());
        b.setText(user.getBirthdate());

        // TODO eventually make it work the logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = "Adios " + user.getUsername() + ", fue un gusto verte";
                Toast.makeText(postLogin.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // TODO also eventually make it work the notification button

        String tittle = "Titulo belico de notificacion";
        String subject = "sujeto de la noti";
        String body = "Cuerpo de la noti";

        noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //notificationManagerCompat.notify(1, notification);

                createNotification();
                // TODO custom notification sound
            }
        });

        };

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
        // relleno del indio
        // Intent notificationIntent = new Intent(this.)
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
}