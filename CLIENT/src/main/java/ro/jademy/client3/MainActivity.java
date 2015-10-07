package ro.jademy.client3;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "CLIENT_3";
   // static final int SocketPORT = 4001;
    TextView infoip;
    TextView msg;
    EditText edit;
    EditText tAddress;
    EditText tPort;
    Button bCONECT;
    Button bSEND;
    Button bRECEIVE;
    String sentence;
    String modifiedSentence;
    Socket sendSocket = null;
//    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoip = (TextView) findViewById(R.id.infoip);
        msg = (TextView) findViewById(R.id.msg);
        edit = (EditText) findViewById(R.id.edit);
        tAddress = (EditText) findViewById(R.id.address);
        tPort = (EditText) findViewById(R.id.port);
        bCONECT = (Button) findViewById(R.id.bCONECT);
        bCONECT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyClientTaskA myClientTaskA = new MyClientTaskA();
                myClientTaskA.start();
                Log.d(TAG, "bCONECT");
            }
        });
        bRECEIVE = (Button) findViewById(R.id.bRECEIVE);
        bRECEIVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "bRECEIVE");
                MyClientTaskR myClientTaskR = new MyClientTaskR();
                myClientTaskR.execute();
            }
        });
        bSEND = (Button) findViewById(R.id.bSEND);
        bSEND.setOnClickListener(bConnectOnClickListener);

        infoip.setText(getIpAddress());
    }
    View.OnClickListener bConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "bSEND");
//            String hostName = tAddress.getText().toString();
//            int portNumber = Integer.parseInt(tPort.getText().toString());
            sentence = edit.getText().toString();
//            Log.d(TAG, "apas bConnect");
//            String tMsg = welcomeMsg.getText().toString();
//            if (tMsg.equals("")) {
//                tMsg = null;
//                Toast.makeText(MainActivity.this, "No Welcome Msg sent", Toast.LENGTH_LONG).show();
//            }
            MyClientTaskS myClientTaskS = new MyClientTaskS(
                    //tAddress.getText().toString(),
                    //Integer.parseInt(tPort.getText().toString()),
                    sentence);
//            cream multi AsyncTask
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                myClientTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            } else
            myClientTaskS.execute();
        }
    };
    private class MyClientTaskA extends Thread {
        @Override
        public void run() {
            try {
                String hostName = tAddress.getText().toString();
                int portNumber = Integer.parseInt(tPort.getText().toString());
                sendSocket = new Socket(hostName,portNumber);
                Log.d(TAG, "sendSocket");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class MyClientTaskS extends AsyncTask<Void, Void, Void> {

        String destAddress;
        int destPort;
        String msgToServer;

        MyClientTaskS(
                //String addr, int port,
                String msgTo) {
            //destAddress = addr;
            //destPort = port;
            msgToServer = msgTo;
        }
        protected Void doInBackground(Void... v) {
            Log.d(TAG, "doInBackground");
//            sendSocket = null;
//            BufferedReader inFromUser = null;
//            DataInputStream inFromServer = null;
            DataOutputStream outToServer = null;
            try {
//                String hostName = tAddress.getText().toString();
//                int portNumber = Integer.parseInt(tPort.getText().toString());
//                sendSocket = new Socket(hostName,portNumber);
/*                inFromUser = new BufferedReader(new InputStreamReader(System.in));*/
                Log.d(TAG, "socket");
                outToServer = new DataOutputStream(sendSocket.getOutputStream());
//                inFromServer = new DataInputStream(clientSocket.getInputStream());

                outToServer.writeUTF(msgToServer + '\n');
                outToServer.flush();
                Log.d(TAG, "write");
//                modifiedSentence = inFromServer.readUTF();
//                publishProgress("FROM SERVER: " + modifiedSentence);

//                clientSocket.close();
            } catch (UnknownHostException e) {
                e.printStackTrace();
//                modifiedSentence = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
//                modifiedSentence = "IOException: " + e.toString();
            } finally {
//                if (sendSocket != null) {
//                    try {
//                        sendSocket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
                if (outToServer != null) {
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                if (inFromServer != null) {
//                    try {
//                        inFromServer.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//            msg.setText(modifiedSentence);
            Log.d(TAG, "onPostExecute");
            super.onPostExecute(result);
        }
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//            Log.d(TAG, "metoda onProgressUpdate");
//            msg.setText(values[0] + "");
//        }

    }
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
    class MyClientTaskR extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            Log.d(TAG, "doInBack");
            DataInputStream inFromServer = null;
            try {
                //sendSocket = new Socket();
                inFromServer = new DataInputStream(sendSocket.getInputStream());
                if (inFromServer.available() > 0) {
                    modifiedSentence = inFromServer.readUTF();
                    Log.d(TAG, "READ");
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msg.setText("Received: " + modifiedSentence);
                        Log.d(TAG, "receptie");
                    }
                });
//                publishProgress("FROM SERVER: " + modifiedSentence);
//                Log.d(TAG, "PUBLISH");
            } catch (IOException e) {
                e.printStackTrace();
                modifiedSentence = "IOException: " + e.toString();
            } finally {
                if (inFromServer != null) {
                    try {
                        inFromServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
//            msg.setText(modifiedSentence);
            Log.d(TAG, "onPostExecute");
            super.onPostExecute(result);
        }
//        @Override
//        protected void onProgressUpdate(String... values) {
//            setProgressPercent(progress[0]);
//            super.onProgressUpdate(values);
//            Log.d(TAG, "metoda onProgressUpdate");
//            msg.setText(values[0] + "");
//        }
    }
}

