package com.library;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class BookDAO {

    public static ObservableList<Book> getAll() throws SQLException {
        ObservableList<Book> list = FXCollections.observableArrayList();
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("isbn"), rs.getString("genre"), rs.getInt("published_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            }
        }
        return list;
    }

    public static ObservableList<Book> search(String keyword) throws SQLException {
        return searchByTitleAuthorGenre(keyword);
    }

    public static ObservableList<Book> searchByTitleAuthorGenre(String keyword) throws SQLException {
        ObservableList<Book> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR genre LIKE ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k); ps.setString(2, k); ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"),
                        rs.getString("isbn"), rs.getString("genre"), rs.getInt("published_year"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
            }
        }
        return list;
    }

    public static void insert(String title, String author, String isbn, String genre, int year, int total) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, genre, published_year, total_copies, available_copies) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title); ps.setString(2, author); ps.setString(3, isbn);
            ps.setString(4, genre); ps.setInt(5, year); ps.setInt(6, total); ps.setInt(7, total);
            ps.executeUpdate();
        }
    }

    public static void delete(int id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
