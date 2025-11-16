package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FilmeActivity extends AppCompatActivity {

    private EditText editTitulo, editDuracao, editClassificacao, editBuscar;
    private Spinner spinnerGenero;
    private Button btnAdicionar, btnExcluir, btnSalvar, btnCancelar;

    private FilmeDAO filmesDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_filme);

        // Inicializar DAO
        filmesDAO = new FilmeDAO(this);

        // Inicializar componentes
        inicializarComponentes();

        // Configurar eventos
        configurarEventos();
    }

    private void inicializarComponentes() {
        editTitulo = findViewById(R.id.filme_editTitulo);
        editDuracao = findViewById(R.id.filme_editDuracao);
        editClassificacao = findViewById(R.id.filme_editClassificacao);
        editBuscar = findViewById(R.id.filme_buscar);
        spinnerGenero = findViewById(R.id.sessao_spinnerGenero);

        btnAdicionar = findViewById(R.id.filme_btnAdicionar);
        btnExcluir = findViewById(R.id.filme_btnExcluir);
        btnSalvar = findViewById(R.id.filme_btnSalvar);
        btnCancelar = findViewById(R.id.filme_btnCancelar);
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarFilme();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirFilme();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarFilme();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Fecha a activity
            }
        });
    }

    private void adicionarFilme() {
        String titulo = editTitulo.getText().toString().trim();
        String duracaoStr = editDuracao.getText().toString().trim();
        String classificacaoStr = editClassificacao.getText().toString().trim();

        if (titulo.isEmpty() || duracaoStr.isEmpty() || classificacaoStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duracao = Integer.parseInt(duracaoStr);
            int classificacao = Integer.parseInt(classificacaoStr);
            int genero = spinnerGenero.getSelectedItemPosition(); // Assumindo que o spinner tem os gêneros

            FilmeModel filme = new FilmeModel(titulo, genero, duracao, classificacao);
            //long resultado = filmesDAO.cadastrarFilme(filme);

            /*if (resultado != -1) {
                limparCampos();
                Toast.makeText(this, "Filme cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            }*/

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duração e classificação devem ser números!", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirFilme() {
        String titulo = editBuscar.getText().toString().trim();

        if (titulo.isEmpty()) {
            Toast.makeText(this, "Digite um título para buscar!", Toast.LENGTH_SHORT).show();
            return;
        }

        /*int resultado = filmesDAO.excluirFilme(titulo);
        if (resultado > 0) {
            limparCampos();
        }*/
    }

    private void salvarFilme() {
        // Implementar lógica de atualização
        Toast.makeText(this, "Funcionalidade de salvar em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        editTitulo.setText("");
        editDuracao.setText("");
        editClassificacao.setText("");
        editBuscar.setText("");
        spinnerGenero.setSelection(0);
    }
}