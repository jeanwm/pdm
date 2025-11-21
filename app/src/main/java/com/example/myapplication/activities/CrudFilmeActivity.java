package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.FilmeDAO;
import com.example.myapplication.models.FilmeModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CrudFilmeActivity extends AppCompatActivity {

    // UI
    private ExtendedFloatingActionButton btnAdicionar;
    private ImageButton btnVoltar;
    private RecyclerView recyclerViewFilmes;
    private View emptyState;

    // Modal UI
    private MaterialCardView modalCadastro;
    private View modalOverlay;
    private TextInputEditText editTitulo, editGenero, editDuracao, editClassificacao;
    private Button btnSalvarModal, btnCancelarModal;

    private FilmeDAO filmeDAO;
    private List<FilmeModel> listaFilmes;
    private FilmeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_filme);

        filmeDAO = new FilmeDAO(this);

        inicializarComponentes();
        configurarEventos();
        setupRecyclerView();

        // Carregar dados
        new CarregarFilmesTask().execute();
    }

    private void inicializarComponentes() {
        btnAdicionar = findViewById(R.id.filme_btnAdicionar);
        btnVoltar = findViewById(R.id.btnVoltar);
        recyclerViewFilmes = findViewById(R.id.recyclerViewFilmes);
        emptyState = findViewById(R.id.emptyStateFilmes);

        // Modal
        modalCadastro = findViewById(R.id.modalCadastroFilme);
        modalOverlay = findViewById(R.id.modalOverlayFilme);
        editTitulo = findViewById(R.id.modal_editTitulo);
        editGenero = findViewById(R.id.modal_editGenero);
        editDuracao = findViewById(R.id.modal_editDuracao);
        editClassificacao = findViewById(R.id.modal_editClassificacao);
        btnSalvarModal = findViewById(R.id.modal_btnSalvar);
        btnCancelarModal = findViewById(R.id.modal_btnCancelar);
    }

    private void setupRecyclerView() {
        recyclerViewFilmes.setLayoutManager(new LinearLayoutManager(this));
        listaFilmes = new ArrayList<>();
        adapter = new FilmeAdapter(listaFilmes);
        recyclerViewFilmes.setAdapter(adapter);
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(v -> abrirModal());
        btnVoltar.setOnClickListener(v -> finish());
        modalOverlay.setOnClickListener(v -> fecharModal());
        btnCancelarModal.setOnClickListener(v -> fecharModal());
        btnSalvarModal.setOnClickListener(v -> salvarFilme());
    }

    private void abrirModal() {
        editTitulo.setText("");
        editGenero.setText("");
        editDuracao.setText("");
        editClassificacao.setText("");

        modalCadastro.setVisibility(View.VISIBLE);
        modalOverlay.setVisibility(View.VISIBLE);
        modalCadastro.setAlpha(0f);
        modalCadastro.animate().alpha(1f).setDuration(200).start();
    }

    private void fecharModal() {
        modalCadastro.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> {
                    modalCadastro.setVisibility(View.GONE);
                    modalOverlay.setVisibility(View.GONE);
                })
                .start();
    }

    private void salvarFilme() {
        String titulo = editTitulo.getText().toString().trim();
        String genero = editGenero.getText().toString().trim();
        String duracaoStr = editDuracao.getText().toString().trim();
        String classStr = editClassificacao.getText().toString().trim();

        if (titulo.isEmpty() || duracaoStr.isEmpty()) {
            Toast.makeText(this, "Preencha Título e Duração", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int duracao = Integer.parseInt(duracaoStr);
            int classificacao = classStr.isEmpty() ? 0 : Integer.parseInt(classStr);

            FilmeModel novoFilme = new FilmeModel(titulo, genero, duracao, classificacao);
            new SalvarFilmeTask().execute(novoFilme);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Use apenas números em Duração/Classificação", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirFilme(int idFilme) {
        new ExcluirFilmeTask().execute(idFilme);
    }

    private void verificarListaVazia() {
        boolean vazia = listaFilmes == null || listaFilmes.isEmpty();
        emptyState.setVisibility(vazia ? View.VISIBLE : View.GONE);
        recyclerViewFilmes.setVisibility(vazia ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (modalCadastro.getVisibility() == View.VISIBLE) {
            fecharModal();
        } else {
            super.onBackPressed();
        }
    }

    // ================== ASYNC TASKS ==================

    private class CarregarFilmesTask extends AsyncTask<Void, Void, List<FilmeModel>> {
        @Override
        protected List<FilmeModel> doInBackground(Void... voids) {
            return filmeDAO.listarFilmes();
        }

        @Override
        protected void onPostExecute(List<FilmeModel> filmes) {
            listaFilmes = filmes;
            adapter.atualizarLista(listaFilmes);
            verificarListaVazia();
        }
    }

    private class SalvarFilmeTask extends AsyncTask<FilmeModel, Void, Long> {
        @Override
        protected Long doInBackground(FilmeModel... filmes) {
            return filmeDAO.inserirFilme(filmes[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            if (result == -2) {
                Toast.makeText(CrudFilmeActivity.this, "Filme já existe!", Toast.LENGTH_SHORT).show();
            } else if (result > 0) {
                Toast.makeText(CrudFilmeActivity.this, "Filme salvo!", Toast.LENGTH_SHORT).show();
                fecharModal();
                new CarregarFilmesTask().execute();
            } else {
                Toast.makeText(CrudFilmeActivity.this, "Erro ao salvar.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ExcluirFilmeTask extends AsyncTask<Integer, Void, Integer> {
        @Override
        protected Integer doInBackground(Integer... ids) {
            return filmeDAO.excluirFilme(ids[0]);
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            if (resultado == 1) {
                Toast.makeText(CrudFilmeActivity.this, "Filme excluído.", Toast.LENGTH_SHORT).show();
                new CarregarFilmesTask().execute();
            } else if (resultado == -2) {
                Toast.makeText(CrudFilmeActivity.this, "Não é possível excluir um filme associado a uma sessão existente.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CrudFilmeActivity.this, "Erro ao excluir Filme ou Filme não encontrado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ================== ADAPTER ==================

    private class FilmeAdapter extends RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder> {
        private List<FilmeModel> lista;

        public FilmeAdapter(List<FilmeModel> lista) {
            this.lista = lista;
        }

        public void atualizarLista(List<FilmeModel> novaLista) {
            this.lista = novaLista;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public FilmeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_filme, parent, false);
            return new FilmeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FilmeViewHolder holder, int position) {
            FilmeModel filme = lista.get(position);

            holder.txtTitulo.setText(filme.getTitulo());
            holder.txtGenero.setText(filme.getGenero());
            holder.txtDuracao.setText(filme.getDuracao() + " min");
            holder.txtClassificacao.setText("+" + filme.getClassificacao() + " anos");

            holder.btnExcluir.setOnClickListener(v -> excluirFilme(filme.getId()));
        }

        @Override
        public int getItemCount() {
            return lista != null ? lista.size() : 0;
        }

        class FilmeViewHolder extends RecyclerView.ViewHolder {
            TextView txtTitulo, txtGenero, txtDuracao, txtClassificacao;
            ImageButton btnExcluir;

            public FilmeViewHolder(@NonNull View itemView) {
                super(itemView);
                txtTitulo = itemView.findViewById(R.id.txtTitulo);
                txtGenero = itemView.findViewById(R.id.txtGenero);
                txtDuracao = itemView.findViewById(R.id.txtDuracao);
                txtClassificacao = itemView.findViewById(R.id.txtClassificacao);
                btnExcluir = itemView.findViewById(R.id.btnExcluirFilme);
            }
        }
    }
}