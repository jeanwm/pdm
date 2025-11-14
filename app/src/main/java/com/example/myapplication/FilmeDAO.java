package com.example.myapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class FilmeDAO {

    public void cadastrarFilmeDAO(Filme filme) {

        if (isFilmeDuplicado(filme.getTitulo())) {
            JOptionPane.showMessageDialog(null, "Filme já cadastrado. Filme não foi inserido.", "FILME",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO FILME (TITULO, GENERO, DURACAO, CLASSIFICACAO) VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filme.getTitulo());
            ps.setInt(2, filme.getGenero());
            ps.setInt(3, filme.getDuracao());
            ps.setInt(4, filme.getClassificacao());

            ps.execute();
            JOptionPane.showMessageDialog(null, "Filme cadastrado com sucesso!", "FILME",
                    JOptionPane.INFORMATION_MESSAGE);
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    public void excluirFilmeDAO(Filme filme) {
        String sql = "DELETE FROM FILME WHERE TITULO = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filme.getTitulo());
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Filme excluído com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Operação cancelada, nenhum filme excluído.", null,
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir filme do banco." + e, null, JOptionPane.ERROR_MESSAGE);
        }

    }

    public void atualizarFilmeDAO(Filme filme, String tituloAntigo) {
        String sql = "UPDATE FILME SET TITULO = ?, GENERO = ?, CLASSIFICACAO = ?, DURACAO = ? WHERE TITULO = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filme.getTitulo());
            ps.setInt(2, filme.getGenero());
            ps.setInt(3, filme.getClassificacao());
            ps.setInt(4, filme.getDuracao());
            ps.setString(5, tituloAntigo);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Filme atualizado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Filme não encontrado.", null, JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar Filme!\n" + e, null, JOptionPane.ERROR_MESSAGE);
        }

    }

    private boolean isFilmeDuplicado(String titulo) {
        String sql = "SELECT * FROM FILME WHERE TITULO = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, titulo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar filme: " + e.getMessage());
            return false;
        }

    }

    public List<Filme> listarFilmes() {
        List<Filme> filmes = new ArrayList<>();
        String sql = "SELECT * FROM filme";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id_filme = rs.getInt("id_filme");
                String titulo = rs.getString("titulo");
                int genero = rs.getInt("genero");
                int duracao = rs.getInt("duracao");
                int classificacao = rs.getInt("classificacao");
                filmes.add(new Filme(id_filme, titulo, genero, duracao, classificacao));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Não há filmes cadastrados." + e);
        }

        return filmes;

    }

    public String listarGenero(int id_filme) {
        String sql = "SELECT g.descricao FROM filme f JOIN genero g ON f.genero = g.id_genero WHERE f.id_filme = ?";
        String descricao = null;
    
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
    
            ps.setInt(1, id_filme); 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    descricao = rs.getString("descricao"); 
                }
            }
    
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao retornar gênero: " + e.getMessage());
        }
    
        return descricao;
    }


}
