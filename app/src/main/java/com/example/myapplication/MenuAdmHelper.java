package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.models.FilmeModel;
import com.example.myapplication.models.LocalModel;
import com.example.myapplication.models.SessaoModel;

import java.util.List;

public class MenuAdmHelper {

    public static void cadastrarFilme(Context context, List<FilmeModel> listaDeFilmes) {
        // Criar os campos para entrada de dados
        final EditText editTextTitulo = new EditText(context);
        editTextTitulo.setHint("Título do filme");

        final EditText editTextDuracao = new EditText(context);
        editTextDuracao.setHint("Duração (em minutos)");
        editTextDuracao.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        final EditText editTextClassificacao = new EditText(context);
        editTextClassificacao.setHint("Classificação (idade mínima)");
        editTextClassificacao.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        // Spinner para seleção de gênero
        String[] generos = {"Ação", "Anime", "Comédia", "Drama", "Ficção Científica", "Romance", "Suspense", "Terror"};
        final Spinner spinnerGeneros = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, generos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGeneros.setAdapter(adapter);

        // Criar o AlertDialog para exibir os campos
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cadastrar Filme");

        // Layout para os campos
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(editTextTitulo);
        layout.addView(editTextDuracao);
        layout.addView(editTextClassificacao);
        layout.addView(spinnerGeneros);

        builder.setView(layout);

        builder.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    // Capturar os dados inseridos
                    String titulo = editTextTitulo.getText().toString();
                    String duracaoStr = editTextDuracao.getText().toString();
                    String classificacaoStr = editTextClassificacao.getText().toString();

                    // Validações
                    if (titulo.isEmpty() || duracaoStr.isEmpty() || classificacaoStr.isEmpty()) {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //int generoSelecionado = spinnerGeneros.getSelectedItemPosition() + 1; // Gênero selecionado (1-8)
                    String generoSelecionadoStr = spinnerGeneros.getSelectedItem().toString();



                    int duracao = Integer.parseInt(duracaoStr);
                    int classificacao = Integer.parseInt(classificacaoStr);


                    // Criar objeto Filme e adicionar à lista
                    FilmeModel filme = new FilmeModel(titulo, generoSelecionadoStr, duracao, classificacao);
                    listaDeFilmes.add(filme);

                    // Feedback para o usuário
                    Toast.makeText(context, "Filme cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Erro nos campos numéricos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para cadastrar um local - CORRIGIDO
    public static void cadastrarLocal(Context context, List<LocalModel> listaDeLocais) {
        final EditText editTextBloco = new EditText(context);
        editTextBloco.setHint("Bloco (ex: A, B, C)");

        final EditText editTextSala = new EditText(context);
        editTextSala.setHint("Sala (número)");
        editTextSala.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(editTextBloco);
        layout.addView(editTextSala);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cadastrar Local");
        builder.setView(layout);

        builder.setPositiveButton("Cadastrar", (dialog, which) -> {
            try {
                String bloco = editTextBloco.getText().toString();
                String salaStr = editTextSala.getText().toString();

                if (bloco.isEmpty() || salaStr.isEmpty()) {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int sala = Integer.parseInt(salaStr);

                // CORREÇÃO: A ordem dos parâmetros deve ser (sala, bloco)
                LocalModel local = new LocalModel(sala, bloco);
                listaDeLocais.add(local);

                Toast.makeText(context, "Local cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Erro no campo numérico!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para cadastrar uma sessão - CORRIGIDO
    public static void cadastrarSessao(Context context, List<SessaoModel> listaDeSessoes,
                                       List<FilmeModel> listaDeFilmes, List<LocalModel> listaDeLocais) {
        if (listaDeFilmes.isEmpty() || listaDeLocais.isEmpty()) {
            Toast.makeText(context, "Cadastre pelo menos um filme e um local antes!", Toast.LENGTH_LONG).show();
            return;
        }

        final EditText editTextData = new EditText(context);
        editTextData.setHint("Data (dd/mm/aaaa)");

        final EditText editTextHora = new EditText(context);
        editTextHora.setHint("Hora (hh:mm)");

        Spinner spinnerFilmes = new Spinner(context);
        ArrayAdapter<FilmeModel> adapterFilmes = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaDeFilmes);
        adapterFilmes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilmes.setAdapter(adapterFilmes);

        Spinner spinnerLocais = new Spinner(context);
        ArrayAdapter<LocalModel> adapterLocais = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, listaDeLocais);
        adapterLocais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocais.setAdapter(adapterLocais);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 0, 50, 0);
        layout.addView(editTextData);
        layout.addView(editTextHora);
        layout.addView(spinnerFilmes);
        layout.addView(spinnerLocais);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cadastrar Sessão");
        builder.setView(layout);

        builder.setPositiveButton("Cadastrar", (dialog, which) -> {
            try {
                String dataStr = editTextData.getText().toString();
                String hora = editTextHora.getText().toString();

                if (dataStr.isEmpty() || hora.isEmpty()) {
                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date data = sdf.parse(dataStr);

                FilmeModel filmeSelecionado = (FilmeModel) spinnerFilmes.getSelectedItem();
                LocalModel localSelecionado = (LocalModel) spinnerLocais.getSelectedItem();

                // CORREÇÃO: Usar os IDs reais do filme e local
                SessaoModel sessao = new SessaoModel(data, hora, localSelecionado.getId(), filmeSelecionado.getId());
                listaDeSessoes.add(sessao);

                Toast.makeText(context, "Sessão cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Erro ao cadastrar sessão! Verifique o formato da data.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    // Método para listar filmes
    public static void listarFilmes(Context context, List<FilmeModel> listaDeFilmes) {
        if (listaDeFilmes.isEmpty()) {
            Toast.makeText(context, "Não há filmes cadastrados!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder filmesListados = new StringBuilder("Filmes cadastrados:\n\n");
        for (FilmeModel filme : listaDeFilmes) {
            filmesListados.append(filme.exibirInfoFilme()).append("\n\n");
        }

        // Mostrar em um AlertDialog para melhor visualização
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lista de Filmes");
        builder.setMessage(filmesListados.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}