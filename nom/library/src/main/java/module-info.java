module com.library {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mariadb.jdbc;

    opens com.library to javafx.fxml;
    exports com.library;
}
