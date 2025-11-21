package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.activities.CrudFilmeActivity;
import com.example.myapplication.activities.CrudLocalActivity;
import com.example.myapplication.activities.CrudSessaoActivity;


public class MenuAdm extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_admin);

        configurarBotoesMenu();
    }

    private void configurarBotoesMenu() {
        // Botão para gerenciar sessões
        Button btnGerSessoes = findViewById(R.id.gerSessoes);
        btnGerSessoes.setOnClickListener(v -> {
            startActivity(new Intent(MenuAdm.this, CrudSessaoActivity.class));
        });

        // Botão para gerenciar filmes
        Button btnGerFilmes = findViewById(R.id.gerFilmes);
        btnGerFilmes.setOnClickListener(v -> {
            startActivity(new Intent(MenuAdm.this, CrudFilmeActivity.class));
        });

        // Botão para gerenciar locais
        Button btnGerLocais = findViewById(R.id.gerLocais);
        btnGerLocais.setOnClickListener(v -> {
            startActivity(new Intent(MenuAdm.this, CrudLocalActivity.class));
        });

        // Botão para sair
        Button btnMenuSair = findViewById(R.id.menuSair);
        btnMenuSair.setOnClickListener(v -> {
            startActivity(new Intent(MenuAdm.this, MainActivity.class));
            finish();
        });
    }
}