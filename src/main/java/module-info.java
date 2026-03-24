module org.example.litetspel {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.media;

    opens org.example.litetspel to javafx.fxml;
    exports org.example.litetspel;
    exports org.example.litetspel.map;
    opens org.example.litetspel.map to javafx.fxml;
    exports org.example.litetspel.bullets;
    opens org.example.litetspel.bullets to javafx.fxml;
    exports org.example.litetspel.enemies;
    opens org.example.litetspel.enemies to javafx.fxml;
}