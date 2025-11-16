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
import com.example.myapplication.models.Filme;
import com.example.myapplication.models.Local;
import com.example.myapplication.models.Sessao;
import com.example.myapplication.services.GoogleCalendarManager;
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
    
    private EditText editData, editHora, editBuscar;
    private Spinner spinnerLocal, spinnerFilme;
    private Button btnSalvar, btnCancelar, btnAdicionar, btnExcluir;
    private GoogleCalendarManager calendarManager;
    
    private List<Filme> listaFilmes;
    private List<Local> listaLocais;
    private ArrayAdapter<Filme> adapterFilmes;
    private ArrayAdapter<Local> adapterLocais;
    
    private SessaoDAO sessaoDAO;
    private FilmeDAO filmeDAO;
    private LocalDAO localDAO;
    
    private Sessao sessaoSalva;
    private Filme filmeSelecionado;
    private Local localSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_sessao);
        
        // Inicializar DAOs
        sessaoDAO = new SessaoDAO();
        filmeDAO = new FilmeDAO();
        localDAO = new LocalDAO();
        
        // Inicializar o gerenciador do Google Calendar
        calendarManager = new GoogleCalendarManager(this);
        
        // Inicializar listas
        listaFilmes = new ArrayList<>();
        listaLocais = new ArrayList<>();
        
        // Inicializar componentes
        inicializarComponentes();
        configurarEventos();
        
        // Carregar dados do banco em background
        new CarregarDadosTask().execute();
    }

    private void inicializarComponentes() {
        editBuscar = findViewById(R.id.sessao_buscar);
        editData = findViewById(R.id.sessao_editData);
        editHora = findViewById(R.id.sessao_editHora);
        spinnerLocal = findViewById(R.id.sessao_spinnerLocal);
        spinnerFilme = findViewById(R.id.sessao_spinnerFilme);
        
        btnSalvar = findViewById(R.id.sessao_btnSalvar);
        btnCancelar = findViewById(R.id.sessao_btnCancelar);
        btnAdicionar = findViewById(R.id.sessao_btnAdicionar);
        btnExcluir = findViewById(R.id.sessao_btnExcluir);
    }

    private void configurarEventos() {
        btnSalvar.setOnClickListener(v -> salvarSessao());
        btnCancelar.setOnClickListener(v -> finish());
        btnAdicionar.setOnClickListener(v -> limparFormulario());
        btnExcluir.setOnClickListener(v -> excluirSessao());
        
        // Listeners para capturar a seleção dos spinners
        spinnerFilme.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaFilmes.size()) {
                    filmeSelecionado = listaFilmes.get(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                filmeSelecionado = null;
            }
        });

        spinnerLocal.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaLocais.size()) {
                    localSelecionado = listaLocais.get(position);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                localSelecionado = null;
            }
        });
    }

    private void carregarSpinners() {
        // Adapter para filmes - mostra o título
        adapterFilmes = new ArrayAdapter<Filme>(this, 
                android.R.layout.simple_spinner_item, listaFilmes) {
            @Override
            public java.lang.CharSequence getItem(int position) {
                Filme filme = listaFilmes.get(position);
                return filme.getTitulo() + " (" + filme.getDuracao() + "min)";
            }
        };
        adapterFilmes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilme.setAdapter(adapterFilmes);

        // Adapter para locais - mostra bloco e sala
        adapterLocais = new ArrayAdapter<Local>(this, 
                android.R.layout.simple_spinner_item, listaLocais) {
            @Override
            public java.lang.CharSequence getItem(int position) {
                Local local = listaLocais.get(position);
                return "Bloco " + local.getBloco() + " - Sala " + local.getSala();
            }
        };
        adapterLocais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(adapterLocais);
    }

    private void salvarSessao() {
        if (validarFormulario()) {
            new SalvarSessaoTask().execute();
        }
    }

    private boolean validarFormulario() {
        if (editData.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe a data (dd/MM/aaaa)", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (editHora.getText().toString().isEmpty()) {
            Toast.makeText(this, "Informe o horário (HH:mm)", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (filmeSelecionado == null) {
            Toast.makeText(this, "Selecione um filme", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (localSelecionado == null) {
            Toast.makeText(this, "Selecione um local", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        return true;
    }

    private Date parseData(String dataString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return format.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Formato de data inválido. Use dd/MM/aaaa", Toast.LENGTH_SHORT).show();
            return new Date(); // Retorna data atual em caso de erro
        }
    }

    private void adicionarAoGoogleCalendar() {
        if (filmeSelecionado == null || localSelecionado == null) {
            Toast.makeText(this, "Dados incompletos para criar evento no Calendar", Toast.LENGTH_SHORT).show();
            return;
        }

        if (calendarManager.isUserLoggedIn()) {
            // Usuário já está logado - criar evento diretamente
            GoogleSignInAccount account = calendarManager.getLoggedInAccount();
            calendarManager.initializeCalendarService(account);
            
            // Passar os dados completos para o Calendar
            calendarManager.criarEventoNoCalendar(
                sessaoSalva, 
                filmeSelecionado.getTitulo(),
                "Bloco " + localSelecionado.getBloco() + " - Sala " + localSelecionado.getSala()
            );
            
            Toast.makeText(this, "Sessão adicionada ao Google Calendar!", Toast.LENGTH_SHORT).show();
            finish(); // Fechar activity após salvar
        } else {
            // Usuário não está logado - solicitar login
            solicitarLoginGoogle();
        }
    }

    private void solicitarLoginGoogle() {
        Intent signInIntent = calendarManager.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Login bem-sucedido
            calendarManager.initializeCalendarService(account);
            
            // Agora criar o evento no Calendar
            if (sessaoSalva != null && filmeSelecionado != null && localSelecionado != null) {
                calendarManager.criarEventoNoCalendar(
                    sessaoSalva, 
                    filmeSelecionado.getTitulo(),
                    "Bloco " + localSelecionado.getBloco() + " - Sala " + localSelecionado.getSala()
                );
                Toast.makeText(this, "Sessão adicionada ao Google Calendar!", Toast.LENGTH_SHORT).show();
                finish(); // Fechar activity após salvar
            }
            
        } catch (ApiException e) {
            // Login falhou, mas a sessão já foi salva localmente
            Log.w("GoogleCalendar", "Login falhou: " + e.getStatusCode());
            Toast.makeText(this, "Sessão salva no banco, mas não foi possível adicionar ao Calendar", Toast.LENGTH_SHORT).show();
            finish(); // Fechar activity mesmo com falha no Calendar
        }
    }

    private void limparFormulario() {
        editData.setText("");
        editHora.setText("");
        editBuscar.setText("");
        if (spinnerLocal.getCount() > 0) spinnerLocal.setSelection(0);
        if (spinnerFilme.getCount() > 0) spinnerFilme.setSelection(0);
        filmeSelecionado = null;
        localSelecionado = null;
        
        Toast.makeText(this, "Formulário limpo", Toast.LENGTH_SHORT).show();
    }

    private void excluirSessao() {
        String busca = editBuscar.getText().toString();
        if (busca.isEmpty()) {
            Toast.makeText(this, "Digite um termo de busca para excluir", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // TODO: Implementar lógica de exclusão baseada na busca
        // Você precisará buscar a sessão primeiro e depois excluir
        Toast.makeText(this, "Funcionalidade de exclusão a implementar para: " + busca, Toast.LENGTH_SHORT).show();
    }

    // AsyncTask para carregar dados do banco
    private class CarregarDadosTask extends AsyncTask<Void, Void, Boolean> {
        
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Carregar filmes e locais do banco
                listaFilmes.clear();
                listaLocais.clear();
                
                List<Filme> filmes = filmeDAO.listarFilmes();
                List<Local> locais = localDAO.listarLocais();
                
                if (filmes != null) listaFilmes.addAll(filmes);
                if (locais != null) listaLocais.addAll(locais);
                
                return true;
            } catch (Exception e) {
                Log.e("CrudSessao", "Erro ao carregar dados: " + e.getMessage());
                return false;
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                carregarSpinners();
                if (listaFilmes.isEmpty()) {
                    Toast.makeText(CrudSessaoActivity.this, 
                        "Nenhum filme cadastrado. Cadastre filmes primeiro.", Toast.LENGTH_LONG).show();
                }
                if (listaLocais.isEmpty()) {
                    Toast.makeText(CrudSessaoActivity.this, 
                        "Nenhum local cadastrado. Cadastre locais primeiro.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(CrudSessaoActivity.this, 
                    "Erro ao carregar dados do banco", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask para salvar sessão no banco
    private class SalvarSessaoTask extends AsyncTask<Void, Void, Boolean> {
        private Sessao novaSessao;
        
        @Override
        protected void onPreExecute() {
            // Coletar dados do formulário
            Date data = parseData(editData.getText().toString());
            String hora = editHora.getText().toString();
            int localId = localSelecionado != null ? localSelecionado.getId() : -1;
            int filmeId = filmeSelecionado != null ? filmeSelecionado.getId() : -1;

            // Criar objeto Sessao
            novaSessao = new Sessao(data, hora, localId, filmeId);
        }
        
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                sessaoDAO.cadastrarSessaoDAO(novaSessao);
                sessaoSalva = novaSessao;
                return true;
            } catch (Exception e) {
                Log.e("CrudSessao", "Erro ao salvar sessão: " + e.getMessage());
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
                Log.d("CrudSessao", "Sessão salva: " + novaSessao.toString());
                
                // Tentar adicionar ao Google Calendar
                adicionarAoGoogleCalendar();
                
            } else {
                Toast.makeText(CrudSessaoActivity.this, 
                    "Erro ao salvar sessão no banco", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
