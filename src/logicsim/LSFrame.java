package logicsim;

import logicsim.localization.I18N;
import logicsim.localization.Lang;
import logicsim.ui.LSButton;
import logicsim.ui.LSFrame_AboutBox;
import logicsim.ui.LSScrollBarUI;
import logicsim.ui.LSToggleButton;
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
import java.io.Serial;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.datatransfer.*;

public class LSFrame extends JFrame implements ActionListener, CircuitChangedListener {
	@Serial
    private static final long serialVersionUID = -5281157929385660575L;

	LogicSimFile lsFile;

	JMenuBar mnuBar;
	JToolBar btnBar;

	DefaultListModel<Object> partListModel = new DefaultListModel<>();
	JList<Object> lstParts = new JList<>(partListModel);
	JComboBox<String> cbNumInputs = null;
	LSPanel lspanel = new LSPanel();

    JSplitPane splitPane;
    JPanel pnlGateList = new JPanel();
    int dividerLocation;


	JLabel sbText = new JLabel();
	JLabel sbCoordinates = new JLabel();

	int popupGateIdx;
	JPopupMenu popup;
	JMenuItem menuItem_remove;
	JMenuItem menuItem_properties;
	JMenuItem menuItem_rotate;
	JMenuItem menuItem_mirror;
    JMenuItem menuItem_increase_inputs;
    JMenuItem menuItem_decrease_inputs;

    int mouseX, mouseY;

