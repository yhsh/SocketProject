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
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

        Intent intentByte = new Intent();
        intentByte.setComponent(new ComponentName("cn.yhsh.serversocketbyte", "cn.yhsh.serversocketbyte.ServerServiceByte"));
        startService(intentByte);
    }

    private void sendMsgToServer() {
        sendPortData(9999);
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
                Log.e("?????????????????????", hostAddress + "--------???????????????" + hostName);
                while (true) {
                    if (socket.isConnected()) {
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        String s = inputStream.readUTF();
                        handler.post(() -> tvReceiver.setText(s));
                        Log.e("SocketClientActivity???" + Thread.currentThread().getName(), "????????????????????????" + s);
//                        handler.post(() -> Toast.makeText(getApplicationContext(), "?????????????????????????????????" + s, Toast.LENGTH_SHORT).show());
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
                //?????????????????????????????????
                receiverServerData();
                break;
            case R.id.bt_send_msg:
                //????????????????????????
                sendMsgToServer();
                break;
            default:
                break;
        }
    }

    public void sendMessage2(View view) {
        sendPortData(10000);
    }

    public void sendMessage3(View view) {
        sendPortData(10001);
    }

    private void sendPortData(int port) {
        poolExecutor.submit(() -> {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String ip = localHost.toString().split("/")[1];
                Socket socket = new Socket(ip, port);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream.writeUTF("?????????--?????????" + socket.getPort());
                outputStream.flush();
                String s = inputStream.readUTF();
                runOnUiThread(() -> Toast.makeText(SocketClientActivity.this, "?????????????????????" + s, Toast.LENGTH_LONG).show());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMessage4(View view) {
        poolExecutor.submit(() -> {
            try {
                InetAddress localHost = InetAddress.getLocalHost();
                String ip = localHost.toString().split("/")[1];
                Socket socket = new Socket(ip, 1111);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                String s = "????????????????????????1111?????????--?????????" + socket.getPort();
                outputStream.write(s.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}