package logicsim.ui;

import logicsim.LSProperties;
import logicsim.Simulation;
import logicsim.Wire;
import logicsim.controllers.AppController;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;


public class MenuManager {
    private final AppController parent;
    private final ArrayList<JMenu> menusToHide = new ArrayList<>();
    private final HashSet<JMenuItem> itemsDisabledBySimulation = new HashSet<>();

    public MenuManager(AppController parent) {
        this.parent = parent;
    }

    /** @return a JMenuBar object containing all the menu hierarchy */
    public JMenuBar makeMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        JMenu mnu = new JMenu(I18N.tr(Lang.FILE));

        final JMenuItem mnuNew = createMenuItem(Lang.NEW, KeyEvent.VK_N, false);
        mnuNew.setEnabled(!Simulation.getInstance().isRunning());
        mnuNew.addActionListener(e -> parent.actionNewCircuit());
        mnu.add(mnuNew);
        itemsDisabledBySimulation.add(mnuNew);

        final JMenuItem mnuOpen = createMenuItem(Lang.OPEN, KeyEvent.VK_O, true);
        mnuOpen.setEnabled(!Simulation.getInstance().isRunning());
        mnuOpen.addActionListener(e -> parent.actionOpenCircuit());
        mnu.add(mnuOpen);
        itemsDisabledBySimulation.add(mnuOpen);

        mnu.addSeparator();

        final JMenuItem mnuFileProperties = createMenuItem(Lang.PROPERTIES, 0, true);
        mnuFileProperties.addActionListener(e -> parent.actionEditFileInfo());
        mnu.add(mnuFileProperties);

        mnu.addSeparator();

        final JMenuItem mnuSave = createMenuItem(Lang.SAVE, KeyEvent.VK_S, true);
        mnuSave.addActionListener(e -> parent.actionSave(false));
        mnu.add(mnuSave);

        final JMenuItem mnuSaveAs = createMenuItem(Lang.SAVEAS, 0, true);
        mnuSaveAs.addActionListener(e -> parent.actionSave(true));
        mnu.add(mnuSaveAs);

        final JMenuItem mnuExport = createMenuItem(Lang.EXPORT, 0, true);
        mnuExport.addActionListener(e -> parent.actionExportImage());
        mnu.add(mnuExport);

        mnu.addSeparator();

        final JMenuItem mnuExit = createMenuItem(Lang.EXIT, KeyEvent.VK_X, false);
        mnuExit.addActionListener(e -> parent.actionExit());
        mnu.add(mnuExit);

        menuBar.add(mnu);
        // ------------------------------------------------------------------
        // Module
        final JMenu moduleMenu = new JMenu(I18N.tr(Lang.MENU_MODULE));

        final JMenuItem mnuCreateModule = createMenuItem(Lang.MENU_MODULECREATE, 0, true);
        mnuCreateModule.addActionListener(e -> parent.actionCreateModule());
        moduleMenu.add(mnuCreateModule);
        itemsDisabledBySimulation.add(mnuCreateModule);

        final JMenuItem mnuLoadModule = new JMenuItem(I18N.tr(Lang.MENU_LOADMODULE));
        mnuLoadModule.addActionListener(e -> parent.actionLoadModule());
        moduleMenu.add(mnuLoadModule);
        itemsDisabledBySimulation.add(mnuLoadModule);
        menuBar.add(moduleMenu);
        menusToHide.add(moduleMenu);

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
        hideMenusForComplexity(complexity);
        mnu.add(mnuComplexity);