    int lastPressedListIndex = -1;
    boolean listDragArmed = false;

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
			Object o;
			int idx = lastPressedListIndex >= 0 ? lastPressedListIndex : lstParts.getSelectedIndex();
			if (idx < 0 || idx >= partListModel.getSize()) return null;
			o = partListModel.getElementAt(idx);
			if (!(o instanceof Gate gate)) return null;
			// Eingangsanzahl aus ComboBox
			int numInputs = 2;
			try {
				String sel = Objects.requireNonNull(cbNumInputs.getSelectedItem()).toString();
				numInputs = Integer.parseInt(sel.substring(0, 1));
			} catch (Exception ignored) {}
			return new GateInfoTransferable(new GateDragInfo(gate, numInputs));
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
			if (!canImport(support)) return false;
			try {
				GateDragInfo info = (GateDragInfo) support.getTransferable().getTransferData(GateDragInfo.FLAVOR);
				if (info == null || info.getPrototype() == null) return false;
				Gate prototype = info.getPrototype();
				Gate gate = GateLoaderHelper.create(prototype);
				// Eingänge ggf. setzen
				if (gate.supportsVariableInputs()) {
					gate.createDynamicInputs(info.getNumInputs());
				}
				// Drop-Position (Component-Koordinaten -> Weltkoordinaten)
				Point p = support.getDropLocation().getDropPoint();
				int worldX = (int) lspanel.getTransformer().screenToWorldX(p.x);
				int worldY = (int) lspanel.getTransformer().screenToWorldY(p.y);
				lspanel.addGateAt(gate, worldX, worldY);
				return true;
			} catch (UnsupportedFlavorException | IOException ex) {
				ex.printStackTrace();
				return false;
			}
		}
	}

	public LSFrame(String title) {
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
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (!showDiscardDialog(I18N.tr(Lang.EXIT)))
				return;
			System.exit(0);
		}
	}

	private String defaultModuleFileName() {
		String fn = App.getModulePath();
		fn += I18N.tr(Lang.UNNAMED);
		fn += "." + App.MODULE_FILE_SUFFIX;
		return fn;
	}

	private String defaultCircuitFileName() {
		String fn = App.getCircuitPath();
		fn += I18N.tr(Lang.UNNAMED);
		fn += "." + App.CIRCUIT_FILE_SUFFIX;
		return fn;
	}

	/** Component initialization */
	private void createUI() {
		setTitle("LogicSim");

		String mode = LSProperties.getInstance().getProperty(LSProperties.MODE, LSProperties.MODE_NORMAL);

        // ------------------------------------------------------------------
        // MENU
        // ------------------------------------------------------------------
		mnuBar = new JMenuBar();

		JMenu mnu = new JMenu(I18N.tr(Lang.FILE));

		JMenuItem m = createMenuItem(Lang.NEW, KeyEvent.VK_N, false);
        m.setEnabled(!Simulation.getInstance().isRunning());
		m.addActionListener(this::actionNew);
		mnu.add(m);

		m = createMenuItem(Lang.OPEN, KeyEvent.VK_O, true);
        m.setEnabled(!Simulation.getInstance().isRunning());
		m.addActionListener(this::actionOpen);
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.SAVE, KeyEvent.VK_S, true);
		m.addActionListener(e -> actionSave(e, false));
		mnu.add(m);

		m = createMenuItem(Lang.SAVEAS, 0, true);
		m.addActionListener(e -> actionSave(e, true));
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.MODULECREATE, 0, true);
		m.addActionListener(this::actionCreateModule);
		mnu.add(m);

		m = createMenuItem(Lang.PROPERTIES, 0, true);
		m.addActionListener(_ -> {
            if (FileInfoDialog.showFileInfo(LSFrame.this, lsFile)) {
                setAppTitle();
            }
        });
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.EXPORT, 0, true);
		m.addActionListener(e -> exportImage());
		mnu.add(m);

		m = createMenuItem(Lang.PRINT, 0, true);
		m.addActionListener(_ -> lspanel.doPrint());
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.EXIT, KeyEvent.VK_X, false);
		m.addActionListener(e -> {
            if (!showDiscardDialog(I18N.tr(Lang.EXIT)))
                return;
            System.exit(0);
        });
		mnu.add(m);

		mnuBar.add(mnu);

		// ------------------------------------------------------------------
		// EDIT
		/*mnu = new JMenu(I18N.tr(Lang.EDIT));

		m = createMenuItem(Lang.SELECTALL, KeyEvent.VK_A, false);
		m.addActionListener(_ -> {
            lspanel.circuit.selectAll();
            lspanel.repaint();
        });
		mnu.add(m);

		m = createMenuItem(Lang.SELECT, 0, false);
		m.addActionListener(_ -> {
            lspanel.setAction(LSPanel.ACTION_SELECT);
            lspanel.requestFocusInWindow();
        });
		mnu.add(m);

		m = createMenuItem(Lang.SELECTNONE, 0, false);
		m.addActionListener(_ -> {
            lspanel.circuit.deselectAll();
            lspanel.repaint();
        });
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.WIRENEW, KeyEvent.VK_W, false);
		//m.addActionListener(e -> Objects.requireNonNull(getButtonWidget(Lang.WIRENEW)).doClick());
		m.setEnabled(LSProperties.MODE_EXPERT.equals(mode));
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.INPUTHIGH, 0, false);
		m.addActionListener(_ -> {
            lspanel.setAction(Pin.HIGH);
            setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
        });
		mnu.add(m);

		m = createMenuItem(Lang.INPUTLOW, 0, false);
		m.addActionListener(_ -> {
            lspanel.setAction(Pin.LOW);
            setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
        });
		mnu.add(m);

		m = createMenuItem(Lang.INPUTINV, 0, false);
		m.addActionListener(_ -> {
            lspanel.setAction(Pin.INVERTED);
            setStatusText(I18N.tr(Lang.INPUTINV_HELP));
        });
		mnu.add(m);

		m = createMenuItem(Lang.INPUTNORM, 0, false);
		m.addActionListener(_ -> {
            lspanel.setAction(Pin.NORMAL);
            setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
        });
		mnu.add(m);

		mnu.addSeparator();

		m = createMenuItem(Lang.ROTATE, 0, false);
		m.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK, false));
		m.addActionListener(_ -> lspanel.rotateSelected());
		mnu.add(m);

		m = createMenuItem(Lang.MIRROR, KeyEvent.VK_M, false);
		m.addActionListener(_ -> lspanel.mirrorSelected());
		mnu.add(m);*/

		//mnuBar.add(mnu);
		// ------------------------------------------------------------------
		// SETTINGS
		mnu = new JMenu(I18N.tr(Lang.SETTINGS));

		boolean sel = LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true);
		final JCheckBoxMenuItem mSettingsPaintGrid = new JCheckBoxMenuItem(I18N.tr(Lang.PAINTGRID));
		mSettingsPaintGrid.setSelected(sel);
		mSettingsPaintGrid.addActionListener(e -> {
            LSProperties.getInstance().setPropertyBoolean(LSProperties.PAINTGRID, mSettingsPaintGrid.isSelected());
            lspanel.repaint();
        });
		mnu.add(mSettingsPaintGrid);

		boolean autowire = LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true);
		final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(I18N.tr(Lang.AUTOWIRE));
		cbMenuItem.setSelected(autowire);
		cbMenuItem.addActionListener(e -> {
            JCheckBoxMenuItem bmi = (JCheckBoxMenuItem) e.getSource();
            LSProperties.getInstance().setPropertyBoolean(LSProperties.AUTOWIRE, bmi.isSelected());
            lspanel.repaint();
        });
		mnu.add(cbMenuItem);

		m = new JMenu(I18N.tr(Lang.GATEDESIGN));
		String gatedesign = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN,
				LSProperties.GATEDESIGN_IEC);

		JRadioButtonMenuItem mGatedesignIEC = new JRadioButtonMenuItem();
		mGatedesignIEC.setText(I18N.tr(Lang.GATEDESIGN_IEC));
		mGatedesignIEC.addActionListener(this::actionGateDesign);
		mGatedesignIEC.setSelected(LSProperties.GATEDESIGN_IEC.equals(gatedesign));
		m.add(mGatedesignIEC);

		JRadioButtonMenuItem mGatedesignANSI = new JRadioButtonMenuItem();
		mGatedesignANSI.setText(I18N.tr(Lang.GATEDESIGN_ANSI));
		mGatedesignANSI.addActionListener(this::actionGateDesign);
		mGatedesignANSI.setSelected(LSProperties.GATEDESIGN_ANSI.equals(gatedesign));
		m.add(mGatedesignANSI);

		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(mGatedesignIEC);
		btnGroup.add(mGatedesignANSI);

		mnu.add(m);

		JMenu mnuMode = new JMenu(I18N.tr(Lang.MODE));
		btnGroup = new ButtonGroup();

		JRadioButtonMenuItem mnuItem = new JRadioButtonMenuItem(I18N.tr(Lang.NORMAL));
		mnuItem.addActionListener(this::actionMode);
		mnuItem.setSelected(LSProperties.MODE_NORMAL.equals(mode));
		btnGroup.add(mnuItem);
		mnuMode.add(mnuItem);

		mnuItem = new JRadioButtonMenuItem(I18N.tr(Lang.EXPERT));
		mnuItem.addActionListener(this::actionMode);
		mnuItem.setSelected(LSProperties.MODE_EXPERT.equals(mode));
		btnGroup.add(mnuItem);
		mnuMode.add(mnuItem);

		mnu.add(mnuMode);

		// ---------------------------------------------------------------

		m = new JMenu(I18N.tr(Lang.COLORMODE));
		btnGroup = new ButtonGroup();
		String cMode = LSProperties.getInstance().getProperty(LSProperties.COLORMODE, LSProperties.COLORMODE_ON);

		JRadioButtonMenuItem mCmOn = new JRadioButtonMenuItem();
		mCmOn.setText(I18N.tr(Lang.COLORMODE_ON));
		mCmOn.addActionListener(this::actionColorMode);
		mCmOn.setSelected(LSProperties.COLORMODE_ON.equals(cMode));
		m.add(mCmOn);

		JRadioButtonMenuItem mCmOff = new JRadioButtonMenuItem();
		mCmOff.setText(I18N.tr(Lang.COLORMODE_OFF));
		mCmOff.addActionListener(this::actionColorMode);
		mCmOff.setSelected(LSProperties.COLORMODE_OFF.equals(cMode));
		m.add(mCmOff);

		btnGroup.add(mCmOn);
		btnGroup.add(mCmOff);

		mnu.add(m);

		JMenu mnuLang = new JMenu(I18N.tr(Lang.LANGUAGE));
		String currentLanguage = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "de");
		createLanguageMenu(mnuLang, currentLanguage);
		mnu.add(mnuLang);

		mnu.addSeparator();

		m = createMenuItem(Lang.GATESETTINGS, 0, true);
		m.addActionListener(_ -> lspanel.gateSettings());
		mnu.add(m);

		mnuBar.add(mnu);

		// ------------------------------------------------------------------
		// HELP
		mnu = new JMenu(I18N.tr(Lang.HELP));

		m = createMenuItem(Lang.HELP, 0, true);
		m.addActionListener(_ -> new HTMLHelp());
		mnu.add(m);

		m = createMenuItem(Lang.ABOUT, 0, true);
		m.addActionListener(_ -> new LSFrame_AboutBox(LSFrame.this));
		mnu.add(m);

		mnuBar.add(mnu);

		setJMenuBar(mnuBar);

		// ------------------------------------------------------------------
		// compose GUI

		JPanel statusBar = new JPanel();
		statusBar.setLayout(new BorderLayout());
		statusBar.add(sbText, BorderLayout.WEST);
		statusBar.add(sbCoordinates, BorderLayout.EAST);
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setStatusText(" ");
		// sbText.setPreferredSize(new Dimension(700, 15));
		sbCoordinates.setText(" ");
		// sbCoordinates.setPreferredSize(new Dimension(200, 20));
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
                    if (!(o instanceof Gate gateProto)) return;
                    // Neues Gate aus Prototyp erstellen
                    Gate gate = GateLoaderHelper.create(gateProto);
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

		btnBar = new JToolBar();

		LSButton btnLS = new LSButton("new", Lang.NEW);
        btnLS.setEnabled(!Simulation.getInstance().isRunning());
		btnLS.addActionListener(this::actionNew);
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("open", Lang.OPEN);
        btnLS.setEnabled(!Simulation.getInstance().isRunning());
		btnLS.addActionListener(this::actionOpen);
		btnBar.add(btnLS);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("save", Lang.SAVE);
		btnLS.addActionListener(e -> actionSave(e, false));
		btnBar.add(btnLS);

		btnBar.add(getMenuGap());

		LSToggleButton btnToggle = new LSToggleButton("play", Lang.SIMULATE);
		btnToggle.addActionListener(this::actionSimulate);
		btnBar.add(btnToggle, null);
        btnBar.add(getMenuGap());

		btnLS = new LSButton("inputnorm", Lang.INPUTNORM);
		btnLS.addActionListener(_ -> {
            lspanel.setAction(Pin.NORMAL);
            setStatusText(I18N.tr(Lang.INPUTNORM_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("inputinv", Lang.INPUTINV);
		btnLS.addActionListener(_ -> {
            lspanel.setAction(Pin.INVERTED);
            setStatusText(I18N.tr(Lang.INPUTINV_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("inputhigh", Lang.INPUTHIGH);
		btnLS.addActionListener(_ -> {
            lspanel.setAction(Pin.HIGH);
            setStatusText(I18N.tr(Lang.INPUTHIGH_HELP));
        });
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("inputlow", Lang.INPUTLOW);
		btnLS.addActionListener(_ -> {
            lspanel.setAction(Pin.LOW);
            setStatusText(I18N.tr(Lang.INPUTLOW_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);

		btnBar.add(getMenuGap());

		btnLS = new LSButton("newwire", Lang.WIRENEW);
		btnLS.setEnabled(LSProperties.MODE_EXPERT.equals(mode));
		btnLS.addActionListener(_ -> {
            lspanel.setAction(LSPanel.ACTION_ADDWIRE);
            setStatusText(I18N.tr(Lang.WIRENEW_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("addpoint", Lang.ADDPOINT);
		btnLS.addActionListener(_ -> {
            lspanel.setAction(LSPanel.ACTION_ADDPOINT);
            setStatusText(I18N.tr(Lang.ADDPOINT_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);
        btnBar.add(getSmallMenuGap());

		btnLS = new LSButton("delpoint", Lang.REMOVEPOINT);
		btnLS.addActionListener(e -> {
            lspanel.setAction(LSPanel.ACTION_DELPOINT);
            setStatusText(I18N.tr(Lang.REMOVEPOINT_HELP));
            lspanel.requestFocusInWindow();
        });
		btnBar.add(btnLS, null);

		add(btnBar, BorderLayout.NORTH);

		// ------------------------------------------------------------------
		// Create the popup menu.
		popup = new JPopupMenu();

		menuItem_remove = new JMenuItem(I18N.tr(Lang.REMOVEGATE));
		menuItem_remove.addActionListener(this);
		popup.add(menuItem_remove);

		menuItem_properties = new JMenuItem(I18N.tr(Lang.PROPERTIES));
		menuItem_properties.addActionListener(this);
		popup.add(menuItem_properties);

		// rotate and mirror actions for popup
		menuItem_rotate = new JMenuItem(I18N.tr(Lang.ROTATE));
		menuItem_rotate.addActionListener(this);
		popup.add(menuItem_rotate);

		menuItem_mirror = new JMenuItem(I18N.tr(Lang.MIRROR));
		menuItem_mirror.addActionListener(this);
		popup.add(menuItem_mirror);

        menuItem_increase_inputs = new JMenuItem(I18N.tr(Lang.ADDINPUT));
        menuItem_increase_inputs.addActionListener(this);
        popup.add(menuItem_increase_inputs);

        menuItem_decrease_inputs = new JMenuItem(I18N.tr(Lang.REMOVEINPUT));
        menuItem_decrease_inputs.addActionListener(this);
        popup.add(menuItem_decrease_inputs);
		// Add listener to components that can bring up popup menus.
		lspanel.addMouseListener(new PopupListener());

		fillGateList();
		setAppTitle();

		lspanel.requestFocusInWindow();
	}

	private JMenuItem createMenuItem(Lang lang, int key, boolean isDialog) {
		JMenuItem m = new JMenuItem(I18N.tr(lang) + (isDialog ? "..." : ""));
		if (key != 0)
			m.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK, false));
		m.setName(lang.toString());
		return m;
	}

	private void setStatusText(String string) {
		sbText.setText("  " + string);
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
		} else if (source == menuItem_properties) {
			if (popupGateIdx >= 0) {
				Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
				g.showPropertiesUI(this);
				lspanel.repaint();
			}
		} else if (source == menuItem_rotate) {
			if (popupGateIdx >= 0) {
				Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
				// if multiple parts selected and the clicked gate is part of the selection,
				// rotate all selected; otherwise rotate only the clicked gate
				CircuitPart[] sel = lspanel.circuit.getSelected();
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
		} else if (source == menuItem_mirror) {
			if (popupGateIdx >= 0) {
				Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
				CircuitPart[] sel = lspanel.circuit.getSelected();
				boolean clickedInSelection = false;
				for (CircuitPart p : sel) {
					if (p == g) {
						clickedInSelection = true;
						break;
					}
				}
				if (clickedInSelection && sel.length > 1) {
					lspanel.mirrorSelected();
				} else {
					g.mirror();
					lspanel.changedCircuit();
				}
				lspanel.repaint();
			}
		} else if (source == menuItem_increase_inputs) {
            if (popupGateIdx >= 0) {
                Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
                CircuitPart[] sel = lspanel.circuit.getSelected();
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
        } else if (source == menuItem_decrease_inputs) {
            if (popupGateIdx >= 0) {
                Gate g = (Gate) lspanel.circuit.getParts().get(popupGateIdx);
                CircuitPart[] sel = lspanel.circuit.getSelected();
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
                menuItem_properties.setEnabled(g.hasPropertiesUI());
                menuItem_decrease_inputs.setEnabled(g.variableInputCountSupported && g.getNumInputs() > 2);
                menuItem_increase_inputs.setEnabled(g.variableInputCountSupported && g.getNumInputs() < 5);
                popup.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
                break;
            }
		}
	}

	void actionSimulate(ActionEvent e) {
		LSToggleButton btn = (LSToggleButton) e.getSource();

		if (btn.isSelected()) {
			if (!Simulation.getInstance().isRunning()) {
				lspanel.circuit.deselectAll();
				repaint();
				Simulation.getInstance().start();
				changedStatusText(I18N.tr(Lang.SIMULATION_STARTED));
                dividerLocation = splitPane.getDividerLocation();
                splitPane.remove(pnlGateList);
			}
		} else {
			if (Simulation.getInstance().isRunning()) {
				Simulation.getInstance().stop();
				changedStatusText(I18N.tr(Lang.SIMULATION_STOPPED));
                splitPane.add(pnlGateList, JSplitPane.LEFT);
                splitPane.setDividerLocation(dividerLocation);
			}
		}

        Objects.requireNonNull(getMenuWidget(Lang.OPEN)).setEnabled(!Simulation.getInstance().isRunning());
        Objects.requireNonNull(getButtonWidget(Lang.OPEN)).setEnabled(!Simulation.getInstance().isRunning());
        Objects.requireNonNull(getMenuWidget(Lang.NEW)).setEnabled(!Simulation.getInstance().isRunning());
        Objects.requireNonNull(getButtonWidget(Lang.NEW)).setEnabled(!Simulation.getInstance().isRunning());
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
	 * 
	 * @param e Event
	 */
	void actionNew(ActionEvent e) {
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
	 * 
	 * @param e Event
	 */
	void actionOpen(ActionEvent e) {
		if (Simulation.getInstance().isRunning())
			return;

		if (!showDiscardDialog(I18N.tr(Lang.OPEN)))
			return;

		File file = new File(lsFile.fileName);
		JFileChooser chooser = new JFileChooser(file.getParent());
		chooser.setFileFilter(setupFilter());
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			lsFile.fileName = chooser.getSelectedFile().getAbsolutePath();
		} else
			return;

		try {
			lsFile = XMLLoader.loadXmlFile(lsFile.fileName);
		} catch (RuntimeException x) {
			System.err.println(x);
			x.printStackTrace(System.err);
			Dialogs.messageDialog(this, I18N.tr(Lang.READERROR) + " " + x.getMessage());
		}
		if (lsFile.getErrorString() != null) {
			Dialogs.messageDialog(this, lsFile.getErrorString());
		}
		setAppTitle();
		lspanel.clear();
		lspanel.circuit = lsFile.circuit;
		lspanel.circuit.setRepaintListener(lspanel);
		lspanel.circuit.reset();
	}

	/**
	 * Set up a file filter for displaying files who have the correct ending
	 * 
	 * @return FileFilter
	 */
	private FileFilter setupFilter() {
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(App.CIRCUIT_FILE_SUFFIX);
		filter.addExtension(App.MODULE_FILE_SUFFIX);
		filter.setDescription(
				"LogicSim Files (" + "." + App.CIRCUIT_FILE_SUFFIX + ", " + "." + App.MODULE_FILE_SUFFIX + ")");
		return filter;
	}

	/**
	 * set window title
	 */
	private void setAppTitle() {
		String name = lsFile.getName();
		name = "LogicSim - " + name;
		if (lsFile.changed)
			name += "*";
		this.setTitle(name);
	}

	/**
	 * handles saving of circuit file
	 * 
	 * @param e Event
	 */
	void actionSave(ActionEvent e, boolean saveAs) {
		String fileName = lsFile.fileName;
		boolean unnamed = lsFile.extractFileName().equals(I18N.tr(Lang.UNNAMED));
        boolean showDialog = fileName == null || fileName.isEmpty() || unnamed || saveAs;

		if (showDialog)
			if (!showSaveDialog())
				return;
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
		fillGateList();
	}

	/**
	 * helper method to show the save dialog
	 * 
	 * @return true when user selected a file, false when user canceled the dialog
	 */
	public boolean showSaveDialog() {
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
		} else
			return false;
	}

	/**
	 * handles initial steps to create a new module
	 * 
	 * @param e Event
	 */
	void actionCreateModule(ActionEvent e) {
		if (lsFile.circuit.isModule()) {
			Dialogs.messageDialog(this, I18N.tr(Lang.ALREADYMODULE));
			return;
		}

		if (!lsFile.circuit.isEmpty()) {
			String filename = lsFile.extractFileName();
			String fn = App.getModulePath();
			fn += filename;
			fn += "." + App.MODULE_FILE_SUFFIX;
			lsFile.fileName = fn;
			lsFile.changed = true;
		} else {
			lsFile = new LogicSimFile(defaultModuleFileName());
			lsFile.circuit.setRepaintListener(lspanel);
		}

		if (!FileInfoDialog.showFileInfo(this, lsFile))
			return;

		setAppTitle();

		Gate g = new MODIN();
		g.moveTo(150, 100);
		g.loadProperties();
		lsFile.circuit.addGate(g);
		g = new MODOUT();
		g.moveTo(650, 100);
		g.loadProperties();
		lsFile.circuit.addGate(g);
		lspanel.circuit = lsFile.circuit;
		lspanel.circuit.setRepaintListener(lspanel);
		lspanel.repaint();
	}

	/**
	 * save image in file system
	 */
	void exportImage() {
		String filename = "logicsim.png";
		JFileChooser chooser = new JFileChooser();
		LogicSimFileFilter filter = new LogicSimFileFilter();
		filter.addExtension(".png");
		filter.setDescription("Portable Network Graphics");
		chooser.setFileFilter(filter);

		chooser.setDialogTitle(I18N.tr(Lang.SAVECIRCUIT));
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			filename = chooser.getSelectedFile().getAbsolutePath();
			if (!filename.endsWith(".png")) {
				filename += ".png";
			}
		} else {
			return;
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

	/**
	 * fill gate list
	 */
	void fillGateList() {
		partListModel.clear();
		for (Category cat : App.cats) {
			if ("hidden".equals(cat.getTitle()))
				continue;
			if (cat.getGates().isEmpty())
				continue;
			partListModel.addElement(cat.getTitle());
			for (Gate g : cat.getGates()) {
				partListModel.addElement(g);
			}
		}
	}

	/**
	 * handles gates list
	 * 
	 * @param e Event
	 */
	void actionLstGatesSelected(ListSelectionEvent e) {
		if (Simulation.getInstance().isRunning())
			return;
		if (e.getValueIsAdjusting()) return;
		int sel = lstParts.getSelectedIndex();
		if (sel < 0)
			return;
		Object o = lstParts.getSelectedValue();
		if (!(o instanceof Gate gate))
			return;
		if (gate.type != null) {
			if (gate.type.contains("test"))
				setStatusText(gate.type);
			else if (I18N.hasString(gate.type, "description")) {
				setStatusText(I18N.getString(gate.type, "description"));
			} else {
				setStatusText(I18N.getString(gate.type, "title"));
			}
		}
	}

	/**
	 * handles gate design (IEC/ISO)
	 * 
	 * @param e Event
	 */
	void actionGateDesign(ActionEvent e) {
		String gatedesign;
		JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
		if (src.getText().equals(I18N.tr(Lang.GATEDESIGN_IEC))) {
			if (src.isSelected())
				gatedesign = LSProperties.GATEDESIGN_IEC;
			else
				gatedesign = LSProperties.GATEDESIGN_ANSI;
		} else {
			if (src.isSelected())
				gatedesign = LSProperties.GATEDESIGN_ANSI;
			else
				gatedesign = LSProperties.GATEDESIGN_IEC;
		}
		LSProperties.getInstance().setProperty(LSProperties.GATEDESIGN, gatedesign);
		this.lspanel.repaint();
	}

	/**
	 * handles color mode (on-redblack / off - blackwhite for printing)
	 * 
	 * @param e Event
	 */
	private void actionColorMode(ActionEvent e) {
		String mode;
		JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
		if (src.getText().equals(I18N.tr(Lang.COLORMODE_ON))) {
			if (src.isSelected())
				mode = LSProperties.COLORMODE_ON;
			else
				mode = LSProperties.COLORMODE_OFF;
		} else {
			// the expert item is clicked
			if (src.isSelected()) {
				mode = LSProperties.COLORMODE_OFF;
			} else {
				mode = LSProperties.COLORMODE_ON;
			}
		}
		LSProperties.getInstance().setProperty(LSProperties.COLORMODE, mode);

		Wire.setColorMode();

		this.lspanel.repaint();
	}

	/**
	 * handles mode (normal/expert)
	 * 
	 * @param e Event
	 */
	void actionMode(ActionEvent e) {
		String mode;
		JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
		if (src.getText().equals(I18N.tr(Lang.NORMAL))) {
			if (src.isSelected())
				mode = LSProperties.MODE_NORMAL;
			else
				mode = LSProperties.MODE_EXPERT;
		} else {
			// the expert item is clicked
			if (src.isSelected()) {
				mode = LSProperties.MODE_EXPERT;
			} else {
				mode = LSProperties.MODE_NORMAL;
			}
		}
		LSProperties.getInstance().setProperty(LSProperties.MODE, mode);

		// activate widgets
		//Objects.requireNonNull(getMenuWidget(Lang.WIRENEW)).setEnabled(LSProperties.MODE_EXPERT.equals(mode));
		//Objects.requireNonNull(getButtonWidget(Lang.WIRENEW)).setEnabled(LSProperties.MODE_EXPERT.equals(mode));

		this.lspanel.repaint();
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
				if (lang.toString().equals(c.getName()))
					return (AbstractButton) c;
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
			if (lang.toString().equals(c.getName()))
				return (AbstractButton) c;
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
                Dialogs.messageDialog(LSFrame.this, I18N.tr(Lang.LSRESTART));
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
		// this is a hack - maybe it is ok...
		if (LSPanel.NOTHING.equals(text)) {
			for (Component c : btnBar.getComponents()) {
				if (c instanceof LSToggleButton b) {
                    if (!b.id.equals(I18N.langToStr(Lang.SIMULATE)))
						b.setSelected(false);
				}
			}
			return;
		}
		if (LSPanel.MSG_ABORTED.equals(text)) {
			for (Component c : btnBar.getComponents()) {
				if (c instanceof LSToggleButton b) {
                    b.setSelected(false);
				}
			}
			setStatusText(I18N.tr(Lang.ABORTED));
			repaint();
		} else
			setStatusText(text);
	}

	@Override
	public void changedZoomPos(double zoom, Point pos) {
        mouseX = pos.x;
        mouseY = pos.y;
		sbCoordinates.setText(
				"X: " + pos.x / 10 * 10 + ", Y: " + pos.y / 10 * 10 + "   Zoom: " + Math.round(zoom * 100) + "%");
	}

	@Override
	public void setAction(int action) {
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
	}

}
