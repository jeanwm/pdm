package com.example.myapplication;

import java.util.Date;

public class Sessao {
    private int id_sessao;
    private Date data;
    private String hora;
    private int local;
    private int filme;

    public Sessao(Date data, String hora, int local, int filme) {
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.filme = filme;
    }

    public Sessao(int id_sessao, Date data, String hora, int local, int filme) {
        this.id_sessao = id_sessao;
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.filme = filme;
    }

    public int getId() {
        return id_sessao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getLocal() {
        return local;
    }

    public void setLocal(int local) {
        this.local = local;
    }

    public int getFilme() {
        return filme;
    }

    public void setFilme(int filme) {
        this.filme = filme;
    }

    @Override
    public String toString() {
        return "Data: " + data + " | Hora: " + hora + " | Local: " + local + " | Filme: " + filme;
    }
}
