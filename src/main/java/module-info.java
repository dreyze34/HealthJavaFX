module com.example.healthjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.opencsv;
    requires org.apache.commons.collections4;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.net.http;
    requires org.json;

    opens com.example.healthjavafx to javafx.fxml;
    exports com.example.healthjavafx;
}
