package ru.nsu.ccfit.saltanova;

import ru.nsu.ccfit.saltanova.factory.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.OutputStreamWriter;

    public class SimpleGUI extends JFrame {

        Factory factory;

        private JLabel engineLabel = new JLabel("Engine");
        private JLabel numberOfEngines = new JLabel("0");
        private JLabel bodyLabel = new JLabel("Body");
        private JLabel numberOfBodies = new JLabel("0");
        private JLabel accessoryLabel = new JLabel("Accessory");
        private JLabel numberOfAccessories = new JLabel("0");
        private JLabel dealerLabel = new JLabel("Dealer  |  Cars");
        private JLabel numberOfCars = new JLabel("0");

        private JSlider speedOfBodySuppliersSlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 0);
        private JSlider speedOfEngineSuppliersSlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 0);
        private JSlider speedOfAccessorySuppliersSlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 0);
        private JSlider speedOfDealersSlider = new JSlider(JSlider.HORIZONTAL, 0, 3000, 0);

        private JLabel speedOfBodySuppliers = new JLabel(Integer.toString(speedOfBodySuppliersSlider.getValue()));
        private JLabel speedOfEngineSuppliers = new JLabel(Integer.toString(speedOfEngineSuppliersSlider.getValue()));
        private JLabel speedOfAccessorySuppliers = new JLabel(Integer.toString(speedOfAccessorySuppliersSlider.getValue()));
        private JLabel speedOfDealers = new JLabel(Integer.toString(speedOfDealersSlider.getValue()));

        private void setSpeedOfBodySuppliers() {
            speedOfBodySuppliers.setText(Integer.toString(speedOfBodySuppliersSlider.getValue()));
            factory.getBodySupplier().setTimeout(speedOfBodySuppliersSlider.getValue());
        }

        private void setSpeedOfEngineSuppliers() {
            speedOfEngineSuppliers.setText(Integer.toString(speedOfEngineSuppliersSlider.getValue()));
            factory.getEngineSupplier().setTimeout(speedOfEngineSuppliersSlider.getValue());
        }

        private void setSpeedOfAccessorySuppliers() {
            speedOfAccessorySuppliers.setText(Integer.toString(speedOfAccessorySuppliersSlider.getValue()));
            factory.getAccessorySupplier().setTimeout(speedOfAccessorySuppliersSlider.getValue());
        }

        private void setSpeedOfDealers() {
            speedOfDealers.setText(Integer.toString(speedOfDealersSlider.getValue()));
            factory.getDealer().setTimeout(speedOfDealersSlider.getValue());
        }

        void setNumberOfBodies(Integer label) {
            numberOfBodies.setText(label.toString());
        }
        void setNumberOfEngines(Integer label) {
            numberOfEngines.setText(label.toString());
        }
        void setNumberOfAccessories(Integer label) {
            numberOfAccessories.setText(label.toString());
        }
        void setNumberOfCars(Integer label) { numberOfCars.setText(label.toString()); }

        public SimpleGUI(OutputStreamWriter fout, Factory f) {
            super("Factory");
            factory = f;
            this.setBounds(0, 0, 600, 250);
            WindowListener exitListener = new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (fout != null){
                        try {
                            fout.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    f.finish();
                    System.exit(0);
                }
            };

            this.addWindowListener(exitListener);
            this.setVisible(true);

            JPanel container = new JPanel();
            JPanel container2 = new JPanel();
            JPanel container3 = new JPanel();

            speedOfBodySuppliersSlider.addChangeListener(e -> setSpeedOfBodySuppliers());
            speedOfEngineSuppliersSlider.addChangeListener(e -> setSpeedOfEngineSuppliers());
            speedOfAccessorySuppliersSlider.addChangeListener(e -> setSpeedOfAccessorySuppliers());
            speedOfDealersSlider.addChangeListener(e -> setSpeedOfDealers());

            container.setLayout(new GridLayout(5, 2));
            container.add(new JLabel("          Speed:"));
            container.add(new JLabel(""));
            container.add(speedOfBodySuppliersSlider);
            container.add(speedOfBodySuppliers);
            container.add(speedOfEngineSuppliersSlider);
            container.add(speedOfEngineSuppliers);
            container.add(speedOfAccessorySuppliersSlider);
            container.add(speedOfAccessorySuppliers);
            container.add(speedOfDealersSlider);
            container.add(speedOfDealers);

            container2.setLayout(new GridLayout(5,1));
            container2.add(new JLabel(""));
            container2.add(bodyLabel);
            container2.add(engineLabel);
            container2.add(accessoryLabel);
            container2.add(dealerLabel);

            container3.setLayout(new GridLayout(5,1));
            container3.add(new JLabel("Stock:             "));
            container3.add(numberOfBodies);
            container3.add(numberOfEngines);
            container3.add(numberOfAccessories);
            container3.add(numberOfCars);

            this.getContentPane().add(container, BorderLayout.WEST);
            this.getContentPane().add(container2, BorderLayout.CENTER);
            this.getContentPane().add(container3, BorderLayout.EAST);
        }
}
