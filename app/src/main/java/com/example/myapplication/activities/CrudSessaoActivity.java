// CrudSessaoActivity.java
package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.FilmeDAO;
import com.example.myapplication.database.LocalDAO;
import com.example.myapplication.database.SessaoDAO;
import com.example.myapplication.models.FilmeModel;
import com.example.myapplication.models.LocalModel;
import com.example.myapplication.models.SessaoModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrudSessaoActivity extends AppCompatActivity {

    private static final String TAG = "CrudSessaoActivity";

    // Componentes de UI
    private ExtendedFloatingActionButton btnAdicionar;
    private ImageButton btnVoltar;
    private Button btnSalvar, btnCancelar;
    private EditText editData, editHora;
    private Spinner spinnerLocal, spinnerFilme;
    private MaterialCardView modalCadastro;
    private View modalOverlay;
    private RecyclerView recyclerViewSessoes;
    private View emptyState;

    // DAOs
    private SessaoDAO sessaoDAO;
    private FilmeDAO filmeDAO;
    private LocalDAO localDAO;

    // Dados
    private List<FilmeModel> listaFilmes;
    private List<LocalModel> listaLocais;
    private List<SessaoModel> listaSessoes;
    private SessaoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_sessao);

        // Inicializa DAOs
        sessaoDAO = new SessaoDAO(this);
        filmeDAO = new FilmeDAO(this);
        localDAO = new LocalDAO(this);

        inicializarComponentes();
        configurarEventos();
        setupRecyclerView();

        // Carregar dados
        new CarregarDadosTask().execute();
    }

    private void inicializarComponentes() {
        btnAdicionar = findViewById(R.id.sessao_btnAdicionar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnSalvar = findViewById(R.id.sessao_btnSalvar);
        btnCancelar = findViewById(R.id.sessao_btnCancelar);
        modalCadastro = findViewById(R.id.modalCadastro);
        modalOverlay = findViewById(R.id.modalOverlay);
        recyclerViewSessoes = findViewById(R.id.recyclerViewSessoes);
        emptyState = findViewById(R.id.emptyState);
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(v -> abrirModal());
        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarSessao());
        btnCancelar.setOnClickListener(v -> fecharModal());
        modalOverlay.setOnClickListener(v -> fecharModal());
    }

    private void setupRecyclerView() {
        recyclerViewSessoes.setLayoutManager(new LinearLayoutManager(this));
        listaSessoes = new ArrayList<>();
        adapter = new SessaoAdapter(listaSessoes);
        recyclerViewSessoes.setAdapter(adapter);
    }

    private void abrirModal() {
        editData.setText("");
        editHora.setText("");
        if (spinnerFilme.getAdapter() != null) spinnerFilme.setSelection(0);
        if (spinnerLocal.getAdapter() != null) spinnerLocal.setSelection(0);

        modalCadastro.setVisibility(View.VISIBLE);
        modalOverlay.setVisibility(View.VISIBLE);

        modalCadastro.setAlpha(0f);
        modalCadastro.animate()
                .alpha(1f)
                .setDuration(200)
                .start();
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

    private void salvarSessao() {
        String dataStr = editData.getText().toString().trim();
        String hora = editHora.getText().toString().trim();

        if (dataStr.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha a data e hora.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaFilmes == null || listaLocais == null || listaFilmes.isEmpty() || listaLocais.isEmpty()) {
            Toast.makeText(this, "Dados de filmes/locais não carregados.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date data = sdf.parse(dataStr);

            int filmeIndex = spinnerFilme.getSelectedItemPosition();
            int localIndex = spinnerLocal.getSelectedItemPosition();

            if (filmeIndex < 0 || localIndex < 0) {
                Toast.makeText(this, "Selecione um filme e um local válidos.", Toast.LENGTH_SHORT).show();
                return;
            }

            FilmeModel filmeSelecionado = listaFilmes.get(filmeIndex);
            LocalModel localSelecionado = listaLocais.get(localIndex);

            SessaoModel novaSessao = new SessaoModel(data, hora, localSelecionado.getId(), filmeSelecionado.getId());

            new SalvarSessaoTask(novaSessao, filmeSelecionado, localSelecionado).execute();

        } catch (ParseException e) {
            Toast.makeText(this, "Formato de data inválido. Use DD/MM/AAAA.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao preparar sessão: " + e.getMessage());
            Toast.makeText(this, "Erro interno ao salvar sessão.", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirSessao(int idSessao) {
        new ExcluirSessaoTask().execute(idSessao);
    }

    private void verificarListaVazia() {
        boolean listaVazia = listaSessoes == null || listaSessoes.isEmpty();
        emptyState.setVisibility(listaVazia ? View.VISIBLE : View.GONE);
        recyclerViewSessoes.setVisibility(listaVazia ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (modalCadastro.getVisibility() == View.VISIBLE) {
            fecharModal();
        } else {
            super.onBackPressed();
        }
    }

    // ==================== AsyncTasks ====================

    private class CarregarDadosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            listaFilmes = filmeDAO.listarFilmes();
            listaLocais = localDAO.listarLocais();
            listaSessoes = sessaoDAO.listarSessoes();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Configurar Spinner de Filmes
            if (listaFilmes != null && !listaFilmes.isEmpty()) {
                List<String> titulosFilmes = new ArrayList<>();
                for (FilmeModel filme : listaFilmes) {
                    titulosFilmes.add(filme.getTitulo());
                }
                ArrayAdapter<String> adapterFilme = new ArrayAdapter<>(
                        CrudSessaoActivity.this,
                        android.R.layout.simple_spinner_item,
                        titulosFilmes);
                adapterFilme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerFilme.setAdapter(adapterFilme);
            }

            // Configurar Spinner de Locais
            if (listaLocais != null && !listaLocais.isEmpty()) {
                List<String> infoLocais = new ArrayList<>();
                for (LocalModel local : listaLocais) {
                    infoLocais.add(local.toString());
                }
                ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(
                        CrudSessaoActivity.this,
                        android.R.layout.simple_spinner_item,
                        infoLocais);
                adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocal.setAdapter(adapterLocal);
            }

            // Atualizar RecyclerView
            if (listaSessoes != null) {
                adapter.atualizarLista(listaSessoes);
            }
            verificarListaVazia();
        }
    }

    private class SalvarSessaoTask extends AsyncTask<Void, Void, Boolean> {
        private final SessaoModel novaSessao;
        private final FilmeModel filmeSelecionado;
        private final LocalModel localSelecionado;

        public SalvarSessaoTask(SessaoModel novaSessao, FilmeModel filmeSelecionado, LocalModel localSelecionado) {
            this.novaSessao = novaSessao;
            this.filmeSelecionado = filmeSelecionado;
            this.localSelecionado = localSelecionado;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                long result = sessaoDAO.inserirSessao(novaSessao);
                if (result == -1) return false;

                novaSessao.setId((int) result);
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Erro ao salvar sessão: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudSessaoActivity.this, "Sessão salva com sucesso!", Toast.LENGTH_SHORT).show();
                fecharModal();
                new CarregarDadosTask().execute();
            } else {
                Toast.makeText(CrudSessaoActivity.this, "Erro ao salvar sessão", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ExcluirSessaoTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... ids) {
            if (ids.length == 0) return false;
            try {
                return sessaoDAO.excluirSessao(ids[0]);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao excluir sessão: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudSessaoActivity.this, "Sessão excluída!", Toast.LENGTH_SHORT).show();
                new CarregarDadosTask().execute();
            } else {
                Toast.makeText(CrudSessaoActivity.this, "Erro ao excluir sessão.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ==================== Adapter ====================

    private class SessaoAdapter extends RecyclerView.Adapter<SessaoAdapter.SessaoViewHolder> {
        private List<SessaoModel> sessoes;

        public SessaoAdapter(List<SessaoModel> sessoes) {
            this.sessoes = sessoes;
        }

        public void atualizarLista(List<SessaoModel> novaLista) {
            this.sessoes = novaLista;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public SessaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sessao, parent, false);
            return new SessaoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SessaoViewHolder holder, int position) {
            SessaoModel sessao = sessoes.get(position);

            // Buscar filme e local
            FilmeModel filme = buscarFilmePorId(sessao.getFilme());
            LocalModel local = buscarLocalPorId(sessao.getLocal());

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            holder.txtFilme.setText(filme != null ? filme.getTitulo() : "Filme não encontrado");
            holder.txtData.setText(sdf.format(sessao.getData()));
            holder.txtHora.setText(sessao.getHora());
            holder.txtLocal.setText(local != null ? local.toString() : "Local não encontrado");

            holder.btnExcluir.setOnClickListener(v -> excluirSessao(sessao.getId()));
        }

        @Override
        public int getItemCount() {
            return sessoes != null ? sessoes.size() : 0;
        }

        private FilmeModel buscarFilmePorId(int id) {
            if (listaFilmes != null) {
                for (FilmeModel filme : listaFilmes) {
                    if (filme.getId() == id) return filme;
                }
            }
            return null;
        }

        private LocalModel buscarLocalPorId(int id) {
            if (listaLocais != null) {
                for (LocalModel local : listaLocais) {
                    if (local.getId() == id) return local;
                }
            }
            return null;
        }

        class SessaoViewHolder extends RecyclerView.ViewHolder {
            TextView txtFilme, txtData, txtHora, txtLocal;
            ImageButton btnExcluir;

            public SessaoViewHolder(@NonNull View itemView) {
                super(itemView);
                txtFilme = itemView.findViewById(R.id.txtFilme);
                txtData = itemView.findViewById(R.id.txtData);
                txtHora = itemView.findViewById(R.id.txtHora);
                txtLocal = itemView.findViewById(R.id.txtLocal);
                btnExcluir = itemView.findViewById(R.id.btnExcluir);
            }
        }
    }
}