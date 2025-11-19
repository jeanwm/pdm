package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.activities.CrudFilmeActivity;
import com.example.myapplication.activities.CrudLocalActivity;
import com.example.myapplication.activities.CrudSessaoActivity;
import com.example.myapplication.models.FilmeModel;
import com.example.myapplication.models.LocalModel;
import com.example.myapplication.models.SessaoModel;

import java.util.ArrayList;
import java.util.List;

public class MenuAdm extends AppCompatActivity {

    // Listas para armazenar os dados (em uma aplicação real, isso viria de um banco de dados)
    private List<FilmeModel> listaDeFilmes;
    private List<LocalModel> listaDeLocais;
    private List<SessaoModel> listaDeSessoes;

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
                // Abre a ActivityFilmes
                Intent intent = new Intent(MenuAdm.this, CrudFilmeActivity.class);
                startActivity(intent);
            }
        });

        // Botão para gerenciar locais
        Button btnGerLocais = findViewById(R.id.gerLocais);
        btnGerLocais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre a ActivityFilmes
                Intent intent = new Intent(MenuAdm.this, CrudLocalActivity.class);
                startActivity(intent);
            }
        });

        // Botão para gerenciar sessões
        Button btnGerSessoes = findViewById(R.id.gerSessoes);
        btnGerSessoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre a ActivityFilmes
                Intent intent = new Intent(MenuAdm.this, CrudSessaoActivity.class);
                startActivity(intent);
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
    public List<FilmeModel> getListaDeFilmes() {
        return listaDeFilmes;
    }

    public List<LocalModel> getListaDeLocais() {
        return listaDeLocais;
    }

    public List<SessaoModel> getListaDeSessoes() {
        return listaDeSessoes;
    }
}