package com.example.myapplication.models;

public class FilmeModel {
    private int id_filme;
    private String titulo;
    private String genero;
    private int duracao;
    private int classificacao;

    // Construtor utilizado para a inserção de novos Filmes no Banco
    public FilmeModel(String titulo, String genero, int duracao, int classificacao) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracao = duracao;
        this.classificacao = classificacao;
    }

    // Construtor adicional com ID, utilizar quando for listar os filmes do Banco
    public FilmeModel(int id_filme, String titulo, String genero, int duracao, int classificacao) {
        this.id_filme = id_filme;
        this.titulo = titulo;
        this.genero = genero;
        this.duracao = duracao;
        this.classificacao = classificacao;
    }

    public int getId() {
        return id_filme;
    }

    public void setId(int id) {
        this.id_filme = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public int getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(int classificacao) {
        this.classificacao = classificacao;
    }

    @Override
    public String toString() {
        return titulo;
    }

    public String exibirInfoFilme() {
        return "Título: " + titulo + "\n" +
                "Gênero: " + genero + "\n" +
                "Duração: " + duracao + " minutos\n" +
                "Classificação: " + classificacao + " anos";
    }
}