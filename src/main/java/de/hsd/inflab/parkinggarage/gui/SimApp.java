package de.hsd.inflab.parkinggarage.gui;

import de.hsd.inflab.parkinggarage.models.vehicles.VehicleTypes;
import de.hsd.inflab.parkinggarage.parking.Lot;
import de.hsd.inflab.parkinggarage.parking.ParkingSpot;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Entrance;
import de.hsd.inflab.parkinggarage.parking.gate.iface.Exit;
import de.hsd.inflab.parkinggarage.simulator.SimClock;
import de.hsd.inflab.parkinggarage.simulator.Simulator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class SimApp extends Application {

    private static final int COLS = 10;
    private static final int CELL = 34;
    private static final int GAP = 4;

    private static final Color FREE_COLOR = Color.web("#e0e0e0");

    private Simulator sim;
    private Lot lot;
    private Canvas grid;
    private Label timeLabel;
    private Label occupancyLabel;
    private Label transactionLabel;
    private Timeline timeline;

    @Override
    public void start(Stage stage) {
        SimClock clock = new SimClock(LocalDateTime.now());
        lot = Lot.buildLot(clock);
        sim = new Simulator(lot, clock, new Random());

        int rows = (lot.getCapacity() + COLS - 1) / COLS;
        grid = new Canvas(COLS * (CELL + GAP) + GAP, rows * (CELL + GAP) + GAP);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setCenter(grid);
        root.setRight(buildSidebar());
        root.setBottom(buildBottom());

        draw();
        updateLabels();

        stage.setTitle("Parkhaus-Simulator");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox buildSidebar() {
        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        occupancyLabel = new Label();
        occupancyLabel.setStyle("-fx-font-size: 14px;");

        VBox legend = new VBox(6,
                legendRow(VehicleTypes.CAR),
                legendRow(VehicleTypes.SEMI),
                legendRow(VehicleTypes.MOTORCYCLE));

        VBox box = new VBox(14, timeLabel, occupancyLabel, buildTargetOccupancy(),
                buildGateSelection(), new Label("Legende:"), legend);
        box.setPadding(new Insets(0, 0, 0, 20));
        box.setPrefWidth(200);
        return box;
    }

    private VBox buildGateSelection() {
        List<Entrance> entrances = Lot.availableEntrances();
        ComboBox<Entrance> entranceBox = new ComboBox<>();
        entranceBox.getItems().addAll(entrances);
        entranceBox.setConverter(classNameConverter());
        entranceBox.setMaxWidth(Double.MAX_VALUE);
        entranceBox.setValue(entrances.get(0));
        lot.setEntrance(entrances.get(0));
        entranceBox.setOnAction(e -> {
            Entrance selected = entranceBox.getValue();
            if (selected != null)
                lot.setEntrance(selected);
        });

        List<Exit> exits = Lot.availableExits();
        ComboBox<Exit> exitBox = new ComboBox<>();
        exitBox.getItems().addAll(exits);
        exitBox.setConverter(classNameConverter());
        exitBox.setMaxWidth(Double.MAX_VALUE);
        exitBox.setValue(exits.get(0));
        lot.setExit(exits.get(0));
        exitBox.setOnAction(e -> {
            Exit selected = exitBox.getValue();
            if (selected != null)
                lot.setExit(selected);
        });

        return new VBox(4, new Label("Einfahrt-Logik:"), entranceBox,
                new Label("Ausfahrt-Logik:"), exitBox);
    }

    private static <T> StringConverter<T> classNameConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : value.getClass().getSimpleName();
            }

            @Override
            public T fromString(String name) {
                return null;        // Dropdown ist nicht editierbar
            }
        };
    }

    private VBox buildTargetOccupancy() {
        Label caption = new Label("Ziel-Auslastung:");
        Label value = new Label();

        Slider slider = new Slider(0, 200, sim.getTargetOccupancyRatio() * 100);
        slider.setPrefWidth(160);
        slider.valueProperty().addListener((obs, old, val) -> {
            sim.setTargetOccupancyRatio(val.doubleValue() / 100.0);
            value.setText(val.intValue() + " %");
        });
        value.setText((int) Math.round(slider.getValue()) + " %");

        return new VBox(4, caption, slider, value);
    }

    private HBox legendRow(VehicleTypes type) {
        Rectangle swatch = new Rectangle(16, 16, colorFor(type));
        swatch.setArcWidth(4);
        swatch.setArcHeight(4);
        HBox row = new HBox(8, swatch, new Label(labelFor(type)));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildBottom() {
        Button playPause = new Button("Start");
        playPause.setPrefWidth(80);
        playPause.setOnAction(e -> {
            if (timeline.getStatus() == Animation.Status.RUNNING) {
                timeline.pause();
                playPause.setText("Start");
            } else {
                timeline.play();
                playPause.setText("Pause");
            }
        });

        Slider speed = new Slider(1, 10, 2);
        speed.setPrefWidth(200);
        speed.valueProperty().addListener((obs, old, val) -> timeline.setRate(val.doubleValue()));
        timeline.setRate(speed.getValue());

        HBox controls = new HBox(12, playPause, new Label("Tempo:"), speed);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(12, 0, 8, 0));

        transactionLabel = new Label();
        transactionLabel.setStyle(
                "-fx-font-size: 13px; -fx-padding: 10; -fx-background-color: #f4f4f4; "
                + "-fx-border-color: #bbbbbb; -fx-border-radius: 4; -fx-background-radius: 4;");
        transactionLabel.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(4, controls, new Label("Letzte Transaktion:"), transactionLabel);
        box.setPadding(new Insets(8, 0, 0, 0));
        return box;
    }

    private void tick() {
        sim.step();
        draw();
        updateLabels();
    }

    private void draw() {
        GraphicsContext gc = grid.getGraphicsContext2D();
        gc.clearRect(0, 0, grid.getWidth(), grid.getHeight());

        ParkingSpot[] spots = lot.getSpots();
        for (int i = 0; i < spots.length; i++) {
            int col = i % COLS;
            int row = i / COLS;
            double x = GAP + col * (CELL + GAP);
            double y = GAP + row * (CELL + GAP);
            if(spots[i].isFree())
                gc.setFill(FREE_COLOR);
            else
                gc.setFill(colorFor(spots[i].getOccupant().getVehicleType()));
            gc.fillRoundRect(x, y, CELL, CELL, 6, 6);
        }
    }

    private void updateLabels() {
        timeLabel.setText("Uhrzeit: " + sim.getTime());
        int occupied = lot.countVehicles();
        int capacity = lot.getCapacity();
        int percent = capacity == 0 ? 0 : occupied * 100 / capacity;
        occupancyLabel.setText("Belegung: " + occupied + " / " + capacity + " (" + percent + " %)");
        transactionLabel.setText(sim.getLastTransaction());
    }

    private static Color colorFor(VehicleTypes type) {
        return switch (type) {
            case CAR -> Color.web("#3d7de0");
            case SEMI -> Color.web("#e08a3d");
            case MOTORCYCLE -> Color.web("#57b84a");
        };
    }

    private static String labelFor(VehicleTypes type) {
        return switch (type) {
            case CAR -> "PKW";
            case SEMI -> "LKW";
            case MOTORCYCLE -> "Motorrad";
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
