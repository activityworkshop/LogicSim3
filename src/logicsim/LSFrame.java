package logicsim;

import logicsim.controllers.AppController;
import logicsim.gatelist.GateDefinition;
import logicsim.gatelist.GateLibrary;
import logicsim.localization.I18N;
import logicsim.localization.Lang;
import logicsim.module.MODIN;
import logicsim.module.MODOUT;
import logicsim.module.ModuleLoader;
import logicsim.ui.*;
import logicsim.xml.XMLCreator;
import logicsim.xml.XMLLoader;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.*;

public class LSFrame extends JFrame implements AppController, ActionListener, CircuitChangedListener {

    private LogicSimFile lsFile;

    private JMenuBar mnuBar;
    private JToolBar btnBar;

    private final DefaultListModel<Object> partListModel = new DefaultListModel<>();
    private final JList<Object> lstParts = new JList<>(partListModel);
    private JComboBox<String> cbNumInputs = null;
    private final LSPanel lspanel = new LSPanel();

    private JSplitPane splitPane;
    private final JPanel pnlGateList = new JPanel();
    private int dividerLocation;

    private final JLabel sbText = new JLabel();
    private final JLabel sbCoordinates = new JLabel();

    private int popupGateIdx;
    private JPopupMenu popup;
    private JMenuItem menuItem_remove;
    private JMenuItem menuItem_disconnect;
    private JMenuItem menuItem_properties;
    private JMenuItem menuItem_rotate;
    private JMenuItem menuItem_increase_inputs;
    private JMenuItem menuItem_decrease_inputs;

    private int mouseX, mouseY;

    private int lastPressedListIndex = -1;
    private boolean listDragArmed = false;
    private final AppTitleManager appTitleManager = new AppTitleManager();


