package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.models.FilmeModel;


import java.util.ArrayList;
import java.util.List;

public class FilmeDAO {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public FilmeDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public long inserirFilme(FilmeModel filme) {
        if (isFilmeDuplicado(filme.getTitulo())) {
            return -2;
        }

        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_NOME_FILME, filme.getTitulo());
        values.put(DatabaseHelper.COL_DURACAO, filme.getDuracao());
        values.put(DatabaseHelper.COL_GENERO, filme.getGenero());
        values.put(DatabaseHelper.COL_CLASSIFICACAO, filme.getClassificacao());

        long id = db.insert(DatabaseHelper.TABELA_FILMES, null, values);
        db.close();


        return id;
    }

   public int excluirFilme(int idFilme) {
        if (isFilmeVinculadoSessao(idFilme)) {
            return -2;
        }

        db = dbHelper.getWritableDatabase();
        int linhas = db.delete(DatabaseHelper.TABELA_FILMES,
                DatabaseHelper.COL_ID_FILME + " = ?",
                new String[]{String.valueOf(idFilme)});

        db.close();

        return linhas > 0 ? 1 : 0; // 1 se excluiu, 0 se não encontrou ou erro
   }

   private boolean isFilmeVinculadoSessao(int idFilme) {
        db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COL_FK_FILME + "=?";
        String[] selectionArgs = {String.valueOf(idFilme)};

        Cursor cursor = db.query(DatabaseHelper.TABELA_SESSOES,
                new String[]{DatabaseHelper.COL_ID_SESSAO},
                selection,
                selectionArgs,
                null,
                null,
                null);

        boolean vinculado = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return vinculado;
   }

    public int atualizarFilme(FilmeModel filme) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_NOME_FILME, filme.getTitulo());
        values.put(DatabaseHelper.COL_DURACAO, filme.getDuracao());
        values.put(DatabaseHelper.COL_GENERO, filme.getGenero());
        values.put(DatabaseHelper.COL_CLASSIFICACAO, filme.getClassificacao());

        String whereClause = DatabaseHelper.COL_ID_FILME + " = ?";
        String[] whereArgs = {String.valueOf(filme.getId())};

        int linhasAfetadas = db.update(
                DatabaseHelper.TABELA_FILMES,
                values,
                whereClause,
                whereArgs
        );

        db.close();
        return linhasAfetadas;
    }

    private boolean isFilmeDuplicado(String titulo) {
        db = dbHelper.getReadableDatabase();
        String selection = DatabaseHelper.COL_NOME_FILME + " = ?";
        String[] selectionArgs = {titulo};
        Cursor cursor = db.query(DatabaseHelper.TABELA_FILMES, null, selection, selectionArgs, null, null, null);

        boolean exist = cursor.getCount() > 0;
        cursor.close();
        return exist;
    }

    public List<FilmeModel> listarFilmes() {
        db = dbHelper.getReadableDatabase();
        List<FilmeModel> filmes = new ArrayList<>();

        String[] colunas = {
            DatabaseHelper.COL_ID_FILME,
            DatabaseHelper.COL_NOME_FILME,
            DatabaseHelper.COL_DURACAO,
            DatabaseHelper.COL_GENERO,
            DatabaseHelper.COL_CLASSIFICACAO
        };

        Cursor cursor = db.query(DatabaseHelper.TABELA_FILMES, colunas, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_FILME));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOME_FILME));
            int duracao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DURACAO));
            String genero = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENERO));
            int classificacao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLASSIFICACAO));

            filmes.add(new FilmeModel(id, nome, genero, duracao, classificacao));
        }

        cursor.close();
        db.close();

        return filmes;
    }

    public FilmeModel buscarFilmePorId(int idFilme) {
        db = dbHelper.getReadableDatabase();
        FilmeModel filme = null;

        String[] colunas = {
                DatabaseHelper.COL_ID_FILME,
                DatabaseHelper.COL_NOME_FILME,
                DatabaseHelper.COL_DURACAO,
                DatabaseHelper.COL_GENERO,
                DatabaseHelper.COL_CLASSIFICACAO
        };

        String selection = DatabaseHelper.COL_ID_FILME + " = ?";
        String[] selectionArgs = {String.valueOf(idFilme)};

        Cursor cursor = db.query(DatabaseHelper.TABELA_FILMES, colunas, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_FILME));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOME_FILME));
            int duracao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DURACAO));
            String genero = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_GENERO));
            int classificacao = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CLASSIFICACAO));

            filme = new FilmeModel(id, nome, genero, duracao, classificacao);
        }

        cursor.close();
        db.close();
        return filme;
    }

}