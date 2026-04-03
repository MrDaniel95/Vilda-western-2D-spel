package org.example.litetspel.engine;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.litetspel.bullets.Bullet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.media.AudioClip;
import org.example.litetspel.enemies.Enemy;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;

public class GamePane extends Pane {
    private Image cowboyIdle;
    private Image cowboyWalk1;
    private Image cowboyWalk2;

    private int lives = 3;
    private final int maxLives = 3;
    private boolean gameOver = false;

    private Text livesText;
    private Text gameOverText;

    private Text restartText;
    private Rectangle healthBarBackground;
    private Rectangle healthBarFill;

    private int score = 0;
    private Text scoreText;

    private AudioClip gunshotSound;

    private List<Bullet> playerBullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();
    private int playerDirection = 1; // 1 = höger, -1 = vänster

    private Image cowboyShoot;
    private int shootTimer = 0;
    private Image muzzleFlashImage;
    private ImageView muzzleFlash;
    private int flashTimer = 0;
    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_MAX = 20;
    private double recoilOffset = 0;

    private int animationCounter = 0;
    private boolean isMoving = false;

    private final double WINDOW_WIDTH = 800;
    private final double WINDOW_HEIGHT = 600;

    private final double WORLD_WIDTH = 2500;
    private final double WORLD_HEIGHT = 600;

    private final double PLAYER_WIDTH = 120;
    private final double PLAYER_HEIGHT = 120;

    private List<Enemy> enemies = new ArrayList<>();

    private long lastEnemySpawnTime = 0;
    private final long enemySpawnInterval = 2_000_000_000L; // 2 sekunder

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

        cowboyIdle = new Image(getClass().getResourceAsStream("/images/cowboy_idle.png"));
        cowboyWalk1 = new Image(getClass().getResourceAsStream("/images/cowboy_walk1.png"));
        cowboyWalk2 = new Image(getClass().getResourceAsStream("/images/cowboy_walk2.png"));
        cowboyShoot = new Image(getClass().getResourceAsStream("/images/cowboy_shoot.png"));
        muzzleFlashImage = new Image(getClass().getResourceAsStream("/images/muzzle_flash.png"));

        muzzleFlash = new ImageView(muzzleFlashImage);
        muzzleFlash.setFitWidth(70);
        muzzleFlash.setFitHeight(50);
        muzzleFlash.setVisible(false);

        gunshotSound = new AudioClip(getClass().getResource("/sounds/gunshot.wav").toExternalForm());
        gunshotSound.setVolume(0.3);

        livesText = new Text("Lives: " + lives);
        livesText.setFont(Font.font(24));
        livesText.setFill(Color.WHITE);
        livesText.setTranslateX(20);
        livesText.setTranslateY(35);

        scoreText = new Text("Score: " + score);
        scoreText.setFont(Font.font(24));
        scoreText.setFill(Color.WHITE);
        scoreText.setTranslateX(650);
        scoreText.setTranslateY(35);

        gameOverText = new Text("GAME OVER");
        gameOverText.setFont(Font.font(48));
        gameOverText.setFill(Color.RED);
        gameOverText.setTranslateX(260);
        gameOverText.setTranslateY(220);
        gameOverText.setVisible(false);

        restartText = new Text("Press R to restart");
        restartText.setFont(Font.font(24));
        restartText.setFill(Color.WHITE);
        restartText.setTranslateX(270);
        restartText.setTranslateY(270);
        restartText.setVisible(false);

        healthBarBackground = new Rectangle(200, 20, Color.DARKRED);
        healthBarBackground.setTranslateX(20);
        healthBarBackground.setTranslateY(45);

        healthBarFill = new Rectangle(200, 20, Color.LIMEGREEN);
        healthBarFill.setTranslateX(20);
        healthBarFill.setTranslateY(45);


        player = new ImageView(cowboyIdle);
        player.setFitWidth(PLAYER_WIDTH);
        player.setFitHeight(PLAYER_HEIGHT);
        player.setTranslateX(100);
        player.setTranslateY(groundY);

