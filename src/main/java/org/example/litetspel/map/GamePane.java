package org.example.litetspel.map;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.litetspel.bullets.Bullet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.media.AudioClip;

public class GamePane extends Pane {
    private Image cowboyIdle;
    private Image cowboyWalk1;
    private Image cowboyWalk2;

    private AudioClip gunshotSound;

    private List<Bullet> bullets = new ArrayList<>();
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

        player = new ImageView(cowboyIdle);
        player.setFitWidth(PLAYER_WIDTH);
        player.setFitHeight(PLAYER_HEIGHT);
        player.setTranslateX(100);
        player.setTranslateY(groundY);

        world.getChildren().addAll(background, player, muzzleFlash);
        getChildren().add(world);

        setupControls();
        startGameLoop();
    }

    private void setupControls() {
        setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A -> left = true;
                case D -> right = true;
                case SPACE -> shoot();
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
        animatePlayer();
        updateBullets();
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
        bullets.add(bullet);
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