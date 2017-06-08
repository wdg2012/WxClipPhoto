package rec.yuanmai_driver.administrator.example.com.wxclipphoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;

/**
 * Created by ${wdgan} on 2017/5/27 0027.
 * 邮箱18149542718@163
 */

public class ScoendActivity extends AppCompatActivity {
    private Bitmap mBitmap;
    private WxClipPhotoView mWxClipPhotoView;
    private Button mBtnClip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_second);
        mBtnClip = (Button) findViewById(R.id.btn_clip);
        mBtnClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap = mWxClipPhotoView.getClipBitmap();
                Intent intent = new Intent();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                intent.putExtra("bitmap", datas);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        mWxClipPhotoView = (WxClipPhotoView) findViewById(R.id.image);

    }
}
