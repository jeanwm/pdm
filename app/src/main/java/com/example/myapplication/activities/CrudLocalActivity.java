package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.LocalDAO;
import com.example.myapplication.models.LocalModel;

public class CrudLocalActivity extends AppCompatActivity {

    private EditText editSala, editBloco, editBuscar;
    private Button btnSalvar, btnCancelar, btnExcluir, btnAdicionar;
    private LocalDAO localDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_local);

        localDAO = new LocalDAO(this);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        editSala = findViewById(R.id.local_editSala);
        editBloco = findViewById(R.id.local_editBloco);
        editBuscar = findViewById(R.id.local_buscar);

        btnSalvar = findViewById(R.id.local_btnSalvar);
        btnCancelar = findViewById(R.id.local_btnCancelar);
        btnExcluir = findViewById(R.id.local_btnExcluir);
        btnAdicionar = findViewById(R.id.local_btnAdicionar);
    }

    private void configurarEventos() {
        btnSalvar.setOnClickListener(v -> salvarLocal());
        btnAdicionar.setOnClickListener(v -> salvarLocal());

        btnCancelar.setOnClickListener(v -> finish());

        btnExcluir.setOnClickListener(v -> {
            Toast.makeText(this, "Implementar Exclusão por ID", Toast.LENGTH_SHORT).show();
        });
    }

    private void salvarLocal() {
        String salaStr = editSala.getText() != null ? editSala.getText().toString().trim() : "";
        String bloco = editBloco.getText() != null ? editBloco.getText().toString().trim() : "";

        if (salaStr.isEmpty() || bloco.isEmpty()) {
            Toast.makeText(this, "Preencha Sala e Bloco", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int sala = Integer.parseInt(salaStr);

            LocalModel local = new LocalModel(sala, bloco);
            new SalvarLocalTask().execute(local);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "A sala deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirLocalPorId() {
        String idStr = editBuscar.getText() != null ? editBuscar.getText().toString().trim() : "";
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Informe o ID do local a ser excluído", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idLocal = Integer.parseInt(idStr);
            new ExcluirLocalTask().execute(idLocal);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "O ID deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private class SalvarLocalTask extends AsyncTask<LocalModel, Void, Boolean> {
        @Override
        protected Boolean doInBackground(LocalModel... locals) {
            try {
                long result = localDAO.inserirLocal(locals[0]);
                return result != -1;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudLocalActivity.this, "Local salvo com sucesso", Toast.LENGTH_SHORT).show();
                editSala.setText("");
                editBloco.setText("");
                editBuscar.setText("");
                editSala.requestFocus();
            } else {
                Toast.makeText(CrudLocalActivity.this, "Erro ao salvar local (Verifique se já existe)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ExcluirLocalTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... ids) {
            if (ids.length == 0) return false;
            int idLocal = ids[0];

            return localDAO.excluirLocal(idLocal);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudLocalActivity.this, "Local excluído com sucesso", Toast.LENGTH_SHORT).show();
                editSala.setText("");
                editBloco.setText("");
                editBuscar.setText("");
            } else {
                Toast.makeText(CrudLocalActivity.this, "Erro ao excluir local", Toast.LENGTH_SHORT).show();
            }
        }
    }
}