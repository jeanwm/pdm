package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.FilmeDAO;
import com.example.myapplication.models.FilmeModel;

import java.lang.ref.WeakReference;

public class CrudFilmeActivity extends AppCompatActivity {

    private EditText editNome, editDuracao, editGenero, editClassificacao, editBuscar;
    private Button btnSalvar, btnCancelar, btnExcluir, btnBuscar;
    private FilmeDAO filmeDAO;

    private FilmeModel filmeEmEdicao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_filme);

        filmeDAO = new FilmeDAO(this);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.filme_editTitulo);
        editDuracao = findViewById(R.id.filme_editDuracao);
        editGenero = findViewById(R.id.filme_editGenero);
        editClassificacao = findViewById(R.id.filme_editClassificacao);
        editBuscar = findViewById(R.id.filme_buscar);

        btnSalvar = findViewById(R.id.filme_btnSalvar);
        btnCancelar = findViewById(R.id.filme_btnCancelar);
        btnExcluir = findViewById(R.id.filme_btnExcluir);
        btnBuscar = findViewById(R.id.filme_btnBuscar);
    }

    private void configurarEventos() {
        btnSalvar.setOnClickListener(v -> salvarFilme());
        btnCancelar.setOnClickListener(v -> finish());

        btnBuscar.setOnClickListener(v -> buscarFilmePorId());

        btnExcluir.setOnClickListener(v -> excluirFilmePorId());
    }

    private void buscarFilme() {
        String idStr = editBuscar.getText().toString();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Informe o ID do filme", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            new BuscarFilmeTask(this).execute(id);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private void preencherCampos(FilmeModel filme) {
        editNome.setText(filme.getTitulo());
        editDuracao.setText(String.valueOf(filme.getDuracao()));
        editGenero.setText(filme.getGenero());
        editClassificacao.setText(String.valueOf(filme.getClassificacao()));
        Toast.makeText(this, "Filme '" + filme.getTitulo() + "' carregado para edição.", Toast.LENGTH_SHORT).show();
    }

    private void salvarFilme() {
        String nome = editNome.getText() != null ? editNome.getText().toString().trim() : "";
        String duracaoStr = editDuracao.getText() != null ? editDuracao.getText().toString().trim() : "";
        String genero = editGenero.getText() != null ? editGenero.getText().toString().trim() : "";
        String classificacaoStr = editClassificacao.getText() != null ? editClassificacao.getText().toString().trim() : "";

        if (nome.isEmpty() || duracaoStr.isEmpty()) {
            Toast.makeText(this, "Preencha Nome e Duração", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duracao = Integer.parseInt(duracaoStr);
            int classificacao = Integer.parseInt(classificacaoStr);

            if (filmeEmEdicao == null) {
                filmeEmEdicao = new FilmeModel(nome, genero, duracao, classificacao);
            } else {
                filmeEmEdicao.setTitulo(nome);
                filmeEmEdicao.setGenero(genero);
                filmeEmEdicao.setDuracao(duracao);
                filmeEmEdicao.setClassificacao(classificacao);
            }

            new SalvarFilmeTask(this).execute(filmeEmEdicao);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duração e Classificação devem ser números!", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirFilmePorId() {
        String idStr = editBuscar.getText() != null ? editBuscar.getText().toString().trim() : "";
        if (idStr.isEmpty()) {
            Toast.makeText(this,"Digite o ID no campo de busca.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idFilme = Integer.parseInt(idStr);
            new ExcluirFilmeTask(this).execute(idFilme);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarFilmePorId() {
        String idStr = editBuscar.getText() != null ? editBuscar.getText().toString().trim() : "";
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Digite o ID no campo de busca.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idFilme = Integer.parseInt(idStr);
            new BuscarFilmeTask(this).execute(idFilme);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private class SalvarFilmeTask extends AsyncTask<FilmeModel, Void, Boolean> {
        private final WeakReference<CrudFilmeActivity> activityRef;
        private boolean isUpdate = false;

        SalvarFilmeTask(CrudFilmeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }


        @Override
        protected Boolean doInBackground(FilmeModel... filmes) {
            FilmeModel filme = filmes[0];
            try {
                if (filme.getId()>0) { // ID > 0 significa que é uma atualização
                    isUpdate = true;
                    int linhasAfetadas = filmeDAO.atualizarFilme(filme);
                    return linhasAfetadas > 0;
                } else { // Se não houver ID ou for 0, é uma inserção
                    long id = filmeDAO.inserirFilme(filme);
                    if (id != -1) {
                        filme.setId((int) id);
                    }
                    return id != -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean sucesso) {
            CrudFilmeActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return; // Evita execução se a Activity não existir mais
            }

            // CORREÇÃO: Usar a referência segura da Activity
            if (sucesso) {
                String msg = isUpdate ? "Filme atualizado com sucesso!" : "Filme salvo com sucesso!";
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                activity.limparCampos(); // Chamar métodos da Activity através da referência
            } else {
                Toast.makeText(activity, "Erro ao salvar filme.", Toast.LENGTH_SHORT).show();
            }
        }

//        @Override
//        protected void onPostExecute(Boolean sucesso) {
//            if (sucesso) {
//                String msg = isUpdate ? "Filme atualizado com sucesso!" : "Filme salvo com sucesso!";
//                Toast.makeText(CrudFilmeActivity.this, msg, Toast.LENGTH_SHORT).show();
//                limparCampos();
//            } else {
//                Toast.makeText(CrudFilmeActivity.this, "Erro ao salvar filme.", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private class ExcluirFilmeTask extends AsyncTask<Integer, Void, Boolean> {
        private final WeakReference<CrudFilmeActivity> activityRef;

        ExcluirFilmeTask(CrudFilmeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(Integer... ids) {
            if (ids.length == 0) return false;
            int idFilme = ids[0];
            try {
                return filmeDAO.excluirFilme(idFilme);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            CrudFilmeActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (success) {
                Toast.makeText(activity, "Filme excluído com sucesso!", Toast.LENGTH_SHORT).show();
                activity.limparCampos();
            } else {
                Toast.makeText(activity, "Erro ao excluir. ID não encontrado.", Toast.LENGTH_SHORT).show();
            }
        }

//        @Override
//        protected void onPostExecute(Boolean success) {
//            if (success) {
//                Toast.makeText(CrudFilmeActivity.this, "Filme excluído com sucesso!", Toast.LENGTH_SHORT).show();
//                limparCampos();
//            } else {
//                Toast.makeText(CrudFilmeActivity.this, "Erro ao excluir. ID não encontrado.", Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private class BuscarFilmeTask extends AsyncTask<Integer, Void, FilmeModel> {
        private final WeakReference<CrudFilmeActivity> activityRef;

        BuscarFilmeTask(CrudFilmeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected FilmeModel doInBackground(Integer... ids) {
            if (ids.length == 0) return null;
            int idFilme = ids[0];
            return filmeDAO.buscarFilmePorId(idFilme);
        }

        @Override
        protected void onPostExecute(FilmeModel filme) {
            CrudFilmeActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            // CORREÇÃO: Acesso aos métodos da Activity
            if (filme != null) {
                activity.filmeEmEdicao = filme;
                activity.editNome.setText(filme.getTitulo());
                activity.editDuracao.setText(String.valueOf(filme.getDuracao()));
                activity.editGenero.setText(filme.getGenero());
                activity.editClassificacao.setText(String.valueOf(filme.getClassificacao()));
                Toast.makeText(activity, "Filme carregado para edição.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Filme com ID não encontrado.", Toast.LENGTH_SHORT).show();
                activity.limparCampos();
            }
        }

//        @Override
//        protected void onPostExecute(FilmeModel filme) {
//            if (filme != null) {
//                filmeEmEdicao = filme;
//                editNome.setText(filme.getTitulo());
//                editDuracao.setText(String.valueOf(filme.getDuracao()));
//                editGenero.setText(filme.getGenero());
//                editClassificacao.setText(String.valueOf(filme.getClassificacao()));
//                Toast.makeText(CrudFilmeActivity.this, "Filme carregado para edição.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(CrudFilmeActivity.this, "Filme com ID não encontrado.", Toast.LENGTH_SHORT).show();
//                limparCampos();
//            }
//        }
    }

    private void limparCampos() {
        editNome.setText("");
        editDuracao.setText("");
        editGenero.setText("");
        editClassificacao.setText("");
        editBuscar.setText("");
        editNome.requestFocus();

        filmeEmEdicao = null;

        btnSalvar.setText("Salvar");
    }
}