package de.rhaeus.dndsync;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DNDSyncListenerService extends WearableListenerService {
    private static final String TAG = "DNDSyncListenerService";
    private static final String DND_SYNC_MESSAGE_PATH = "/wear-dnd-sync";

    @Override
    public void onMessageReceived (@NonNull MessageEvent messageEvent) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onMessageReceived: " + messageEvent);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (messageEvent.getPath().equalsIgnoreCase(DND_SYNC_MESSAGE_PATH)) {
            Log.d(TAG, "received path: " + DND_SYNC_MESSAGE_PATH);

            boolean vibrate = prefs.getBoolean("vibrate_key", false);
            Log.d(TAG, "vibrate: " + vibrate);
            if (vibrate) {
                vibrate();
            }

            byte[] data = messageEvent.getData();
            // data[0] contains dnd mode of phone
            // 0 = INTERRUPTION_FILTER_UNKNOWN
            // 1 = INTERRUPTION_FILTER_ALL (all notifications pass)
            // 2 = INTERRUPTION_FILTER_PRIORITY
            // 3 = INTERRUPTION_FILTER_NONE (no notification passes)
            // 4 = INTERRUPTION_FILTER_ALARMS
            // Custom
            // 5 = BedTime Mode On
            // 6 = BedTime Mode Off
            byte dndStatePhone = data[0];
            Log.d(TAG, "dndStatePhone: " + dndStatePhone);

            // get dnd state
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int filterState = mNotificationManager.getCurrentInterruptionFilter();
            if (filterState < 0 || filterState > 4) {
                Log.d(TAG, "DNDSync weird current dnd state: " + filterState);
            }
            byte currentDndState = (byte) filterState;
            Log.d(TAG, "currentDndState: " + currentDndState);

            if(dndStatePhone == 5 || dndStatePhone ==6) {
                int bedTimeModeValue = (dndStatePhone ==5)?1:0;
                boolean useBedtimeMode = prefs.getBoolean("bedtime_key", true);
                Log.d(TAG, "useBedtimeMode: " + useBedtimeMode);
                if (useBedtimeMode) {
                    boolean success = Settings.Global.putInt(
                            getApplicationContext().getContentResolver(), "setting_bedtime_mode_running_state", bedTimeModeValue);
                    if (success) {
                        Log.d(TAG, "Bedtime mode value toggled");
                    } else {
                        Log.d(TAG, "Bedtime mode toggle failed");
                    }
                }
            }

            if ((dndStatePhone != currentDndState) && (dndStatePhone !=5 && dndStatePhone !=6)) {
                Log.d(TAG, "dndStatePhone != currentDndState: " + dndStatePhone + " != " + currentDndState);
                // set DND anyways, also in case bedtime toggle does not work to have at least DND
                if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                    mNotificationManager.setInterruptionFilter(dndStatePhone);
                    Log.d(TAG, "DND set to " + dndStatePhone);
                } else {
                    Log.d(TAG, "attempting to set DND but access not granted");
                }
            }

        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
    }

}
