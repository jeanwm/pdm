package com.example.myapplication;

public class LocalModel {
    private int id_filme;
    private String titulo;
    private int genero;
    private int duracao;
    private int classificacao;

    // Construtor corrigido
    public LocalModel(String titulo, int genero, int duracao, int classificacao) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracao = duracao;
        this.classificacao = classificacao;
    }

    // Construtor adicional com ID
    public LocalModel(int id_filme, String titulo, int genero, int duracao, int classificacao) {
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

    public int getGenero() {
        return genero;
    }

    public void setGenero(int genero) {
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
        return "Título: " + titulo +
                " | Duração: " + duracao + "min" +
                " | Classificação: " + classificacao + " anos";
    }

    public String exibirInfoFilme() {
        // Método melhorado para mostrar o gênero como string
        String[] generos = {"", "Ação", "Anime", "Comédia", "Drama", "Ficção Científica", "Romance", "Suspense", "Terror"};
        String generoStr = (genero >= 1 && genero < generos.length) ? generos[genero] : "Desconhecido";

        return "Título: " + titulo + "\n" +
                "Gênero: " + generoStr + "\n" +
                "Duração: " + duracao + " minutos\n" +
                "Classificação: " + classificacao + " anos";
    }
}