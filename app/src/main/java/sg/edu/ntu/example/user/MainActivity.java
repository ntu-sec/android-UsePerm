package sg.edu.ntu.example.user;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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

}
