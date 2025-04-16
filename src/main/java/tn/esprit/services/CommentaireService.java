package tn.esprit.services;

import tn.esprit.models.Commentaire;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService {
    private static final String INSERT_QUERY = "INSERT INTO commentaire (contenu, auteur) VALUES (?, ?)";
    private static final String UPDATE_QUERY = "UPDATE commentaire SET contenu = ?, auteur = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM commentaire WHERE id = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM commentaire";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM commentaire WHERE id = ?";

    public void add(Commentaire commentaire) throws SQLException {
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            
            preparedStatement.setString(1, commentaire.getContenu());
            preparedStatement.setString(2, commentaire.getAuteur());

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commentaire.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void update(Commentaire commentaire) throws SQLException {
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {
            
            preparedStatement.setString(1, commentaire.getContenu());
            preparedStatement.setString(2, commentaire.getAuteur());
            preparedStatement.setInt(3, commentaire.getId());

            preparedStatement.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY)) {
            
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public List<Commentaire> getAll() throws SQLException {
        List<Commentaire> commentaires = new ArrayList<>();
        
        try (Connection connection = MyDatabase.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SELECT_ALL_QUERY)) {
            
            while (resultSet.next()) {
                commentaires.add(extractCommentaireFromResultSet(resultSet));
            }
        }
        
        return commentaires;
    }

    public Commentaire getById(int id) throws SQLException {
        try (Connection connection = MyDatabase.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            
            preparedStatement.setInt(1, id);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCommentaireFromResultSet(resultSet);
                }
            }
        }
        
        return null;
    }

    private Commentaire extractCommentaireFromResultSet(ResultSet resultSet) throws SQLException {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(resultSet.getInt("id"));
        commentaire.setContenu(resultSet.getString("contenu"));
        commentaire.setAuteur(resultSet.getString("auteur"));
        return commentaire;
    }
}
