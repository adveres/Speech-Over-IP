package speech_over_ip;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Receiver implements Runnable {

    ServerSocket serverSocket = null;
    InputStream in = null;
    DataInputStream dis = null;
    Thread thread = null;
    
    AudioBytePlayer abPlayer = new AudioBytePlayer();

    boolean keepListening = true;

    public Receiver(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        thread = null;
    }

    public void run() {
        Socket socket = null;
        InputStream is = null;
        int bufferSize = 0;

        try {
            socket = serverSocket.accept();
        } catch (IOException ex) {
            System.out.println("Can't accept client connection. ");
        }

        System.out.println("Accepted connection: " + socket);

        try {
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream.");
            System.exit(1);
        }

        byte[] receivedData = null;
        int count = 0;

        try {

            while (keepListening || (bufferSize = dis.readInt()) > -1) {
                bufferSize = dis.readInt();
                System.out.println("Read integer: " + bufferSize);
                if(bufferSize < 0){
                    continue;
                }
                receivedData = new byte[bufferSize];

                count = dis.read(receivedData);
                abPlayer.Play(receivedData);
                
                
                
                int[] x = Utils.byte_array_to_ints(receivedData);
                System.out.println("int arr len: " + x.length);
            }

            is.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.stop();
    }

    // public byte[] readBytes() throws IOException {
    // int len = dis.readInt();
    // byte[] data = new byte[len];
    // if (len > 0) {
    // dis.readFully(data);
    // }
    // return data;
    // }
}
