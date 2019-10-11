package akoamay.cell;

import java.awt.*;
import javax.swing.*;

public class App {

    int w = 600;
    int h = 600;

    public static void main(String[] args) {
        new App(args[0]);
    }

    public App(String mode) {
        JFrame frame = new JFrame("Epidemic simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        // setBounds(x, y, w, h);

        frame.setVisible(true);

        SimulationCanvas canvas = new SimulationCanvas(w, h);

        JPanel pane = new JPanel();
        frame.getContentPane().add(pane);

        canvas.setPreferredSize(new Dimension(w, h));
        pane.add(canvas);

        frame.setVisible(true);
    }
}
