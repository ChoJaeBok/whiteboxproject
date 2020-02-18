package gst.study.yellowtv;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import  android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


public class SubActivity extends AppCompatActivity  {

    public static String uri1 = ip.getIp1();
    public static String uri2 = ip.getIp2();
//외부카메라, 내부카메라인 두 라즈베리파이제로에서 실행되는 스트리밍 화면을
    //안드로이드 앱에서 볼 수 있도록 해주는 곳입니다.
    //Video View를 이용하였으며 스트리밍을 통한 화면을 스크린샷 하는 부분에서 문제점을 발견했습니다.
    //또한 Video View를 통한 스트리밍은 딜레이가 약간 있게 된다는 점도 있습니다.
    //차후에는 NDK라는 툴킷을 이용하여 스트리밍을 플레이 할 수 있는 환경으로 보완할 예정입니다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},MODE_PRIVATE); //외부 접근 권한

 /*       // 액션바 숨김
        ActionBar ab = getSupportActionBar();
        ab.hide();
*/
        VideoView v = (VideoView) findViewById( R.id.videoView );
        v.setVideoURI( Uri.parse(uri1) );
        v.requestFocus();
        v.start();

        VideoView v2 = (VideoView) findViewById( R.id.videoView2 );
        v2.setVideoURI( Uri.parse(uri2) );
        v2.requestFocus();
        v2.start();


        Button backBtn = (Button) findViewById(R.id.backBtn02) ;
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
                /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
            }
        });

        Button captureBtn;
        captureBtn = (Button) findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new View.OnClickListener() {
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


    }

/*    // menu1.xml 실행, 메뉴 설정창 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu2, menu);
        return true;
    }
    // menu에 해당되는 버튼들 선택 시, 작동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){

            // 목록 클릭 후, 카메라1 (외부) 클릭시 이벤트
            case R.id.itemCAM_03:



                intent = new Intent(getApplicationContext(),ExternalFullScrren.class);
                startActivity(intent);
                return true;
            // 목록, 카메라2 (내부) 클릭시 이벤트


            case R.id.itemCAM_04:
                intent = new Intent(getApplicationContext(), InsideFullScrren.class);
                startActivity(intent);

                return true;


        }
        return false;
    }*/

}
