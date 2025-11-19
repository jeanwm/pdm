package com.example.myapplication.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.FilmeDAO;
import com.example.myapplication.database.LocalDAO;
import com.example.myapplication.database.SessaoDAO;
import com.example.myapplication.models.FilmeModel;
import com.example.myapplication.models.LocalModel;
import com.example.myapplication.models.SessaoModel;
//import com.example.myapplication.services.GoogleCalendarManager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CrudSessaoActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private static final String TAG = "CrudSessaoActivity";

    // Componentes de UI
    private EditText editData, editHora, editBuscar;
    private Spinner spinnerLocal, spinnerFilme;
    private Button btnSalvar, btnCancelar, btnExcluir, btnAdicionar; // btnAdicionar não foi mapeado

    // DAOs
    private SessaoDAO sessaoDAO;
    private FilmeDAO filmeDAO;
    private LocalDAO localDAO;

    // Dados para Spinners
    private List<FilmeModel> listaFilmes;
    private List<LocalModel> listaLocais;

    // Objetos da Sessão para salvar e gerenciar
    private SessaoModel sessaoSalva; // Usada para o Calendar após o salvamento
    private FilmeModel filmeSelecionado;
    private LocalModel localSelecionado;

// ----------------------------------------------------------------------
// Métodos do Ciclo de Vida e Configuração
// ----------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_sessao); // Assumindo o layout crud_sessao.xml

        // Inicializa DAOs
        sessaoDAO = new SessaoDAO(this);
        filmeDAO = new FilmeDAO(this);
        localDAO = new LocalDAO(this);

        inicializarComponentes();
        configurarEventos();

        // Carregar listas em background
        new CarregarDadosTask().execute();
    }

    private void inicializarComponentes() {
        editData = findViewById(R.id.sessao_editData); // Assumindo IDs no layout
        editHora = findViewById(R.id.sessao_editHora);
        spinnerLocal = findViewById(R.id.sessao_spinnerLocal);
        spinnerFilme = findViewById(R.id.sessao_spinnerFilme);
        btnSalvar = findViewById(R.id.sessao_btnSalvar);
        btnCancelar = findViewById(R.id.sessao_btnCancelar);
        btnExcluir = findViewById(R.id.sessao_btnExcluir);
        btnAdicionar = findViewById(R.id.sessao_btnAdicionar);
        editBuscar = findViewById(R.id.sessao_buscar);
    }

    private void configurarEventos() {
        btnSalvar.setOnClickListener(v -> salvarSessao());
        btnCancelar.setOnClickListener(v -> finish());
        btnExcluir.setOnClickListener(v -> excluirSessao());
        btnAdicionar.setOnClickListener(v -> salvarSessao());
    }

