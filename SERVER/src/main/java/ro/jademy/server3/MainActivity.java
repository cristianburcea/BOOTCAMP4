package ro.jademy.server3;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "SERVER_3";
    static final int SocketServerPORT = 4001;
    EditText edit;
    EditText tAddress;
    EditText tPort;
    TextView info;
    TextView infoip;
    String clientSentence;
    String capitalizedSentence;
    TextView msg;
    Button bConect;
    Button bReceive;
    Button bSend;
    ServerSocket welcomeSocket;
    Socket conectionSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit = (EditText) findViewById(R.id.edit);
        tAddress = (EditText) findViewById(R.id.address);
        tPort = (EditText) findViewById(R.id.port);
        info = (TextView) findViewById(R.id.info);
        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        bConect = (Button) findViewById(R.id.bConnect);
        bConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread socketServerThreadA = new SocketServerThreadA();
                socketServerThreadA.start();
            }
        });
        bReceive = (Button) findViewById(R.id.bReceive);
        bReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "bRECEIVE");
                Thread socketServerThreadR = new SocketServerThreadR();
                socketServerThreadR.start();
            }
        });
        bSend = (Button) findViewById(R.id.bSend);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "bSEND");
//                String clientName = tAddress.getText().toString();
//                int clientNumber = Integer.parseInt(tPort.getText().toString());
                capitalizedSentence = edit.getText().toString();
                MyServerTaskS myServerTaskS = new MyServerTaskS(
                        //tAddress.getText().toString(),
                        //Integer.parseInt(tPort.getText().toString()),
                        capitalizedSentence);
                myServerTaskS.execute();
            }
        });

        infoip.setText(getIpAddress());
    }

    private class SocketServerThreadA extends Thread {
        @Override
        public void run() {
            try {
                welcomeSocket = new ServerSocket(SocketServerPORT);
                conectionSocket = welcomeSocket.accept();
                Log.d(TAG, "conectionSocket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SocketServerThreadR extends Thread {
//        static final int SocketServerPORT = 4001;
        @Override
        public void run() {
            DataInputStream inFronClient = null;

            try {
//                welcomeSocket = new ServerSocket(SocketServerPORT);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    Log.d(TAG, "run");
                        info.setText("I'm waiting here: " + welcomeSocket.getLocalPort());
                    }
                });

                Log.d(TAG, "socket");
                inFronClient = new DataInputStream(conectionSocket.getInputStream());
//                    outToClient = new DataOutputStream(conectionSocket.getOutputStream());
                if (inFronClient.available() > 0) {
                    clientSentence = inFronClient.readUTF();
                    Log.d(TAG, "client");
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText("Received: " + clientSentence);
                        Log.d(TAG, "receptie");
                    }
                });
//                    capitalizedSentence = edit.getText().toString();
//                    outToClient.writeUTF(capitalizedSentence);

//

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inFronClient != null) {
                    try {
                        inFronClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                if (conectionSocket != null) {
//                    try {
//                        conectionSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }
    class MyServerTaskS extends AsyncTask<Void, Void, Void> {
        int countS = 0;
        String destAddress;
        int destPort;
        String msgToClient;

        MyServerTaskS(
                //String addr, int port,
                String msgTo) {
//            destAddress = addr;
//            destPort = port;
            msgToClient = msgTo;
        }

        protected Void doInBackground(Void... v) {
            Log.d(TAG, "doInBackground");
            DataOutputStream outToClient = null;

            try {
                Log.d(TAG, "socket");
//                String clientName = tAddress.getText().toString();
//                int clientNumber = Integer.parseInt(tPort.getText().toString());
//                conectionSocket = new Socket(clientName, clientNumber);
                outToClient = new DataOutputStream(conectionSocket.getOutputStream());
                outToClient.writeUTF("Send #" + countS + ": " + msgToClient);
                outToClient.flush();
                Log.d(TAG, "write");
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText("Send #" + countS + ": " + capitalizedSentence);
                    }
                });
            } catch (UnknownHostException e) {
                e.printStackTrace();
//                modifiedSentence = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
//                modifiedSentence = "IOException: " + e.toString();
            } finally {
                if (outToClient != null) {
                    try {
                        outToClient.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//            msg.setText(modifiedSentence);
            Log.d(TAG, "onPostExecute");
            super.onPostExecute(result);
        }
    }


//    private  class SocketServerThreadS extends Thread {
//        int countS = 0;
//        String destAddress;
//        int destPort;
//        String msgToClient;
//
////        SocketServerThreadS(String addr, int port,String msgTo) {
////            destAddress = addr;
////            destPort = port;
////            msgToClient = msgTo;
////        }
//        @Override
//        public  void run() {
//            DataOutputStream outToClient = null;
////            socket = null;
//            try {
//                while (true) {
////                    String clientName = tAddress.getText().toString();
////                    int clientNumber = Integer.parseInt(tPort.getText().toString());
////                    socket = new Socket(clientName, clientNumber);
//                    Log.d(TAG, "socket");
//                    outToClient = new DataOutputStream(socket.getOutputStream());
//                    outToClient.writeUTF("Send #" + countS + ": " + msgToClient);
//                    outToClient.flush();
//                    Log.d(TAG, "write");
//                    MainActivity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            msg.setText("Send #" + countS + ": " + capitalizedSentence);
//                        }
//                    });
//                    countS++;
////                    if (message.contains("bye")) {
////                        dataOutputStream.close();
////                    }
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (outToClient != null) {
//                    try {
//                        outToClient.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
////                if (socket != null) {
////                    try {
////                        socket.close();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
//
////                final String errMsg = e.toString();
////                MainActivity.this.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        msg.setText(errMsg);
////                    }
////                });
//            }
//
//        }
//    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (welcomeSocket != null) {
//            try {
//                welcomeSocket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
