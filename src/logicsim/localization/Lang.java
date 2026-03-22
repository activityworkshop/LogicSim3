package logicsim.localization;

public enum Lang {
	FILE("menu.file"),
	NEW("menu.file.new"),
	OPEN("menu.file.open"),
	SAVE("menu.file.save"),
	SAVEAS("menu.file.saveas"),
	PROPERTIES("menu.file.properties"),
	EXPORT("menu.file.export"),
	PRINT("menu.file.print"),
	EXIT("menu.file.exit"),

	MENU_MODULE("menu.module"),
	MENU_MODULECREATE("menu.module.create"),
	MENU_LOADMODULE("menu.module.load"),

	SETTINGS("menu.settings"),
	PAINTGRID("menu.settings.paintgrid"),
	AUTOWIRE("menu.settings.autowire"),
	GATEDESIGN("menu.settings.gatedesign"),
	GATEDESIGN_IEC("menu.settings.gatedesign.iec"),
	GATEDESIGN_ANSI("menu.settings.gatedesign.ansi"),
	SETTINGS_COMPLEXITY("menu.settings.complexity"),
	COMPLEXITY_LEVEL("menu.settings.complexity.level"),
	COLORMODE("menu.settings.colormode"),
	COLORMODE_ON("menu.settings.colormode.on"),
	COLORMODE_OFF("menu.settings.colormode.off"),
	LANGUAGE("menu.settings.language"),
	HELP("menu.help"),
	HELP_HELP("menu.help.help"),
	ABOUT("menu.help.about"),

	REMOVEGATE("popup.removegate"),
	POPUP_DISCONNECT("popup.disconnect"),
	POPUP_PROPERTIES("popup.properties"),
	ROTATE("popup.rotate"),
	ADDINPUT("popup.addinput"),
	REMOVEINPUT("popup.removeinput"),

	INPUTS("mainpanel.combo.inputs"),
	CATEGORY_BASIC("category.basic"),
	CATEGORY_INPUTS("category.inputs"),
	CATEGORY_OUTPUTS("category.outputs"),
	CATEGORY_FLIPFLOPS("category.flipflops"),
	CATEGORY_MODULES("category.modules"),

	TOOLBAR_NEW("toolbar.new"),
	TOOLBAR_OPEN("toolbar.open"),
	TOOLBAR_SAVE("toolbar.save"),
	SIMULATE("toolbar.simulate"),
	INPUTNORM("toolbar.inputnormal"),
	INPUTINV("toolbar.inputinv"),
	INPUTHIGH("toolbar.inputhigh"),
	INPUTLOW("toolbar.inputlow"),
	WIRENEW("toolbar.wirenew"),
	ADDPOINT("toolbar.addpoint"),
	REMOVEPOINT("toolbar.removepoint"),
	ZOOMIN("toolbar.zoomin"),
	ZOOMOUT("toolbar.zoomout"),

	INPUTNORM_HELP("status.inputnormal.help"),
	INPUTINV_HELP("status.inputinv.help"),
	INPUTHIGH_HELP("status.inputhigh.help"),
	INPUTLOW_HELP("status.inputlow.help"),
	ADDPOINT_HELP("status.addpoint.help"),
	REMOVEPOINT_HELP("status.removepoint.help"),
	WIRENEW_HELP("status.wirenew.help"),
	STATUS_GATE_ADDED("status.gateadded"),
	SAVED("status.saved"),
	ABORTED("status.aborted"),
	SIMULATION_STARTED("status.simulation.started"),
	SIMULATION_STOPPED("status.simulation.stopped"),
	PIN("status.pin"),
	WIRE("status.wire"),
	WIREPOINT("status.wirepoint"),
	WIREEDIT("status.wireedit"),
	PARTSDELETED("status.partsdeleted"),
	PARTSSELECTED("status.partsselected"),
	MODULE("status.module"),

	LABEL("dialog.fileinfo.label"),
	NAME("dialog.fileinfo.name"),
	DESCRIPTION("dialog.fileinfo.description"),
	TEXT("dialog.properties.text"),
	UNNAMED("filename.unnamed"),

	SAVECIRCUIT("dialog.savecircuit.title"),
	GATE_PROPERTIES("dialog.gateproperties.title"),

	QUESTION_CONFIRMDISCARD("question.confirmdiscard"),
	QUESTION_CONFIRMSAVE("question.confirmsave"),
	QUESTION_DELETE("question.confirmdelete"),
	LSRESTART("info.needtorestart"),
	ALREADYMODULE("error.alreadymodule"),
	MODULENOTFOUND("error.modulenotfound"),
	NOMODULE("error.nomodule"),
	READERROR("error.readerror"),
	SAVEERROR("error.saveerror"),

	OK("button.ok"),
	CANCEL("button.cancel"),
	YES("button.yes"),
	NO("button.no"),
	DONTSAVE("button.dontsave");

	private final String key;

	Lang(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
