package hu.skrivoo.whac_a_mole;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {

    private static final String CHANNEL_ID = "mole_channel";
    private static final long[] MOLE_VIBRATE_PATTERN = {1000, 500, 1000, 500};
    private static final int NOTIFICATION_ID = 6969;
    private final NotificationManager manager;
    private final Context context;

    public NotificationHandler(Context context) {
        this.context = context;
        this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID,
                            "Whac-a-Mole notifications",
                            NotificationManager.IMPORTANCE_LOW);
            channel.enableVibration(true);
            channel.setVibrationPattern(MOLE_VIBRATE_PATTERN);
            channel.setDescription("News about Moles");
            manager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String messege) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.mole_in_toplist)
                .setColorized(true)
                .setContentTitle("Whac-a-Mole")
                .setContentText(messege);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotification() {
        manager.cancel(NOTIFICATION_ID);
    }
}
