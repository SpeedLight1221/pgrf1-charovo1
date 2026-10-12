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
        populateSidebar();
    }

    public JRadioButton editRadioBtn;
    public JRadioButton drawRadioBtn;

    public void populateSidebar(){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.PAGE_START;

        editRadioBtn = new JRadioButton();
        editRadioBtn.setText("Edit");


        gbc.gridx = 0;
        gbc.gridy = 0;

        add(editRadioBtn,gbc);


        drawRadioBtn = new JRadioButton();
        drawRadioBtn.setText("Draw");
        gbc.gridx = 1;
        gbc.gridy = 0;
        drawRadioBtn.setSelected(true);
        add(drawRadioBtn,gbc);

        ButtonGroup modeBtns = new ButtonGroup();
        modeBtns.add(editRadioBtn);
        modeBtns.add(drawRadioBtn);






    }




}
