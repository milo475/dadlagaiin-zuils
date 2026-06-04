package com.library;

import javafx.beans.property.*;

public class Book {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty author = new SimpleStringProperty();
    private final StringProperty isbn = new SimpleStringProperty();
    private final StringProperty genre = new SimpleStringProperty();
    private final IntegerProperty publishedYear = new SimpleIntegerProperty();
    private final IntegerProperty totalCopies = new SimpleIntegerProperty();
    private final IntegerProperty availableCopies = new SimpleIntegerProperty();

    public Book(int id, String title, String author, String isbn, String genre, int publishedYear, int totalCopies, int availableCopies) {
        this.id.set(id); this.title.set(title); this.author.set(author);
        this.isbn.set(isbn); this.genre.set(genre); this.publishedYear.set(publishedYear);
        this.totalCopies.set(totalCopies); this.availableCopies.set(availableCopies);
    }

    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty authorProperty() { return author; }
    public StringProperty isbnProperty() { return isbn; }
    public StringProperty genreProperty() { return genre; }
    public IntegerProperty publishedYearProperty() { return publishedYear; }
    public IntegerProperty totalCopiesProperty() { return totalCopies; }
    public IntegerProperty availableCopiesProperty() { return availableCopies; }

    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getAuthor() { return author.get(); }
    public String getIsbn() { return isbn.get(); }
    public String getGenre() { return genre.get(); }
    public int getPublishedYear() { return publishedYear.get(); }
    public int getTotalCopies() { return totalCopies.get(); }
    public int getAvailableCopies() { return availableCopies.get(); }
}
