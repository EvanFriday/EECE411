package tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class IpTools {

    public static String getIp() throws Exception {
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = null;

            in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ip = in.readLine();
            return ip;
    }
    public static InetAddress getInet(){
    	InetAddress inet = null;
    	try {
			inet =  InetAddress.getByName(getIp());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return inet;
    	
    }
    public static String getIpFromHostname(String hostname) throws Exception {  
        String ip = InetAddress.getByName(hostname).getHostAddress();
    	return ip;
}  
    public static String getHostnameFromIp(String ip) throws Exception {
    	String hostname = InetAddress.getByName(ip).getHostName();
    	return hostname;
    }
    
    
    
}
