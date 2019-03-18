package pb;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

    public MenuItem importFile;
    private File file;
    private double originalW, originalH;
    private Image originalImage;
    private double scale;
    private int originalPositionX, originalPositionY;
    private String path, fileName, fileExtension;

    @FXML
    private MenuBar menuBar;
    @FXML
    private ImageView originalImageView;
    @FXML
    private ImageView editedImageView;
    @FXML
    private ImageView histogramView;
    @FXML
    private Slider zoomSlider;
    @FXML
    public Label rgbLabel, positionXLabel, positionYLabel;
    @FXML
    public TextField pixelX, pixelY, pixelRed, pixelGreen, pixelBlue;
    @FXML
    public Button saveBtn, histogramBtn;



    @FXML
    public void doHistogram(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("histogram.fxml"));
        create(originalImage);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Biometria excercise1");
        primaryStage.setScene(new Scene(root, 900, 900));
        primaryStage.show();
    }

    public void create(Image originalImage)
    {

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<String, Number> chartHistogram
                = new LineChart<>(xAxis, yAxis);
        chartHistogram.setCreateSymbols(false);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(chartHistogram);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(hBox);

        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        chartHistogram.getData().clear();
        ImageHistogram imageHistogram = new ImageHistogram(originalImage);
        if(imageHistogram.isSuccess()){
            chartHistogram.getData().addAll(
                    //imageHistogram.getSeriesAlpha(),
                    imageHistogram.getSeriesRed(),
                    imageHistogram.getSeriesGreen(),
                    imageHistogram.getSeriesBlue());
        }
    }


    @FXML
    public void importFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser(); //filechooser dialog i mozliwe w nim rozszerzenia do plikow
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG (*.jpg;*.jpeg)", "*.jpg", ".jpeg"),
                new FileChooser.ExtensionFilter("GIF (*.gif)", "*.gif"),
                new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp"),
                new FileChooser.ExtensionFilter("TIFF (*.tiff;*.tif)", "*.tiff", "*.tif"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*"));

        file = fileChooser.showOpenDialog((Stage) menuBar.getScene().getWindow());
        if (file != null) {
            BufferedImage img = null;
            fileName = file.getName();
            try {
                img = ImageIO.read(file); //wczytanie obrazu
            } catch (IOException e) {
                System.out.println("Blad wczytywania pliku");
            }
            Image image = SwingFXUtils.toFXImage(img, null); //konwrsja typow z swinga do FX
            originalW = image.getWidth();
            originalH = image.getHeight();

            //ustaleie rozmiarow
            originalImageView.setFitWidth(originalW);
            originalImageView.setFitHeight(originalH);
            editedImageView.setFitWidth(originalW);
            editedImageView.setFitHeight(originalH);


            //podczepienie obrazu pod view
            originalImage = image;
            originalImageView.setImage(originalImage);
            editedImageView.setImage(originalImage);

            //domyslne skalowanie na oryginalny rozmiar
            zoomSlider.setValue(1);
            zoomImage();
        }
    }

    @FXML
    public void saveFile() {
        if (editedImageView == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("No picture to save");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG (*.png)", "*.png"),
                new FileChooser.ExtensionFilter("JPEG (*.jpg;*.jpeg)", "*.jpg", ".jpeg"),
                new FileChooser.ExtensionFilter("GIF (*.gif)", "*.gif"),
                new FileChooser.ExtensionFilter("BMP (*.bmp)", "*.bmp"),
                new FileChooser.ExtensionFilter("TIFF (*.tiff;*.tif)", "*.tiff", "*.tif"),
                new FileChooser.ExtensionFilter("Wszystkie pliki", "*.*"));

        file = fileChooser.showSaveDialog((Stage) menuBar.getScene().getWindow());

        if (file != null) {
            path = file.getAbsolutePath();
            fileName = file.getName();
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, file.getName().length());
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(editedImageView.snapshot(null, null), null);
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, null);
            try {
                ImageIO.write(newBufferedImage, fileExtension, file);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Save ");
                alert.showAndWait();
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Error");
                alert.showAndWait();
            }
        }
    }

    @FXML
    void getMousePosition(MouseEvent event) {
        if (originalImageView.getImage() != null) {
            //pozycja na oryginalnym obrazie
            originalPositionX = (int) (event.getX() / scale);
            originalPositionY = (int) (event.getY() / scale);

            //zabezpieczenie przed wyjsciem z obrazu cofa do granic
            if (originalPositionX >= originalW)
                originalPositionX = (int) originalW - 1;
            if (originalPositionY >= originalH)
                originalPositionY = (int) originalH - 1;
            positionXLabel.setText("X: " + originalPositionX);
            positionYLabel.setText("Y: " + originalPositionY);
            rgbLabel.setText("R: " + getRedColor() + " G: " + getGreenColor() + " B: " + getBlueColor());
        }
    }

    @FXML
    public void zoomImage() {
        scale = zoomSlider.getValue();
        originalImageView.setFitWidth(originalW * scale);
        originalImageView.setFitHeight(originalH * scale);
        editedImageView.setFitWidth(originalW * scale);
        editedImageView.setFitHeight(originalH * scale);
    }

    @FXML
    public void choosePixel(MouseEvent mouseEvent) {
        if (originalImageView.getImage() == null)
            return;

        pixelX.setText(originalPositionX + "");
        pixelY.setText(originalPositionY + "");
    }

    @FXML
    public void setNewRGB() {
        if (editedImageView == null)
            return;

        int px = Integer.parseInt(pixelX.getText());
        int py = Integer.parseInt(pixelY.getText());

        if (!(between(px, 0, (int) originalW) && (between(py, 0, (int) originalH))))
            return;

        int red = Integer.parseInt(pixelRed.getText());
        int green = Integer.parseInt(pixelGreen.getText());
        int blue = Integer.parseInt(pixelBlue.getText());

        if (!((between(red, 0, 255) && between(green, 0, 255) && between(blue, 0, 255))))
            return;

        Color c = Color.rgb(red, green, blue);
        WritableImage writableImage = new WritableImage(editedImageView.getImage().getPixelReader(), (int) originalW, (int) originalH);
        writableImage.getPixelWriter().setColor(px, py, c);
        editedImageView.setImage(writableImage);
    }

    int getRedColor() {
        return (int) (originalImageView.getImage().getPixelReader().getColor(originalPositionX, originalPositionY).getRed() * 255);
    }

    int getGreenColor() {
        return (int) (originalImageView.getImage().getPixelReader().getColor(originalPositionX, originalPositionY).getGreen() * 255);
    }

    int getBlueColor() {
        return (int) (originalImageView.getImage().getPixelReader().getColor(originalPositionX, originalPositionY).getBlue() * 255);
    }

    private boolean between(int i, int minValue, int maxValue) {
        return i >= minValue && i <= maxValue;
    }



}