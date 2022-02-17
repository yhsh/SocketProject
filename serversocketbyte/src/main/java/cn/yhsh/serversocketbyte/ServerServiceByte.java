package cn.yhsh.serversocketbyte;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

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
 * @date 2022/2/17 17:08
 * 用于接收byte字节的服务端
 */
public class ServerServiceByte extends Service {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Socket socket;
    private int port = 1111;
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
        Log.e("ServerServiceByte", "服务启动了onStart");
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("ServerServiceByte", "服务启动了onCreate");
        //下面for循环代表开启三个服务端socket端口分别为9999,10000,10001
        for (int i = 0; i < 3; i++) {
            poolExecutor.execute(() -> {
                try {
                    ServerSocket serverSocket = new ServerSocket(port++);
                    while (true) {
                        //阻塞方法
                        socket = serverSocket.accept();
                        InetAddress inetAddress = socket.getInetAddress();
                        Log.e("ServerServiceByte", "主机名字：" + inetAddress.getHostName() + "--主机地址：" + inetAddress.getHostAddress() + "--本地端口：" + socket.getLocalPort());
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        while (true) {
                            //方式一:读取字符串
//                                    String receive = inputStream.readUTF();
//                                    Log.e("ServerServiceByte：" + Thread.currentThread().getName(), "接收到的数据为：" + receive);
//                                    handler.post(() -> Toast.makeText(getApplicationContext(), "ServerServiceByte：" + receive, Toast.LENGTH_SHORT).show());
//                                    outputStream.writeUTF(receive);
                            //方式二：读取byte字节
                            int len;
                            byte[] bytes = new byte[1024];
                            while ((len = inputStream.read(bytes)) != -1) {
                                Log.e("ServerServiceByte：", "接收到的byte数据为：" + new String(bytes, 0, len));
                            }
                            outputStream.flush();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ServerServiceByte", "服务启动了onStartCommand");
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
