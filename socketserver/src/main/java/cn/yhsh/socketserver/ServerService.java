package cn.yhsh.socketserver;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiayiye5
 * @date 2022/1/14 17:08
 */
public class ServerService extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Socket socket;
    int coreSize = Runtime.getRuntime().availableProcessors() + 2;
    int maxSize = coreSize * 2 + 1;
    ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(coreSize, maxSize, 3,
            TimeUnit.MINUTES, new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("ServerService", "服务启动了onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ServerService", "服务启动了onCreate");
        poolExecutor.execute(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(9999);
                while (true) {
                    //阻塞方法
                    socket = serverSocket.accept();
                    InetAddress inetAddress = socket.getInetAddress();
                    Log.e("ServerService", "主机地址：" + inetAddress.getHostAddress());
                    Log.e("ServerService", "主机名字：" + inetAddress.getHostName());
                    poolExecutor.execute(() -> {
                        try {
                            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                            while (true) {
                                String receive = inputStream.readUTF();
                                Log.e("ServerService：" + Thread.currentThread().getName(), "接收到的数据为：" + receive);
                                handler.post(() -> Toast.makeText(getApplicationContext(), "ServerService接收到的数据为：" + receive, Toast.LENGTH_SHORT).show());
                                outputStream.writeUTF(receive);
                                outputStream.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ServerService", "服务启动了onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
