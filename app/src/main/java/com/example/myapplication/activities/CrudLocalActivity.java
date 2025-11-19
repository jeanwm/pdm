// CrudLocalActivity.java
package com.example.myapplication.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.LocalDAO;
import com.example.myapplication.models.LocalModel;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CrudLocalActivity extends AppCompatActivity {

    private static final String TAG = "CrudLocalActivity";

    // Componentes de UI
    private ExtendedFloatingActionButton btnAdicionar;
    private ImageButton btnVoltar;
    private Button btnSalvar, btnCancelar;
    private EditText editSala, editBloco;
    private MaterialCardView modalCadastro;
    private View modalOverlay;
    private RecyclerView recyclerViewLocais;
    private View emptyState;

    // DAO
    private LocalDAO localDAO;

    // Dados
    private List<LocalModel> listaLocais;
    private LocalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_local);

        // Inicializa DAO
        localDAO = new LocalDAO(this);

        inicializarComponentes();
        configurarEventos();
        setupRecyclerView();

        // Carregar dados
        new CarregarLocaisTask().execute();
    }

    private void inicializarComponentes() {
        btnAdicionar = findViewById(R.id.local_btnAdicionar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnSalvar = findViewById(R.id.local_btnSalvar);
        btnCancelar = findViewById(R.id.local_btnCancelar);
        editSala = findViewById(R.id.local_editSala);
        editBloco = findViewById(R.id.local_editBloco);
        modalCadastro = findViewById(R.id.modalCadastro);
        modalOverlay = findViewById(R.id.modalOverlay);
        recyclerViewLocais = findViewById(R.id.recyclerViewLocais);
        emptyState = findViewById(R.id.emptyState);
    }

    private void configurarEventos() {
        btnAdicionar.setOnClickListener(v -> abrirModal());
        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvarLocal());
        btnCancelar.setOnClickListener(v -> fecharModal());
        modalOverlay.setOnClickListener(v -> fecharModal());
    }

    private void setupRecyclerView() {
        recyclerViewLocais.setLayoutManager(new LinearLayoutManager(this));
        listaLocais = new ArrayList<>();
        adapter = new LocalAdapter(listaLocais);
        recyclerViewLocais.setAdapter(adapter);
    }

    private void abrirModal() {
        editSala.setText("");
        editBloco.setText("");

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

    private void salvarLocal() {
        String salaStr = editSala.getText().toString().trim();
        String bloco = editBloco.getText().toString().trim();

        if (salaStr.isEmpty() || bloco.isEmpty()) {
            Toast.makeText(this, "Preencha Sala e Bloco", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int sala = Integer.parseInt(salaStr);
            LocalModel novoLocal = new LocalModel(sala, bloco);
            new SalvarLocalTask(novoLocal).execute();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "A sala deve ser um número inteiro", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirLocal(int idLocal) {
        new ExcluirLocalTask().execute(idLocal);
    }

    private void verificarListaVazia() {
        boolean listaVazia = listaLocais == null || listaLocais.isEmpty();
        emptyState.setVisibility(listaVazia ? View.VISIBLE : View.GONE);
        recyclerViewLocais.setVisibility(listaVazia ? View.GONE : View.VISIBLE);
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

    private class CarregarLocaisTask extends AsyncTask<Void, Void, List<LocalModel>> {
        @Override
        protected List<LocalModel> doInBackground(Void... voids) {
            return localDAO.listarLocais();
        }

        @Override
        protected void onPostExecute(List<LocalModel> locais) {
            if (locais != null) {
                listaLocais = locais;
                adapter.atualizarLista(listaLocais);
            }
            verificarListaVazia();
        }
    }

    private class SalvarLocalTask extends AsyncTask<Void, Void, Boolean> {
        private final LocalModel novoLocal;

        public SalvarLocalTask(LocalModel novoLocal) {
            this.novoLocal = novoLocal;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                long result = localDAO.inserirLocal(novoLocal);
                return result != -1;
            } catch (Exception e) {
                Log.e(TAG, "Erro ao salvar local: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudLocalActivity.this, "Local salvo com sucesso!", Toast.LENGTH_SHORT).show();
                fecharModal();
                new CarregarLocaisTask().execute();
            } else {
                Toast.makeText(CrudLocalActivity.this, "Erro ao salvar local", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ExcluirLocalTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... ids) {
            if (ids.length == 0) return false;
            try {
                return localDAO.excluirLocal(ids[0]);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao excluir local: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudLocalActivity.this, "Local excluído!", Toast.LENGTH_SHORT).show();
                new CarregarLocaisTask().execute();
            } else {
                Toast.makeText(CrudLocalActivity.this, "Erro ao excluir local.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ==================== Adapter ====================

    private class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.LocalViewHolder> {
        private List<LocalModel> locais;

        public LocalAdapter(List<LocalModel> locais) {
            this.locais = locais;
        }

        public void atualizarLista(List<LocalModel> novaLista) {
            this.locais = novaLista;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_local, parent, false);
            return new LocalViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LocalViewHolder holder, int position) {
            LocalModel local = locais.get(position);

            holder.txtBlocoSala.setText("Bloco " + local.getBloco() + " - Sala " + local.getSala());
            holder.txtSala.setText(String.valueOf(local.getSala()));
            holder.txtBloco.setText(local.getBloco());

            holder.btnExcluir.setOnClickListener(v -> excluirLocal(local.getId()));
        }

        @Override
        public int getItemCount() {
            return locais != null ? locais.size() : 0;
        }

        class LocalViewHolder extends RecyclerView.ViewHolder {
            TextView txtBlocoSala, txtSala, txtBloco;
            ImageButton btnExcluir;

            public LocalViewHolder(@NonNull View itemView) {
                super(itemView);
                txtBlocoSala = itemView.findViewById(R.id.txtBlocoSala);
                txtSala = itemView.findViewById(R.id.txtSala);
                txtBloco = itemView.findViewById(R.id.txtBloco);
                btnExcluir = itemView.findViewById(R.id.btnExcluir);
            }
        }
    }
}