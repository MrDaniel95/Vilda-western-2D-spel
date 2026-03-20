package org.example.litetspel.map;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.example.litetspel.bullets.Bullet;
import org.example.litetspel.enemies.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePane extends Pane {

    private Rectangle player;
    private Enemy enemy;

    private boolean up, down, left, right;
    private List<Bullet> bullets = new ArrayList<>();

    public GamePane() {
        setPrefSize(800, 600);
        setStyle("-fx-background-color: beige;");

        player = new Rectangle(40, 40, Color.BROWN);
        player.setTranslateX(100);
        player.setTranslateY(100);

        enemy = new Enemy(600, 200);

        getChildren().addAll(player, enemy);

        setupControls();
        startGameLoop();
    }

    private void setupControls() {
        setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> up = true;
                case S -> down = true;
                case A -> left = true;
                case D -> right = true;
                case SPACE -> shoot();
            }
        });

        setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> up = false;
                case S -> down = false;
                case A -> left = false;
                case D -> right = false;
            }
        });
    }

    private void shoot() {
        double bulletX = player.getTranslateX() + 40;
        double bulletY = player.getTranslateY() + 18;

        Bullet bullet = new Bullet(bulletX, bulletY);
        bullets.add(bullet);
        getChildren().add(bullet);
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
        if (up) player.setTranslateY(player.getTranslateY() - 3);
        if (down) player.setTranslateY(player.getTranslateY() + 3);
        if (left) player.setTranslateX(player.getTranslateX() - 3);
        if (right) player.setTranslateX(player.getTranslateX() + 3);

        updateBullets();
        checkBulletEnemyCollision();
    }

    private void updateBullets() {
        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();

            if (bullet.isOutOfScreen(getPrefWidth())) {
                getChildren().remove(bullet);
                iterator.remove();
            }
        }
    }

    private void checkBulletEnemyCollision() {
        if (enemy == null) {
            return;
        }

        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();

            if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                getChildren().remove(bullet);
                iterator.remove();

                getChildren().remove(enemy);
                enemy = null;

                break;
            }
        }
    }
}