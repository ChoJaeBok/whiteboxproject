package gst.study.yellowtv;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import static gst.study.yellowtv.SettingActivity.ip;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    Socket socket = null;
    String ip_num = "";
    static String his_msg = "histogram";
    static String com_msg = "";
    static int but_num= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //액션바 숨기기
        ActionBar ab = getSupportActionBar();
        ab.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //설정 버튼 클릭 시 SettingActivity 실행
        Button button_settings = (Button) findViewById(R.id.settings);
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);

            }
        });

        //설정 버튼 클릭 시 SubActivity 실행
        Button button_streaming = (Button) findViewById(R.id.streaming);
        button_streaming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                startActivity(intent);
            }
        });

        // 좌석 매핑 버튼 클릭 시, SeatMapping 실행
        Button button_mapping = (Button) findViewById(R.id.check);
        button_mapping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SeatMapping.class);
                startActivity(intent);
            }
        });

        // 실외 전체화면 버튼 클릭 시, ExternalFullScrren 실행
        Button button_e_fullscreen = (Button) findViewById(R.id.external_full_screen);
        button_e_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ExternalFullScrren.class);
                startActivity(intent);
            }
        });


        Button button_i_fullscreen = (Button) findViewById(R.id.inside_full_screen);
        button_i_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), InsideFullScrren.class);
                startActivity(intent);
            }
        });


        // 종료 버튼
        Button exitButton = (Button) findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 버튼 2번 눌르는 방식을 만들었는데 소켓 통신 중에 반응이 처음 눌렀을 떄는 없게 되는 것을 발견하였고
                // 두 번 이후 부터 서버의 반응이 보이게 되어 이러한 방식을 선택하였습니다.
                if(but_num==0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    String b3_msg = ip.getB3_msg();
                    String comsg = "서버의 응답: detech";
                    ip_num = ip.getB3_ip();
                    //아래 두 줄의 코드가 서버에 원하는 행동을 보내는 것으로
                    //ip_num은 서버에 연결되어있는 wifi의 ip이며, EndROI는 서버에 저장되어 있는 EndROI의 명령어를 사용하기 위해 보내는 것입니다.
                    //종료버튼에서 처음 명령을 보낼 때 비교할 이미지를 ROI를 하기위해 실행을 해줍니다.
                    MyClientTask myClientTask2 = new MyClientTask(ip_num, 8888, "EndROI");//종료
                    myClientTask2.execute();
                    myClientTask2 = new MyClientTask(ip_num, 8888, his_msg);//종료
                    myClientTask2.execute();
                    Toast.makeText(getApplicationContext(), com_msg, Toast.LENGTH_LONG).show();
                    builder.setMessage("통신중입니다. 다시 한번 눌러주십시오.");
                    builder.setTitle("경고알림창")
                            .setCancelable(false)
                            .setNegativeButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int i) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("종료 알림창");
                    alert.show();

                }
                else if (but_num > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    String comsg = "서버의 응답: detech";
                    ip_num = ip.getB3_ip();

                    MyClientTask myClientTask2 =new MyClientTask(ip_num, 8888, his_msg);//종료
                    myClientTask2.execute();
                    Toast.makeText(getApplicationContext(), com_msg, Toast.LENGTH_LONG).show();
                    if (comsg.equals(com_msg)) {
                        builder.setMessage("★★★경고 아이가 남아 있습니다.★★★");
                        builder.setTitle("경고알림창")
                                .setCancelable(false)
                                .setNegativeButton("확인하십시오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("종료 알림창");
                        alert.show();
                    } else {
                        builder.setMessage("아이가 없습니다. 종료하시겠습니까?");
                        builder.setTitle("종료 알림창")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        finish();
                                        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                                        am.restartPackage(getPackageName());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setTitle("종료 알림창");
                        alert.show();

                    }
                    but_num = 0;
                }

            }
        });
    }

    //뒤로가기 버튼을 두번 연속으로 눌러야 종료되게끔 하는 메소드
    private long time= 0;
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis()-time>=2000){
            time=System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"뒤로 버튼을 한번 더 누르면 종료합니다.",Toast.LENGTH_SHORT).show();
        }else if(System.currentTimeMillis()-time<2000){
            finish();
        }
    }
    //서버
    //서버와 연결하기 위한 연결고리로써 메인 화면에서는 필요한 이유가 종료버튼에서
    //아이나 사람, 물체가 감지되었을 때 종료버튼이 실행되지않고 경고창을 나타내기 위해서 필요합니다.
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage = "";

        //constructor
        MyClientTask(String addr, int port, String message){
            dstAddress = addr;
            dstPort = port;
            myMessage = message;
        }
        //서버와 통신하기위한 부분.
        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            myMessage = myMessage.toString();
            try {
                socket = new Socket(dstAddress, dstPort);
                //송신
                OutputStream out = socket.getOutputStream();
                out.write(myMessage.getBytes());

                //수신
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();

   /*   notice:
     * inputStream.read() will block if no data return*/

                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    String msg = byteArrayOutputStream.toString()+"byteArrayOutputStream 이거 실행중";
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                response = "서버의 응답: " + response;
                //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // recieveText.setText(response);

            ip.setB3_msg(response);
            com_msg=response;
            String comsg = "서버의 응답: detech";
            String comsg2 = "서버의 응답: no people";
            if (comsg.equals(com_msg))
            {      but_num++;}
            else if(comsg2.equals(com_msg)){
                but_num++;
            }
            super.onPostExecute(result);
        }
    }//서버 끝

}
