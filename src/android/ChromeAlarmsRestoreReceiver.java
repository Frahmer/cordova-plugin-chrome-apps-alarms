package org.chromium;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChromeAlarmsRestoreReceiver extends BroadcastReceiver{



    @Override
    public void onReceive (Context context, Intent intent)
    {
        final ChromeAlarmsRestore chromeAlarmsRestore = new ChromeAlarmsRestore(context);

        for(final String id : chromeAlarmsRestore.getIds())
        {
            final String alarmTime = chromeAlarmsRestore.get(id);

            final Long alarmTimeTmp = Long.valueOf(alarmTime);

            if(alarmTimeTmp != null)
            {
                chromeAlarmsRestore.planAlarm(id, alarmTimeTmp.longValue());
            }
        }
    }

}