    /** Transferable für GateDragInfo */
    private static class GateInfoTransferable implements Transferable {
        private final GateDragInfo info;
        private final DataFlavor[] flavors = new DataFlavor[]{GateDragInfo.FLAVOR};
        GateInfoTransferable(GateDragInfo info) { this.info = info; }
        @Override public DataFlavor[] getTransferDataFlavors() { return flavors; }
        @Override public boolean isDataFlavorSupported(DataFlavor flavor) { return flavor.equals(GateDragInfo.FLAVOR); }
        @Override public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException { if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor); return info; }
    }

    /** TransferHandler für die Gate-Liste (Quelle) */
    private class GateListTransferHandler extends TransferHandler {
        @Override
        public int getSourceActions(JComponent c) { return COPY; }

        @Override
        protected Transferable createTransferable(JComponent c) {
            int idx = lastPressedListIndex >= 0 ? lastPressedListIndex : lstParts.getSelectedIndex();
            if (idx < 0 || idx >= partListModel.getSize()) {
                return null;
            }
            final Object o = partListModel.getElementAt(idx);
            if (!(o instanceof GateDefinition gateDef)) {
                return null;
            }
            // Eingangsanzahl aus ComboBox
            int numInputs = 2;
            try {
                String sel = Objects.requireNonNull(cbNumInputs.getSelectedItem()).toString();
                numInputs = Integer.parseInt(sel.substring(0, 1));
            } catch (Exception ignored) {}
            return new GateInfoTransferable(new GateDragInfo(gateDef, numInputs));
        }

        @Override
        public boolean canImport(TransferSupport support) { return false; }
    }

    /** TransferHandler für das Canvas (Ziel) */
    private class CanvasTransferHandler extends TransferHandler {
        @Override
        public boolean canImport(TransferSupport support) {
            if (Simulation.getInstance().isRunning()) return false;
            return support.isDataFlavorSupported(GateDragInfo.FLAVOR);
        }

        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }
            try {
                GateDragInfo info = (GateDragInfo) support.getTransferable().getTransferData(GateDragInfo.FLAVOR);
                if (info == null || info.getDefinition() == null) {
                    return false;
                }
                final Gate gate = info.getDefinition().create();
                if (!gate.isValid()) {
                    return false;
                }
                // Eingänge ggf. setzen
                if (gate.supportsVariableInputs()) {
                    gate.createDynamicInputs(info.getNumInputs());
                }
                // Drop-Position (Component-Koordinaten -> Weltkoordinaten)
                final Point p = support.getDropLocation().getDropPoint();
                final int worldX = (int) lspanel.getTransformer().screenToWorldX(p.x);
                final int worldY = (int) lspanel.getTransformer().screenToWorldY(p.y);
                lspanel.addGateAt(gate, worldX, worldY);
                return true;
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public LSFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        lsFile = new LogicSimFile(defaultCircuitFileName());
        lsFile.circuit.setRepaintListener(lspanel);
        lspanel.setChangeListener(this);

        try {
            createUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ask if we should close
     */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING
                && showDiscardDialog(I18N.tr(Lang.EXIT))) {
            System.exit(0);
        }
    }

    private String defaultModuleFileName() {
        return App.getModulePath() + I18N.tr(Lang.UNNAMED)
                + "." + App.MODULE_FILE_SUFFIX;
    }

    private String defaultCircuitFileName() {
        return App.getCircuitPath() + I18N.tr(Lang.UNNAMED)
                + "." + App.CIRCUIT_FILE_SUFFIX;
    }

    /** Component initialization */
    private void createUI() {
        setTitle("LogicSim");

        // ------------------------------------------------------------------
        // MENU
        // ------------------------------------------------------------------
        mnuBar = makeMenuBar();
        setJMenuBar(mnuBar);

        // ------------------------------------------------------------------
        // compose GUI

        JPanel statusBar = new JPanel();
        statusBar.setLayout(new BorderLayout());
        statusBar.add(sbText, BorderLayout.WEST);
        statusBar.add(sbCoordinates, BorderLayout.EAST);
        statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        setStatusText(" ");
        sbCoordinates.setText(" ");
        add(statusBar, BorderLayout.SOUTH);

        lspanel.setPreferredSize(new Dimension(1000, 600));
        lspanel.setBackground(Color.white);
        lspanel.setDoubleBuffered(true);

        lstParts.addMouseListener(new PopupListener());
        lstParts.setCellRenderer(new GateListRenderer());
        lstParts.addListSelectionListener(this::actionLstGatesSelected);
        lstParts.setDragEnabled(true);
        lstParts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstParts.setTransferHandler(new GateListTransferHandler());
        // Track Mausindex für robustes DnD
        lstParts.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                lastPressedListIndex = lstParts.locationToIndex(e.getPoint());
                Object o = (lastPressedListIndex >= 0 && lastPressedListIndex < partListModel.size())
                        ? partListModel.getElementAt(lastPressedListIndex) : null;
                listDragArmed = (o instanceof Gate);
            }
            @Override public void mouseReleased(MouseEvent e) {
                lastPressedListIndex = -1;
                listDragArmed = false;
            }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    if (Simulation.getInstance().isRunning()) return;
                    int idx = lstParts.locationToIndex(e.getPoint());
                    if (idx < 0 || idx >= partListModel.getSize()) return;
                    Object o = partListModel.getElementAt(idx);
                    if (!(o instanceof GateDefinition gateDef)) {
                        return;
                    }
                    final Gate gate = gateDef.create();
                    if (!gate.isValid()) {
                        return;
                    }
                    // Eingänge gemäß Auswahl
                    int numInputs = 2;
                    try {
                        String sel = Objects.requireNonNull(cbNumInputs.getSelectedItem()).toString();
                        numInputs = Integer.parseInt(sel.substring(0, 1));
                    } catch (Exception ignored) {}
                    if (gate.supportsVariableInputs()) {
                        gate.createDynamicInputs(numInputs);
                    }
                    // Direkt aufs Canvas setzen (Standardposition anhand Offset)
                    lspanel.setAction(gate);
                }
            }
        });
        lstParts.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (listDragArmed) {
                    TransferHandler th = lstParts.getTransferHandler();
                    if (th != null) {
                        th.exportAsDrag(lstParts, e, TransferHandler.COPY);
                    }
                    listDragArmed = false;
                }
            }
        });

        String[] gateInputNums = new String[4];
        for (int i = 0; i < 4; i++) {
            gateInputNums[i] = (i + 2) + " " + I18N.tr(Lang.INPUTS);
        }
        cbNumInputs = new JComboBox<>(gateInputNums);

        pnlGateList.setLayout(new BorderLayout());

        pnlGateList.setPreferredSize(new Dimension(120, 200));
        pnlGateList.setMinimumSize(new Dimension(100, 200));
        JScrollPane gateScrollPane = new JScrollPane(lstParts);
        gateScrollPane.setHorizontalScrollBar(null);
        JScrollBar gateScrollBar = new JScrollBar(JScrollBar.VERTICAL);
        gateScrollBar.setUI(new LSScrollBarUI());
        gateScrollBar.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        gateScrollPane.setVerticalScrollBar(gateScrollBar);
        gateScrollPane.getVerticalScrollBar().setUnitIncrement(32);
        pnlGateList.add(gateScrollPane, BorderLayout.CENTER);
        pnlGateList.add(cbNumInputs, BorderLayout.SOUTH);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(170);
        splitPane.add(pnlGateList, JSplitPane.LEFT);
        splitPane.add(lspanel, JSplitPane.RIGHT);

        // Drag-and-Drop Ziel für Canvas
        lspanel.setTransferHandler(new CanvasTransferHandler());

        getContentPane().add(splitPane, BorderLayout.CENTER);

        btnBar = makeButtonBar();
        add(btnBar, BorderLayout.NORTH);

        // ------------------------------------------------------------------
        // Create the popup menu.
        popup = new JPopupMenu();

        menuItem_remove = new JMenuItem(I18N.tr(Lang.REMOVEGATE));
        menuItem_remove.addActionListener(this);
        popup.add(menuItem_remove);

        menuItem_disconnect = new JMenuItem(I18N.tr(Lang.POPUP_DISCONNECT));
        menuItem_disconnect.addActionListener(this);
        popup.add(menuItem_disconnect);

        menuItem_properties = new JMenuItem(I18N.tr(Lang.POPUP_PROPERTIES));
        menuItem_properties.addActionListener(this);
        popup.add(menuItem_properties);

        menuItem_rotate = new JMenuItem(I18N.tr(Lang.ROTATE));
        menuItem_rotate.addActionListener(this);
        menuItem_rotate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, false));
        popup.add(menuItem_rotate);

        menuItem_increase_inputs = new JMenuItem(I18N.tr(Lang.ADDINPUT));
        menuItem_increase_inputs.addActionListener(this);
        menuItem_increase_inputs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK, false));
        popup.add(menuItem_increase_inputs);

        menuItem_decrease_inputs = new JMenuItem(I18N.tr(Lang.REMOVEINPUT));
        menuItem_decrease_inputs.addActionListener(this);
        menuItem_decrease_inputs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK, false));
        popup.add(menuItem_decrease_inputs);
        // Add listener to components that can bring up popup menus.
        lspanel.addMouseListener(new PopupListener());

        refreshGateList();
        setAppTitle();

        lspanel.requestFocusInWindow();
    }

    @Override
    public void actionShowHelp() {
        new HTMLHelp();
    }

    @Override
    public void actionShowAbout() {
        new AboutBox(this);
    }

    @Override
    public void actionEditFileInfo() {
        if (FileInfoDialog.showFileInfo(LSFrame.this, lsFile)) {
            setAppTitle();
        }
    }

    @Override
    public void actionExit() {
        if (showDiscardDialog(I18N.tr(Lang.EXIT))) {
            System.exit(0);
        }
    }

    private JMenuBar makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu mnu = new JMenu(I18N.tr(Lang.FILE));

        final JMenuItem mnuNew = createMenuItem(Lang.NEW, KeyEvent.VK_N, false);
        mnuNew.setEnabled(!Simulation.getInstance().isRunning());
        mnuNew.addActionListener(e -> actionNewCircuit());
        mnu.add(mnuNew);

        final JMenuItem mnuOpen = createMenuItem(Lang.OPEN, KeyEvent.VK_O, true);
        mnuOpen.setEnabled(!Simulation.getInstance().isRunning());
        mnuOpen.addActionListener(e -> actionOpenCircuit());
        mnu.add(mnuOpen);

        mnu.addSeparator();

        final JMenuItem mnuFileProperties = createMenuItem(Lang.PROPERTIES, 0, true);
        mnuFileProperties.addActionListener(e -> actionEditFileInfo());
        mnu.add(mnuFileProperties);

        mnu.addSeparator();

        final JMenuItem mnuSave = createMenuItem(Lang.SAVE, KeyEvent.VK_S, true);
        mnuSave.addActionListener(e -> actionSave( false));
        mnu.add(mnuSave);

        final JMenuItem mnuSaveAs = createMenuItem(Lang.SAVEAS, 0, true);
        mnuSaveAs.addActionListener(e -> actionSave( true));
        mnu.add(mnuSaveAs);

        final JMenuItem mnuExport = createMenuItem(Lang.EXPORT, 0, true);
        mnuExport.addActionListener(e -> actionExportImage());
        mnu.add(mnuExport);

        mnu.addSeparator();

        final JMenuItem mnuExit = createMenuItem(Lang.EXIT, KeyEvent.VK_X, false);
        mnuExit.addActionListener(e -> actionExit());
        mnu.add(mnuExit);

        menuBar.add(mnu);
        // ------------------------------------------------------------------
        // Module
        final JMenu moduleMenu = new JMenu(I18N.tr(Lang.MENU_MODULE));

        final JMenuItem mnuCreateModule = createMenuItem(Lang.MENU_MODULECREATE, 0, true);
        mnuCreateModule.addActionListener(e -> actionCreateModule());
        moduleMenu.add(mnuCreateModule);

        final JMenuItem mnuLoadModule = new JMenuItem(I18N.tr(Lang.MENU_LOADMODULE));
        mnuLoadModule.addActionListener(e -> actionLoadModule());
        moduleMenu.add(mnuLoadModule);
        menuBar.add(moduleMenu);

        // ------------------------------------------------------------------
        // SETTINGS
        mnu = new JMenu(I18N.tr(Lang.SETTINGS));

        final int complexity = LSProperties.getInstance().getComplexity();
        JMenu mnuComplexity = new JMenu(I18N.tr(Lang.SETTINGS_COMPLEXITY));
        ButtonGroup btnGroup = new ButtonGroup();
        for (int level=0; level<=4; level++) {
            String key = Lang.COMPLEXITY_LEVEL.getKey() + level;
            JRadioButtonMenuItem mnuItem = new JRadioButtonMenuItem(I18N.tr(key));
            mnuItem.setName("complexity" + level);
            mnuItem.setSelected(level == complexity);
            mnuItem.addActionListener(this::changeComplexity);
            btnGroup.add(mnuItem);
            mnuComplexity.add(mnuItem);
            btnGroup.add(mnuItem);
        }
        mnu.add(mnuComplexity);

        boolean sel = LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true);
        final JCheckBoxMenuItem paintGridCheckbox = new JCheckBoxMenuItem(I18N.tr(Lang.PAINTGRID));
        paintGridCheckbox.setSelected(sel);
        paintGridCheckbox.addActionListener(e -> {
            LSProperties.getInstance().setPropertyBoolean(LSProperties.PAINTGRID, paintGridCheckbox.isSelected());
            actionChangedSettings(false);
        });
        mnu.add(paintGridCheckbox);

        boolean autowire = LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true);
        final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(I18N.tr(Lang.AUTOWIRE));
        cbMenuItem.setSelected(autowire);
        cbMenuItem.addActionListener(e -> {
            JCheckBoxMenuItem bmi = (JCheckBoxMenuItem) e.getSource();
            LSProperties.getInstance().setPropertyBoolean(LSProperties.AUTOWIRE, bmi.isSelected());
            actionChangedSettings(false);
        });
        mnu.add(cbMenuItem);

        final JMenuItem mnuStyle = new JMenu(I18N.tr(Lang.GATEDESIGN));
        String gateStyle = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN,
                LSProperties.GATEDESIGN_IEC);

        JRadioButtonMenuItem mnuStyleIEC = new JRadioButtonMenuItem();
        mnuStyleIEC.setText(I18N.tr(Lang.GATEDESIGN_IEC));
        mnuStyleIEC.addActionListener(this::actionGateDesign);
        mnuStyleIEC.setSelected(LSProperties.GATEDESIGN_IEC.equals(gateStyle));
        mnuStyle.add(mnuStyleIEC);

        JRadioButtonMenuItem mnuStyleANSI = new JRadioButtonMenuItem();
        mnuStyleANSI.setText(I18N.tr(Lang.GATEDESIGN_ANSI));
        mnuStyleANSI.addActionListener(this::actionGateDesign);
        mnuStyleANSI.setSelected(LSProperties.GATEDESIGN_ANSI.equals(gateStyle));
        mnuStyle.add(mnuStyleANSI);

        btnGroup = new ButtonGroup();
        btnGroup.add(mnuStyleIEC);
        btnGroup.add(mnuStyleANSI);

        mnu.add(mnuStyle);

        final JMenuItem mnuColorMode = new JMenu(I18N.tr(Lang.COLORMODE));
        btnGroup = new ButtonGroup();
        String cMode = LSProperties.getInstance().getProperty(LSProperties.COLORMODE, LSProperties.COLORMODE_ON);

        JRadioButtonMenuItem mCmOn = new JRadioButtonMenuItem();
        mCmOn.setText(I18N.tr(Lang.COLORMODE_ON));
        mCmOn.addActionListener(this::actionColorMode);
        mCmOn.setSelected(LSProperties.COLORMODE_ON.equals(cMode));
        mnuColorMode.add(mCmOn);

        JRadioButtonMenuItem mCmOff = new JRadioButtonMenuItem();
        mCmOff.setText(I18N.tr(Lang.COLORMODE_OFF));
        mCmOff.addActionListener(this::actionColorMode);
        mCmOff.setSelected(LSProperties.COLORMODE_OFF.equals(cMode));
        mnuColorMode.add(mCmOff);

        btnGroup.add(mCmOn);
        btnGroup.add(mCmOff);

        mnu.add(mnuColorMode);

        JMenu mnuLang = new JMenu(I18N.tr(Lang.LANGUAGE));
        String currentLanguage = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "de");
        createLanguageMenu(mnuLang, currentLanguage);
        mnu.add(mnuLang);

        menuBar.add(mnu);

        // ------------------------------------------------------------------
        // HELP
        mnu = new JMenu(I18N.tr(Lang.HELP));

        final JMenuItem mnuHelpHelp = createMenuItem(Lang.HELP_HELP, 0, true);
        mnuHelpHelp.addActionListener(e -> actionShowHelp());
        mnu.add(mnuHelpHelp);

        final JMenuItem mnuAbout = createMenuItem(Lang.ABOUT, 0, true);
        mnuAbout.addActionListener(e -> actionShowAbout());
        mnu.add(mnuAbout);

        menuBar.add(mnu);
        return menuBar;
    }

    private JMenuItem createMenuItem(Lang lang, int key, boolean isDialog) {
        JMenuItem m = new JMenuItem(I18N.tr(lang) + (isDialog ? "..." : ""));
        if (key != 0)
            m.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK, false));
        m.setName(lang.toString());
        return m;
    }

    private JToolBar makeButtonBar() {
        final JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        LSButton btnLS = new LSButton("new", Lang.TOOLBAR_NEW);
        btnLS.setEnabled(!Simulation.getInstance().isRunning());
        btnLS.addActionListener(e -> actionNewCircuit());
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("open", Lang.TOOLBAR_OPEN);
        btnLS.setEnabled(!Simulation.getInstance().isRunning());
        btnLS.addActionListener(e -> actionOpenCircuit());
        toolbar.add(btnLS);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("save", Lang.TOOLBAR_SAVE);
        btnLS.addActionListener(e -> actionSave( false));
        toolbar.add(btnLS);

        toolbar.add(getMenuGap());

        LSToggleButton btnToggle = new LSToggleButton("play", Lang.SIMULATE);
        btnToggle.addActionListener(this::actionSimulate);
        toolbar.add(btnToggle, null);
        toolbar.add(getMenuGap());

        btnLS = new LSButton("inputnorm", Lang.INPUTNORM);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_PINNORMAL));
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("inputinv", Lang.INPUTINV);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_PININVERTED));
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("inputhigh", Lang.INPUTHIGH);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_PINHIGH));
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("inputlow", Lang.INPUTLOW);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_PINLOW));
        toolbar.add(btnLS, null);

        toolbar.add(getMenuGap());

        btnLS = new LSButton("newwire", Lang.WIRENEW);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_ADDWIRE));
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("addpoint", Lang.ADDPOINT);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_ADDPOINT));
        toolbar.add(btnLS, null);
        toolbar.add(getSmallMenuGap());

        btnLS = new LSButton("delpoint", Lang.REMOVEPOINT);
        btnLS.addActionListener(e -> actionSetAction(Action.ACTION_DELPOINT));
        toolbar.add(btnLS, null);

        toolbar.add(getMenuGap());
        // Zoom in and out
        final LSButton zoomOutButton = new LSButton("zoomout", Lang.ZOOMOUT);
        zoomOutButton.addActionListener(e -> actionZoom(Zoom.ZOOMOUT));
        toolbar.add(zoomOutButton, null);
        final LSButton zoomInButton = new LSButton("zoomin", Lang.ZOOMIN);
        zoomInButton.addActionListener(e -> actionZoom(Zoom.ZOOMIN));
        toolbar.add(zoomInButton, null);
        return toolbar;
    }

    private void setStatusText(String string) {
        sbText.setText("  " + string);
    }

    @Override
    public void actionZoom(Zoom zoomDir) {
        if (zoomDir == Zoom.ZOOMIN) {
            lspanel.zoomIn();
        }
        else if (zoomDir == Zoom.ZOOMOUT) {
            lspanel.zoomOut();
        }
    }

    private Component getMenuGap() {
        return Box.createHorizontalStrut(10);
    }

    private Component getSmallMenuGap() {
        return Box.createHorizontalStrut(3);
    }

    /**
     * handles popup menus
     */
    public void actionPerformed(ActionEvent e) { // popup menu
        JMenuItem source = (JMenuItem) (e.getSource());
        if (source == menuItem_remove) {
            lspanel.circuit.removeGateIdx(popupGateIdx);
            lspanel.repaint();
        } else if (source == menuItem_disconnect) {
            lspanel.circuit.disconnectGateIdx(popupGateIdx);
            lspanel.repaint();
        } else if (source == menuItem_properties) {
            if (popupGateIdx >= 0) {
                Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
                g.showPropertiesUI(this);
                lspanel.repaint();
            }
        } else if (source == menuItem_rotate) {
            rotateSelected();
        } else if (source == menuItem_increase_inputs) {
            increaseInputsForSelected();
        } else if (source == menuItem_decrease_inputs) {
            decreaseInputsForSelected();
        }
    }

    public void rotateSelected() {
        if (popupGateIdx >= 0) {
            Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
            CircuitPart[] sel = lspanel.circuit.getSelected();
            if (sel == null || sel.length == 0) return;
            boolean clickedInSelection = false;
            for (CircuitPart p : sel) {
                if (p == g) {
                    clickedInSelection = true;
                    break;
                }
            }
            if (clickedInSelection && sel.length > 1) {
                lspanel.rotateSelected();
            } else {
                g.rotate();
                lspanel.changedCircuit();
            }
            lspanel.repaint();
        }
    }

    public void decreaseInputsForSelected() {
        if (popupGateIdx >= 0) {
            Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
            CircuitPart[] sel = lspanel.circuit.getSelected();
            if (sel == null || sel.length == 0) return;
            boolean clickedInSelection = false;
            for (CircuitPart p : sel) {
                if (p == g) {
                    clickedInSelection = true;
                    break;
                }
            }
            if (clickedInSelection && sel.length > 1) {
                for (CircuitPart p : sel) {
                    if (p instanceof Gate g2) {
                        if (g2.variableInputCountSupported) {
                            g2.createDynamicInputs(g2.getNumInputs() - 1);
                        }
                    }
                }
            } else {
                if (g.variableInputCountSupported) {
                    g.createDynamicInputs(g.getNumInputs() - 1);
                }
                lspanel.changedCircuit();
            }
            lspanel.repaint();
        }
    }

    public void increaseInputsForSelected() {
        if (popupGateIdx >= 0) {
            Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
            CircuitPart[] sel = lspanel.circuit.getSelected();
            if (sel == null || sel.length == 0) return;
            boolean clickedInSelection = false;
            for (CircuitPart p : sel) {
                if (p == g) {
                    clickedInSelection = true;
                    break;
                }
            }
            if (clickedInSelection && sel.length > 1) {
                for (CircuitPart p : sel) {
                    if (p instanceof Gate g2) {
                        if (g2.variableInputCountSupported) {
                            g2.createDynamicInputs(g2.getNumInputs() + 1);
                        }
                    }
                }
            } else {
                if (g.variableInputCountSupported) {
                    g.createDynamicInputs(g.getNumInputs() + 1);
                }
                lspanel.changedCircuit();
            }
            lspanel.repaint();
        }
    }

    public int getSelectedCount() {
        return lspanel.circuit.getSelected().length;
    }

    class PopupListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent mouseEvent) {
            if (!mouseEvent.isPopupTrigger()) return;
            if (mouseEvent.getSource() != lspanel) return;
            if (Simulation.getInstance().isRunning()) return;
            for (CircuitPart part : lspanel.circuit.getParts()) {
                if (!(part instanceof Gate g)) continue;
                if (!g.insideFrame(mouseX, mouseY)) continue;
                popupGateIdx = lspanel.circuit.getParts().indexOf(part);
                menuItem_disconnect.setEnabled(part.isConnected());
                menuItem_properties.setEnabled(g.hasPropertiesUI());
                menuItem_decrease_inputs.setEnabled(g.variableInputCountSupported && g.getNumInputs() > 2);
                menuItem_increase_inputs.setEnabled(g.variableInputCountSupported && g.getNumInputs() < 5);
                popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                break;
            }
        }

    }
    @Override
    public void actionToggleSimulation(boolean start) {
        if (start && !Simulation.getInstance().isRunning()) {
            // Start simulation
            lspanel.circuit.resetAll();
            repaint();
            Simulation.getInstance().start();
            changedStatusText(I18N.tr(Lang.SIMULATION_STARTED));
            dividerLocation = splitPane.getDividerLocation();
            splitPane.remove(pnlGateList);
        }
        else if (!start && Simulation.getInstance().isRunning()) {
            // Stop simulation
            Simulation.getInstance().stop();
            changedStatusText(I18N.tr(Lang.SIMULATION_STOPPED));
            splitPane.add(pnlGateList, JSplitPane.LEFT);
            splitPane.setDividerLocation(dividerLocation);
        }
    }

    void actionSimulate(ActionEvent e) {
        LSToggleButton btn = (LSToggleButton) e.getSource();
        actionToggleSimulation(btn.isSelected());

        final boolean simRunning = Simulation.getInstance().isRunning();
        Objects.requireNonNull(getMenuWidget(Lang.OPEN)).setEnabled(!simRunning);
        Objects.requireNonNull(getButtonWidget(Lang.TOOLBAR_OPEN)).setEnabled(!simRunning);
        Objects.requireNonNull(getMenuWidget(Lang.NEW)).setEnabled(!simRunning);
        Objects.requireNonNull(getButtonWidget(Lang.TOOLBAR_NEW)).setEnabled(!simRunning);
    }

    boolean showDiscardDialog(String title) {
        if (lsFile.changed) {
            int result = Dialogs.confirmDiscardDialog(this);
            return (result == JOptionPane.YES_OPTION);
        }
        return true;
    }

    /**
     * handles initial steps to create a new circuit file
     */
    public void actionNewCircuit() {
        if (Simulation.getInstance().isRunning())
            return;
        if (!showDiscardDialog(I18N.tr(Lang.NEW)))
            return;
        lsFile = new LogicSimFile(defaultCircuitFileName());
        lsFile.circuit.setRepaintListener(lspanel);
        setAppTitle();
        lspanel.clear();
    }

    /**
     * handles opening of files
     */
    public void actionOpenCircuit() {
        if (Simulation.getInstance().isRunning())
            return;

        if (!showDiscardDialog(I18N.tr(Lang.OPEN)))
            return;

        File file = new File(lsFile.fileName);
        JFileChooser chooser = new JFileChooser(file.getParent());
        chooser.setFileFilter(setupFilter());
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();

        try {
            lsFile = XMLLoader.loadXmlFile(chooser.getSelectedFile());
        } catch (RuntimeException | IOException x) {
            System.err.println(x);
            x.printStackTrace(System.err);
            Dialogs.messageDialog(this, I18N.tr(Lang.READERROR) + " " + x.getMessage());
        }
        final String errorString = lsFile.getErrorString();
        if (errorString != null) {
            Dialogs.messageDialog(this, errorString);
        }
        setAppTitle();
        lspanel.clear();
        lspanel.circuit = lsFile.circuit;
        lspanel.circuit.setRepaintListener(lspanel);
        lspanel.circuit.reset();
    }

    /**
     * Set up a file filter for displaying files with the correct ending
     */
    private FileFilter setupFilter() {
        // TODO: I18n for "Files"?
        final String description = "LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX
                + ", " + "." + App.MODULE_FILE_SUFFIX + ")";
        return new LogicSimFileFilter(description, App.CIRCUIT_FILE_SUFFIX, App.MODULE_FILE_SUFFIX);
    }

    /**
     * set window title
     */
    private void setAppTitle() {
        String circuitName = (lsFile == null ? null : lsFile.getName());
        // Could also use the filename if the name is blank, but then
        // we'd get lots of "Unnamed" if the file hasn't been saved
        appTitleManager.setAppTitleIfChanged(circuitName, lsFile != null && lsFile.changed);
    }

    /**
     * handles saving of circuit file
     */
    @Override
    public void actionSave(boolean chooseFile) {
        String fileName = lsFile.fileName;
        boolean unnamed = lsFile.extractFileName().equals(I18N.tr(Lang.UNNAMED));
        boolean showDialog = fileName == null || fileName.isEmpty() || unnamed || chooseFile;

        if (showDialog) {
            if (!showSaveDialog()) {
                return;
            }
        }
        lsFile.circuit = lspanel.circuit;
        try {
            XMLCreator.createXML(lsFile);
        } catch (RuntimeException err) {
            System.err.println(err);
            err.printStackTrace(System.err);
            Dialogs.messageDialog(this, I18N.tr(Lang.SAVEERROR) + " " + err.getMessage());
        }

        setAppTitle();
        setStatusText(String.format(I18N.tr(Lang.SAVED), lsFile.fileName));
        lsFile.changed = false;
        refreshGateList();
    }

    /**
     * helper method to show the save dialog
     *
     * @return true when user selected a file, false when user canceled the dialog
     */
    private boolean showSaveDialog() {
        File file = new File(lsFile.fileName);
        String parentDirName = file.getParent();

        JFileChooser chooser = new JFileChooser(parentDirName);
        chooser.setDialogTitle(I18N.tr(Lang.SAVECIRCUIT));

        String s = "LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX + ", " + "." + App.MODULE_FILE_SUFFIX + ")";
        FileNameExtensionFilter filter = new FileNameExtensionFilter(s, App.CIRCUIT_FILE_SUFFIX,
                App.MODULE_FILE_SUFFIX);
        chooser.setFileFilter(filter);

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();
            // check fileName
            int lastSeparator = lsFile.fileName.lastIndexOf(File.separatorChar);
            int lastDot = lsFile.fileName.lastIndexOf(".");
            if (lastDot < lastSeparator) {
                // ending is missing
                if (lsFile.circuit.isModule())
                    lsFile.fileName += "." + App.MODULE_FILE_SUFFIX;
                else
                    lsFile.fileName += "." + App.CIRCUIT_FILE_SUFFIX;
            }
            return true;
        }
        return false;
    }

    /**
     * handles initial steps to create a new module
     */
    @Override
    public void actionCreateModule() {
        if (lsFile.circuit.isModule()) {
            Dialogs.messageDialog(this, I18N.tr(Lang.ALREADYMODULE));
            return;
        }

        if (!lsFile.circuit.isEmpty()) {
            lsFile.fileName = App.getModulePath() + lsFile.extractFileName()
                    + "." + App.MODULE_FILE_SUFFIX;
            lsFile.changed = true;
        } else {
            lsFile = new LogicSimFile(defaultModuleFileName());
            lsFile.circuit.setRepaintListener(lspanel);
        }

        if (!FileInfoDialog.showFileInfo(this, lsFile)) {
            return;
        }

        setAppTitle();

        final Gate modin = new MODIN();
        modin.moveTo(150, 100);
        modin.loadProperties();
        lsFile.circuit.addGate(modin);
        final Gate modout = new MODOUT();
        modout.moveTo(650, 100);
        modout.loadProperties();
        lsFile.circuit.addGate(modout);
        lspanel.circuit = lsFile.circuit;
        lspanel.circuit.setRepaintListener(lspanel);
        lspanel.repaint();
    }

    /**
     * save image in file system
     */
    @Override
    public void actionExportImage() {
        final JFileChooser chooser = new JFileChooser();
        LogicSimFileFilter filter = new LogicSimFileFilter();
        filter.addExtension(".png");
        filter.setDescription("Portable Network Graphics");
        chooser.setFileFilter(filter);

        chooser.setDialogTitle(I18N.tr(Lang.SAVECIRCUIT));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String filename = chooser.getSelectedFile().getAbsolutePath();
        if (!filename.endsWith(".png")) {
            filename += ".png";
        }

        BufferedImage image = (BufferedImage) this.createImage(this.lspanel.getWidth(), this.lspanel.getHeight());
        Graphics g = image.getGraphics();
        lspanel.circuit.deselectAll();
        lspanel.paint(g);
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshGateList() {
        int complexity = LSProperties.getInstance().getComplexity();
        GateLibrary.populateListModel(partListModel, complexity);
    }

    /**
     * handles click on gates list
     */
    void actionLstGatesSelected(ListSelectionEvent e) {
        if (Simulation.getInstance().isRunning()
                || e.getValueIsAdjusting()
                || lstParts.getSelectedIndex() < 0) {
            return;
        }
        Object o = lstParts.getSelectedValue();
        if (o instanceof GateDefinition gateDef) {
            setStatusText(gateDef.getDescriptionString());
        }
    }

    /**
     * handles gate design (IEC/ISO)
     */
    void actionGateDesign(ActionEvent e) {
        final String gateDesign;
        JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
        if (src.getText().equals(I18N.tr(Lang.GATEDESIGN_IEC))) {
            gateDesign = (src.isSelected() ? LSProperties.GATEDESIGN_IEC : LSProperties.GATEDESIGN_ANSI);
        } else {
            gateDesign = (src.isSelected() ? LSProperties.GATEDESIGN_ANSI : LSProperties.GATEDESIGN_IEC);
        }
        LSProperties.getInstance().setProperty(LSProperties.GATEDESIGN, gateDesign);
        actionChangedSettings(false);
    }

    /**
     * handles color mode (on-redblack / off - blackwhite for printing)
     */
    private void actionColorMode(ActionEvent e) {
        final String mode;
        JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
        if (src.getText().equals(I18N.tr(Lang.COLORMODE_ON))) {
            mode = (src.isSelected() ? LSProperties.COLORMODE_ON : LSProperties.COLORMODE_OFF);
        } else {
            mode = (src.isSelected() ? LSProperties.COLORMODE_OFF : LSProperties.COLORMODE_ON);
        }
        LSProperties.getInstance().setProperty(LSProperties.COLORMODE, mode);
        Wire.setColorMode();

        actionChangedSettings(false);
    }

    private void changeComplexity(ActionEvent e) {
        JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
        final int nameLen = src.getName().length();
        final String level = (nameLen < 1 ? "2" : src.getName().substring(nameLen - 1));
        LSProperties.getInstance().setProperty(LSProperties.COMPLEXITY, level);
        refreshGateList();
    }

    /**
     * helper method to get a certain menu component
     * so we don't have to set every item as member variable
     *
     * @param lang language enum
     * @return menu item
     */
    private AbstractButton getMenuWidget(Lang lang) {
        for (int i = 0; i < mnuBar.getMenuCount(); i++) {
            JMenu mnu = mnuBar.getMenu(i);
            for (Component c : mnu.getMenuComponents()) {
                if (lang.toString().equals(c.getName())) {
                    return (AbstractButton) c;
                }
            }
        }
        return null;
    }

    /**
     * helper method to get a certain button component
     * so we don't have to set every button as member variable
     *
     * @param lang language enum
     * @return button
     */
    private AbstractButton getButtonWidget(Lang lang) {
        for (Component c : btnBar.getComponents()) {
            if (lang.toString().equals(c.getName())) {
                return (AbstractButton) c;
            }
        }
        return null;
    }

    /**
     * add all languages from file system to languages menu
     *
     * @param menu the menu to fill
     * @param currentLanguage the current language
     */
    void createLanguageMenu(JMenu menu, String currentLanguage) {
        List<String> langs = I18N.getLanguages();
        ButtonGroup btnGroup = new ButtonGroup();
        for (String lang : langs) {
            JMenuItem item = new JRadioButtonMenuItem(lang);
            if (lang.equals(currentLanguage))
                item.setSelected(true);
            item.addActionListener(e -> {
                LSProperties.getInstance().setProperty(LSProperties.LANGUAGE,
                        ((JMenuItem) e.getSource()).getText());
                actionChangedSettings(true);
            });
            btnGroup.add(item);
            menu.add(item);
        }
    }

    @Override
    public void changedCircuit() {
        if (lsFile != null) {
            lsFile.changed = true;
        }
        setAppTitle();
    }

    @Override
    public void changedStatusText(String text) {
        setStatusText(text);
    }

    @Override
    public void changedZoomPos(double zoom, Point pos) {
        mouseX = pos.x;
        mouseY = pos.y;
        sbCoordinates.setText("X: " + pos.x / 10 * 10
                + ", Y: " + pos.y / 10 * 10
                + "   Zoom: " + Math.round(zoom * 100) + "%");
    }

    @Override
    public void setAction(int action) {
    }

    @Override
    public void needsRepaint(CircuitPart circuitPart) {
    }

    private class AppTitleManager {

        private String circuitName = null;
        private boolean fileChanged = false;
        private boolean titleSet = false;
        private void setAppTitleIfChanged(String currentName, boolean isChanged) {
            if (Objects.equals(circuitName, currentName) && (fileChanged == isChanged) && titleSet) {
                return;
            }
            circuitName = currentName;
            fileChanged = isChanged;
            titleSet = true;
            setTitle(makeAppTitle());
        }

        private String makeAppTitle() {
            if (circuitName == null || circuitName.isEmpty()) {
                return "LogicSim";
            }
            return "LogicSim - " + circuitName + (fileChanged ? "*" : "");
        }

    }
    @Override
    public void actionLoadModule() {
        if (Simulation.getInstance().isRunning()) {
            return;
        }

        final File file = new File(lsFile.fileName);
        JFileChooser chooser = new JFileChooser(file.getParent());
        final String filterDesc = "Modules (." + App.MODULE_FILE_SUFFIX + ")";
        chooser.setFileFilter(new LogicSimFileFilter(filterDesc, App.MODULE_FILE_SUFFIX));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final File selectedFile = chooser.getSelectedFile();
        if (!selectedFile.exists() || !selectedFile.isFile()) {
            return;
        }
        Collection<GateDefinition> moduleDefs = ModuleLoader.loadModule(selectedFile);
        if (moduleDefs != null) {
            for (GateDefinition moduleDef : moduleDefs) {
                GateLibrary.addModule(moduleDef);
            }
            refreshGateList();
        }
    }

    @Override
    public void actionChangedSettings(boolean needRestart) {
        lspanel.repaint();
        if (needRestart) {
            Dialogs.messageDialog(this, I18N.tr(Lang.LSRESTART));
        }
    }

    @Override
    public void actionSetAction(Action action) {
        switch (action) {
            case ACTION_PINNORMAL:
                lspanel.setAction(Pin.NORMAL);
                setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
                lspanel.requestFocusInWindow();
                break;
            case ACTION_PININVERTED:
                lspanel.setAction(Pin.INVERTED);
                setStatusText(I18N.tr(Lang.INPUTINV_HELP));
                lspanel.requestFocusInWindow();
                break;
            case ACTION_PINHIGH:
                lspanel.setAction(Pin.HIGH);
                setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
                break;
            case ACTION_PINLOW:
                lspanel.setAction(Pin.LOW);
                setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
                lspanel.requestFocusInWindow();
                break;
            case ACTION_ADDWIRE:
                lspanel.setAction(LSPanel.ACTION_ADDWIRE);
                setStatusText(I18N.tr(Lang.WIRENEW_HELP));
                lspanel.requestFocusInWindow();
                break;
            case ACTION_ADDPOINT:
                lspanel.setAction(LSPanel.ACTION_ADDPOINT);
                setStatusText(I18N.tr(Lang.ADDPOINT_HELP));
                lspanel.requestFocusInWindow();
                break;
            case ACTION_DELPOINT:
                lspanel.setAction(LSPanel.ACTION_DELPOINT);
                setStatusText(I18N.tr(Lang.REMOVEPOINT_HELP));
                lspanel.requestFocusInWindow();
                break;
        }
    }
}
