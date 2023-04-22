module itmo.lab8.client_ui {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                requires org.kordamp.ikonli.javafx;
            requires org.kordamp.bootstrapfx.core;
    requires chunker;

    opens itmo.lab8.client_ui to javafx.fxml;
    exports itmo.lab8.client_ui;
    exports itmo.lab8.client_ui.ui.controllers;
    opens itmo.lab8.client_ui.ui.controllers to javafx.fxml;
}