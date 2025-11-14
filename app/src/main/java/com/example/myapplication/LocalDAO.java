package com.example.myapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class LocalDAO {

    public void cadastrarLocalDAO(Local local) {

        if (isLocalDuplicado(local.getSala(), local.getBloco())) {
            JOptionPane.showMessageDialog(null, "Local já cadastrado. Local não foi inserido.", "LOCAL",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO LOCAL (SALA, BLOCO) VALUES (?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, local.getSala());
            ps.setString(2, local.getBloco());

            ps.execute();
            JOptionPane.showMessageDialog(null, "Local cadastrado com sucesso!", "LOCAL",
                    JOptionPane.INFORMATION_MESSAGE);
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void excluirLocalDAO(Local local) {
        String sql = "DELETE FROM LOCAL WHERE SALA = ? AND BLOCO = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, local.getSala());
            ps.setString(2, local.getBloco());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Local excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Operação cancelada, nenhum local excluído.", null,
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir local do banco." + e, null, JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean isLocalDuplicado(int sala, String bloco) {
        String sql = "SELECT * FROM LOCAL WHERE SALA = ? AND BLOCO = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sala);
            ps.setString(2, bloco);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar local: " + e.getMessage());
            return false;
        }

    }

    public List<Local> listarLocais() {
        List<Local> locais = new ArrayList<>();
        String sql = "SELECT * FROM LOCAL";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id_local = rs.getInt("id_local");
                int sala = rs.getInt("sala");
                String bloco = rs.getString("bloco");
                locais.add(new Local(id_local, sala, bloco));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Não há locais cadastrados." + e);
        }

        return locais;

    }

}
