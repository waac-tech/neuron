package world.waac.neuron.transfer;

import android.content.Context;
import android.os.Build;
import android.util.Log;


import world.waac.neuron.globals.GlobalConstants;
import world.waac.neuron.globals.NotificationToast;
import world.waac.neuron.globals.Utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class ConnectionListener extends Thread {

    private int mPort;
    private Context mContext;
    private ServerSocket mServer;

    private boolean acceptRequests = true;

    public ConnectionListener(Context context, int port) {
        this.mContext = context;
        this.mPort = port;
    }

    @Override
    public void run() {
        try {
            Log.d("DXDX", Build.MANUFACTURER + ": conn listener: " + mPort);
            mServer = new ServerSocket(mPort);
            mServer.setReuseAddress(true);

            if (mServer != null && !mServer.isBound()) {
                mServer.bind(new InetSocketAddress(mPort));
            }

            Log.d("DDDD", "Inet4Address: " + Inet4Address.getLocalHost().getHostAddress());

            Socket socket = null;
            while (acceptRequests) {
                // this is a blocking operation
                socket = mServer.accept();
                handleData(socket.getInetAddress().getHostAddress(), socket.getInputStream());
            }
            Log.e("DXDX", Build.MANUFACTURER + ": Connection listener terminated. " +
                    "acceptRequests: " + acceptRequests);
            socket.close();
            socket = null;

        } catch (IOException e) {
            Log.e("DXDX", Build.MANUFACTURER + ": Connection listener EXCEPTION. " + e.toString());
            e.printStackTrace();
        }
    }

    private void handleData(String senderIP, InputStream inputStream) {
        try {
            byte[] input = Utility.getInputStreamByteArray(inputStream);

            ObjectInput oin = null;
            try {
                oin = new ObjectInputStream(new ByteArrayInputStream(input));
                ITransferable transferObject = (ITransferable) oin.readObject();

                //processing incoming data
                (new DataHandler(mContext, senderIP, transferObject)).process();

                oin.close();
                return;

            } catch (ClassNotFoundException cnfe) {
                Log.e("DDDD", cnfe.toString());
                cnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                if (oin != null) {
                    oin.close();
                }
            }

            //If control comes here that means the byte array sent is not the transfer object
            // that was expected. Processing it as a file (JPEG)

            String filePath = GlobalConstants.requestedFilePath;

            if (filePath != null && !filePath.equals("")) {
                filePath = GlobalConstants.getBasePath() + File.separator + Utility.getFileNameFromPath(filePath);
            } else {
                filePath = GlobalConstants.getBasePath() + File.separator + System.currentTimeMillis();
            }

            final File f = new File(filePath);

            File dirs = new File(f.getParent());
            if (!dirs.exists()) {
                boolean dirsSuccess = dirs.mkdirs();
            }
            boolean fileCreationSuccess = f.createNewFile();

            Utility.copyFile(new ByteArrayInputStream(input), new FileOutputStream(f));


            //opening the received file. (if exists)
            if (f.exists() && f.length() > 0) {


                NotificationToast.showToast(mContext, "file received");

                if (GlobalConstants.copyingDialog != null && GlobalConstants.copyingDialog.isShowing()) {
                    GlobalConstants.copyingDialog.dismiss();
                }


//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setData(Uri.parse("file://" + f.getAbsolutePath()));
//                mContext.startActivity(intent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tearDown() {
        acceptRequests = false;
    }
}
