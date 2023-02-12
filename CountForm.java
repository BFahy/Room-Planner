package Room_Planner;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;

public class CountForm extends JFrame implements ActionListener {
    LinkedList<FurnitureData> furnitureList = new LinkedList<FurnitureData>();
    JButton btnExit;
    SpringLayout myLayout = new SpringLayout();
    JTextField[][] textFields;
    JLabel lblItems, lblCount;

    public CountForm(LinkedList<FurnitureData> data) {
        furnitureList = data;
        Collections.sort(furnitureList);

        setSize(300, (furnitureList.size() * 30) + 100);

        setLayout(myLayout);
        setLocation(300, 100);

        lblItems = UIBuilder.CreateALabel("Item", 63, 10, myLayout, this);
        lblCount= UIBuilder.CreateALabel("Count", 160, 10, myLayout, this);
        btnExit = UIBuilder.CreateAButton("Exit", 80, 25, 125, furnitureList.size() * 25 + 40, this, myLayout, this);

        BuildTextFields();

        setVisible(true);
    }

    private void BuildTextFields()
    {
        textFields = new JTextField[furnitureList.size()][2];
        for (int i = 0; i < furnitureList.size(); i++)
        {
            int yPos = 40 + i * 25;

            textFields[i][0] = UIBuilder.CreateATextField(7, 40, yPos, myLayout, this);
            textFields[i][1] = UIBuilder.CreateATextField(7, 140, yPos, myLayout, this);

            textFields[i][0].setText(furnitureList.get(i).getFurniture());
            textFields[i][1].setText(furnitureList.get(i).getCount() + "");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnExit)
        {
            this.dispose();
        }
    }
}

