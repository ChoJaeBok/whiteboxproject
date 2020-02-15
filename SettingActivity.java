package gst.study.yellowtv;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
// 여기서는 IP셋팅을 해주는 역할을 하며 라즈베리파이 제로(카메라)2 대와 라즈베리파이 B3(서버)
// 이 세가지를 연결할 수 있도록 IP를 적어주는 곳입니다.

public class SettingActivity extends AppCompatActivity {
    public  static IpReceiver ip;
    View dialogView;
    EditText dlgEdtIP_01, dlgEdtIP_02;
    boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 기본은 숨김이나 ActionBar를 보이기
        ActionBar ab = getSupportActionBar();
        ab.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("설정");

        // 뒤로 가기 버튼
        Button backBtn03 = (Button) findViewById(R.id.backBtn03);
        backBtn03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
               /* Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);*/
            }
        });
    }

    // menu1.xml 실행, 메뉴 설정창 생성
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater mInflater = getMenuInflater();
        mInflater.inflate(R.menu.menu1, menu);
        return true;
    }

    // menu에 해당되는 버튼들 선택 시, 작동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

                // 목록 클릭 후, 카메라1 (외부) 클릭시 이벤트
            case R.id.itemCAM_01:
                dialogView = (View) View.inflate(SettingActivity.this,R.layout.dialog1, null);
                AlertDialog.Builder dlg_01 = new AlertDialog.Builder (SettingActivity.this);

                dlg_01.setTitle("ip 주소 입력");
                dlg_01.setIcon(R.drawable.pi_icon_round);
                dlg_01.setView(dialogView);
                dlg_01.setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dlgEdtIP_01 = (EditText) dialogView.findViewById(R.id.dlgEdt1);
                        dlgEdtIP_02 = (EditText) dialogView.findViewById(R.id.dlgEdt2);

                        if(!check)
                            ip = new IpReceiver();

                        check = true;
                        ip.setName(dlgEdtIP_01.getText().toString());
                        ip.setIp1(dlgEdtIP_02.getText().toString());
                    }
                });
                dlg_01.setNegativeButton("취소", null);
                dlg_01.show();

                return true;

                // 목록, 카메라2 (내부) 클릭시 이벤트
            case R.id.itemCAM_02:

                dialogView = (View) View.inflate(SettingActivity.this,R.layout.dialog1, null);
                AlertDialog.Builder dlg_02 = new AlertDialog.Builder (SettingActivity.this);

                dlg_02.setTitle("ip 주소 입력");
                dlg_02.setIcon(R.drawable.pi_icon_round);
                dlg_02.setView(dialogView);
                dlg_02.setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dlgEdtIP_01 = (EditText) dialogView.findViewById(R.id.dlgEdt1);
                        dlgEdtIP_02 = (EditText) dialogView.findViewById(R.id.dlgEdt2);

                        if(!check)
                            ip = new IpReceiver();

                        check = true;

                        ip.setName(dlgEdtIP_01.getText().toString());
                        ip.setIp2(dlgEdtIP_02.getText().toString());
                    }
                });
                dlg_02.setNegativeButton("취소", null);
                dlg_02.show();

                return true;
            case R.id.itemB3://수정
                dialogView = (View) View.inflate(SettingActivity.this,R.layout.dialog1, null);
                AlertDialog.Builder dlg_03 = new AlertDialog.Builder (SettingActivity.this);

                dlg_03.setTitle("ip 주소 입력");
                dlg_03.setIcon(R.drawable.pi_icon_round);
                dlg_03.setView(dialogView);
                dlg_03.setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dlgEdtIP_01 = (EditText) dialogView.findViewById(R.id.dlgEdt1);
                        dlgEdtIP_02 = (EditText) dialogView.findViewById(R.id.dlgEdt2);

                        ip = new IpReceiver();

                        ip.setName(dlgEdtIP_01.getText().toString());
                        ip.setB3_ip(dlgEdtIP_02.getText().toString());
                    }
                });
                dlg_03.setNegativeButton("취소", null);
                dlg_03.show();
                return true;

        }
        return false;
    }


}
