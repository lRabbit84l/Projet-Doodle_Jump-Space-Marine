/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projet_doodle.jump_space_marine;

/**
 *
 * @author raphv
 */
public class Doodler {
    
    private int x, y, width = 40, height = 40;
    private double velocityY = 0;
    private double gravity = 0.6;
    private double jumpStrength = -15;

    public Doodler(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    

    public void update() {
        velocityY += gravity; // La gravité tire vers le bas
        y += velocityY;       // Mise à jour de la position
    }

    public void jump() {
        velocityY = jumpStrength; // Impulsion vers le haut
    }
}
    

