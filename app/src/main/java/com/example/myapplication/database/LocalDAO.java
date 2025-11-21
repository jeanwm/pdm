package com.example.myapplication.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.models.LocalModel;

import java.util.ArrayList;
import java.util.List;

public class LocalDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public LocalDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public long inserirLocal(LocalModel local) {
        if (isLocalDuplicado(local.getSala(), local.getBloco())) {
            return -2;
        }

        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_SALA, local.getSala()); // INT
        values.put(DatabaseHelper.COL_BLOCO, local.getBloco()); // STRING


        long id = db.insert(DatabaseHelper.TABELA_LOCAIS, null, values);
        db.close();

        return id;
    }

    public int excluirLocal(int idLocal) {
        if (isLocalVinculadoSessao(idLocal)) {
            return -2; // local associado a uma sessão
        }

        db = dbHelper.getWritableDatabase();
        int linhas = db.delete(DatabaseHelper.TABELA_LOCAIS,
                DatabaseHelper.COL_ID_LOCAL + " = ?",
                new String[]{String.valueOf(idLocal)});
        db.close();

        return linhas > 0 ? 1 : 0; // 1 para sucesso, 0 para erro
    }

    private boolean isLocalVinculadoSessao(int idLocal) {
        db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COL_FK_LOCAL + " = ?";
        String[] selectionArgs = {String.valueOf(idLocal)};

        Cursor cursor = db.query(DatabaseHelper.TABELA_SESSOES,
                new String[]{DatabaseHelper.COL_ID_SESSAO},
                selection,
                selectionArgs,
                null, null, null);

        boolean vinculado = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return vinculado;
    }

    public List<LocalModel> listarLocais() {
        db = dbHelper.getReadableDatabase();
        List<LocalModel> lista = new ArrayList<>();

        String[] colunas = {
                DatabaseHelper.COL_ID_LOCAL,
                DatabaseHelper.COL_SALA,
                DatabaseHelper.COL_BLOCO
        };

        Cursor cursor = db.query(DatabaseHelper.TABELA_LOCAIS, colunas, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id_local = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_LOCAL));
            int sala = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SALA));
            String bloco = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_BLOCO));

            lista.add(new LocalModel(id_local, sala, bloco));
        }

        cursor.close();
        db.close();
        return lista;
    }

    private boolean isLocalDuplicado(int sala, String bloco) {
        db = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COL_SALA + " = ? AND " + DatabaseHelper.COL_BLOCO + " = ?";
        String[] args = {String.valueOf(sala), bloco};

        Cursor cursor = db.query(DatabaseHelper.TABELA_LOCAIS, null, selection, args, null, null, null);

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }
}