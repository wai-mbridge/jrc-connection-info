package utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class AlertUtil {

    /* ---------- BASIC ALERT ---------- */
    public static void showAlert(String title, String message, AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            setIcon(stage);

            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /* ---------- DOWNLOAD COMPLETE DIALOG ---------- */
    public static void showDownloadCompleteDialog(File file) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            setIcon(stage);

            alert.setTitle("ダウンロード完了");
            alert.setHeaderText(null);
            alert.setContentText(file.getName() + "をダウンロードしました。");

            ButtonType openFileButton = new ButtonType("ファイルを開く");
            ButtonType openFolderButton = new ButtonType("フォルダーを開く");
            ButtonType closeButton = new ButtonType("閉じる");

            alert.getButtonTypes().setAll(openFileButton, openFolderButton, closeButton);

            String css = AlertUtil.class.getResource("/css/customAlert.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);

            alert.showAndWait().ifPresent(response -> {
                if (response == openFileButton) {
                    openFile(file);
                } else if (response == openFolderButton) {
                    openFolder(file);
                }
            });
        });
    }

    /* ---------- PRIVATE HELPERS ---------- */
    private static void openFile(File file) {
        try {
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                showAlert("エラー", "ファイルが見つかりません！", AlertType.ERROR);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("エラー", "ファイルを開けませんでした!", AlertType.ERROR);
        }
    }

    private static void openFolder(File file) {
        try {
            if (file.exists()) {
                Desktop.getDesktop().open(file.getParentFile());
            } else {
                showAlert("エラー", "ファイルが見つかりません！", AlertType.ERROR);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("エラー", "フォルダを開けませんでした!", AlertType.ERROR);
        }
    }

    private static void setIcon(Stage stage) {
        InputStream imageStream = AlertUtil.class.getClassLoader().getResourceAsStream("icons/jr.jpg");
        if (imageStream != null) {
            stage.getIcons().add(new Image(imageStream));
        }
    }
}
