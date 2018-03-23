package RestarantApp.model;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
public class NetworkConnection {
    public static void main(String[] args) throws UnknownHostException, IOException{
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            System.out.println("Network Interface Name : [" + networkInterface.getDisplayName() + "]");
            System.out.println("Is It connected? : [" + networkInterface.isUp() + "]");
            for (InterfaceAddress i : networkInterface.getInterfaceAddresses()){
                System.out.println("Host Name : "+i.getAddress().getCanonicalHostName());
                System.out.println("Host Address : "+i.getAddress().getHostAddress());
            }
            System.out.println("----------------------");
        }
    }


}