// ----------------------------------------------------------------------
// Lógica CRUD
// ----------------------------------------------------------------------

    private void salvarSessao() {
        String dataStr = editData.getText().toString().trim();
        String hora = editHora.getText().toString().trim();

        if (dataStr.isEmpty() || hora.isEmpty()) {
            Toast.makeText(this, "Preencha a data e hora.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (listaFilmes == null || listaLocais == null || listaFilmes.isEmpty() || listaLocais.isEmpty()) {
            Toast.makeText(this, "Dados de filmes/locais não carregados. Tente novamente.", Toast.LENGTH_LONG).show();
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

            // Busca os objetos completos a partir das listas
            filmeSelecionado = listaFilmes.get(filmeIndex);
            localSelecionado = listaLocais.get(localIndex);

            // Cria a nova SessaoModel com os IDs
            SessaoModel novaSessao = new SessaoModel(data, hora, localSelecionado.getId(), filmeSelecionado.getId());

            // Executa a tarefa de salvar em segundo plano
            new SalvarSessaoTask(novaSessao, filmeSelecionado, localSelecionado).execute();

        } catch (ParseException e) {
            Toast.makeText(this, "Formato de data inválido. Use DD/MM/AAAA.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao preparar sessão para salvar: " + e.getMessage());
            Toast.makeText(this, "Erro interno ao salvar sessão.", Toast.LENGTH_SHORT).show();
        }
    }

    private void excluirSessao() {
        String idStr = editBuscar.getText().toString().trim();
        if (idStr.isEmpty()) {
            Toast.makeText(this, "Informe o ID da Sessão no campo de busca para excluir.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idSessao = Integer.parseInt(idStr);
            // Executa a tarefa de exclusão em segundo plano
            new ExcluirSessaoTask().execute(idSessao);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "O ID deve ser um número inteiro.", Toast.LENGTH_SHORT).show();
        }
    }

    private void limparCampos() {
        editData.setText("");
        editHora.setText("");
        editBuscar.setText("");
        if (spinnerFilme.getAdapter() != null) spinnerFilme.setSelection(0);
        if (spinnerLocal.getAdapter() != null) spinnerLocal.setSelection(0);

        sessaoSalva = null;
        filmeSelecionado = null;
        localSelecionado = null;
        // Recarrega os dados para o caso de ter sido feita uma exclusão/edição
        new CarregarDadosTask().execute();
    }

// ----------------------------------------------------------------------
// Integração com Google Calendar
// ----------------------------------------------------------------------

//    private void adicionarAoGoogleCalendar() {
//        if (sessaoSalva == null || filmeSelecionado == null || localSelecionado == null) {
//            Toast.makeText(this, "Dados da sessão não estão prontos para o calendário.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        GoogleCalendarManager calendarManager = new GoogleCalendarManager(this);
//        // Inicia o processo de login no Google
//        calendarManager.requestSignIn(this, RC_SIGN_IN);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleCalendarManager.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            if (account != null && sessaoSalva != null && filmeSelecionado != null && localSelecionado != null) {
//
//                String tituloEvento = "Sessão de Filme: " + filmeSelecionado.getTitulo();
//                String localEvento = "Bloco " + localSelecionado.getBloco() + " - Sala " + localSelecionado.getSala();
//
//                // Assume que a duração do filme é em minutos
//                String descricaoEvento = "Duração: " + filmeSelecionado.getDuracao() + "min. Classificação: " + filmeSelecionado.getClassificacao() + " anos. ID da Sessão: " + sessaoSalva.getId();
//
//                GoogleCalendarManager calendarManager = new GoogleCalendarManager(this, account);
//                calendarManager.addEvent(
//                        sessaoSalva.getData(),
//                        sessaoSalva.getHora(),
//                        filmeSelecionado.getDuracao(), // Duração em minutos
//                        tituloEvento,
//                        descricaoEvento,
//                        localEvento);
//
//                Toast.makeText(this, "Evento adicionado ao Google Calendar.", Toast.LENGTH_LONG).show();
//            } else if (account == null) {
//                Toast.makeText(this, "Login do Google falhou ou cancelado.", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (ApiException e) {
//            Log.w(TAG, "Falha no login do Google para o Calendar. Código: " + e.getStatusCode());
//            Toast.makeText(this, "Falha no login do Google para o Calendar. Código: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
//        }
//    }

// ----------------------------------------------------------------------
// AsyncTasks para Operações de Banco de Dados
// ----------------------------------------------------------------------

    // 1. Carregar Dados para Spinners
    private class CarregarDadosTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            // Chamadas aos DAOs
            listaFilmes = filmeDAO.listarFilmes();
            listaLocais = localDAO.listarLocais();
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
            } else {
                Toast.makeText(CrudSessaoActivity.this, "Nenhum filme cadastrado.", Toast.LENGTH_SHORT).show();
            }

            // Configurar Spinner de Locais
            if (listaLocais != null && !listaLocais.isEmpty()) {
                List<String> infoLocais = new ArrayList<>();
                for (LocalModel local : listaLocais) {
                    infoLocais.add(local.toString()); // Usando toString() do LocalModel (ex: Bloco X | Sala Y)
                }
                ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(
                        CrudSessaoActivity.this,
                        android.R.layout.simple_spinner_item,
                        infoLocais);
                adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLocal.setAdapter(adapterLocal);
            } else {
                Toast.makeText(CrudSessaoActivity.this, "Nenhum local cadastrado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 2. Salvar Sessão
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
                // Insere no banco. Assume que inserirSessao retorna o ID (long) ou -1 em caso de falha.
                long result = sessaoDAO.inserirSessao(novaSessao);

                if (result == -1) {
                    return false;
                }

                // Atualiza o ID no objeto SessaoModel
                novaSessao.setId((int) result);
                sessaoSalva = novaSessao; // Armazena a sessão salva (com ID) para o Calendar
                return true;
            } catch (Exception e) {
                Log.e(TAG, "Erro ao salvar sessão: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                String mensagem = "Sessão salva no banco!\n" +
                        "Filme: " + (filmeSelecionado != null ? filmeSelecionado.getTitulo() : "N/A") + "\n" +
                        "Local: " + (localSelecionado != null ?
                        "Bloco " + localSelecionado.getBloco() + " - Sala " + localSelecionado.getSala() : "N/A") + "\n" +
                        "Data: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(novaSessao.getData()) + "\n" +
                        "Hora: " + novaSessao.getHora();

                Toast.makeText(CrudSessaoActivity.this, mensagem, Toast.LENGTH_LONG).show();
                Log.d(TAG, "Sessão salva: " + novaSessao.toString());

                // Tentar adicionar ao Google Calendar
                //adicionarAoGoogleCalendar();

                // Limpar campos
                limparCampos();

            } else {
                Toast.makeText(CrudSessaoActivity.this,
                        "Erro ao salvar sessão no banco", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 3. Excluir Sessão
    private class ExcluirSessaoTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... ids) {
            if (ids.length == 0) return false;
            int idSessao = ids[0];
            try {
                // Assume que excluirSessao retorna true se a exclusão foi bem-sucedida (linhas afetadas > 0)
                return sessaoDAO.excluirSessao(idSessao);
            } catch (Exception e) {
                Log.e(TAG, "Erro ao excluir sessão: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(CrudSessaoActivity.this, "Sessão excluída com sucesso!", Toast.LENGTH_SHORT).show();
                limparCampos();
            } else {
                Toast.makeText(CrudSessaoActivity.this, "Erro ao excluir sessão. Verifique o ID.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}