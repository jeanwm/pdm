package com.example.myapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexao
 */

public class Conexao {
    private static final String url = "jdbc:mysql://localhost:3306/cinema";
    private static final String user = "root";
    private static final String password = "123456";

    private static Connection conn;

    public static Connection getConexao() {

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados.\n", e);
        }

    }

}
