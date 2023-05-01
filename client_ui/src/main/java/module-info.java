module itmo.lab8.client_ui {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires chunker;

    exports itmo.lab8.basic.baseenums;
    exports itmo.lab8.basic.baseclasses;
    opens itmo.lab8 to javafx.fxml;
    exports itmo.lab8;
    exports itmo.lab8.ui.controllers;
    opens itmo.lab8.ui.controllers to javafx.fxml;
    exports itmo.lab8.ui;
    opens itmo.lab8.ui to javafx.fxml;
}