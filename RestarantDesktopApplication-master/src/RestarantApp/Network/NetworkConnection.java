package RestarantApp.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkConnection {
    public static NetworkChangeListener networkChangeListener;
    public NetworkConnection(NetworkChangeListener networkChangeListener)
    {
        this.networkChangeListener = networkChangeListener;
        networkDetected();
    }

        public void networkDetected()
        {
            Thread timerc = new Thread()
            {
                @Override
                public void run() {
                    for (;;)
                    {
                        Socket socket = new Socket();
                        InetSocketAddress inetSocketAddress = new InetSocketAddress("www.google.com",80);

                        try {
                            socket.connect(inetSocketAddress);
                            networkChangeListener.Networkchanged(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                            networkChangeListener.Networkchanged(false);
                        }
                        try {
                            sleep(1000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            };
            timerc.start();
        }
}
