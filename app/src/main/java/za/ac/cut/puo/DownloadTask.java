package za.ac.cut.puo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<URL, Void, Bitmap> {
    AppCompatActivity activity;
    ImageView view;

    public DownloadTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        for (URL url : params) {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                    inputStream.close();
                    return imageBitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        view.setImageBitmap(bitmap);
        //super.onPostExecute(bitmap);
    }
}
