package com.example.myapplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private static final String url = "jdbc:mysql://SEU_SERVIDOR:3306/cinema";
    private static final String user = "root";
    private static final String password = "123456";

    // No Android, precisamos carregar o driver explicitamente
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar driver JDBC", e);
        }
    }

    public static Connection getConexao() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage(), e);
        }
    }

    // Método para testar a conexão
    public static boolean testarConexao() {
        try (Connection conn = getConexao()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}