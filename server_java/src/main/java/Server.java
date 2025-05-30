/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author marco
 */

public class Server {

    public static void main(String[] args) {
        final int PORTA = 8081;

        try (ServerSocket servidorSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor HTTP Multithread escutando na porta " + PORTA + "...");

            while (true) {
                System.out.println("\nAguardando uma nova conexao de cliente...");
                
                // 1. O servidor aceita uma nova conexão de cliente.
                Socket socketCliente = servidorSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

                // 2. Cria uma nova instância do nosso tratador para este cliente específico.
                Client cliente = new Client(socketCliente);
                
                // 3. Cria uma nova Thread, entrega o tratador para ela, e a inicia.
                //    O servidor não espera a thread terminar. Ele volta imediatamente 
                //    para o `servidorSocket.accept()` para esperar o próximo cliente.
                Thread novaThread = new Thread(cliente);
                novaThread.start(); // Inicia a execução do método run() do tratador.
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
