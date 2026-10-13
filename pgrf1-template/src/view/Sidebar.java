package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Sidebar extends JPanel {

    public Sidebar(int width, int height){
        setPreferredSize(new Dimension(width,height));


        setLayout(new GridBagLayout());
        setBackground(new Color(0x1F1F1F));
        populateSidebar();
    }

    public JRadioButton editRadioBtn;
    public JRadioButton drawRadioBtn;
    public JRadioButton deleteRadioBtn;
    public JButton colorPickBtn;
    public JPanel lineColorRect;
    public JButton applyToAllBtn;

    public void populateSidebar(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.PAGE_START;


        editRadioBtn = new JRadioButton();
        editRadioBtn.setText("Edit");
        styleBtn(editRadioBtn);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth =2;
        add(editRadioBtn,gbc);

        drawRadioBtn = new JRadioButton();
        drawRadioBtn.setText("Draw");
        styleBtn(drawRadioBtn);
        gbc.gridx = 2;
        gbc.gridy = 0;
        drawRadioBtn.setSelected(true);
        add(drawRadioBtn,gbc);

        deleteRadioBtn = new JRadioButton();
        deleteRadioBtn.setText("Delete");
        styleBtn(deleteRadioBtn);
        gbc.gridx=0;
        gbc.gridy =1;

        add(deleteRadioBtn,gbc);

        ButtonGroup modeBtns = new ButtonGroup();
        modeBtns.add(editRadioBtn);
        modeBtns.add(drawRadioBtn);
        modeBtns.add(deleteRadioBtn);

        JSeparator hl1 = new JSeparator();
        gbc.gridy=2;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.insets = new Insets(10,0,10,0);
        add(hl1,gbc);



        JPanel cont = new JPanel(new GridBagLayout());
        cont.setOpaque(false);
        gbc.gridy = 3;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.ipady = 0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.insets = new Insets(0,0,0,0);
        add(cont,gbc);

        GridBagConstraints nGbc = new GridBagConstraints();

        lineColorRect = new JPanel();
        lineColorRect.setBackground(new Color(0xFF0000));
        lineColorRect.setPreferredSize(new Dimension(20,20));
        nGbc.fill = GridBagConstraints.NONE;
        nGbc.gridx = 0;
        nGbc.weightx = 1;

        cont.add(lineColorRect,nGbc);

        colorPickBtn = new JButton();
        colorPickBtn.setText("Pick");
        nGbc.fill = GridBagConstraints.HORIZONTAL;
        nGbc.gridx = 1;
        nGbc.weightx = 3;
        cont.add(colorPickBtn,nGbc);

        applyToAllBtn = new JButton();
        applyToAllBtn.setText("Apply to All");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth =4;
        gbc.insets = new Insets(5,0,0,0);
        add(applyToAllBtn,gbc);


        JSeparator hl2 = new JSeparator();
        gbc.gridy=5;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.weighty = 1;
        gbc.insets = new Insets(10,0,10,0);
        add(hl2,gbc);












    }

    private void styleBtn(AbstractButton elm){
        elm.setContentAreaFilled(false);
        elm.setForeground(new Color(0xFFFFFF));
    }




}
