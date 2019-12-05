package com.example.whowroteit;

import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.Loader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Object> {
    private EditText mBookInput;
    private TextView mTitleText;
    private TextView mAuthorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBookInput = findViewById(R.id.bookInput);
        mTitleText = findViewById(R.id.titleText);
        mAuthorText = findViewById(R.id.authorText);

        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }
    }

    public void searchBooks(View view) {
        //Get the search String
        String queryString = mBookInput.getText().toString();
        //Hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //Check Internet connection & query string presence;
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        mAuthorText.setText("");
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            } else {
                mTitleText.setText(R.string.no_network);
            }
        }


    @NonNull
    @Override
    public Loader<Object> onCreateLoader(int id, @Nullable Bundle args) {
        String queryString = "";
        if (args != null) {
            queryString = args.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<Object> loader, Object data) {
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(data));
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String authors = null;
            while (i < itemsArray.length() && (authors == null && title == null)){
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                //Try to get the title and author
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                i++;
            }

            // If title & author are found display results
            if (title != null && authors != null) {
                mTitleText.setText(title);
                mAuthorText.setText(authors);
            } else {
                // show failed results
                mTitleText.setText(R.string.no_result);
                mAuthorText.setText("");
            }
        } catch (Exception e) {
            //If proper JSON string was not received show failed result;
            mTitleText.setText(R.string.no_result);
            mAuthorText.setText("");
        }
    }

    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<Object> loader) {

    }
}
