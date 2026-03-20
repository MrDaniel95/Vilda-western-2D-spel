package org.example.litetspel.enemies;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Enemy extends Rectangle {

    private double speed = 1.2;

    public Enemy(double x, double y) {
        super(40, 40, Color.DARKRED);
        setTranslateX(x);
        setTranslateY(y);
    }

    public void moveTowards(double targetX, double targetY) {
        double dx = targetX - getTranslateX();
        double dy = targetY - getTranslateY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            setTranslateX(getTranslateX() + (dx / distance) * speed);
            setTranslateY(getTranslateY() + (dy / distance) * speed);
        }
    }
}