        boolean sel = LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true);
        final JCheckBoxMenuItem paintGridCheckbox = new JCheckBoxMenuItem(I18N.tr(Lang.PAINTGRID));
        paintGridCheckbox.setSelected(sel);
        paintGridCheckbox.addActionListener(e -> {
            LSProperties.getInstance().setPropertyBoolean(LSProperties.PAINTGRID, paintGridCheckbox.isSelected());
            parent.actionChangedSettings(false);
        });
        mnu.add(paintGridCheckbox);

        boolean autowire = LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true);
        final JCheckBoxMenuItem cbMenuItem = new JCheckBoxMenuItem(I18N.tr(Lang.AUTOWIRE));
        cbMenuItem.setSelected(autowire);
        cbMenuItem.addActionListener(e -> {
            JCheckBoxMenuItem bmi = (JCheckBoxMenuItem) e.getSource();
            LSProperties.getInstance().setPropertyBoolean(LSProperties.AUTOWIRE, bmi.isSelected());
            parent.actionChangedSettings(false);
        });
        mnu.add(cbMenuItem);

        final JMenuItem mnuStyle = new JMenu(I18N.tr(Lang.GATEDESIGN));
        String gateStyle = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN,
                LSProperties.GATEDESIGN_IEC);

        JRadioButtonMenuItem mnuStyleIEC = new JRadioButtonMenuItem();
        mnuStyleIEC.setText(I18N.tr(Lang.GATEDESIGN_IEC));
        mnuStyleIEC.addActionListener(e -> changeGateStyle(LSProperties.GATEDESIGN_IEC));
        mnuStyleIEC.setSelected(LSProperties.GATEDESIGN_IEC.equals(gateStyle));
        mnuStyle.add(mnuStyleIEC);

        JRadioButtonMenuItem mnuStyleANSI = new JRadioButtonMenuItem();
        mnuStyleANSI.setText(I18N.tr(Lang.GATEDESIGN_ANSI));
        mnuStyleANSI.addActionListener(e -> changeGateStyle(LSProperties.GATEDESIGN_ANSI));
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
        mCmOn.addActionListener(e -> setColorMode(true));
        mCmOn.setSelected(LSProperties.COLORMODE_ON.equals(cMode));
        mnuColorMode.add(mCmOn);

        JRadioButtonMenuItem mCmOff = new JRadioButtonMenuItem();
        mCmOff.setText(I18N.tr(Lang.COLORMODE_OFF));
        mCmOff.addActionListener(e -> setColorMode(false));
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
        mnuHelpHelp.addActionListener(e -> parent.actionShowHelp());
        mnu.add(mnuHelpHelp);

        final JMenuItem mnuAbout = createMenuItem(Lang.ABOUT, 0, true);
        mnuAbout.addActionListener(e -> parent.actionShowAbout());
        mnu.add(mnuAbout);

        menuBar.add(mnu);
        return menuBar;
    }

    /**
     * handles gate style (IEC/ANSI)
     */
    private void changeGateStyle(String gateStyle) {
        LSProperties.getInstance().setProperty(LSProperties.GATEDESIGN, gateStyle);
        parent.actionChangedSettings(false);
    }

    private void changeComplexity(ActionEvent e) {
        JRadioButtonMenuItem src = (JRadioButtonMenuItem) e.getSource();
        final int nameLen = src.getName().length();
        final String level = (nameLen < 1 ? "2" : src.getName().substring(nameLen - 1));
        LSProperties.getInstance().setProperty(LSProperties.COMPLEXITY, level);
        final int levelNum = Integer.parseInt(level);
        hideMenusForComplexity(levelNum);
        parent.complexityChanged();
    }

    private void hideMenusForComplexity(int levelNum) {
        for (JMenu menu : menusToHide) {
            menu.setVisible(levelNum >= 2);
        }
    }

    private JMenuItem createMenuItem(Lang lang, int key, boolean isDialog) {
        JMenuItem m = new JMenuItem(I18N.tr(lang) + (isDialog ? "..." : ""));
        if (key != 0)
            m.setAccelerator(KeyStroke.getKeyStroke(key, InputEvent.CTRL_DOWN_MASK, false));
        m.setName(lang.toString());
        return m;
    }

    /**
     * add all languages from file system to languages menu
     *
     * @param menu the menu to fill
     * @param currentLanguage the current language
     */
    private void createLanguageMenu(JMenu menu, String currentLanguage) {
        ButtonGroup btnGroup = new ButtonGroup();
        for (String lang : I18N.getLanguages()) {
            JMenuItem item = new JRadioButtonMenuItem(lang);
            if (lang.equals(currentLanguage))
                item.setSelected(true);
            item.addActionListener(e -> {
                LSProperties.getInstance().setProperty(LSProperties.LANGUAGE,
                        ((JMenuItem) e.getSource()).getText());
                parent.actionChangedSettings(true);
            });
            btnGroup.add(item);
            menu.add(item);
        }
    }

    /**
     * handles color mode (on-redblack / off - blackwhite for printing)
     */
    private void setColorMode(boolean colorsOn) {
        final String mode = (colorsOn ? LSProperties.COLORMODE_ON : LSProperties.COLORMODE_OFF);
        LSProperties.getInstance().setProperty(LSProperties.COLORMODE, mode);
        Wire.setColorMode();

        parent.actionChangedSettings(false);
    }

    /** Enable or disable items when the simulation is started or stopped */
    public void informSimulationStartedStopped() {
        final boolean simRunning = Simulation.getInstance().isRunning();
        for (JMenuItem item : itemsDisabledBySimulation) {
            item.setEnabled(!simRunning);
        }
    }
}
