package sg.edu.ntu.example.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    public static final String TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String info = intent.getStringExtra("INFO");
        if (info != null) {
            Log.i(TAG, info);
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "no info");
        }
    }
}
