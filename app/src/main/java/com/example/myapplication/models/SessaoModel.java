package com.example.myapplication.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SessaoModel {
    private int id_sessao;
    private Date data;
    private String hora;
    private int local;
    private int filme;

    public SessaoModel(Date data, String hora, int local, int filme) {
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.filme = filme;
    }

    public SessaoModel(int id_sessao, Date data, String hora, int local, int filme) {
        this.id_sessao = id_sessao;
        this.data = data;
        this.hora = hora;
        this.local = local;
        this.filme = filme;
    }

    public int getId() {
        return id_sessao;
    }

    public void setId(int id_sessao) {
        this.id_sessao = id_sessao;
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

    // Método para converter Date para String no formato SQL
    public String getDataString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(data);
    }

    // Método para converter String para Date
    public static Date stringToDate(String dataString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Data: " + sdf.format(data) + " | Hora: " + hora + " | Local ID: " + local + " | Filme ID: " + filme;
    }

    public String exibirInfoSessao() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return "Data: " + sdf.format(data) + "\n" +
                "Hora: " + hora + "\n" +
                "Local ID: " + local + "\n" +
                "Filme ID: " + filme;
    }
}