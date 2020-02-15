package gst.study.yellowtv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import static gst.study.yellowtv.SettingActivity.ip;

public class SeatMapping extends AppCompatActivity {
    //이 자바 파일에서는 좌석 매핑을 위한 XML와 연결된 자바 코드입니다.
    public static String b3_uri = ip.getB3_ip();//수정
    static int m = 0;//드래그 시작, 정지하는 변수
    static int i = 0;//좌석 지정횟수 드래그할때마다 1씩 증가하여 횟수 증가.
    //이미지 불러오기
    private static final int REQUEST_CODE = 0;
    private ImageView imageView;
    //  EditText ed;
    Socket socket = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_mapping);
    }

    //menu부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapping_menu, menu);
        return true;
    }
    //메뉴 아이템 추가
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //이미지 불러오기
        imageView = findViewById(R.id.image1);
        // ed = (EditText)findViewById(R.id.et);
        // String ip_msg = ed.getText().toString();
        switch (item.getItemId()){
            // case R.id.ip_num:
            //ip입력하는 부분.
            //  ip_msg = ed.getText().toString();
            //  break;
            case R.id.menu1:
                //이미지 불러오기
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
                m=0;
                break;
            case R.id.menu2:
                //좌석 좌표 지정하기.
                m+=1;
                String msg = "m="+m;
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "좌표 설정 완료",Toast.LENGTH_LONG).show();
                break;

            case R.id.menu3:
                //이미지 보내기
                m=0;
                i=0;

                Toast.makeText(getApplicationContext(), "이미지 보내기 완료",Toast.LENGTH_LONG).show();

                break;

            case R.id.menu4:
                //start ROI 좌석 등록
                m=0;
                i=0;
                MyClientTask myClientTask1 = new MyClientTask(b3_uri, 8888,"StartROI");
                Toast.makeText(getApplicationContext(), "좌석 등록 완료",Toast.LENGTH_LONG).show();

                myClientTask1.execute();
                break;


        }
        return true;
    }

    //이미지 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imageView.setImageBitmap(img);
                }catch (Exception e){

                }

            }
            else if(resultCode ==RESULT_CANCELED){
                Toast.makeText(this,"사진선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// 드래그 이벤트 부분.
        TextView tv2= (TextView)findViewById(R.id.te2);
        TextView tv3= (TextView)findViewById(R.id.te3);
        TextView tv4= (TextView)findViewById(R.id.te4);

        //여기서 불러온 이미지에서 원하는 곳에 좌석을 매핑하는 부분.
        //처음에 터치한 곳과 마지막에 터치를 때는 곳 두 점을 서버에 보내는 방식.
        if(m>0) {
            int x =0;
            int y =0;

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = (int)event.getX();
                y = (int)event.getY();
                //int형인 x와 y를 ','와 더하여 String으로 만든 이유는
                //서버에 보낼 때 문자열로 인식한 다음 ',' 기준으로 x와y를 나누기 위해 사용하였습니다.
                String msg = x+","+y;
                //처음 터치한 지점에서 서버에 보내는 명령어입니다(아래)
                MyClientTask myClientTask = new MyClientTask(b3_uri, 8888, msg);

                myClientTask.execute();
                tv2.setText(msg);

                // Toast.makeText(getApplicationContext(), "Down", Toast.LENGTH_LONG).show();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                //  Toast.makeText(getApplicationContext(), "Move", Toast.LENGTH_LONG).show();
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                int end_x = (int)event.getX();
                int end_y = (int)event.getY();
                String msg = end_x+","+end_y;
                //터치하는 곳을 때는 지점에서 서버에 보내는 명령어입니다(아래)
                MyClientTask myClientTask = new MyClientTask(b3_uri, 8888, msg);
                myClientTask.execute();
                tv3.setText(msg);
                i+=1;
                tv4.setText(i+"번째 좌석지정중");
                // Toast.makeText(getApplicationContext(), "Up", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        else{

            return false;
        }

    }
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
                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */
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
            TextView tv4= (TextView)findViewById(R.id.te4);
            String comsg = "서버의 응답: detech";
            //이부분에서 경고창 띄우는것 하면 됨.
            /*ip.setB3_msg(response);*/
            if(comsg.equals(response)){
                tv4.setText(response);

            }
            super.onPostExecute(result);
        }
    }
}
