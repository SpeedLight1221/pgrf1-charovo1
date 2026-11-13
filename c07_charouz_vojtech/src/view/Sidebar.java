package view;

import javax.swing.*;
import java.awt.*;

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
    public JRadioButton freeDrawRadioBtn;
    public JButton lineColorPickBtn;
    public JPanel lineColorRect;
    public JButton applyToAllBtn;
    public JComboBox<String> polygonPicker;
    public JButton addPolygonBtn;
    public JButton removePolygonBtn;
    public JPanel fillColorRect;
    public JButton fillColorPickBtn;
    public JButton seedFillBtn;
    public JButton scanLineFillBtn;
    public JButton removeFillBtn;
    public JButton lineCropBtn;


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
        drawRadioBtn.setText("Poly");
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

        freeDrawRadioBtn = new JRadioButton();
        freeDrawRadioBtn.setText("Line");
        styleBtn(freeDrawRadioBtn);
        gbc.gridx=2;
        add(freeDrawRadioBtn,gbc);

        ButtonGroup modeBtns = new ButtonGroup();
        modeBtns.add(editRadioBtn);
        modeBtns.add(drawRadioBtn);
        modeBtns.add(deleteRadioBtn);
        modeBtns.add(freeDrawRadioBtn);

        JSeparator hl1 = new JSeparator();
        gbc.gridy=2;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.insets = new Insets(10,0,10,0);
        add(hl1,gbc);


        ColorPicker lineColorPick = new ColorPicker();
        gbc.gridy = 3;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.ipady = 0;
        gbc.insets = new Insets(0,5,0,5);
        add(lineColorPick.getCont(),gbc);
        lineColorRect = lineColorPick.getColorRect();
        lineColorPickBtn = lineColorPick.getPickerBtn();


        /*JPanel cont = new JPanel(new GridBagLayout());
        cont.setOpaque(false);
        gbc.gridy = 3;
        gbc.gridx =0;
        gbc.gridwidth =4;
        gbc.ipady = 0;
        gbc.insets = new Insets(0,5,0,5);
        gbc.anchor = GridBagConstraints.PAGE_START;
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
        cont.add(colorPickBtn,nGbc);*/

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
        gbc.insets = new Insets(10,0,10,0);
        add(hl2,gbc);

        polygonPicker = new JComboBox<>();
        gbc.gridy = 6;
        gbc.insets = new Insets(5,0,0,0);

        add(polygonPicker,gbc);

        addPolygonBtn = new JButton();
        addPolygonBtn.setText("+");
        gbc.insets = new Insets(5,5,5,5);
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        add(addPolygonBtn,gbc);

        removePolygonBtn = new JButton();
        removePolygonBtn.setText("-");
        gbc.gridx = 2;
        add(removePolygonBtn,gbc);

        JSeparator hl3 = new JSeparator();
        gbc.insets = new Insets(10,0,10,0);
        gbc.gridy = 8;
        gbc.gridx =0;
        gbc.gridwidth =4;
        add(hl3,gbc);

        ColorPicker fillColorPicker = new ColorPicker();
        gbc.gridy = 9;
        gbc.ipady = 0;
        gbc.insets = new Insets(0,5,0,5);
        add(fillColorPicker.getCont(),gbc);
        fillColorRect = fillColorPicker.getColorRect();
        fillColorPickBtn = fillColorPicker.getPickerBtn();

        seedFillBtn = new JButton();
        seedFillBtn.setText("Seed Fill");
        gbc.gridy = 10;
        gbc.insets = new Insets(10,0,5,0);
        add(seedFillBtn,gbc);

        scanLineFillBtn = new JButton();
        scanLineFillBtn.setText("ScanLine");
        gbc.insets = new Insets(5,0,5,0);
        gbc.gridy = 11;
        add(scanLineFillBtn,gbc);

        removeFillBtn = new JButton();
        removeFillBtn.setText("Clear Fill");

        gbc.gridy = 12;
        gbc.insets = new Insets(5,0,5,0);
        add(removeFillBtn,gbc);

        JSeparator hl4 = new JSeparator();
        gbc.insets = new Insets(10,0,10,0);
        gbc.gridy = 13;
        gbc.gridx =0;
        gbc.gridwidth =4;
        add(hl4,gbc);


        lineCropBtn = new JButton();
        lineCropBtn.setText("Crop Lines");
        gbc.weighty =1;
        gbc.gridy = 14;
        gbc.insets = new Insets(0,0,5,0);
        add(lineCropBtn,gbc);





















    }

    private void styleBtn(AbstractButton elm){
        elm.setContentAreaFilled(false);
        elm.setForeground(new Color(0xFFFFFF));
    }




}
