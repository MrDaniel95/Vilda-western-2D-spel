package org.example.litetspel.map;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.example.litetspel.bullets.Bullet;
import org.example.litetspel.enemies.Enemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GamePane extends Pane {

    private Rectangle player;
    private boolean up, down, left, right;

    private List<Bullet> bullets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    private Random random = new Random();

    private int lives = 3;
    private boolean gameOver = false;

    private Text livesText;
    private Text gameOverText;

    private long lastEnemySpawnTime = 0;
    private final long enemySpawnInterval = 2_000_000_000L; // 2 sekunder

    public GamePane() {
        setPrefSize(800, 600);
        setStyle("-fx-background-color: beige;");

        player = new Rectangle(40, 40, Color.BROWN);
        player.setTranslateX(100);
        player.setTranslateY(100);

        livesText = new Text("Lives: " + lives);
        livesText.setFont(Font.font(24));
        livesText.setTranslateX(20);
        livesText.setTranslateY(30);

        gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font(50));
        gameOverText.setTranslateX(250);
        gameOverText.setTranslateY(300);
        gameOverText.setVisible(false);

        getChildren().addAll(player, livesText, gameOverText);

        setupControls();
        startGameLoop();
    }

    private void setupControls() {
        setOnKeyPressed(e -> {
            if (gameOver) return;

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

    private void spawnEnemy() {
        double x = 700 + random.nextInt(80);
        double y = random.nextInt(560);

        Enemy enemy = new Enemy(x, y);
        enemies.add(enemy);
        getChildren().add(enemy);
    }

    private void startGameLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    update(now);
                }
            }
        }.start();
    }

    private void update(long now) {
        movePlayer();
        updateBullets();
        spawnEnemiesOverTime(now);
        updateEnemies();
        checkBulletEnemyCollision();
        checkPlayerEnemyCollision();
    }

    private void movePlayer() {
        if (up) player.setTranslateY(player.getTranslateY() - 3);
        if (down) player.setTranslateY(player.getTranslateY() + 3);
        if (left) player.setTranslateX(player.getTranslateX() - 3);
        if (right) player.setTranslateX(player.getTranslateX() + 3);

        keepPlayerInsideScreen();
    }

    private void keepPlayerInsideScreen() {
        if (player.getTranslateX() < 0) player.setTranslateX(0);
        if (player.getTranslateY() < 0) player.setTranslateY(0);
        if (player.getTranslateX() > getPrefWidth() - 40) player.setTranslateX(getPrefWidth() - 40);
        if (player.getTranslateY() > getPrefHeight() - 40) player.setTranslateY(getPrefHeight() - 40);
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

    private void spawnEnemiesOverTime(long now) {
        if (now - lastEnemySpawnTime > enemySpawnInterval) {
            spawnEnemy();
            lastEnemySpawnTime = now;
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveTowards(player.getTranslateX(), player.getTranslateY());
        }
    }

    private void checkBulletEnemyCollision() {
        Iterator<Bullet> bulletIterator = bullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    getChildren().remove(bullet);
                    getChildren().remove(enemy);

                    bulletIterator.remove();
                    enemyIterator.remove();
                    break;
                }
            }
        }
    }

    private void checkPlayerEnemyCollision() {
        Iterator<Enemy> iterator = enemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                getChildren().remove(enemy);
                iterator.remove();

                lives--;
                livesText.setText("Lives: " + lives);

                if (lives <= 0) {
                    gameOver = true;
                    gameOverText.setVisible(true);
                }
            }
        }
    }
}