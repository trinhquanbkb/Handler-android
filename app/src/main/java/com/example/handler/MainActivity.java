package com.example.handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    EditText editTextNumber;
    Button button;
    EditText editText;
    LinearLayout linearLayout;
    int n;
    //layoutParams là các thành phần con nằm bên trong Viewgroup
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    Random random = new Random();
    //handler là biến xử lý trong UI thread (luồng chính), có thể thao tác được với các view và lấy dữ liệu từ các background thread
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //khi luồng con sendMessage() thì ngay lập tức phương thức này sẽ nhận được msg.
            super.handleMessage(msg);
            //khi tiến trình kết thúc in ra END
            if(msg.obj=="END"){
                editText.setText("100%");
                Toast.makeText(MainActivity.this, "Kết thúc tiến trình", Toast.LENGTH_SHORT).show();
            }else{
                int percent = msg.arg1;
                int value = msg.arg2;
                //tạo ra một button mới để ném vào linearlayout trong scrollview
                Button btn = new Button(MainActivity.this);
                btn.setText(value+"");
                btn.setLayoutParams(layoutParams);
                linearLayout.addView(btn);
                //cập nhật lại percent trong edit text
                editText.setText(percent+"%");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControl();
        addDraw();
    }

    private void addDraw() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleButton();
            }
        });
    }

    private void handleButton() {
        //n sẽ lấy số button muốn tạo ra từ edittext
        n = Integer.parseInt(editTextNumber.getText().toString());
        //thread là một background thread và không thể thao tác trực tiếp được với view, dữ liệu xử lý ở đây sẽ được đẩy lên UI thread thông qua message
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<n; i++){
                    //lấy message từ UI Thread
                    Message msg = handler.obtainMessage();
                    int percent = i*100/n;
                    int value = random.nextInt(n);
                    //gán dữ liệu cho message msg
                    msg.arg1 = percent;
                    msg.arg2 = value;
                    handler.sendMessage(msg);
                    //mỗi lần truyền dữ liệu sẽ nghỉ 100 ms rồi mới thực hiện tiếp
                    SystemClock.sleep(100);
                }
                Message msg = handler.obtainMessage();
                msg.obj="END";
                handler.sendMessage(msg);
            }
        });
        //khởi tạo luồng con
        thread.start();
    }


    private void addControl() {
        textView = findViewById(R.id.textView);
        editTextNumber = findViewById(R.id.editTextNumber);
        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        linearLayout = findViewById(R.id.linearLayout);
    }
}