package com.example.myapplication;

public class Local {
    private int id_local;
    private int sala;
    private String bloco;

    public Local(int sala, String bloco) {
        this.sala = sala;
        this.bloco = bloco;
    }

    public Local(int id_local, int sala, String bloco) {
        this.id_local = id_local;
        this.sala = sala;
        this.bloco = bloco;
    }

    public int getId() {
        return id_local;
    }

    public int getSala() {
        return sala;
    }

    public void setSala(int sala) {
        this.sala = sala;
    }

    public String getBloco() {
        return bloco;
    }

    public void setBloco(String bloco) {
        this.bloco = bloco;
    }

    @Override
    public String toString() {
        return "Bloco: " + bloco + 
               " | Sala: " + sala;
    }

    public String exibirInfoLocal() {
        return "Bloco: " + bloco + 
               " | Sala: " + sala;
    }
}
