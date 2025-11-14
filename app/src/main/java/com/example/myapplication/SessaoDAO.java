package com.example.myapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class SessaoDAO {

    public void cadastrarSessaoDAO(Sessao sessao) {

        if (isSessaoDuplicada(sessao.getHora(), sessao.getLocal(), sessao.getData())) {
            JOptionPane.showMessageDialog(null, "Sessão já cadastrada. Sessão não foi inserida.", "SESSÃO",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "INSERT INTO SESSAO (DATA, HORA, FILME, LOCAL) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(sessao.getData().getTime()));
            ps.setString(2, sessao.getHora());
            ps.setInt(3, sessao.getFilme());
            ps.setInt(4, sessao.getLocal());

            ps.execute();
            JOptionPane.showMessageDialog(null, "Sessão cadastrada com sucesso!", "SESSÃO",
                    JOptionPane.INFORMATION_MESSAGE);
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void excluirSessaoDAO(Sessao sessao) {
        String sql = "DELETE FROM SESSAO WHERE id_sessao = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessao.getId());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Sessão excluída com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Operação cancelada, nenhuma sessão excluída.", null,
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir sessão do banco." + e, null,
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void atualizarSessaoDAO(Sessao sessao) {
        String sql = "UPDATE SESSAO SET DATA = ?, HORA = ? WHERE ID_SESSAO = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, new java.sql.Date(sessao.getData().getTime()));
            ps.setString(2, sessao.getHora());
            ps.setInt(3, sessao.getId());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Sessão atualizada com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Sessão não encontrada.", null, JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar sessão!\n" + e, null, JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean isSessaoDuplicada(String hora, int local, Date data) {
        String sql = "SELECT * FROM SESSAO WHERE HORA = ? AND LOCAL = ? AND DATA = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hora);
            ps.setInt(2, local);
            ps.setDate(3, new java.sql.Date(data.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar sessão: " + e.getMessage());
            return false;
        }

    }

    public List<Sessao> listarSessoes() {
        List<Sessao> sessoes = new ArrayList<>();
        String sql = "SELECT * FROM SESSAO";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id_sessao = rs.getInt("id_sessao");
                Date data = rs.getDate("data");
                String hora = rs.getString("hora");
                int local = rs.getInt("local");
                int filme = rs.getInt("filme");
                sessoes.add(new Sessao(id_sessao, data, hora, local, filme));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Não há sessões cadastradas.");
        }

        return sessoes;

    }

}
