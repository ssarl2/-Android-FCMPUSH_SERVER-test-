package test.ssarl.fcmserver;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAuqT1Aas:APA91bH8h_UTrMybRHzj-MR0UB8t7SGFXqyzQyu_3W9LcsY5LwhTw55zsuMtMIRqgBvvaSVDHuK6uW9-8GiYmgCudUlYfydM4noUlwWZ16s_N6XSBa_dFFH3geS7xhMOcU2d04xACEhc";
    private DatabaseReference mDatabase;
    int count=0;
    static int random;
    Button btnn;
    TextView tvv1, tvv2, tvv3;
    EditText ett1, ett2;
    String userCount;
    String validTime;

    ArrayList<String> data=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("gettoken").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {//파베에 있는 토큰 값 받기
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//데이터 전체를 받기위해 반복문 실행
                    data.add(snapshot.getValue().toString());//data배열에 넣기
                    Log.d("토큰 값 : " + count, data.get(count));
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btnn = (Button) findViewById(R.id.btn);
        ett1 = (EditText) findViewById(R.id.et1);
        ett2 = (EditText) findViewById(R.id.et2);
        tvv1 = (TextView) findViewById(R.id.tv1);
        tvv2 = (TextView) findViewById(R.id.tv2);
        tvv3 = (TextView) findViewById(R.id.tv3);

        btnn.setOnClickListener(this);



    }



    @Override
    public void onClick(View v) {
        userCount = data.get(0);//ett1.getText().toString();
        validTime = ett2.getText().toString();

        tvv1.setText(userCount);
        tvv2.setText(validTime);
        tvv3.setText("{\"userCount\":\"" + userCount + "\",\"validTime\":\"" + validTime + "\"}");
        (new Thread(this)).start();
    }


    @Override
    public void run() {
        try {
            Random rand = new Random();//랜덤유저 선택
            random = rand.nextInt(count);
            String mToken;
            mToken = data.get(random);//토큰저장하는 변수에 랜덤 토큰 값 넣기
            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText(random+".st");
            // FMC 메시지 생성 start
            JSONObject root = new JSONObject();

            JSONObject data = new JSONObject();
            data.put("userCount", userCount);
            data.put("validTime", validTime);
            data.put("title", getString(R.string.app_name));
            root.put("data", data);
            root.put("to", mToken);
            // FMC 메시지 생성 end
            URL Url = new URL(FCM_MESSAGE_URL);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-type", "application/json");
            OutputStream os = conn.getOutputStream();
            os.write(root.toString().getBytes("utf-8"));
            os.flush();
            conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}