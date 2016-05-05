package com.leocai.multidevicesalign;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.leocai.publiclibs.PublicConstants;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by leocai on 16-1-15.
 */
public class BleServer extends Observable {


    List<OutputStream> outs = new ArrayList<>();
    List<BluetoothSocket> sockets = new ArrayList<>();
    private BluetoothServerSocket mmServerSocket;


    private String fileName;

    public BleServer() {
        BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(PublicConstants.NAME_WEAR_DATA,
                    PublicConstants.WEAR_UUID_INSECURE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        BluetoothSocket socket = mmServerSocket.accept();
                        sockets.add(socket);
                        OutputStream out = socket.getOutputStream();
                        outs.add(out);
                        PrintWriter pw = new PrintWriter(out);
                        pw.write(fileName + "\n");
                        pw.flush();
                        setChanged();
                        notifyObservers(outs.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();


    }

    void sendStartCommands() {
        for (OutputStream out : outs) {
            try {
                out.write(new byte[]{1});
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendStopCommands() {
        for (OutputStream out : outs) {
            try {
                out.write(new byte[]{2});
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
