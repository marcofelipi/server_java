/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author 20231PF.CC0023
 */
public class Server {

    public static void main(String[] args) {
        //System.out.println("Hello World!");
        final int porta = 8080;
        try{
            ServerSocket serversocket = new ServerSocket(porta); //reserva a porta
            System.out.println("Server está escutando!");
            while (true){
                System.out.println("Aguardando conexão..");
                Socket clientsocket = serversocket.accept(); //server PARA E ESPERA até um cliente tentar se conectar
                System.out.println("Cliente conectado: " + clientsocket.getInetAddress().getHostAddress()); //mostra ip do cliente
                System.out.println("Conexão com o cliente será fechada (teste inicial).");
                clientsocket.close();
            }
        } catch (IOException e){
            System.err.println("Erro no Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
