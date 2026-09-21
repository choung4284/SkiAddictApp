package fun.skiaddict.projector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(239, 246, 250));

        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setAdjustViewBounds(false);

        Bitmap bitmap = loadMaster();
        if (bitmap != null) image.setImageBitmap(bitmap);

        root.addView(image, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        ));

        setContentView(root);
    }

    private Bitmap loadMaster() {
        int[] ids = {
                R.raw.home_master_0,
                R.raw.home_master_1,
                R.raw.home_master_2,
                R.raw.home_master_3,
                R.raw.home_master_4,
                R.raw.home_master_5
        };
        StringBuilder b64 = new StringBuilder(100000);
        try {
            for (int id : ids) {
                InputStream in = getResources().openRawResource(id);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) b64.append(line.trim());
                br.close();
            }
            byte[] data = Base64.decode(b64.toString(), Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        } catch (Exception e) {
            return null;
        }
    }
}
