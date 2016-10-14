package sg.edu.ntu.example.user;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DB_URI = "content://sg.edu.ntu.testperm.simpleprovider.MyProvider/cte";
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_CONTACTS = 3;
    TextView resultView = null;
    CursorLoader cursorLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (TextView) findViewById(R.id.res);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onClickDisplayNames(View view) {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Log.i(TAG, "onCreateLoader");
        cursorLoader = new CursorLoader(this, Uri.parse(DB_URI), null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        cursor.moveToFirst();
        StringBuilder res = new StringBuilder();
        while (!cursor.isAfterLast()) {
            res.append("\n").append(cursor.getString(cursor.getColumnIndex("id"))).append("-").append(cursor.getString(cursor.getColumnIndex("name")));
            cursor.moveToNext();
        }
        resultView.setText(res);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    public void onClickedIntentBroadCast(View view) {
        String perm = Manifest.permission.READ_PHONE_STATE;
//        if (ContextCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED) {
        Intent intent = new Intent();
        intent.setAction("sg.edu.ntu.testperm.MYINTENT");
        sendBroadcast(intent);
//        } else {
//            Toast.makeText(this, "no perm: " + perm, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String info = data.getStringExtra("INFO");
                Log.i(TAG, "info=" + info);
            } else {
                Log.e(TAG, "resultCode=" + resultCode);
            }
        } else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.i(TAG, imageBitmap != null ? imageBitmap.toString() : null);
        } else if (requestCode == REQUEST_CONTACTS && resultCode == RESULT_OK) {
            int myID = 1;
            Uri contactData = data.getData();
            Cursor cur = managedQuery(contactData, null, null, null, null);
            ContentResolver cr = getContentResolver();
            if (Integer.parseInt(cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{String.valueOf(myID)}, null);
                assert pCur != null;
                while (pCur.moveToNext()) {
                    String name = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.i(TAG, "get the name: " + name);
                    // Do something with phones
                }
                pCur.close();
            }
        } else {
            Log.i(TAG, "requestCode=" + requestCode);
        }
    }

    public void onClickedIntentStartActivity(View view) {
        Intent intent = new Intent();
        intent.setAction("sg.edu.ntu.testperm.MYINTENT2");
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onClickedIntentStartService(View view) {
        Intent intent = new Intent();
        Log.i(TAG, "xxx " + android.os.Process.myPid());
        intent.putExtra("PackageNameInfo", getPackageName());
        Log.i(TAG, "data=" + intent.getStringExtra("PackageNameInfo"));
        intent.setComponent(new ComponentName("sg.edu.ntu.testperm", "sg.edu.ntu.testperm.simpleservice.SimpleService"));
        startService(intent);
    }

    public void onClickedIntentStartCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        } else {
            Log.i(TAG, "cannot resolve");
        }
    }

    public void onClickedIntentStartContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACTS);
    }
}
