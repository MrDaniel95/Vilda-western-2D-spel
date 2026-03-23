package org.example.litetspel.bullets;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Rectangle {

    private double speed = 8;
    private int direction = 1; // 1 = höger, -1 = vänster

    public Bullet(double x, double y, int direction) {
        super(20, 6, Color.BLACK);
        this.direction = direction;

        setTranslateX(x);
        setTranslateY(y);
    }

    public void move() {
        setTranslateX(getTranslateX() + speed * direction);
    }

    public boolean isOutOfBounds(double worldWidth) {
        return getTranslateX() < 0 || getTranslateX() > worldWidth;
    }
}