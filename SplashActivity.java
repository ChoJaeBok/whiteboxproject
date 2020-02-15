package gst.study.yellowtv;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class SplashActivity extends Activity
{
//처음 앱을 시작했을 때 3초동안 메인 로고를 보여주는 곳입니다.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Handler hd = new Handler() ;
        hd.postDelayed(new splashandler(), 3000) ; // 로딩화면 3초동안 동작
    }

    private class splashandler implements Runnable
    {
        public void run()
        {
            startActivity(new Intent(getApplication(), MainActivity.class)) ; // 로딩이 끝난 후, Main으로 이동
            SplashActivity.this.finish() ;
        }
    }

    @Override
    public void onBackPressed()
    {
        // 로딩화면에서 뒤로가기 눌러도 버튼 인식 안됨
    }
}