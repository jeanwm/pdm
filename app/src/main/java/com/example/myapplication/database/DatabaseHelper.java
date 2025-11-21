package com.example.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String NOME_BANCO="cinema.db";
    private static final int VERSAO_BANCO=1;

    public static final String TABELA_FILMES="filmes";
    public static final String TABELA_LOCAIS="locais";
    public static final String TABELA_SESSOES="sessoes";

    //Colunas da tabela de filmes
    public static final String COL_ID_FILME = "idFilme";
    public static final String COL_NOME_FILME = "nome";
    public static final String COL_DURACAO = "duracao";
    public static final String COL_GENERO = "genero";
    public static final String COL_CLASSIFICACAO = "classificacao";

    //Colunas da tabela de locais
    public static final String COL_ID_LOCAL = "idLocal";
    public static final String COL_SALA = "Sala";
    public static final String COL_BLOCO = "Bloco";

    //Colunas da tabela de sessoes
    public static final String COL_ID_SESSAO = "idSessao";
    public static final String COL_HORA = "hora";
    public static final String COL_DATA = "data";
    public static final String COL_FK_LOCAL = "idLocal"; // FK
    public static final String COL_FK_FILME = "idFilme"; // FK

    public static final String CREATE_TABLE_FILMES = "CREATE TABLE " + TABELA_FILMES + " ("
            + COL_ID_FILME + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NOME_FILME + " TEXT, "
            + COL_DURACAO + " INTEGER, "
            + COL_GENERO + " TEXT, "
            + COL_CLASSIFICACAO + " TEXT);";

    public static final String CREATE_TABLE_LOCAIS = "CREATE TABLE " + TABELA_LOCAIS + " ("
            + COL_ID_LOCAL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SALA + " TEXT, "
            + COL_BLOCO + " TEXT);";

    private static final String CREATE_TABLE_SESSOES = "CREATE TABLE " + TABELA_SESSOES + " ("
            + COL_ID_SESSAO + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_HORA + " TEXT, "
            + COL_DATA + " TEXT, "
            + COL_FK_LOCAL + " INTEGER, "
            + COL_FK_FILME + " INTEGER, "
            + "FOREIGN KEY(" + COL_FK_LOCAL + ") REFERENCES " + TABELA_LOCAIS + "(" + COL_ID_LOCAL + "), "
            + "FOREIGN KEY(" + COL_FK_FILME + ") REFERENCES " + TABELA_FILMES + "(" + COL_ID_FILME + "));";

    public DatabaseHelper(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_FILMES);
        db.execSQL(CREATE_TABLE_LOCAIS);
        db.execSQL(CREATE_TABLE_SESSOES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_FILMES);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_LOCAIS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_SESSOES);
        onCreate(db);
    }
}
