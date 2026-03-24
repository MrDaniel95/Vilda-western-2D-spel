package org.example.litetspel.bullets;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bullet extends ImageView {

    private double speed = 10;
    private int direction;

    public Bullet(double x, double y, int direction) {
        super(new Image(Bullet.class.getResourceAsStream("/images/bullet.png")));

        this.direction = direction;

        setFitWidth(30);
        setFitHeight(10);

        setTranslateX(x);
        setTranslateY(y);

        // vänd bilden beroende på riktning
        setScaleX(direction);
    }

    public void move() {
        setTranslateX(getTranslateX() + speed * direction);
    }

    public boolean isOutOfBounds(double worldWidth) {
        return getTranslateX() < -50 || getTranslateX() > worldWidth + 50;
    }
}