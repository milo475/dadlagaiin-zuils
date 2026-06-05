package com.library;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {

    @FXML private TabPane tabPane;

    // ---- BOOK TAB ----
    @FXML private TextField txtTitle, txtAuthor, txtIsbn, txtGenre, txtYear, txtCopies, txtSearch;
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, Integer> colId, colTotal, colAvailable, colYear;
    @FXML private TableColumn<Book, String>  colTitle, colAuthor, colIsbn, colGenre;
    @FXML private Label lblBookStatus;

    // ---- MEMBER TAB ----
    @FXML private TextField txtFirstName, txtLastName, txtEmail, txtPhone, txtMemberSearch;
    @FXML private TableView<Member> tblMembers;
    private int selectedMemberId = -1;
    @FXML private TableColumn<Member, Integer> colMemberId;
    @FXML private TableColumn<Member, String>  colFirstName, colLastName, colEmail, colPhone, colRegDate;
    @FXML private Label lblMemberStatus;

    // ---- BORROW TAB ----
    @FXML private TextField txtBorrowBookId, txtBorrowMemberId, txtDueDate;
    @FXML private TableView<Borrowing> tblBorrowings;
    @FXML private TableColumn<Borrowing, Integer> colBorrowId, colBorrowBookId, colBorrowMember;
    @FXML private TableColumn<Borrowing, String>  colBorrowDate, colDueDate, colReturnDate, colStatus;
    @FXML private Label lblBorrowStatus;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Book columns
        colId.setCellValueFactory(d -> d.getValue().idProperty().asObject());
        colTitle.setCellValueFactory(d -> d.getValue().titleProperty());
        colAuthor.setCellValueFactory(d -> d.getValue().authorProperty());
        colIsbn.setCellValueFactory(d -> d.getValue().isbnProperty());
        colGenre.setCellValueFactory(d -> d.getValue().genreProperty());
        colYear.setCellValueFactory(d -> d.getValue().publishedYearProperty().asObject());
        colTotal.setCellValueFactory(d -> d.getValue().totalCopiesProperty().asObject());
        colAvailable.setCellValueFactory(d -> d.getValue().availableCopiesProperty().asObject());

        // Member columns
        colMemberId.setCellValueFactory(d -> d.getValue().idProperty().asObject());
        colFirstName.setCellValueFactory(d -> d.getValue().firstNameProperty());
        colLastName.setCellValueFactory(d -> d.getValue().lastNameProperty());
        colEmail.setCellValueFactory(d -> d.getValue().emailProperty());
        colPhone.setCellValueFactory(d -> d.getValue().phoneProperty());
        colRegDate.setCellValueFactory(d -> d.getValue().registeredDateProperty());

        // Borrow columns
        colBorrowId.setCellValueFactory(d -> d.getValue().idProperty().asObject());
        colBorrowBookId.setCellValueFactory(d -> d.getValue().bookIdProperty().asObject());
        colBorrowMember.setCellValueFactory(d -> d.getValue().memberIdProperty().asObject());
        colBorrowDate.setCellValueFactory(d -> d.getValue().borrowDateProperty());
        colDueDate.setCellValueFactory(d -> d.getValue().dueDateProperty());
        colReturnDate.setCellValueFactory(d -> d.getValue().returnDateProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());

        loadBooks();
        loadMembers();
        loadBorrowings();

        // Tab сонгоход автоматаар reload хийнэ
        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            int i = newIdx.intValue();
            // 0=Номийн бүртгэл, 1=Уншигчийн бүртгэл, 2=Түрээсийн бүртгэл
            if (i == 1) loadMembers();
            else if (i == 2) loadBorrowings();
        });
    }

    // ========== BOOK ==========

    private void loadBooks() {
        try { tblBooks.setItems(BookDAO.getAll()); }
        catch (SQLException e) { lblBookStatus.setStyle("-fx-text-fill:red"); lblBookStatus.setText(e.getMessage()); }
    }

    @FXML void handleSearch() {
        String k = txtSearch.getText().trim();
        try { tblBooks.setItems(k.isEmpty() ? BookDAO.getAll() : BookDAO.search(k)); }
        catch (SQLException e) { lblBookStatus.setText(e.getMessage()); }
    }

    @FXML void handleAddBook(ActionEvent e) {
        try {
            BookDAO.insert(txtTitle.getText(), txtAuthor.getText(), txtIsbn.getText(),
                    txtGenre.getText(), intVal(txtYear), intVal(txtCopies));
            handleClearBook(e);
            loadBooks();
            status(lblBookStatus, "Амжилттай нэмлээ", true);
        } catch (Exception ex) { status(lblBookStatus, ex.getMessage(), false); }
    }

    @FXML void handleDeleteBook(ActionEvent e) {
        Book sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) { status(lblBookStatus, "Устгах номоо сонгоно уу", false); return; }
        try { BookDAO.delete(sel.getId()); loadBooks(); status(lblBookStatus, "Устгасан", true); }
        catch (SQLException ex) { status(lblBookStatus, ex.getMessage(), false); }
    }

    @FXML void handleClearBook(ActionEvent e) {
        txtTitle.clear(); txtAuthor.clear(); txtIsbn.clear();
        txtGenre.clear(); txtYear.clear(); txtCopies.clear();
        lblBookStatus.setText("");
    }

    @FXML void handleTableClick(MouseEvent e) {
        Book sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtTitle.setText(sel.getTitle()); txtAuthor.setText(sel.getAuthor());
        txtIsbn.setText(sel.getIsbn()); txtGenre.setText(sel.getGenre());
        txtYear.setText(String.valueOf(sel.getPublishedYear()));
        txtCopies.setText(String.valueOf(sel.getTotalCopies()));
    }

    // ========== MEMBER ==========

    private void loadMembers() {
        try { tblMembers.setItems(getMembersFromDB()); }
        catch (SQLException e) { lblMemberStatus.setStyle("-fx-text-fill:red"); lblMemberStatus.setText(e.getMessage()); }
    }

    private ObservableList<Member> getMembersFromDB() throws SQLException {
        ObservableList<Member> list = FXCollections.observableArrayList();
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM members")) {
            while (rs.next())
                list.add(new Member(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("email"), rs.getString("phone"), str(rs.getDate("registered_date"))));
        }
        return list;
    }

    @FXML void handleAddMember(ActionEvent e) {
        String sql = "INSERT INTO members (first_name, last_name, email, phone, registered_date) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtFirstName.getText()); ps.setString(2, txtLastName.getText());
            ps.setString(3, txtEmail.getText()); ps.setString(4, txtPhone.getText());
            ps.setDate(5, Date.valueOf(LocalDate.now()));
            ps.executeUpdate();
            handleClearMember(e); loadMembers();
            status(lblMemberStatus, "Амжилттай нэмлээ", true);
        } catch (Exception ex) { status(lblMemberStatus, ex.getMessage(), false); }
    }

    @FXML void handleUpdateMember(ActionEvent e) {
        if (selectedMemberId == -1) { status(lblMemberStatus, "Засах уншигчаа сонгоно уу", false); return; }
        String sql = "UPDATE members SET first_name=?, last_name=?, email=?, phone=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, txtFirstName.getText()); ps.setString(2, txtLastName.getText());
            ps.setString(3, txtEmail.getText()); ps.setString(4, txtPhone.getText());
            ps.setInt(5, selectedMemberId);
            ps.executeUpdate();
            handleClearMember(e); loadMembers();
            status(lblMemberStatus, "Амжилттай заслаа", true);
        } catch (Exception ex) { status(lblMemberStatus, ex.getMessage(), false); }
    }

    @FXML void handleMemberSearch() {
        String k = txtMemberSearch.getText().trim();
        try { tblMembers.setItems(k.isEmpty() ? getMembersFromDB() : searchMembers(k)); }
        catch (SQLException e) { status(lblMemberStatus, e.getMessage(), false); }
    }

    private ObservableList<Member> searchMembers(String k) throws SQLException {
        ObservableList<Member> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM members WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            String p = "%" + k + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p); ps.setString(4, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Member(rs.getInt("id"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("email"), rs.getString("phone"), str(rs.getDate("registered_date"))));
        }
        return list;
    }

    @FXML void handleDeleteMember(ActionEvent e) {
        Member sel = tblMembers.getSelectionModel().getSelectedItem();
        if (sel == null) { status(lblMemberStatus, "Устгах уншигчаа сонгоно уу", false); return; }
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM members WHERE id=?")) {
            ps.setInt(1, sel.getId()); ps.executeUpdate();
            loadMembers(); status(lblMemberStatus, "Устгасан", true);
        } catch (SQLException ex) { status(lblMemberStatus, ex.getMessage(), false); }
    }

    @FXML void handleClearMember(ActionEvent e) {
        txtFirstName.clear(); txtLastName.clear(); txtEmail.clear(); txtPhone.clear();
        if (txtMemberSearch != null) txtMemberSearch.clear();
        selectedMemberId = -1;
        lblMemberStatus.setText("");
    }

    @FXML void handleMemberTableClick(MouseEvent e) {
        Member sel = tblMembers.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        selectedMemberId = sel.getId();
        txtFirstName.setText(sel.getFirstName()); txtLastName.setText(sel.getLastName());
        txtEmail.setText(sel.getEmail()); txtPhone.setText(sel.getPhone());
    }

    // ========== BORROW ==========

    private void loadBorrowings() {
        try { tblBorrowings.setItems(getBorrowingsFromDB()); }
        catch (SQLException e) { lblBorrowStatus.setStyle("-fx-text-fill:red"); lblBorrowStatus.setText(e.getMessage()); }
    }

    private ObservableList<Borrowing> getBorrowingsFromDB() throws SQLException {
        ObservableList<Borrowing> list = FXCollections.observableArrayList();
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM borrowings")) {
            while (rs.next())
                list.add(new Borrowing(rs.getInt("id"), rs.getInt("book_id"), rs.getInt("member_id"),
                        str(rs.getDate("borrow_date")), str(rs.getDate("due_date")),
                        str(rs.getDate("return_date")), rs.getString("status")));
        }
        return list;
    }

    @FXML void handleBorrow(ActionEvent e) {
        String sql = "INSERT INTO borrowings (book_id, member_id, borrow_date, due_date, status) VALUES (?,?,?,?,'borrowed')";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, intVal(txtBorrowBookId)); ps.setInt(2, intVal(txtBorrowMemberId));
            ps.setDate(3, Date.valueOf(LocalDate.now()));
            ps.setDate(4, Date.valueOf(txtDueDate.getText().trim()));
            ps.executeUpdate();
            // reduce available_copies
            try (PreparedStatement u = c.prepareStatement(
                    "UPDATE books SET available_copies = available_copies - 1 WHERE id=? AND available_copies > 0")) {
                u.setInt(1, intVal(txtBorrowBookId)); u.executeUpdate();
            }
            txtBorrowBookId.clear(); txtBorrowMemberId.clear(); txtDueDate.clear();
            loadBorrowings(); loadBooks();
            status(lblBorrowStatus, "Зээлдүүллээ", true);
        } catch (Exception ex) { status(lblBorrowStatus, ex.getMessage(), false); }
    }

    @FXML void handleReturn(ActionEvent e) {
        Borrowing sel = tblBorrowings.getSelectionModel().getSelectedItem();
        if (sel == null) { status(lblBorrowStatus, "Буцаах бичлэгээ сонгоно уу", false); return; }
        String sql = "UPDATE borrowings SET return_date=?, status='returned' WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now())); ps.setInt(2, sel.getId());
            ps.executeUpdate();
            try (PreparedStatement u = c.prepareStatement(
                    "UPDATE books SET available_copies = available_copies + 1 WHERE id=?")) {
                u.setInt(1, sel.getBookId()); u.executeUpdate();
            }
            loadBorrowings(); loadBooks();
            status(lblBorrowStatus, "Буцаасан", true);
        } catch (SQLException ex) { status(lblBorrowStatus, ex.getMessage(), false); }
    }

    @FXML void handleBorrowTableClick(MouseEvent e) {
        Borrowing sel = tblBorrowings.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        txtBorrowBookId.setText(String.valueOf(sel.getBookId()));
        txtBorrowMemberId.setText(String.valueOf(sel.getMemberId()));
    }

    // ========== HELPERS ==========
    private int intVal(TextField f) { return Integer.parseInt(f.getText().trim()); }
    private String str(Date d) { return d == null ? "" : d.toString(); }
    private void status(Label l, String msg, boolean ok) {
        l.setStyle(ok ? "-fx-text-fill:green" : "-fx-text-fill:red");
        l.setText(msg);
    }

    // ---- Inner models ----
    public static class Member {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final StringProperty firstName = new SimpleStringProperty();
        private final StringProperty lastName = new SimpleStringProperty();
        private final StringProperty email = new SimpleStringProperty();
        private final StringProperty phone = new SimpleStringProperty();
        private final StringProperty registeredDate = new SimpleStringProperty();

        public Member(int id, String fn, String ln, String email, String phone, String reg) {
            this.id.set(id); firstName.set(fn); lastName.set(ln);
            this.email.set(email); this.phone.set(phone); registeredDate.set(reg);
        }
        public IntegerProperty idProperty() { return id; }
        public StringProperty firstNameProperty() { return firstName; }
        public StringProperty lastNameProperty() { return lastName; }
        public StringProperty emailProperty() { return email; }
        public StringProperty phoneProperty() { return phone; }
        public StringProperty registeredDateProperty() { return registeredDate; }
        public int getId() { return id.get(); }
        public String getFirstName() { return firstName.get(); }
        public String getLastName() { return lastName.get(); }
        public String getEmail() { return email.get(); }
        public String getPhone() { return phone.get(); }
    }

    public static class Borrowing {
        private final IntegerProperty id = new SimpleIntegerProperty();
        private final IntegerProperty bookId = new SimpleIntegerProperty();
        private final IntegerProperty memberId = new SimpleIntegerProperty();
        private final StringProperty borrowDate = new SimpleStringProperty();
        private final StringProperty dueDate = new SimpleStringProperty();
        private final StringProperty returnDate = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty();

        public Borrowing(int id, int bookId, int memberId, String borrowDate, String dueDate, String returnDate, String status) {
            this.id.set(id); this.bookId.set(bookId); this.memberId.set(memberId);
            this.borrowDate.set(borrowDate); this.dueDate.set(dueDate);
            this.returnDate.set(returnDate); this.status.set(status);
        }
        public IntegerProperty idProperty() { return id; }
        public IntegerProperty bookIdProperty() { return bookId; }
        public IntegerProperty memberIdProperty() { return memberId; }
        public StringProperty borrowDateProperty() { return borrowDate; }
        public StringProperty dueDateProperty() { return dueDate; }
        public StringProperty returnDateProperty() { return returnDate; }
        public StringProperty statusProperty() { return status; }
        public int getId() { return id.get(); }
        public int getBookId() { return bookId.get(); }
        public int getMemberId() { return memberId.get(); }
    }
}
