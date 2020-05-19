package it.course.myblog.config;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;


@Component
@Endpoint(id = "server-info")

public class ServerInfoActuatorEndpoint {

    @ReadOperation
    public List<String> getServerInfo() {
        List<String> serverInfo = new ArrayList<String>();
        try {
            serverInfo.add("Server IP Address : " + InetAddress.getLocalHost().getHostAddress());
            serverInfo.add("Host Name: " + InetAddress.getLocalHost().getHostName());
            serverInfo.add("Server OS : " + System.getProperty("os.name").toLowerCase());
            NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            serverInfo.add("Hardware Address : "+ sb.toString());
            serverInfo.add("Java Version : "+getJavaVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverInfo;
    }
    private int getJavaVersion() {
        String version = System.getProperty("java.version");
        if(version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if(dot != -1) { version = version.substring(0, dot); }
        } return Integer.parseInt(version);
    }

}
