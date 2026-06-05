package com.library;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class BorrowDialogController implements Initializable {

    @FXML private Label lblBookInfo, lblError;
    @FXML private TextField txtLastName, txtFirstName, txtEmail, txtPhone;
    @FXML private ComboBox<String> cboDays;

    private Book book;
    private boolean confirmed = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboDays.getItems().addAll("7 хоног", "14 хоног", "21 хоног");
        cboDays.getSelectionModel().selectFirst();
    }

    public void setBook(Book b) {
        this.book = b;
        lblBookInfo.setText(b.getTitle() + " — " + b.getAuthor() + " (ISBN: " + b.getIsbn() + ")");
    }

    @FXML
    private void handleConfirm() {
        String lastName  = txtLastName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String email     = txtEmail.getText().trim();
        String phone     = txtPhone.getText().trim();

        if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            lblError.setText("Бүх талбарыг бөглөнө үү.");
            return;
        }
        if (book.getAvailableCopies() <= 0) {
            lblError.setText("Боломжтой ном байхгүй байна.");
            return;
        }

        int days = Integer.parseInt(cboDays.getValue().split(" ")[0]);
        LocalDate dueDate = LocalDate.now().plusDays(days);

        try (Connection c = DatabaseConnection.getConnection()) {
            // 1. уншигч member бүртгэлд байхгүй бол нэмнэ
            int memberId = findOrCreateMember(c, firstName, lastName, email, phone);

            // 2. borrowings-д бүртгэнэ
            String sql = "INSERT INTO borrowings (book_id, member_id, borrow_date, due_date, status) VALUES (?,?,?,?,'borrowed')";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, book.getId());
                ps.setInt(2, memberId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.setDate(4, Date.valueOf(dueDate));
                ps.executeUpdate();
            }

            // 3. available_copies -1
            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE books SET available_copies = available_copies - 1 WHERE id = ? AND available_copies > 0")) {
                ps.setInt(1, book.getId());
                ps.executeUpdate();
            }

            confirmed = true;
            close();
        } catch (SQLException e) {
            lblError.setText("Алдаа: " + e.getMessage());
        }
    }

    private int findOrCreateMember(Connection c, String fn, String ln, String email, String phone) throws SQLException {
        // имэйлээр хайна
        try (PreparedStatement ps = c.prepareStatement("SELECT id FROM members WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        // байхгүй бол шинэ бүртгэл
        String ins = "INSERT INTO members (first_name, last_name, email, phone, registered_date) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(ins, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, fn); ps.setString(2, ln);
            ps.setString(3, email); ps.setString(4, phone);
            ps.setDate(5, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        }
    }

    @FXML
    private void handleCancel() { close(); }

    public boolean isConfirmed() { return confirmed; }

    private void close() {
        ((Stage) cboDays.getScene().getWindow()).close();
    }
}
