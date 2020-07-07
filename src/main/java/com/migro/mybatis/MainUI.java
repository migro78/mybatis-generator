package com.migro.mybatis;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

/**
 * <p>
 * mybatis generator main ui
 * </p>
 *
 * @author migro
 * @since 2020/2/15 13:50
 */
public class MainUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/MainUI.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        //scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());

        primaryStage.getIcons().add(new Image("icons/mybatis-logo.png"));
        primaryStage.setTitle("Mybatis实体文件自动生成器v1.0");
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
