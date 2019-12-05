package com.example.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {
        private WeakReference<TextView> mTitleText;
        private WeakReference<TextView> mAuthorText;

        public FetchBook(TextView titleText, TextView authorText) {
            this.mTitleText = new WeakReference<>(titleText);
            this.mAuthorText = new WeakReference<>(authorText);
        }

        @Override
        protected String doInBackground(String... strings) {
            return NetworkUtils.getBookInfo(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
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
                    mTitleText.get().setText(title);
                    mAuthorText.get().setText(authors);
                } else {
                    // show failed results
                    mTitleText.get().setText(R.string.no_result);
                    mAuthorText.get().setText("");
                }
            } catch (Exception e) {
                //If proper JSON string was not received show failed result;
                mTitleText.get().setText(R.string.no_result);
                mAuthorText.get().setText("");
            }
        }
    }
