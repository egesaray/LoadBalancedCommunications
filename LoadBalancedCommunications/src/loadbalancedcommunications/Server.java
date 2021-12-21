
package loadbalancedcommunications;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server implements Runnable {
    
    String name;
    DatagramSocket server_socket;
    byte[] data_to_send = new byte[1024];
    byte[] received_data = new byte[1024];
    InetAddress client_ip = null;
    int client_port = 0;
    
    public static void main(String[] args) throws SocketException {
        
        System.out.println("SERVER");
        
        Runnable server1 = new Server("server1");
        Thread s1 = new Thread(server1);
        Runnable server2 = new Server("server2");
        Thread s2 = new Thread(server2);
        Runnable server3 = new Server("server3");
        Thread s3 = new Thread(server3);
        Runnable server4 = new Server("server4");
        Thread s4 = new Thread(server4);
        
        s1.start();
        s2.start();
        s3.start();
        s4.start();
        
        
        
    }

    public Server(String name) throws SocketException {
        this.server_socket = new DatagramSocket();
        this.name=name;
        
    }
    
    public void sendCompletedAck() throws UnknownHostException, IOException{
        String string_to_send = "Work is Done";
        data_to_send = string_to_send.getBytes();
        DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,InetAddress.getLocalHost(),9876);
        server_socket.send(packet_to_send);
    }

    @Override
    public void run() {
        try {
            // join loadBalancer
            String joinLoadBalancerString = "join";
            byte[] joinData = joinLoadBalancerString.getBytes();
            DatagramPacket joinLoadBlancerPacket = new DatagramPacket(joinData,joinData.length,InetAddress.getLocalHost(),9876);
            server_socket.send(joinLoadBlancerPacket);
            
            
            while(true){
                DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
                server_socket.receive(received_packet);
                client_ip = received_packet.getAddress();
                client_port = received_packet.getPort();
                String received_string = new String(received_packet.getData());
                System.out.println(name+":  Client said --> " + received_string);
                
                
                if(received_string.charAt(0) == '1') {
                    
                    File f = new File("C:");// it is reading the project's files
                    String files[] = f.list();
                    for(String file_to_send : files){
                        
                        data_to_send = file_to_send.getBytes();
                        DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                        server_socket.send(packet_to_send);
                        
                    }
                    String string_to_send = "*****Finished*****";
                    data_to_send = string_to_send.getBytes();
                    DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    
                    sendCompletedAck();
                    
                }else if(received_string.charAt(0) == '2') {
                    
                    byte[] bytefile = new byte[1024];
                    InputStream input = new FileInputStream("deneme.txt");
                    input.read(bytefile);
                
                    DatagramPacket packet_to_send = new DatagramPacket(bytefile, bytefile.length, client_ip, client_port);
                    server_socket.send(packet_to_send);
                    
                    sendCompletedAck();
                    
                }else if(received_string.charAt(0) == '3') {
                    String progress = "Remaining Time : 40 seconds";
                    data_to_send = progress.getBytes();
                    DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    progress = "Remaining Time : 30 seconds...";
                    data_to_send = progress.getBytes();
                    packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    
                    progress = "Remaining Time : 20 seconds...";
                    data_to_send = progress.getBytes();
                    packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    
                    progress = "Remaining Time : 10 seconds...";
                    data_to_send = progress.getBytes();
                    packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        System.out.println(ex);
                    }
                    
                    progress = "Finished";
                    data_to_send = progress.getBytes();
                    packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
                    server_socket.send(packet_to_send);
                    
                    sendCompletedAck();
                    
                }else{break;}
            }
            
            String message = name+" is down";
            data_to_send = message.getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send, data_to_send.length, client_ip, client_port);
            server_socket.send(packet_to_send);
            server_socket.close();
            
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
}