        world.getChildren().addAll(background, player, muzzleFlash);
        getChildren().addAll(world, livesText, scoreText, healthBarBackground, healthBarFill, gameOverText, restartText);
        setupControls();
        startGameLoop();
        updateHealthBar();
    }

    private void setupControls() {
        setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A -> {
                    if (!gameOver) left = true;
                }
                case D -> {
                    if (!gameOver) right = true;
                }
                case SPACE -> {
                    if (!gameOver) shoot();
                }
                case R -> {
                    if (gameOver) restartGame();
                }
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
        if (gameOver) {
            updateCamera();
            return;
        }

        movePlayer();
        animatePlayer();
        updateBullets();
        spawnEnemies();
        updateEnemies();
        checkBulletEnemyCollisions();
        checkEnemyBulletPlayerCollisions();
        updateMuzzleFlash();
        updateShootCooldown();
        updateRecoil();
        updateCamera();
    }

    private void movePlayer() {
        double speed = 4;
        isMoving = false;

        if (left) {
            player.setTranslateX(player.getTranslateX() - speed);
            player.setScaleX(-1);
            playerDirection = -1;
            isMoving = true;
        }

        if (right) {
            player.setTranslateX(player.getTranslateX() + speed);
            player.setScaleX(1);
            playerDirection = 1;
            isMoving = true;
        }

        if (recoilOffset > 0) {
            player.setTranslateX(player.getTranslateX() - playerDirection * 1.5);
        }

        keepPlayerInsideWorld();
        player.setTranslateY(groundY);
    }

    private void animatePlayer() {
        if (shootTimer > 0) {
            player.setImage(cowboyShoot);
            shootTimer--;
            return;
        }

        if (isMoving) {
            animationCounter++;

            if (animationCounter < 18) {
                player.setImage(cowboyWalk1);
            } else if (animationCounter < 36) {
                player.setImage(cowboyIdle);
            } else if (animationCounter < 54) {
                player.setImage(cowboyWalk2);
            } else if (animationCounter < 72) {
                player.setImage(cowboyIdle);
            } else {
                animationCounter = 0;
            }
        } else {
            player.setImage(cowboyIdle);
            animationCounter = 0;
        }
    }

    private void shoot() {
        if (shootCooldown > 0) {
            return;
        }

        gunshotSound.play();
        double bulletX;
        double bulletY = player.getTranslateY() + 40;

        if (playerDirection == 1) {
            bulletX = player.getTranslateX() + 102;
        } else {
            bulletX = player.getTranslateX() + 18;
        }

        Bullet bullet = new Bullet(bulletX, bulletY, playerDirection);
        playerBullets.add(bullet);
        world.getChildren().add(bullet);

        shootTimer = 20;
        flashTimer = 6;
        shootCooldown = SHOOT_COOLDOWN_MAX;
        recoilOffset = 10;

        double flashX;
        double flashY = player.getTranslateY() + 22;

        if (playerDirection == 1) {
            flashX = player.getTranslateX() + 112;
            muzzleFlash.setScaleX(1);
        } else {
            flashX = player.getTranslateX() - 8;
            muzzleFlash.setScaleX(-1);
        }

        muzzleFlash.setTranslateX(flashX);
        muzzleFlash.setTranslateY(flashY);
        muzzleFlash.setVisible(true);
    }

    private void updateBullets() {
        updateBulletList(playerBullets);
        updateBulletList(enemyBullets);
    }

    private void updateBulletList(List<Bullet> bullets) {
        Iterator<Bullet> iterator = bullets.iterator();

        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();

            if (bullet.isOutOfBounds(WORLD_WIDTH)) {
                world.getChildren().remove(bullet);
                iterator.remove();
            }
        }
    }

    private void updateShootCooldown() {
        if (shootCooldown > 0) {
            shootCooldown--;
        }
    }

    private void updateRecoil() {
        if (recoilOffset > 0) {
            recoilOffset -= 1.5;
            if (recoilOffset < 0) {
                recoilOffset = 0;
            }
        }
    }

    private void updateMuzzleFlash() {
        if (flashTimer > 0) {
            flashTimer--;
        } else {
            muzzleFlash.setVisible(false);
        }
    }

    private void spawnEnemies() {
        long now = System.nanoTime();

        if (now - lastEnemySpawnTime > enemySpawnInterval) {
            double enemyX = player.getTranslateX() + 700;

            if (enemyX > WORLD_WIDTH - 120) {
                enemyX = WORLD_WIDTH - 120;
            }

            Enemy enemy = new Enemy(enemyX, groundY);
            enemies.add(enemy);
            world.getChildren().add(enemy);

            lastEnemySpawnTime = now;
        }
    }

    private void spawnEnemyBullet(Enemy enemy) {
        int direction = enemy.getDirection();

        double x;
        double y = enemy.getTranslateY() + 40;

        if (direction == 1) {
            x = enemy.getTranslateX() + 102;
        } else {
            x = enemy.getTranslateX() + 18;
        }

        Bullet bullet = new Bullet(x, y, direction);
        enemyBullets.add(bullet);
        world.getChildren().add(bullet);
        gunshotSound.play();
    }

    private void updateEnemies() {
        for (Enemy enemy : enemies) {
            enemy.moveTowards(player.getTranslateX());
            enemy.animate();

            if (Math.abs(enemy.getTranslateX() - player.getTranslateX()) < 220) {
                if (enemy.tryShoot()) {
                    spawnEnemyBullet(enemy);
                }
            }
        }
    }

    private void checkBulletEnemyCollisions() {
        Iterator<Bullet> bulletIterator = playerBullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            Iterator<Enemy> enemyIterator = enemies.iterator();

            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();

                if (bullet.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                    world.getChildren().remove(bullet);
                    world.getChildren().remove(enemy);

                    bulletIterator.remove();
                    enemyIterator.remove();

                    score++;
                    scoreText.setText("Score: " + score);

                    break;
                }
            }
        }
    }

    private void checkEnemyBulletPlayerCollisions() {
        Iterator<Bullet> bulletIterator = enemyBullets.iterator();

        while (bulletIterator.hasNext()) {
            Bullet bullet = bulletIterator.next();

            if (bullet.getBoundsInParent().intersects(player.getBoundsInParent())) {
                world.getChildren().remove(bullet);
                bulletIterator.remove();

                lives--;
                livesText.setText("Lives: " + lives);
                updateHealthBar();

                if (lives <= 0) {
                    gameOver = true;
                    left = false;
                    right = false;
                    gameOverText.setVisible(true);
                    restartText.setVisible(true);
                }
            }
        }
    }

    private void updateHealthBar() {
        double healthPercentage = (double) lives / maxLives;
        healthBarFill.setWidth(200 * healthPercentage);
    }

    private void restartGame() {
        for (Bullet bullet : playerBullets) {
            world.getChildren().remove(bullet);
        }
        playerBullets.clear();

        for (Bullet bullet : enemyBullets) {
            world.getChildren().remove(bullet);
        }
        enemyBullets.clear();

        for (Enemy enemy : enemies) {
            world.getChildren().remove(enemy);
        }
        enemies.clear();

        player.setTranslateX(100);
        player.setTranslateY(groundY);
        player.setScaleX(1);
        player.setImage(cowboyIdle);

        playerDirection = 1;
        left = false;
        right = false;
        isMoving = false;

        lives = maxLives;
        gameOver = false;
        score = 0;

        livesText.setText("Lives: " + lives);
        scoreText.setText("Score: " + score);
        updateHealthBar();

        gameOverText.setVisible(false);
        restartText.setVisible(false);
        muzzleFlash.setVisible(false);

        shootTimer = 0;
        flashTimer = 0;
        shootCooldown = 0;
        recoilOffset = 0;
        animationCounter = 0;

        lastEnemySpawnTime = 0;
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