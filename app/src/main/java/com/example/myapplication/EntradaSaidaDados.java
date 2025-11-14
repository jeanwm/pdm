package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class EntradaSaidaDados {

    // Método para obter texto (EditText) do usuário
    public static String retornarTexto(Context context, EditText editText) {
        String texto = editText.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(context, "Por favor, insira um texto válido!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return texto;
    }

    // Método para retornar um inteiro (EditText)
    public static int retornarInteiro(Context context, EditText editText) {
        try {
            return Integer.parseInt(editText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Por favor, insira um número válido!", Toast.LENGTH_SHORT).show();
            return -1;
        }
    }

    // Método para retornar um valor real (double)
    public static double retornarReal(Context context, EditText editText) {
        try {
            return Double.parseDouble(editText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(context, "Por favor, insira um número decimal válido!", Toast.LENGTH_SHORT).show();
            return -1.0;
        }
    }

    // Método para escolher um local usando um Spinner
    public static int escolherLocal(Context context, String[] locais) {
        final Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, locais);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lista de Locais")
                .setView(spinner)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = spinner.getSelectedItemPosition();
                        // Faça algo com a posição selecionada
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

        return spinner.getSelectedItemPosition();  // Retorna a posição selecionada
    }

    // Método para escolher um filme usando um Spinner
    public static int escolherFilme(Context context, String[] filmes) {
        final Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, filmes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lista de Filmes")
                .setView(spinner)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = spinner.getSelectedItemPosition();
                        // Faça algo com a posição selecionada
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

        return spinner.getSelectedItemPosition();  // Retorna a posição selecionada
    }

    // Método para escolher uma sessão usando um Spinner
    public static int escolherSessao(Context context, String[] sessoes) {
        final Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, sessoes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lista de Sessões")
                .setView(spinner)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = spinner.getSelectedItemPosition();
                        // Faça algo com a posição selecionada
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

        return spinner.getSelectedItemPosition();  // Retorna a posição selecionada
    }

    // Método para escolher um aluno usando um Spinner
    public static int escolherAluno(Context context, String[] alunos) {
        final Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, alunos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Lista de Alunos")
                .setView(spinner)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = spinner.getSelectedItemPosition();
                        // Faça algo com a posição selecionada
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

        return spinner.getSelectedItemPosition();  // Retorna a posição selecionada
    }
}

