module com.wildlife.eco_simulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.wildlife to javafx.graphics, javafx.fxml;

    exports com.wildlife;
    exports com.wildlife.model.base;
    exports com.wildlife.model.animal;
    exports com.wildlife.model.plant;
    exports com.wildlife.model.environment;
    exports com.wildlife.model.enums;
    exports com.wildlife.strategy;
    exports com.wildlife.factory;
    exports com.wildlife.controller;
    exports com.wildlife.view;
    exports com.wildlife.engine;
    exports com.wildlife.sound;
    exports com.wildlife.observer;
}
