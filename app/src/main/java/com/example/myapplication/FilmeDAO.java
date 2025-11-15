package com.example.myapplication;

import android.content.Context;
import android.widget.Toast;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FilmeDAO {
    private Context context;

    public FilmeDAO(Context context) {
        this.context = context;
    }

    public void cadastrarFilme(FilmeModel filme) {
        if (isFilmeDuplicado(filme.getTitulo())) {
            Toast.makeText(context, "Filme já cadastrado. Filme não foi inserido.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Filme cadastrado com sucesso!", Toast.LENGTH_LONG).show();

        } catch (SQLException e) {
            Toast.makeText(context, "Erro ao cadastrar filme: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void excluirFilme(FilmeModel filme) {
        String sql = "DELETE FROM FILME WHERE TITULO = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, filme.getTitulo());
            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                Toast.makeText(context, "Filme excluído com sucesso!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Operação cancelada, nenhum filme excluído.", Toast.LENGTH_LONG).show();
            }

        } catch (SQLException e) {
            Toast.makeText(context, "Erro ao excluir filme: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void atualizarFilme(FilmeModel filme, String tituloAntigo) {
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
                Toast.makeText(context, "Filme atualizado com sucesso!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Filme não encontrado.", Toast.LENGTH_LONG).show();
            }

        } catch (SQLException e) {
            Toast.makeText(context, "Erro ao atualizar Filme: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Erro ao verificar filme: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public List<FilmeModel> listarFilmes() {
        List<FilmeModel> filmes = new ArrayList<>();
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
                filmes.add(new FilmeModel(id_filme, titulo, genero, duracao, classificacao));
            }

        } catch (SQLException e) {
            Toast.makeText(context, "Erro ao listar filmes: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(context, "Erro ao retornar gênero: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return descricao;
    }
}