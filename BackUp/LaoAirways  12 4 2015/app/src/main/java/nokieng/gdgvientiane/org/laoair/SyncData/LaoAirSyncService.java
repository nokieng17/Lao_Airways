package nokieng.gdgvientiane.org.laoair.SyncData;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class LaoAirSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static CurrencySyncAdapter sCurrencySyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SyncService", "onCreate - SyncService");
        synchronized (sSyncAdapterLock) {
            if (sCurrencySyncAdapter == null) {
                sCurrencySyncAdapter = new CurrencySyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sCurrencySyncAdapter.getSyncAdapterBinder();
    }
}