package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MenuAdm extends AppCompatActivity {

    // Listas para armazenar os dados (em uma aplicação real, isso viria de um banco de dados)
    private List<Filme> listaDeFilmes;
    private List<Local> listaDeLocais;
    private List<Sessao> listaDeSessoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_admin);

        // Inicializar as listas
        listaDeFilmes = new ArrayList<>();
        listaDeLocais = new ArrayList<>();
        listaDeSessoes = new ArrayList<>();

        // Configurar os botões do menu
        configurarBotoesMenu();
    }

    private void configurarBotoesMenu() {
        // Botão para gerenciar filmes
        Button btnGerFilmes = findViewById(R.id.gerFilmes);
        btnGerFilmes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usar o método estático para cadastrar filme
                MenuAdmHelper.cadastrarFilme(MenuAdm.this, listaDeFilmes);
            }
        });

        // Botão para gerenciar locais
        Button btnGerLocais = findViewById(R.id.gerLocais);
        btnGerLocais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Usar o método estático para cadastrar local
                MenuAdmHelper.cadastrarLocal(MenuAdm.this, listaDeLocais);
            }
        });

        // Botão para gerenciar sessões
        Button btnGerSessoes = findViewById(R.id.gerSessoes);
        btnGerSessoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar se há filmes e locais cadastrados antes de permitir criar sessões
                if (listaDeFilmes.isEmpty() || listaDeLocais.isEmpty()) {
                    Toast.makeText(MenuAdm.this,
                            "Cadastre pelo menos um filme e um local antes de criar sessões!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Usar o método estático para cadastrar sessão
                MenuAdmHelper.cadastrarSessao(MenuAdm.this, listaDeSessoes, listaDeFilmes, listaDeLocais);
            }
        });

        // Botão para sair
        Button btnMenuSair = findViewById(R.id.menuSair);
        btnMenuSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Voltar para a tela de login
                Intent intent = new Intent(MenuAdm.this, MainActivity.class);
                startActivity(intent);
                finish(); // Fecha a atividade atual
            }
        });
    }

    // Métodos para obter as listas (podem ser usados por outras atividades)
    public List<Filme> getListaDeFilmes() {
        return listaDeFilmes;
    }

    public List<Local> getListaDeLocais() {
        return listaDeLocais;
    }

    public List<Sessao> getListaDeSessoes() {
        return listaDeSessoes;
    }
}