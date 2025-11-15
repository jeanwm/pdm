package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocalDAO {
    private Context context;

    public LocalDAO(Context context) {
        this.context = context;
    }
    public long cadastrarLocal(LocalModel local) {
        if (isLocalDuplicado(local.getSala(), local.getBloco())) {
            Toast.makeText(context, "Local já cadastrado", Toast.LENGTH_SHORT).show();
            return -1;
        }

        /*ContentValues values = new ContentValues();
        values.put("SALA", local.getSala());
        values.put("BLOCO", local.getBloco());

        long resultado = db.insert("LOCAL", null, values);

        if (resultado != -1) {
            Toast.makeText(context, "Local cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Erro ao cadastrar local", Toast.LENGTH_SHORT).show();
        }

        return resultado;*/
        return 0;
    }

    public int excluirLocal(int sala, String bloco) {
        /*int linhasAfetadas = db.delete("LOCAL", "SALA = ? AND BLOCO = ?",
                new String[]{String.valueOf(sala), bloco});

        if (linhasAfetadas > 0) {
            Toast.makeText(context, "Local excluído com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Local não encontrado", Toast.LENGTH_SHORT).show();
        }

        return linhasAfetadas;*/
        return 0;
    }

    private boolean isLocalDuplicado(int sala, String bloco) {
        /*Cursor cursor = db.query("LOCAL",
                new String[]{"SALA", "BLOCO"},
                "SALA = ? AND BLOCO = ?",
                new String[]{String.valueOf(sala), bloco},
                null, null, null);

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;*/
        return false;
    }

    public List<LocalModel> listarLocais() {
        /*List<LocalModel> locais = new ArrayList<>();
        Cursor cursor = db.query("LOCAL", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id_local = cursor.getInt(cursor.getColumnIndexOrThrow("id_local"));
            int sala = cursor.getInt(cursor.getColumnIndexOrThrow("sala"));
            String bloco = cursor.getString(cursor.getColumnIndexOrThrow("bloco"));

            locais.add(new LocalModel(id_local, sala, bloco));
        }

        cursor.close();
        return locais;*/
        return null;
    }

    // Método para buscar local por ID (útil para outras funcionalidades)
    public LocalModel buscarLocalPorId(int id) {
        /*Cursor cursor = db.query("LOCAL",
                null,
                "id_local = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        LocalModel local = null;
        if (cursor.moveToFirst()) {
            int id_local = cursor.getInt(cursor.getColumnIndexOrThrow("id_local"));
            int sala = cursor.getInt(cursor.getColumnIndexOrThrow("sala"));
            String bloco = cursor.getString(cursor.getColumnIndexOrThrow("bloco"));

            local = new LocalModel(id_local, sala, bloco);
        }

        cursor.close();
        return local;*/

        return null;
    }
}