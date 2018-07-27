package raktak;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Jeff Smith
 */
public class BasicWoodcutterGUI extends JFrame {
    private Main ctx;
    public BasicWoodcutterGUI(Main main){
        this.ctx = main;
        initComponents();
    }

    private void button1ActionPerformed(ActionEvent e) {
        ctx.setStartScript(true);
        this.setVisible(false);
    }

    private void initComponents() {
        button1 = new JButton();
        comboBox1 = new JComboBox<>();
        label1 = new JLabel();

        //======== this ========
        setTitle("Simple Fishing GUI");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- button1 ----
        button1.setText("Start");
        button1.addActionListener(e -> button1ActionPerformed(e));
        contentPane.add(button1);
        button1.setBounds(10, 55, 275, 55);

        //---- comboBox1 ----
        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{
                "Small Net",
                "Bait"
        }));
        contentPane.add(comboBox1);
        comboBox1.setBounds(15, 10, 185, 35);

        //---- label1 ----
        label1.setText("Select your spot type.");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(205, 20), label1.getPreferredSize()));

        contentPane.setPreferredSize(new Dimension(335, 120));
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String getTreeType() {
        return comboBox1.getSelectedItem().toString();
    }

    private JButton button1;
    private JComboBox<String> comboBox1;
    private JLabel label1;
}