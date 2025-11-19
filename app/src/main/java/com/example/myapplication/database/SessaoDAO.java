package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.myapplication.models.SessaoModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SessaoDAO {

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private Context context;

    public SessaoDAO(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public long inserirSessao(SessaoModel sessao) {
        db = dbHelper.getWritableDatabase();

        if (isSessaoDuplicada(sessao.getHora(), sessao.getLocal(), sessao.getDataString())) {
            Toast.makeText(context, "Sessão já cadastrada neste horário/local!", Toast.LENGTH_SHORT).show();
            return -1;
        }

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_DATA, sessao.getDataString());
        values.put(DatabaseHelper.COL_HORA, sessao.getHora());
        values.put(DatabaseHelper.COL_FK_FILME, sessao.getFilme());
        values.put(DatabaseHelper.COL_FK_LOCAL, sessao.getLocal());

        long id = db.insert(DatabaseHelper.TABELA_SESSOES, null, values);
        db.close();

        return id;
    }

    public int atualizarSessao(SessaoModel sessao) {
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.COL_DATA, sessao.getDataString());
        values.put(DatabaseHelper.COL_HORA, sessao.getHora());
        values.put(DatabaseHelper.COL_FK_FILME, sessao.getFilme());
        values.put(DatabaseHelper.COL_FK_LOCAL, sessao.getLocal());

        int linhasAfetadas = db.update(DatabaseHelper.TABELA_SESSOES,
                values,
                DatabaseHelper.COL_ID_SESSAO + " = ?",
                new String[]{String.valueOf(sessao.getId())});

        db.close();

        if (linhasAfetadas > 0) {
            Toast.makeText(context, "Sessão atualizada com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro: Sessão não encontrada.", Toast.LENGTH_SHORT).show();
        }

        return linhasAfetadas;
    }

    public SessaoModel buscarSessaoPorId(int idSessao) {
        db = dbHelper.getReadableDatabase();
        SessaoModel sessao = null;

        String selection = DatabaseHelper.COL_ID_SESSAO + " = ?";
        String[] selectionArgs = {String.valueOf(idSessao)};

        String[] colunas = {
                DatabaseHelper.COL_ID_SESSAO,
                DatabaseHelper.COL_DATA,
                DatabaseHelper.COL_HORA,
                DatabaseHelper.COL_FK_LOCAL,
                DatabaseHelper.COL_FK_FILME
        };

        Cursor cursor = db.query(DatabaseHelper.TABELA_SESSOES, colunas, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_SESSAO));
            String dataStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATA));
            String hora = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HORA));
            int idLocal = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FK_LOCAL));
            int idFilme = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FK_FILME));

            Date data = SessaoModel.stringToDate(dataStr);
            sessao = new SessaoModel(id, data, hora, idLocal, idFilme);
        }

        cursor.close();
        db.close();
        return sessao;
    }

    private boolean isSessaoDuplicada(String hora, int localId, String data) {
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();

        String selection = DatabaseHelper.COL_HORA + " = ? AND " +
                DatabaseHelper.COL_FK_LOCAL + " = ? AND " +
                DatabaseHelper.COL_DATA + " = ?";
        String[] args = {hora, String.valueOf(localId), data};

        Cursor cursor = dbRead.query(DatabaseHelper.TABELA_SESSOES, null, selection,args, null, null, null);

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


    public List<SessaoModel> listarSessoes() {
        db = dbHelper.getReadableDatabase();
        List<SessaoModel> sessoes = new ArrayList<>();

        String[] colunas = {
                DatabaseHelper.COL_ID_SESSAO,
                DatabaseHelper.COL_DATA,
                DatabaseHelper.COL_HORA,
                DatabaseHelper.COL_FK_LOCAL,
                DatabaseHelper.COL_FK_FILME
        };

        Cursor cursor = db.query(DatabaseHelper.TABELA_SESSOES, colunas, null, null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_SESSAO));
                String dataStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DATA));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HORA));
                int idLocal = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FK_LOCAL));
                int idFilme = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FK_FILME));

                Date data = SessaoModel.stringToDate(dataStr);
                sessoes.add(new SessaoModel(id, data, hora, idLocal, idFilme));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return sessoes;
    }


    public boolean excluirSessao(int idSessao) {
        db = dbHelper.getWritableDatabase();
        int linhas = db.delete(DatabaseHelper.TABELA_SESSOES,
                DatabaseHelper.COL_ID_SESSAO + "= ?",
                new String[]{String.valueOf(idSessao)});
        db.close();

        if (linhas > 0) {
            Toast.makeText(context, "Sessão excluida com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro: Sessão não encontrada.", Toast.LENGTH_SHORT).show();
        }

        return linhas > 0;
    }
}