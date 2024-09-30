module com.astier.bts.client_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.astier.bts.client_final to javafx.fxml;
    exports com.astier.bts.client_final;
}