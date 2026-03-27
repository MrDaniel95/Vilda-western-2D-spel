package org.example.litetspel.enemies;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy extends ImageView {

    private final Image enemyIdle;
    private final Image enemyWalk1;
    private final Image enemyWalk2;

    private int animationCounter = 0;
    private boolean isMoving = false;

    private final double speed = 1.5;

    public Enemy(double x, double y) {
        enemyIdle = new Image(getClass().getResourceAsStream("/images/enemy_idle.png"));
        enemyWalk1 = new Image(getClass().getResourceAsStream("/images/enemy_walk1.png"));
        enemyWalk2 = new Image(getClass().getResourceAsStream("/images/enemy_walk2.png"));

        setImage(enemyIdle);
        setFitWidth(120);
        setFitHeight(120);

        setTranslateX(x);
        setTranslateY(y);
    }

    public void moveTowards(double targetX) {
        isMoving = false;

        if (Math.abs(getTranslateX() - targetX) > 10) {
            if (getTranslateX() > targetX) {
                setTranslateX(getTranslateX() - speed);
                setScaleX(1);   // vänster
            } else {
                setTranslateX(getTranslateX() + speed);
                setScaleX(-1);  // höger
            }
            isMoving = true;
        }
    }

    public void animate() {
        if (isMoving) {
            animationCounter++;

            if (animationCounter < 18) {
                setImage(enemyWalk1);
            } else if (animationCounter < 36) {
                setImage(enemyIdle);
            } else if (animationCounter < 54) {
                setImage(enemyWalk2);
            } else if (animationCounter < 72) {
                setImage(enemyIdle);
            } else {
                animationCounter = 0;
            }
        } else {
            setImage(enemyIdle);
            animationCounter = 0;
        }
    }
}
