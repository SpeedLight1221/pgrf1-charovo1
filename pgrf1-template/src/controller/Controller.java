package controller;

import view.Canvas;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Key;

/**
 * Handles user input and controls the application flow related to the {@link Canvas}.
 * The controller coordinates input events, canvas operations, and rendering updates.
 *
 * @author PGRF FIM UHK
 * @version 2026
 */
public class Controller {

    private int x,y;
    private final Canvas canvas;

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Constructors -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= */

    public Controller(Canvas canvas) {

        this.canvas = canvas;
        x = canvas.getWidth()/2;
        y = canvas.getHeight()/2;

    }

    /* -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-= Main functions -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=- */

    public void init() {
        canvas.draw();

       canvas.addMouseListener(new MouseAdapter() {
           @Override
           public void mousePressed(MouseEvent e) {
               super.mousePressed(e);

               canvas.getRaster().setRGB(e.getX(),e.getY(),0xFF0000);
               canvas.repaint();
           }
       });

       canvas.addKeyListener(new KeyAdapter() {
           @Override
           public void keyPressed(KeyEvent e) {
               super.keyPressed(e);

               if(e.getKeyCode() == KeyEvent.VK_X){
                   int midX = canvas.getWidth()/2;
                   int midY = canvas.getHeight()/2;
                   int rBor = canvas.getWidth();

                   for(int i = midX; i < rBor;i++){

                       if(i >= 0 && y >= 0 && x< canvas.getWidth() && y < canvas.getHeight() ){
                           canvas.getRaster().setRGB(i,midY,0xFF00FF);
                       }

                   }

               }
               if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                   x++;
               }
               if(e.getKeyCode() == KeyEvent.VK_LEFT){
                   x--;
               }
               if(e.getKeyCode() == KeyEvent.VK_DOWN){
                   y++;
               }
               if(e.getKeyCode() == KeyEvent.VK_UP){
                   y--;
               }

               if(x >= 0 && y >= 0 && x< canvas.getWidth() && y < canvas.getHeight() ){
                   canvas.getRaster().setRGB(x,y,0x0000FF);
               }


               int cX = canvas.getWidth()/2;
               int cY = canvas.getHeight()/2;
               if(e.getKeyCode() == KeyEvent.VK_U)
               {
                   for(int i = cY; i < canvas.getHeight(); i++){
                       if((i/10)%2 ==0){
                           canvas.getRaster().setRGB(cX,i,0x00FF00);
                       }
                   }
               }
               canvas.repaint();
           }


       });

        canvas.repaint();
    }

}