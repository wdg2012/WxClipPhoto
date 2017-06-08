package rec.yuanmai_driver.administrator.example.com.wxclipphoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
   private   ImageView iv_clip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_main);
        iv_clip = (ImageView) findViewById(R.id.iv_clip);
        iv_clip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this,ScoendActivity.class),1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode!=RESULT_OK){
            return;
        }
        switch (requestCode){
            case 1:
                byte[]bytes = data.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                iv_clip.setImageBitmap(bitmap);
                break;
        }
    }
}
