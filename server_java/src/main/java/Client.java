/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author marco
 */
public class Client implements Runnable {

    // --- ARMAZENAMENTOS COMPARTILHADOS ---
    private static final Map<String, Arquivo> arquivosEmMemoria = new HashMap<>();
    private static final Map<String, String> sessoesAtivas = new HashMap<>();

    static {
        arquivosEmMemoria.put("index.html", new Arquivo("index.html", "<html><body><h1>Bem-vindo!</h1></body></html>", "text/html"));
        arquivosEmMemoria.put("info.txt", new Arquivo("info.txt", "Servidor HTTP basico.", "text/plain"));
    }

    private final Socket socketCliente;

    public Client(Socket socketCliente) { this.socketCliente = socketCliente; }

    @Override
    public void run() {
        try (BufferedReader leitor = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
             OutputStream saida = socketCliente.getOutputStream()) {

            String linhaRequisicao = leitor.readLine();
            if (linhaRequisicao == null) return;
            
            // Lê o corpo da requisição (necessário para POST)
            Map<String, String> cabecalhos = new HashMap<>();
            String linhaHeader;
            while ((linhaHeader = leitor.readLine()) != null && !linhaHeader.isEmpty()) {
                String[] partes = linhaHeader.split(": ", 2);
                if (partes.length == 2) cabecalhos.put(partes[0], partes[1]);
            }
            String corpoRequisicao = "";
            if (cabecalhos.containsKey("Content-Length")) {
                int tamanho = Integer.parseInt(cabecalhos.get("Content-Length"));
                char[] buffer = new char[tamanho];
                leitor.read(buffer, 0, tamanho);
                corpoRequisicao = new String(buffer);
            }

            String metodo = linhaRequisicao.split(" ")[0];
            String caminho = linhaRequisicao.split(" ")[1];
            String respostaHttp = "";

            // ===== LÓGICA DE ROTEAMENTO DIRETA (MAIS SIMPLES DE APRESENTAR) =====
            
            if (metodo.equals("GET") && caminho.startsWith("/arquivos/")) {
                // --- LÓGICA GET ARQUIVO ---
                String nomeArquivo = caminho.substring("/arquivos/".length());
                Arquivo arquivo = arquivosEmMemoria.get(nomeArquivo);
                if (arquivo != null) {
                    respostaHttp = "HTTP/1.1 200 OK\r\nContent-Type: " + arquivo.getTipo() + "\r\n\r\n" + arquivo.getConteudo();
                } else {
                    respostaHttp = "HTTP/1.1 404 Not Found\r\n\r\n";
                }

            } else if (metodo.equals("POST") && caminho.equals("/login")) {
                // --- LÓGICA POST LOGIN ---
                String user = extrairValorJson(corpoRequisicao, "username");
                String pass = extrairValorJson(corpoRequisicao, "password");
                if ("admin".equals(user) && "senha123".equals(pass)) {
                    String token = UUID.randomUUID().toString();
                    sessoesAtivas.put(token, user);
                    String corpoJson = "{\"chave\": \"" + token + "\"}";
                    respostaHttp = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + corpoJson;
                } else {
                    respostaHttp = "HTTP/1.1 403 Forbidden\r\n\r\n";
                }

            } else if (metodo.equals("POST") && caminho.equals("/arquivos")) {
                // --- LÓGICA POST ARQUIVO ---
                String token = cabecalhos.get("Authorization");
                if (token != null && sessoesAtivas.containsKey(token)) {
                    String nome = extrairValorJson(corpoRequisicao, "nome");
                    String conteudo = extrairValorJson(corpoRequisicao, "conteudo");
                    String tipo = extrairValorJson(corpoRequisicao, "tipo");
                    arquivosEmMemoria.put(nome, new Arquivo(nome, conteudo, tipo));
                    respostaHttp = "HTTP/1.1 201 Created\r\n\r\n";
                } else {
                    respostaHttp = "HTTP/1.1 401 Unauthorized\r\n\r\n";
                }
            
            } else {
                // --- RESPOSTA PADRÃO PARA OUTRAS ROTAS ---
                respostaHttp = "HTTP/1.1 404 Not Found\r\n\r\n";
            }

            saida.write(respostaHttp.getBytes("UTF-8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mantemos este utilitário pois ele é pequeno e reutilizado
    private String extrairValorJson(String json, String chave) {
        try {
            String busca = "\"" + chave + "\": \"";
            int inicio = json.indexOf(busca) + busca.length();
            int fim = json.indexOf("\"", inicio);
            return json.substring(inicio, fim);
        } catch (Exception e) {
            return null;
        }
    }
}