package com.example.mobilalkfejl_online_telefonbolt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {
    private static final String CHANNEL_ID = "Best Phone";
    private NotificationManager nManager;
    private Context mContext;

    public NotificationHandler(Context context){
        this.mContext = context;
        this.nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    private void createChannel() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            return;
        }

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Best Shop", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.setLightColor(Color.YELLOW);
        channel.enableVibration(false);
        channel.setDescription("Üzenet a Best Phone-tól!");
        this.nManager.createNotificationChannel(channel);
    }
    public void send(String message){
        Intent intent = new Intent(mContext, ShopMainSite.class);
        //PendingIntent pending = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID).setContentTitle("Best Phone").setContentText(message).setSmallIcon(R.drawable.ic_add_shopping_cart)/*.setContentIntent(pending)*/;
        this.nManager.notify(0, builder.build());
    }
    public void cancel(){
        nManager.cancel(0);
    }
}
