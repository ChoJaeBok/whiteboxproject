package gst.study.yellowtv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static gst.study.yellowtv.SettingActivity.ip;

// 내부 카메라를 확대해준 스트리밍 화면을 보여주는 곳입니다.
public class InsideFullScrren extends AppCompatActivity {

    public static String uri2 = ip.getIp2();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inside_full_scrren);

        // 액션 바 숨김
        ActionBar ab = getSupportActionBar();
        ab.hide();

        VideoView v = (VideoView) findViewById(R.id.videoView04);
        v.setVideoURI(Uri.parse(uri2));
        Log.e("test", "test");
        v.requestFocus();
        v.start();

        Button captureBtn03 = (Button) findViewById(R.id.captureBtn03);
        captureBtn03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                File myDir = new File(path + "/mydir"); //SD카드의 경로 아래에 mydir 폴더를 생성하기 위해 File형의 변수 설정

                View viewCapture = getWindow().getDecorView().getRootView(); //캡처할 영역 (전체화면)
                viewCapture.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(viewCapture.getDrawingCache());
                viewCapture.setDrawingCacheEnabled(false);

                if(!myDir.exists()){ //원하는 경로에 폴더가 있는지 확인

                    myDir.mkdir(); //mkdir폴더 생성
                    Log.d("CAMERA_TEST", "폴더 생성");
                    Toast.makeText(getApplicationContext(), "폴더가 생성되었습니다.", Toast.LENGTH_SHORT).show();
                }

                // 현재 날짜로 파일을 저장하기
                SimpleDateFormat day = new SimpleDateFormat("yyyyMMddHHmmss");
                // 년 월 일 시 분 초
                Date date = new Date();
                String dateString = day.format(date);
                viewCapture.buildDrawingCache();
                Bitmap captureview = viewCapture.getDrawingCache();
                FileOutputStream fos = null;

                try{

                    fos = new FileOutputStream(path + "/Capture" + day.format(date) + ".jpeg"); //저장 경로
                    captureview.compress(Bitmap.CompressFormat.JPEG, 100, fos); //캡처

                    Toast.makeText(getApplicationContext(), dateString + ".jpg 저장", Toast.LENGTH_LONG).show();
                    fos.flush();

                    fos.close();

                    viewCapture.destroyDrawingCache();

                } catch (FileNotFoundException e) {

                    e.printStackTrace();

                } catch (IOException e) {

                    e.printStackTrace();
                    Log.e("스크린", ""+e.toString());

                }
            }
        });

        Button backBtn02 = (Button) findViewById(R.id.backBtn02);
        backBtn02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
              finish();
            }
        });

    }
}
