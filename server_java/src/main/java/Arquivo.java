/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author marco
 */
public class Arquivo {
    private final String nome;
    private final String conteudo;
    private final String tipo;
    
    public Arquivo (String nome, String conteudo, String tipo){
        this.nome = nome;
        this.conteudo = conteudo;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getTipo() {
        return tipo;
    }
    
    
}
