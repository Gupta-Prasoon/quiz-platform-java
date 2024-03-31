module TestingJavaFX 
{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    opens com.quizplatform to javafx.base, javafx.controls, javafx.fxml, javafx.graphics, javafx.media, javafx.swing, javafx.web;
    exports com.quizplatform;
}