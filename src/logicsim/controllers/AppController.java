package logicsim.controllers;

public interface AppController {
    /** File -> new or New from the toolbar */
    public void actionNewCircuit();

    /** File -> open or Open from the toolbar */
    public void actionOpenCircuit();

    /** File -> properties / Module properties */
    public void actionEditFileInfo();

    /** File -> save or File -> save as or Save from the toolbar */
    public void actionSave(boolean chooseFile);

    /** File -> new or New from the toolbar */
    public void actionExportImage();

    /** File -> exit or Window close */
    public void actionExit();

    /** Module -> create module */
    public void actionCreateModule();

    /** Module -> load module */
    public void actionLoadModule();

    /** Settings have been changed from the Settings menu */
    public void actionChangedSettings(boolean needRestart);

    public void complexityChanged();

    /** Help -> Help */
    public void actionShowHelp();

    /** Help -> About */
    public void actionShowAbout();

    /** Simulation start/stop from the toolbar */
    public void actionToggleSimulation(boolean start);

    public enum Action {ACTION_NONE, ACTION_ADDWIRE, ACTION_EDITWIRE, ACTION_ADDPOINT, ACTION_DELPOINT,
        ACTION_SELECT, ACTION_PINNORMAL, ACTION_PINHIGH, ACTION_PINLOW, ACTION_PININVERTED}

    /** Setting a mode from the toolbar */
    public void actionSetAction(Action action);

    public enum Zoom {ZOOMIN, ZOOMOUT}

    /** Zoom in or out from the toolbar */
    public void actionZoom(Zoom zoomDir);
}
