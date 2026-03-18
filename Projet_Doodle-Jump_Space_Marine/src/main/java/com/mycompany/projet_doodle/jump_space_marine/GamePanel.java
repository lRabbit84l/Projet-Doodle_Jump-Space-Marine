/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.projet_doodle.jump_space_marine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.Timer;

/**
 *
 * @author Jacob
 */
public class GamePanel extends javax.swing.JPanel {

    private File fichierPersonnage = new File("src/main/java/images/Space_marine.png");
    private BufferedImage imagePersonnage;

    // Nouvelles variables pour les plateformes
    private List<Rectangle> plateformes;
    private Random random;
    private final int LARGEUR_PLATEFORME = 60;
    private final int HAUTEUR_PLATEFORME = 15;
    private final int NOMBRE_PLATEFORMES = 7; // Ajustez ce nombre selon la difficulté

    // --- NOUVELLES VARIABLES : PHYSIQUE ET JOUEUR ---
    private int persoX = 170; // Position de départ au centre-gauche
    private double persoY = 50;  // Position de départ en hauteur
    private double vitesseY = 0; // Vitesse verticale initiale
    private final double GRAVITE = 0.4; // Force de la gravité (attire vers le bas)
    private final double FORCE_SAUT = -9.5; // Force du saut (négative car l'axe Y monte vers le bas en Java)

    // Taille approximative de votre image pour calculer les collisions (à ajuster selon votre image réelle)
    private final int LARGEUR_PERSO = 40;
    private final int HAUTEUR_PERSO = 60;

    private Timer timer; // Le chronomètre qui fait tourner le jeu

    /**
     * Creates new form GamePanel
     */
    public GamePanel() {
        initComponents();
        try {
            imagePersonnage = ImageIO.read(fichierPersonnage);
        } catch (IOException ex) {
            System.out.println("fichier introuvable");
        }
        // Initialisation des plateformes
        plateformes = new ArrayList<>();
        random = new Random();
        genererPlateformes();

        // --- NOUVEAU : INITIALISATION DE LA BOUCLE DE JEU ---
        // Exécute le code toutes les 16 millisecondes (~60 FPS)
        timer = new Timer(16, e -> {
            mettreAJour(); // Calcule la nouvelle position
            repaint();     // Redessine l'écran
        });
        timer.start(); // Lance le jeu !
    }// Méthode pour créer des plateformes avec des positions X et Y aléatoires

    private void genererPlateformes() {
        // La taille par défaut de votre panel est 400x300 selon NetBeans
        int largeurPanel = 400;
        int hauteurPanel = 300;

        // On place une plateforme de départ juste sous le joueur pour qu'il ne tombe pas dans le vide au lancement
        plateformes.add(new Rectangle(150, 250, LARGEUR_PLATEFORME, HAUTEUR_PLATEFORME));

        for (int i = 0; i < NOMBRE_PLATEFORMES; i++) {
            // Génère un X aléatoire pour que la plateforme reste dans l'écran
            int x = random.nextInt(largeurPanel - LARGEUR_PLATEFORME);
            // Génère un Y aléatoire réparti sur la hauteur de l'écran
            int y = random.nextInt(hauteurPanel);

            plateformes.add(new Rectangle(x, y, LARGEUR_PLATEFORME, HAUTEUR_PLATEFORME));
        }
    }
    // --- NOUVELLE MÉTHODE : LA LOGIQUE DE PHYSIQUE ---

    private void mettreAJour() {
        // 1. Appliquer la gravité à la vitesse du personnage
        vitesseY += GRAVITE;

        // 2. Modifier la position Y du personnage en fonction de sa vitesse
        persoY += vitesseY;

        // 3. Créer une "boîte de collision" virtuelle autour du personnage
        Rectangle rectPerso = new Rectangle(persoX, (int) persoY, LARGEUR_PERSO, HAUTEUR_PERSO);

        // 4. Gérer les collisions avec les plateformes
        // On ne rebondit QUE si le personnage est en train de tomber (vitesseY > 0)
        if (vitesseY > 0) {
            for (Rectangle plateforme : plateformes) {
                // Si la boîte du joueur croise la boîte d'une plateforme
                if (rectPerso.intersects(plateforme)) {
                    // On modifie la vitesse pour simuler un saut vers le haut !
                    vitesseY = FORCE_SAUT;
                    break; // Un seul rebond suffit par image
                }
            }
        }

        // Sécurité temporaire : Si le joueur tombe tout en bas de la fenêtre, on le fait rebondir
        if (persoY > 300) {
            vitesseY = FORCE_SAUT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody

        // Dessiner les plateformes
        g.setColor(Color.GREEN);
        for (Rectangle plateforme : plateformes) {
            g.fillRect(plateforme.x, plateforme.y, plateforme.width, plateforme.height);
            g.setColor(Color.BLACK);
            g.drawRect(plateforme.x, plateforme.y, plateforme.width, plateforme.height);
            g.setColor(Color.GREEN);
        }

        // Dessiner le Space Marine avec ses nouvelles coordonnées dynamiques (persoX, persoY)
        if (imagePersonnage != null) {
            g.drawImage(imagePersonnage, persoX, (int) persoY, null);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
