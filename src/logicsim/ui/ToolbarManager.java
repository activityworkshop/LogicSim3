package logicsim.ui;

import logicsim.Simulation;
import logicsim.controllers.AppController;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;


public class ToolbarManager {
    private final AppController parent;
    private final HashSet<JButton> buttonsToHide = new HashSet<>();
    private final HashSet<JButton> itemsDisabledBySimulation = new HashSet<>();

    public ToolbarManager(AppController parent) {
        this.parent = parent;
    }

    public JToolBar makeButtonBar() {
        final JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        final LSButton btnNew = new LSButton("new", Lang.TOOLBAR_NEW);
        btnNew.setEnabled(!Simulation.getInstance().isRunning());
        btnNew.addActionListener(e -> parent.actionNewCircuit());
        toolbar.add(btnNew, null);
        itemsDisabledBySimulation.add(btnNew);
        toolbar.add(getSmallMenuGap());

        final LSButton btnOpen = new LSButton("open", Lang.TOOLBAR_OPEN);
        btnOpen.setEnabled(!Simulation.getInstance().isRunning());
        btnOpen.addActionListener(e -> parent.actionOpenCircuit());
        toolbar.add(btnOpen);
        itemsDisabledBySimulation.add(btnOpen);
        toolbar.add(getSmallMenuGap());

        final LSButton btnSave = new LSButton("save", Lang.TOOLBAR_SAVE);
        btnSave.addActionListener(e -> parent.actionSave( false));
        toolbar.add(btnSave);

        toolbar.add(getMenuGap());

        LSToggleButton btnToggle = new LSToggleButton("play", Lang.SIMULATE);
        btnToggle.addActionListener(this::actionSimulate);
        toolbar.add(btnToggle, null);
        toolbar.add(getMenuGap());

        final LSButton btnInputNorm = new LSButton("inputnorm", Lang.INPUTNORM);
        btnInputNorm.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_PINNORMAL));
        toolbar.add(btnInputNorm, null);
        buttonsToHide.add(btnInputNorm);
        itemsDisabledBySimulation.add(btnInputNorm);
        toolbar.add(getSmallMenuGap());

        final LSButton btnInputInv = new LSButton("inputinv", Lang.INPUTINV);
        btnInputInv.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_PININVERTED));
        toolbar.add(btnInputInv, null);
        buttonsToHide.add(btnInputInv);
        itemsDisabledBySimulation.add(btnInputInv);
        toolbar.add(getSmallMenuGap());

        final LSButton btnInputHigh = new LSButton("inputhigh", Lang.INPUTHIGH);
        btnInputHigh.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_PINHIGH));
        toolbar.add(btnInputHigh, null);
        buttonsToHide.add(btnInputHigh);
        itemsDisabledBySimulation.add(btnInputHigh);
        toolbar.add(getSmallMenuGap());

        final LSButton btnInputLow = new LSButton("inputlow", Lang.INPUTLOW);
        btnInputLow.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_PINLOW));
        toolbar.add(btnInputLow, null);
        buttonsToHide.add(btnInputLow);
        itemsDisabledBySimulation.add(btnInputLow);

        toolbar.add(getMenuGap());

        final LSButton btnNewWire = new LSButton("newwire", Lang.WIRENEW);
        btnNewWire.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_ADDWIRE));
        toolbar.add(btnNewWire, null);
        buttonsToHide.add(btnNewWire);
        itemsDisabledBySimulation.add(btnNewWire);
        toolbar.add(getSmallMenuGap());

        final LSButton btnAddPoint = new LSButton("addpoint", Lang.ADDPOINT);
        btnAddPoint.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_ADDPOINT));
        toolbar.add(btnAddPoint, null);
        buttonsToHide.add(btnAddPoint);
        itemsDisabledBySimulation.add(btnAddPoint);
        toolbar.add(getSmallMenuGap());

        final LSButton btnDelPoint = new LSButton("delpoint", Lang.REMOVEPOINT);
        btnDelPoint.addActionListener(e -> parent.actionSetAction(AppController.Action.ACTION_DELPOINT));
        toolbar.add(btnDelPoint, null);
        buttonsToHide.add(btnDelPoint);
        itemsDisabledBySimulation.add(btnDelPoint);

        toolbar.add(getMenuGap());
        // Zoom in and out
        final LSButton zoomOutButton = new LSButton("zoomout", Lang.ZOOMOUT);
        zoomOutButton.addActionListener(e -> parent.actionZoom(AppController.Zoom.ZOOMOUT));
        toolbar.add(zoomOutButton, null);
        final LSButton zoomInButton = new LSButton("zoomin", Lang.ZOOMIN);
        zoomInButton.addActionListener(e -> parent.actionZoom(AppController.Zoom.ZOOMIN));
        toolbar.add(zoomInButton, null);
        return toolbar;
    }

    private void actionSimulate(ActionEvent e) {
        LSToggleButton btn = (LSToggleButton) e.getSource();
        parent.actionToggleSimulation(btn.isSelected());

        informSimulationChanged(Simulation.getInstance().isRunning());
    }

    private static Component getMenuGap() {
        return Box.createHorizontalStrut(10);
    }

    private static Component getSmallMenuGap() {
        return Box.createHorizontalStrut(3);
    }

    public void informComplexityChanged(int complexity) {
        for (JButton button : buttonsToHide) {
            button.setVisible(complexity >= 2);
        }
    }

    private void informSimulationChanged(boolean simulationOn) {
        for (JButton button : itemsDisabledBySimulation) {
            button.setEnabled(!simulationOn);
        }
    }
}
