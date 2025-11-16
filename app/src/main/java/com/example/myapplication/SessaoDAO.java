package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessaoDAO {

    private SQLiteDatabase db;
    private Context context;
    private static final String DATABASE_NAME = "cinema.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "SESSAO";

    public SessaoDAO(Context context) {
        this.context = context;
        openDatabase();
    }

    private void openDatabase() {
        try {
            db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            createTableIfNotExists();
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao abrir banco de dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createTableIfNotExists() {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id_sessao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "data TEXT NOT NULL, " +
                "hora TEXT NOT NULL, " +
                "filme INTEGER NOT NULL, " +
                "local INTEGER NOT NULL);";
        db.execSQL(createTable);
    }

    public long cadastrarSessao(SessaoModel sessao) {
        if (isSessaoDuplicada(sessao.getHora(), sessao.getLocal(), sessao.getDataString())) {
            Toast.makeText(context, "Sessão já cadastrada. Sessão não foi inserida.", Toast.LENGTH_SHORT).show();
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("DATA", sessao.getDataString());
        values.put("HORA", sessao.getHora());
        values.put("FILME", sessao.getFilme());
        values.put("LOCAL", sessao.getLocal());

        long resultado = db.insert(TABLE_NAME, null, values);

        if (resultado != -1) {
            Toast.makeText(context, "Sessão cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro ao cadastrar sessão", Toast.LENGTH_SHORT).show();
        }

        return resultado;
    }

    public int excluirSessao(Date data, String hora, int local, int filme) {
        String dataStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(data);

        int linhasAfetadas = db.delete(TABLE_NAME,
                "DATA = ? AND HORA = ? AND LOCAL = ? AND FILME = ?",
                new String[]{dataStr, hora, String.valueOf(local), String.valueOf(filme)});

        if (linhasAfetadas > 0) {
            Toast.makeText(context, "Sessão excluída com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Operação cancelada, nenhuma sessão excluída.", Toast.LENGTH_SHORT).show();
        }

        return linhasAfetadas;
    }

    public int excluirSessaoPorId(int id_sessao) {
        int linhasAfetadas = db.delete(TABLE_NAME, "id_sessao = ?", new String[]{String.valueOf(id_sessao)});

        if (linhasAfetadas > 0) {
            Toast.makeText(context, "Sessão excluída com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Sessão não encontrada.", Toast.LENGTH_SHORT).show();
        }

        return linhasAfetadas;
    }

    public int atualizarSessao(SessaoModel sessao) {
        ContentValues values = new ContentValues();
        values.put("DATA", sessao.getDataString());
        values.put("HORA", sessao.getHora());
        values.put("FILME", sessao.getFilme());
        values.put("LOCAL", sessao.getLocal());

        int linhasAfetadas = db.update(TABLE_NAME, values, "id_sessao = ?",
                new String[]{String.valueOf(sessao.getId())});

        if (linhasAfetadas > 0) {
            Toast.makeText(context, "Sessão atualizada com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Sessão não encontrada.", Toast.LENGTH_SHORT).show();
        }

        return linhasAfetadas;
    }

    private boolean isSessaoDuplicada(String hora, int local, String data) {
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{"hora", "local", "data"},
                "HORA = ? AND LOCAL = ? AND DATA = ?",
                new String[]{hora, String.valueOf(local), data},
                null, null, null);

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public List<SessaoModel> listarSessoes() {
        List<SessaoModel> sessoes = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                int id_sessao = cursor.getInt(cursor.getColumnIndexOrThrow("id_sessao"));
                String dataStr = cursor.getString(cursor.getColumnIndexOrThrow("data"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
                int local = cursor.getInt(cursor.getColumnIndexOrThrow("local"));
                int filme = cursor.getInt(cursor.getColumnIndexOrThrow("filme"));

                // Converter string para Date
                Date data = SessaoModel.stringToDate(dataStr);
                sessoes.add(new SessaoModel(id_sessao, data, hora, local, filme));
            }
        } catch (Exception e) {
            Toast.makeText(context, "Erro ao listar sessões: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
        }

        return sessoes;
    }

    public SessaoModel buscarSessaoPorId(int id) {
        Cursor cursor = db.query(TABLE_NAME,
                null,
                "id_sessao = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        SessaoModel sessao = null;
        if (cursor.moveToFirst()) {
            int id_sessao = cursor.getInt(cursor.getColumnIndexOrThrow("id_sessao"));
            String dataStr = cursor.getString(cursor.getColumnIndexOrThrow("data"));
            String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
            int local = cursor.getInt(cursor.getColumnIndexOrThrow("local"));
            int filme = cursor.getInt(cursor.getColumnIndexOrThrow("filme"));

            Date data = SessaoModel.stringToDate(dataStr);
            sessao = new SessaoModel(id_sessao, data, hora, local, filme);
        }
        cursor.close();
        return sessao;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}