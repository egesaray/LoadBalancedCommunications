
package loadbalancedcommunications;

import java.io.*;
import java.net.*;
import java.util.*;


public class Client  {
    
    String name;
    DatagramSocket client_socket;
    InetAddress server_ip;
    int server_port;
    byte[] data_to_send;
    byte[] received_data;
    byte[] recieved_port = new byte[1024];
    
    public static void main(String[] args) throws UnknownHostException,SocketException,IOException {
        
        Client c1 = new Client("c1",InetAddress.getLocalHost(),9876);
        c1.request();
    }
    

    public Client(String name,InetAddress server_ip, int server_port) throws SocketException,UnknownHostException{
        this.client_socket = new DatagramSocket();
        this.name=name;
        this.server_ip = server_ip;
        this.server_port = server_port;
        this.data_to_send = new byte[1024];
        this.received_data = new byte[1024];
    }

    public void setClient_socket(DatagramSocket client_socket) {
        this.client_socket = client_socket;
    }

    public void setServer_ip(InetAddress server_ip) {
        this.server_ip = server_ip;
    }

    public void setServer_port(int server_port) {
        this.server_port = server_port;
    }
    
    public void setServer(DatagramPacket recievedPort) throws IOException{
        String isFull = new String(recievedPort.getData());
        if (isFull.charAt(0) == 'A') {
            System.out.println(isFull);
            client_socket.close();
            System.exit(0);
        }else{
            ByteArrayInputStream byteIn = new ByteArrayInputStream(recievedPort.getData());
            DataInputStream dataIn = new DataInputStream(byteIn);
            int portno = dataIn.readInt();
            setServer_port(portno);
            System.out.println("Assigned to Server, Port of the server: "+portno);
        }
    }
    
    public void request() throws IOException{
        Scanner scn = new Scanner(System.in);
        
        System.out.print("Enter 1 for directory listing \nEnter 2 for"
                + "File transfer \nEnter 3 for computation \n"
                + "Anything else to close the servers \n"
                + ":  ");
        
        String choice = scn.next();
        
        if (choice.equals("1")) {
            data_to_send = "1".getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_send);
            // load balancer assigns a server and sends it's port number
            DatagramPacket recievedPort = new DatagramPacket(recieved_port,recieved_port.length);
            client_socket.receive(recievedPort);
            setServer(recievedPort);
            //*************************************************
            DatagramPacket packet_to_server = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_server);
            
            // option 1 directory listing
            while(true){
                DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
                client_socket.receive(received_packet);
                String recieved_string = new String(received_packet.getData());
                System.out.println(recieved_string);
                
                for (int i = 0; i < received_data.length; i++) { // to reset the data
                    received_data[i] =0;
                }
                
                if (recieved_string.charAt(0) =='*') {
                    client_socket.close();
                    break;
                }
            }
            
            
            
        }else if (choice.equals("2")) {
            
            data_to_send = "2".getBytes();
            
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_send);
            // load balancer assigns a server and sends it's port number
            DatagramPacket recievedPort = new DatagramPacket(recieved_port,recieved_port.length);
            client_socket.receive(recievedPort);
            setServer(recievedPort);
            //*************************************************
            DatagramPacket packet_to_server = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_server);
        
            //option 2 file download
            DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
            client_socket.receive(received_packet);
            File newFile = new File("downloadedFile.txt");// Not: normalde dosya adını da server gönderiyordu ancak gönderilen isimle dosya oluşturamadım.
                                                      // Çok fazla hata ile karşılaştım ve vaktimi çok götürdüğü için bu şekilde yapma kararı aldım.
            OutputStream out = new FileOutputStream("downloadedFile.txt");
            out.write(received_packet.getData());
            out.close();
            System.out.println("file downloaded as -->  "+"downloadedFile.txt");
            client_socket.close();
            //***************************
            
            
        }else if (choice.equals("3")) {
            data_to_send = "3".getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_send);
            // load balancer assigns a server and sends it's port number
            DatagramPacket recievedPort = new DatagramPacket(recieved_port,recieved_port.length);
            client_socket.receive(recievedPort);
            setServer(recievedPort);
            //*************************************************
            DatagramPacket packet_to_server = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_server);
            
            //option 3 computation
            while(true){
                DatagramPacket received_packet = new DatagramPacket(received_data, received_data.length);
                client_socket.receive(received_packet);
                String recieved_string = new String(received_packet.getData());
                System.out.println(recieved_string);
                
                for (int i = 0; i < received_data.length; i++) { // to reset the data
                    received_data[i] =0;
                }
                
                if (recieved_string.charAt(0) =='F') {
                    client_socket.close();
                    break;
                }
            }
            //***********************
        }else if(choice.charAt(0)== 'j'){
            System.out.println("You can't send this request..");
            client_socket.close();
        }else{
            data_to_send = choice.getBytes();
            DatagramPacket packet_to_send = new DatagramPacket(data_to_send, data_to_send.length, server_ip, server_port);
            client_socket.send(packet_to_send);
            System.out.println("Request sent to close servers");
        }
    }
    
}
