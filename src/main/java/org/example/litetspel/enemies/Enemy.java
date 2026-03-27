package org.example.litetspel.enemies;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Enemy extends ImageView {

    private double speed = 1.5;

    public Enemy(double x, double y) {
        super(new Image(Enemy.class.getResourceAsStream("/images/enemy.png")));

        setFitWidth(120);
        setFitHeight(120);

        setTranslateX(x);
        setTranslateY(y);
    }

    public void moveTowards(double targetX) {
        if (getTranslateX() > targetX) {
            setTranslateX(getTranslateX() - speed);
            setScaleX(-1);
        } else {
            setTranslateX(getTranslateX() + speed);
            setScaleX(1);
        }
    }
}
