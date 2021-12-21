
package loadbalancedcommunications;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class loadBalancer {
    
    
    DatagramSocket loadBalancer_socket;
    byte[] data_to_send = new byte[1024];
    byte[] received_data = new byte[1024];
    InetAddress server_ip = null;
    int[] server_port = new int[4];
    InetAddress client_ip = null;
    int client_port = 0;
    int[][] serverLoad = new int[4][100];
    
    public static void main(String[] args) throws SocketException, IOException {
        
        System.out.println("LOADBALANCER");
        loadBalancer x = new loadBalancer(9876);
        x.start();
        
        
    }
    
    public loadBalancer(int port) throws SocketException {
        
        this.loadBalancer_socket = new DatagramSocket(port);
        
    }
    
    public void AssignServer(char request) throws IOException{
        
        int emptiestServer = 0;
        int emptiestServersLoad = 100; // maximum
        int loadCounter = 0;
        for (int i = 0; i < 4; i++) {
            loadCounter = 0;
            for (int j = 0; j < 100; j++) {
                if (serverLoad[i][j] == 1) {
                    loadCounter++;
                }
            }
            if (loadCounter < emptiestServersLoad) {
                emptiestServersLoad = loadCounter;
                emptiestServer =i;
            }
        }// so this code will give me the emptiest server's index in our array
        
        if (emptiestServersLoad == 100) {
            String denied = "All servers are full, request denied.";
            data_to_send = denied.getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
            loadBalancer_socket.send(packet_to_send);
        }else{
        
            for (int k = 0; k < 100; k++) {
                if (serverLoad[emptiestServer][k] != 1) {
                    serverLoad[emptiestServer][k] = 1;
                    break;
                }           
            }
        
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            DataOutputStream dataout = new DataOutputStream(byteout);
        
            System.out.println("emptiest server:" +emptiestServer);
            System.out.println("client assigned to server with port number of : "+server_port[emptiestServer]);
        
            dataout.writeInt(server_port[emptiestServer]);
            data_to_send = byteout.toByteArray();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,client_ip,client_port);
            loadBalancer_socket.send(packet_to_send);
        }
        
    }
    public void start() throws IOException{
        int i = 0;
        while (true) {
            
            DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
            loadBalancer_socket.receive(received_packet);
            String received_string = new String(received_packet.getData());
            if (received_string.charAt(0)=='j') {
                server_ip = received_packet.getAddress();
                server_port[i] = received_packet.getPort();
                System.out.println("server joined with port number: "+server_port[i]);
                i++;                
            }else if (received_string.charAt(0)=='1') {
                client_ip = received_packet.getAddress();
                client_port=received_packet.getPort();
                AssignServer(received_string.charAt(0));
            }else if (received_string.charAt(0)=='2') {
                client_ip = received_packet.getAddress();
                client_port=received_packet.getPort();
                AssignServer(received_string.charAt(0));
            }else if (received_string.charAt(0)=='3') {
                client_ip = received_packet.getAddress();
                client_port=received_packet.getPort();
                AssignServer(received_string.charAt(0));
            }else if(received_string.charAt(0)=='W'){ // clears the 1 from array which indicates amount of work assigned to the server
                int serverToSearch = received_packet.getPort();
                int position = 4;// there is no fifth server so if somethings go wrong it will give an error instead of a wrong operation
                for (int j = 0; j < 4; j++) {
                    if (serverToSearch == server_port[j]) {
                        position = j;
                        break;
                    }
                }
                for (int k = 99; k >= 0; k--) {
                    if (serverLoad[position][k] == 1) {
                        serverLoad[position][k] = 0;
                        break;
                    }
                }
                
            }else{
                client_ip = received_packet.getAddress();
                client_port=received_packet.getPort();
                break;
            }
        }
        
        for (int j = 0; j < i; j++) {
            String close = "Close Servers";
            data_to_send = close.getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send,data_to_send.length,server_ip,server_port[j]);
            loadBalancer_socket.send(packet_to_send);
            
            DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
            loadBalancer_socket.receive(received_packet);
            String recieved_string = new String(received_packet.getData());
            System.out.println(recieved_string);
            
            for (int k = 0; k < received_data.length; k++) { // to reset the data
                received_data[k] =0;
            }
        }
        loadBalancer_socket.close();
    }
    
}
