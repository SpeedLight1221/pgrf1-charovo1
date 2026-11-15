package view;

import graphics.Raster;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PatternDialog extends JDialog {

    JButton exitBtn;
    JButton clrBtn;
    JPanel side;
    JPanel mid;
    ScaledCanvas patternCanvas;
    int color;

    public PatternDialog(){
        setPreferredSize(new Dimension(400,400));
        setModal(true);
        setTitle("Create Fill Pattern");
        setResizable(false);
        setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        color = 0xFF0000;
        init();
        pack();



    }

    public void init(){

        mid = new JPanel(new GridBagLayout());
        mid.setPreferredSize(new Dimension(340,400));

        add(mid);

        patternCanvas = new ScaledCanvas(25,25,8);



        mid.add(patternCanvas);


        side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(60,400));
        add(side);

        clrBtn = new JButton();
        clrBtn.setPreferredSize(new Dimension(50,40));
        clrBtn.setText("CLR");
        clrBtn.setBackground(new Color(color));
        side.add(clrBtn);


        exitBtn = new JButton();
        exitBtn.setText("Exit");
        exitBtn.setPreferredSize(new Dimension(50,20));
        side.add(exitBtn);


        patternCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                System.out.println(e.getX() + " " + e.getY() + " " + patternCanvas.getRaster().getWidth());
                patternCanvas.setScaledPixel(e.getX(),e.getY(),color);
                patternCanvas.repaint();
            }
        });

        clrBtn.addActionListener(e ->{
            Color selected = JColorChooser.showDialog(side, "Pick Color", new Color(color));
            if(selected != null){
                clrBtn.setBackground(selected);
                color = selected.getRGB();

            }
        });

        exitBtn.addActionListener(e -> {
            this.setVisible(false);
        });


    }

    static PatternDialog pd;
    public static Raster showDialog(){
        if(pd == null){
            pd = new PatternDialog();
        }

        pd.setSize(400,400);
        pd.setVisible(true);

        System.out.println("ehh");

        return pd.patternCanvas.getRaster();
    }



}
