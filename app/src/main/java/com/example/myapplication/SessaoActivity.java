package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessaoActivity extends AppCompatActivity {

    private EditText editData, editHora;
    private Spinner spinnerFilme, spinnerLocal;
    private Button btnAdicionar, btnExcluir, btnSalvar, btnCancelar;

    private SessaoDAO sessaoDAO;
    private List<FilmeModel> listaFilmes;
    private List<LocalModel> listaLocais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_sessao);

        // Inicializar DAO
        sessaoDAO = new SessaoDAO(this);

        // Inicializar componentes
        inicializarComponentes();

        // Configurar eventos
        configurarEventos();

        // Carregar dados para os spinners
        carregarSpinners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sessaoDAO != null) {
            sessaoDAO.close();
        }
    }

    private void inicializarComponentes() {
        editData = findViewById(R.id.sessao_editData);
        editHora = findViewById(R.id.sessao_editHora);
        spinnerFilme = findViewById(R.id.sessao_spinnerFilme);
        spinnerLocal = findViewById(R.id.sessao_spinnerLocal);

        btnAdicionar = findViewById(R.id.sessao_btnAdicionar);
        btnExcluir = findViewById(R.id.sessao_btnExcluir);
        btnSalvar = findViewById(R.id.sessao_btnSalvar);
        btnCancelar = findViewById(R.id.sessao_btnCancelar);
    }

    private void carregarSpinners() {
        // Carregar filmes e locais para os spinners
        // Você precisará implementar a lógica para popular os spinners
        // com dados dos DAOs de Filme e Local
        Toast.makeText(this, "Implementar carregamento dos spinners", Toast.LENGTH_SHORT).show();
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adicionarSessao();
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirSessao();
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarSessao();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void adicionarSessao() {
        String dataStr = editData.getText().toString().trim();
        String hora = editHora.getText().toString().trim();

        if (dataStr.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Converter string para Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date data = sdf.parse(dataStr);

            if (data == null) {
                Toast.makeText(this, "Data inválida! Use o formato DD/MM/AAAA", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obter IDs dos spinners (implementar conforme sua lógica)
            int filmeId = 1; // Placeholder - implementar lógica do spinner
            int localId = 1; // Placeholder - implementar lógica do spinner

            SessaoModel sessao = new SessaoModel(data, hora, localId, filmeId);
            long resultado = sessaoDAO.cadastrarSessao(sessao);

            if (resultado != -1) {
                limparCampos();
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Data inválida! Use o formato DD/MM/AAAA", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirSessao() {
        String dataStr = editData.getText().toString().trim();
        String hora = editHora.getText().toString().trim();

        if (dataStr.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos para excluir!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Obter IDs dos spinners (implementar conforme sua lógica)
            int filmeId = 1; // Placeholder
            int localId = 1; // Placeholder

            // Converter string para Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date data = sdf.parse(dataStr);

            if (data == null) {
                Toast.makeText(this, "Data inválida!", Toast.LENGTH_SHORT).show();
                return;
            }

            int resultado = sessaoDAO.excluirSessao(data, hora, localId, filmeId);

            if (resultado > 0) {
                limparCampos();
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Data inválida! Use o formato DD/MM/AAAA", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarSessao() {
        // Implementar lógica de atualização
        Toast.makeText(this, "Funcionalidade de atualização em desenvolvimento", Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        editData.setText("");
        editHora.setText("");
        editData.requestFocus();
    }
}