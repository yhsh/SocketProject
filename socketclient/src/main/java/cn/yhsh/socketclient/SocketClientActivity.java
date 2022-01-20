package cn.yhsh.socketclient;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author DELL
 */
public class SocketClientActivity extends AppCompatActivity implements View.OnClickListener {

    private long num;
    int coreSize = Runtime.getRuntime().availableProcessors() + 2;
    int maxSize = coreSize * 2 + 1;
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(coreSize, maxSize, 3,
            TimeUnit.MINUTES, new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    private TextView tvReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btConnectSocket = findViewById(R.id.bt_connect_socket);
        Button btSendMsg = findViewById(R.id.bt_send_msg);
        tvReceiver = findViewById(R.id.tv_receiver);
        btConnectSocket.setOnClickListener(this);
        btSendMsg.setOnClickListener(this);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("cn.yhsh.socketserver", "cn.yhsh.socketserver.ServerService"));
        startService(intent);
    }

    private void sendMsgToServer() {
        poolExecutor.submit(() -> {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String ip = localHost.toString().split("/")[1];
//                String ip = "10.53.137.24";
                Socket socket = new Socket(ip, 9999);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF("新中国" + num);
                outputStream.flush();
                String s = inputStream.readUTF();
                runOnUiThread(() -> Toast.makeText(SocketClientActivity.this, "发送的数据为：" + s, Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private final Handler handler = new Handler(Looper.getMainLooper());

    private void receiverServerData() {
        System.setProperty("https.protocols", "TLSv1.2");
        poolExecutor.submit(() -> {
            try {
                String ip = "10.53.137.24";
                Socket socket = new Socket(ip, 9999);
                InetAddress inetAddress = socket.getInetAddress();
                String hostName = inetAddress.getHostName();
                String hostAddress = inetAddress.getHostAddress();
                Log.e("打印主机地址：", hostAddress + "--------主机名字：" + hostName);
                while (true) {
                    if (socket.isConnected()) {
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        String s = inputStream.readUTF();
                        handler.post(() -> tvReceiver.setText(s));
                        Log.e("SocketClientActivity：" + Thread.currentThread().getName(), "接收到的数据为：" + s);
//                        handler.post(() -> Toast.makeText(getApplicationContext(), "客户端接收到的数据为：" + s, Toast.LENGTH_SHORT).show());
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_connect_socket:
                //连接并接收服务端的数据
                receiverServerData();
                break;
            case R.id.bt_send_msg:
                //发送数据到服务端
                sendMsgToServer();
                break;
            default:
                break;
        }
        num++;
    }
}