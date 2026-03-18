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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends javax.swing.JPanel implements KeyListener {

    private File fichierPersonnage = new File("src/main/java/images/Space_marine.png");
    private BufferedImage imagePersonnage;
    private File fichierPlatVerte = new File("src/main/java/images/plateforme_verte.png");
    private BufferedImage imagePlatVerte;
    private File fichierPlatBleue = new File("src/main/java/images/plateforme_bleue.png");
    private BufferedImage imagePlatBleue;
    private File fichierPlatRouge = new File("src/main/java/images/plateforme_rouge.png");
    private BufferedImage imagePlatRouge;

    private File fichierFond = new File("src/main/java/images/Fond.png");
    private BufferedImage imageFond;

    private List<Plateforme> plateformes;
    private Random random;

    private final int LARGEUR_PLATEFORME = 87;
    private final int HAUTEUR_PLATEFORME = 23;
    private final int NOMBRE_PLATEFORMES = 7;
    private final int ECART_Y = 120; // L'écart parfait, fixe et définitif !

    private int persoX = 220; // Au centre (500/2 environ)
    private double persoY = 600; // En bas de l'écran
    private double vitesseY = -10.5; // Il saute automatiquement !

    private final double GRAVITE = 0.4;
    private final double FORCE_SAUT = -12.5;
    private final int LARGEUR_PERSO = 40;
    private final int HAUTEUR_PERSO = 60;

    private boolean toucheGauche = false;
    private boolean toucheDroite = false;
    private final int VITESSE_X = 6;

    private int score = 0;
    private boolean estGameOver = false;
    private Timer timer;

    // --- NOTRE CLASSE PLATEFORME ---
    class Plateforme extends Rectangle {

        int type; // 0=Verte, 1=Bleue, 2=Rouge
        int directionX = 2;
        boolean estDetruite = false;

        public Plateforme(int x, int y, int width, int height, int type) {
            super(x, y, width, height);
            this.type = type;
        }
    }

    public GamePanel() {
        initComponents();
        try {
            imagePersonnage = ImageIO.read(fichierPersonnage);
            imagePlatVerte = ImageIO.read(fichierPlatVerte);
            imagePlatBleue = ImageIO.read(fichierPlatBleue);
            imagePlatRouge = ImageIO.read(fichierPlatRouge);

            imageFond = ImageIO.read(fichierFond);

        } catch (IOException ex) {
            System.out.println("Un ou plusieurs fichiers images sont introuvables");
        }

        plateformes = new ArrayList<>();
        random = new Random();
        genererPlateformes();

        this.setFocusable(true);
        this.addKeyListener(this);

        timer = new Timer(16, e -> {
            mettreAJour();
            repaint();
        });
        timer.start();
    }

    private void recommencerJeu() {
        persoX = 220;
        persoY = 600;
        vitesseY = FORCE_SAUT; // Saute direct !
        score = 0;
        estGameOver = false;
        toucheGauche = false;
        toucheDroite = false;
        genererPlateformes();
    }

    private void genererPlateformes() {
        plateformes.clear();

        // 1. Plateforme de départ sous les pieds
        plateformes.add(new Plateforme(200, 650, LARGEUR_PLATEFORME, HAUTEUR_PLATEFORME, 0));

        // 2. On empile les autres avec l'écart fixe de 120
        for (int i = 1; i < NOMBRE_PLATEFORMES; i++) {
            int x = random.nextInt(400);
            int y = 650 - (i * ECART_Y);

            int chance = random.nextInt(100);
            int type = (chance > 90) ? 2 : ((chance > 70) ? 1 : 0);

            plateformes.add(new Plateforme(x, y, LARGEUR_PLATEFORME, HAUTEUR_PLATEFORME, type));
        }
    }

    private void mettreAJour() {
        if (estGameOver) {
            return;
        }

        if (toucheGauche) {
            persoX -= VITESSE_X;
        }
        if (toucheDroite) {
            persoX += VITESSE_X;
        }

        // La ligne médiane fixe (la moitié de 900)
        int ligneMediane = 450;

        if (persoY < ligneMediane) {
            double decalage = ligneMediane - persoY;
            persoY = ligneMediane;
            score += decalage;

            // On descend les plateformes
            for (Plateforme p : plateformes) {
                p.y += decalage;
            }

            // Recyclage infaillible
            for (Plateforme p : plateformes) {
                if (p.y > 900) {
                    // Trouve la plateforme la plus haute
                    int plusHautY = plateformes.get(0).y;
                    for (Plateforme plt : plateformes) {
                        if (plt.y < plusHautY) {
                            plusHautY = plt.y;
                        }
                    }

                    // Place la nouvelle EXACTEMENT 120 pixels au-dessus
                    p.y = plusHautY - ECART_Y;
                    p.x = random.nextInt(400);

                    int chance = random.nextInt(100);
                    p.type = (chance > 90) ? 2 : ((chance > 70) ? 1 : 0);
                    p.estDetruite = false;
                }
            }
        }

        // Wrap-around de l'écran
        if (persoX < -LARGEUR_PERSO) {
            persoX = 500;
        } else if (persoX > 500) {
            persoX = -LARGEUR_PERSO;
        }

        vitesseY += GRAVITE;
        persoY += vitesseY;

        // Fait bouger les plateformes bleues tout le temps
        for (Plateforme p : plateformes) {
            if (p.type == 1) {
                p.x += p.directionX;
                if (p.x <= 0 || p.x + p.width >= 500) {
                    p.directionX *= -1;
                }
            }
        }

        // Collisions
        Rectangle rectPerso = new Rectangle(persoX, (int) persoY, LARGEUR_PERSO, HAUTEUR_PERSO);

        // On ne vérifie les collisions que si le personnage tombe (vitesse positive vers le bas)
        if (vitesseY > 0) {
            for (Plateforme p : plateformes) {
                // Si on touche une plateforme qui n'est pas détruite
                if (!p.estDetruite && rectPerso.intersects(p)) {

                    // 1. On applique le rebond (on saute !)
                    vitesseY = FORCE_SAUT;

                    // 2. Si c'est une plateforme rouge, on la détruit
                    if (p.type == 2) {
                        p.estDetruite = true; // Pouf !
                    }

                    // 3. On a fait notre rebond, on arrête de vérifier les autres plateformes
                    break;
                }
            }
        }

        if (persoY > 900) {
            estGameOver = true;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imageFond != null) {
            // getWidth() et getHeight() permettent à l'image de prendre 
            // automatiquement toute la taille de ta fenêtre (500x900)
            g.drawImage(imageFond, 0, 0, getWidth(), getHeight(), null);
        }

        for (Plateforme p : plateformes) {
            if (p.estDetruite) {
                continue;
            }

            if (p.type == 0 && imagePlatVerte != null) {
                g.drawImage(imagePlatVerte, p.x, p.y, p.width, p.height, null);
            } else if (p.type == 1 && imagePlatBleue != null) {
                g.drawImage(imagePlatBleue, p.x, p.y, p.width, p.height, null);
            } else if (p.type == 2 && imagePlatRouge != null) {
                g.drawImage(imagePlatRouge, p.x, p.y, p.width, p.height, null);
            } else {
                // Sécurité : si tu oublies de mettre les images dans le dossier, 
                // le jeu dessinera quand même les couleurs de base pour ne pas planter !
                if (p.type == 0) {
                    g.setColor(Color.GREEN);
                } else if (p.type == 1) {
                    g.setColor(Color.BLUE);
                } else if (p.type == 2) {
                    g.setColor(Color.RED);
                }
                g.fillRect(p.x, p.y, p.width, p.height);
            }

            if (imagePersonnage != null) {
                g.drawImage(imagePersonnage, persoX, (int) persoY, null);
            }

            g.setColor(Color.BLACK);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            g.drawString("Score : " + score, 10, 25);

            if (estGameOver) {
                g.setColor(Color.RED);
                g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 50));
                g.drawString("GAME OVER", getWidth() / 2 - 160, getHeight() / 2);

                g.setColor(Color.BLACK);
                g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
                g.drawString("Appuyez sur ESPACE ou ENTRÉE pour rejouer", getWidth() / 2 - 200, getHeight() / 2 + 50);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e
    ) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            toucheGauche = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            toucheDroite = true;
        }

        if (estGameOver && (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER)) {
            recommencerJeu();
        }
    }

    @Override
    public void keyReleased(KeyEvent e
    ) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            toucheGauche = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            toucheDroite = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e
    ) {
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
