package view;

import javax.swing.*;
import java.awt.*;

public class ColorPicker extends JPanel {

    JPanel cont;
    JPanel colorRect;
    JButton pickerBtn;

    public ColorPicker(){

        cont = new JPanel(new GridBagLayout());
        cont.setOpaque(false);
        GridBagConstraints nGbc = new GridBagConstraints();

        colorRect = new JPanel();
        colorRect.setBackground(new Color(0xFF0000));
        colorRect.setPreferredSize(new Dimension(20,20));
        nGbc.fill = GridBagConstraints.NONE;
        nGbc.gridx = 0;
        nGbc.weightx = 1;

        cont.add(colorRect,nGbc);

        pickerBtn = new JButton();
        pickerBtn.setText("Pick");
        nGbc.fill = GridBagConstraints.HORIZONTAL;
        nGbc.gridx = 1;
        nGbc.weightx = 3;
        cont.add(pickerBtn,nGbc);

    }

    public JButton getPickerBtn(){
        return  pickerBtn;
    }

    public JPanel getColorRect(){
        return colorRect;
    }

    public JPanel getCont(){
        return  cont;
    }
}
