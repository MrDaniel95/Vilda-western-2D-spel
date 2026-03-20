package org.example.litetspel.bullets;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Bullet extends Rectangle {

    private double speed = 6;

    public Bullet(double x, double y) {
        super(15, 5, Color.BLACK);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void move() {
        setTranslateX(getTranslateX() + speed);
    }

    public boolean isOutOfScreen(double screenWidth) {
        return getTranslateX() > screenWidth;
    }
}