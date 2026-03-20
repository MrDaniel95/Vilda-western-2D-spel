package org.example.litetspel.map;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GamePane extends Pane {

    private final double WINDOW_WIDTH = 800;
    private final double WINDOW_HEIGHT = 600;

    private final double WORLD_WIDTH = 2500;
    private final double WORLD_HEIGHT = 600;

    private final double PLAYER_WIDTH = 120;
    private final double PLAYER_HEIGHT = 120;

    private Pane world;
    private ImageView background;
    private ImageView player;

    private boolean left, right;

    private final double groundY = 430;

    public GamePane() {
        setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        world = new Pane();
        world.setPrefSize(WORLD_WIDTH, WORLD_HEIGHT);

        Image bgImage = new Image(getClass().getResourceAsStream("/images/background.png"));
        background = new ImageView(bgImage);
        background.setFitWidth(WORLD_WIDTH);
        background.setFitHeight(WORLD_HEIGHT);

        Image playerImage = new Image(getClass().getResourceAsStream("/images/cowboy.png"));
        player = new ImageView(playerImage);
        player.setFitWidth(PLAYER_WIDTH);
        player.setFitHeight(PLAYER_HEIGHT);
        player.setTranslateX(100);
        player.setTranslateY(groundY);

        world.getChildren().addAll(background, player);
        getChildren().add(world);

        setupControls();
        startGameLoop();
    }

    private void setupControls() {
        setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A -> left = true;
                case D -> right = true;
            }
        });

        setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case A -> left = false;
                case D -> right = false;
            }
        });
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        }.start();
    }

    private void update() {
        movePlayer();
        updateCamera();
    }

    private void movePlayer() {
        double speed = 4;

        if (left) {
            player.setTranslateX(player.getTranslateX() - speed);
        }
        if (right) {
            player.setTranslateX(player.getTranslateX() + speed);
        }

        keepPlayerInsideWorld();

        player.setTranslateY(groundY);
    }

    private void keepPlayerInsideWorld() {
        if (player.getTranslateX() < 0) {
            player.setTranslateX(0);
        }

        if (player.getTranslateX() > WORLD_WIDTH - PLAYER_WIDTH) {
            player.setTranslateX(WORLD_WIDTH - PLAYER_WIDTH);
        }
    }

    private void updateCamera() {
        double cameraX = player.getTranslateX() - WINDOW_WIDTH / 2 + PLAYER_WIDTH / 2;

        if (cameraX < 0) {
            cameraX = 0;
        }

        if (cameraX > WORLD_WIDTH - WINDOW_WIDTH) {
            cameraX = WORLD_WIDTH - WINDOW_WIDTH;
        }

        world.setLayoutX(-cameraX);
    }
}