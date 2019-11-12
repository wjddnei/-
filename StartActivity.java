package com.example.hoonkaotalk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoonkaotalk.Model.myInfo;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class StartActivity extends AppCompatActivity {
    TextView tv;
    Button btn_login, btn_register;
    EditText edit_id, edit_password;
    Socket socket;
    int result;
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tv=findViewById(R.id.tv);
        btn_login=findViewById(R.id.btn_login);
        btn_register=findViewById(R.id.btn_register);
        edit_id=findViewById(R.id.edit_id);
        edit_password=findViewById(R.id.edit_password);

        wSocket.connect(); //소켓 연결
        socket=wSocket.get();
        socket.on("loginResult", loginResult); //loginResult 핸들러

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_id=edit_id.getText().toString();
                String txt_password=edit_password.getText().toString();
                myId=txt_id;
                login(txt_id, txt_password);
            }
        }); //로그인 버튼 클릭 핸들러

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        }); //레지스터 버튼 클릭 핸들러
    }

    public void login(String id, String password){
        try{
            JSONObject loginInfo=new JSONObject(); //로그인 정보를 담은 JSON 객체 생성
            loginInfo.put("id", id);
            loginInfo.put("password", password);
            myId=id;
            socket.emit("login", loginInfo); //login 요청
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private Emitter.Listener loginResult=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject receivedData=(JSONObject)args[0]; //서버로 부터 받은 오브젝트 객체를 JSONOBJECT 객체로 캐스팅
            result=-1;

            try{
                result=receivedData.getInt("result");  //JSON객체에서 result라는 int 멤버값을 받음
            }catch(JSONException e){
                e.printStackTrace();
            }

            /*결과값에 따라 다른 토스트 메시지 출력*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch(result){
                        case 0:
                            Toast.makeText(StartActivity.this, "서버 에러", Toast.LENGTH_SHORT).show();
                            edit_id.setText("");
                            edit_password.setText("");
                            break;
                        case 1:
                            Toast.makeText(StartActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                            edit_id.setText("");
                            edit_password.setText("");
                            break;
                        case 2:
                            Toast.makeText(StartActivity.this, "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                            edit_id.setText("");
                            edit_password.setText("");
                            break;
                        case 3:
                            Intent intent=new Intent(StartActivity.this, MainActivity.class);
                            myInfo.setMyId(myId);
                            startActivity(intent);
                            finish();
                            break;
                        default:
                            Toast.makeText(StartActivity.this, "서버 에러", Toast.LENGTH_SHORT).show();
                            edit_id.setText("");
                            edit_password.setText("");
                            break;
                    }
                }
            });
        }
    };
}
