package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LocalActivity extends AppCompatActivity {

    private EditText editSala, editBloco;
    private Button btnAdicionar, btnExcluir, btnSalvar, btnCancelar;

    private LocalDAO localDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_local); // Assumindo que o layout será crud_local.xml

        // Inicializar DAO
        localDAO = new LocalDAO(this);

        // Inicializar componentes
        inicializarComponentes();

        // Configurar eventos
        configurarEventos();
    }

    private void inicializarComponentes() {
        editSala = findViewById(R.id.local_editSala);
        editBloco = findViewById(R.id.local_editBloco);

        btnAdicionar = findViewById(R.id.local_btnAdicionar);
        btnExcluir = findViewById(R.id.local_btnExcluir);
        btnSalvar = findViewById(R.id.local_btnSalvar);
        btnCancelar = findViewById(R.id.local_btnCancelar);
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarLocal();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirLocal();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarLocal();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a activity
            }
        });
    }

    private void adicionarLocal() {
        String salaStr = editSala.getText().toString().trim();
        String bloco = editBloco.getText().toString().trim();

        if (salaStr.isEmpty() || bloco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int sala = Integer.parseInt(salaStr);
            LocalModel local = new LocalModel(sala, bloco);
            long resultado = localDAO.cadastrarLocal(local);

            if (resultado != -1) {
                limparCampos();
                Toast.makeText(this, "Local cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Sala deve ser um número!", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirLocal() {
        String salaStr = editSala.getText().toString().trim();
        String bloco = editBloco.getText().toString().trim();

        if (salaStr.isEmpty() || bloco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos para excluir!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int sala = Integer.parseInt(salaStr);
            int resultado = localDAO.excluirLocal(sala, bloco);

            if (resultado > 0) {
                limparCampos();
                Toast.makeText(this, "Local excluído com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Local não encontrado!", Toast.LENGTH_SHORT).show();
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Sala deve ser um número!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarLocal() {
        // Implementar lógica de atualização
        Toast.makeText(this, "Funcionalidade de salvar em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        editSala.setText("");
        editBloco.setText("");
    }
}