package org.chromium;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChromeAlarmsRestore
{
    // Key for private preferences
    static final String PREF_KEY = "ChromeAlarm";

    private final Context context;

    public ChromeAlarmsRestore(final Context context)
    {
        this.context = context;
    }

    /**
     * All alarm IDs.
     */
    public List<String> getIds() {
        final Set<String> keys = getPrefs().getAll().keySet();
        final ArrayList<String> ids = new ArrayList<String>();

        for (String key : keys) {
            try {
                ids.add(key);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return ids;
    }

    /**
     * Get existent alarm.
     *
     * @param id
     *      Alarm ID
     */
    public String get(String id) {
        final Map<String, ?> alarms = getPrefs().getAll();

        if (!alarms.containsKey(id))
            return null;

        final String alarmTime = alarms.get(id).toString();

        return alarmTime;
    }

    /**
     * Persist the information of this notification to the Android Shared
     * Preferences. This will allow the application to restore the notification
     * upon device reboot, app restart, retrieve notifications, aso.
     */
    public void persist (String id, String data) {
        SharedPreferences.Editor editor = getPrefs().edit();

        editor.putString(id, data);

        editor.apply();
    }

    /**
     * Remove the notification from the Android shared Preferences.
     */
    public void unpersist (String id) {
        SharedPreferences.Editor editor = getPrefs().edit();

        editor.remove(id);
        editor.apply();
    }

    public void planAlarm(String id, long scheduledTime)
    {
        PendingIntent alarmPendingIntent = makePendingIntentForAlarm(id, PendingIntent.FLAG_CANCEL_CURRENT);
        getAlarmMgr().set(AlarmManager.RTC_WAKEUP, scheduledTime, alarmPendingIntent);
    }

    /**
     * Shared private preferences for the application.
     */
    private SharedPreferences getPrefs ()
    {
        return context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    /**
     * Alarm manager for the application.
     */
    private AlarmManager getAlarmMgr ()
    {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private PendingIntent makePendingIntentForAlarm(final String name, int flags) {

        Intent broadcastIntent = new Intent(context, ChromeAlarmsReceiver.class);
        // Use different actions for different alarm names so that PendingIntent.getBroadcast returns different PendingIntents for
        // alarms with different names but replaces existing PendingIntents with a new one if one exists with the same name.
        broadcastIntent.setAction(context.getPackageName() + ".ALARM." + name);
        ChromeAlarms.getEventHandler().makeBackgroundEventIntent(broadcastIntent);
        return PendingIntent.getBroadcast(context, 0, broadcastIntent, flags);
    }
}
