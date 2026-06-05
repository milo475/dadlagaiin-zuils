package com.library;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML private TextField txtSearch;
    @FXML private TableView<Book> tblBooks;
    @FXML private TableColumn<Book, Integer> colId, colYear, colTotal, colAvail;
    @FXML private TableColumn<Book, String>  colTitle, colAuthor, colIsbn, colGenre;

    @FXML private Label lblId, lblTitle, lblAuthor, lblIsbn, lblGenre, lblYear, lblTotal, lblAvail, lblStatus;
    @FXML private Button btnBorrow;

    private Book selectedBook;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(d -> d.getValue().idProperty().asObject());
        colTitle.setCellValueFactory(d -> d.getValue().titleProperty());
        colAuthor.setCellValueFactory(d -> d.getValue().authorProperty());
        colIsbn.setCellValueFactory(d -> d.getValue().isbnProperty());
        colGenre.setCellValueFactory(d -> d.getValue().genreProperty());
        colYear.setCellValueFactory(d -> d.getValue().publishedYearProperty().asObject());
        colTotal.setCellValueFactory(d -> d.getValue().totalCopiesProperty().asObject());
        colAvail.setCellValueFactory(d -> d.getValue().availableCopiesProperty().asObject());
        loadBooks();
    }

    private void loadBooks() {
        try { tblBooks.setItems(BookDAO.getAll()); }
        catch (SQLException e) { lblStatus.setText("Алдаа: " + e.getMessage()); }
    }

    @FXML
    private void handleSearch() {
        String k = txtSearch.getText().trim();
        try {
            tblBooks.setItems(k.isEmpty() ? BookDAO.getAll() : BookDAO.searchByTitleAuthorGenre(k));
        } catch (SQLException e) { lblStatus.setText("Алдаа: " + e.getMessage()); }
    }

    @FXML
    private void handleShowAll() {
        txtSearch.clear();
        loadBooks();
    }

    @FXML
    private void handleTableClick(MouseEvent e) {
        Book sel = tblBooks.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        selectedBook = sel;
        lblId.setText(String.valueOf(sel.getId()));
        lblTitle.setText(sel.getTitle());
        lblAuthor.setText(sel.getAuthor());
        lblIsbn.setText(sel.getIsbn());
        lblGenre.setText(sel.getGenre());
        lblYear.setText(String.valueOf(sel.getPublishedYear()));
        lblTotal.setText(String.valueOf(sel.getTotalCopies()));
        lblAvail.setText(String.valueOf(sel.getAvailableCopies()));
        btnBorrow.setDisable(sel.getAvailableCopies() <= 0);
        lblStatus.setText(sel.getAvailableCopies() <= 0 ? "⚠ Боломжтой ном байхгүй" : "");
    }

    @FXML
    private void handleBorrow() throws IOException {
        if (selectedBook == null) return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BorrowDialog.fxml"));
        Parent root = loader.load();
        BorrowDialogController ctrl = loader.getController();
        ctrl.setBook(selectedBook);

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ном авах");
        dialog.setScene(new Scene(root));
        dialog.showAndWait();

        if (ctrl.isConfirmed()) {
            lblStatus.setText("✅ Амжилттай бүртгэлээ!");
            loadBooks(); // available_copies шинэчилнэ
            // detail panel шинэчилнэ
            tblBooks.getItems().stream()
                .filter(b -> b.getId() == selectedBook.getId())
                .findFirst().ifPresent(b -> {
                    selectedBook = b;
                    lblAvail.setText(String.valueOf(b.getAvailableCopies()));
                    btnBorrow.setDisable(b.getAvailableCopies() <= 0);
                });
        }
    }
}
