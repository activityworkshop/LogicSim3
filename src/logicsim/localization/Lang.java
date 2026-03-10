package logicsim.localization;

public enum Lang {
	FILE("menu.file"),
	NEW("menu.file.new"),
	OPEN("menu.file.open"),
	SAVE("menu.file.save"),
	SAVEAS("menu.file.saveas"),
	MODULECREATE("menu.file.modulecreate"),
	PROPERTIES("menu.file.properties"),
	EXPORT("menu.file.export"),
	PRINT("menu.file.print"),
	EXIT("menu.file.exit"),

	SETTINGS("menu.settings"),
	PAINTGRID("menu.settings.paintgrid"),
	AUTOWIRE("menu.settings.autowire"),
	GATEDESIGN("menu.settings.gatedesign"),
	GATEDESIGN_IEC("menu.settings.gatedesign.iec"),
	GATEDESIGN_ANSI("menu.settings.gatedesign.ansi"),
	MODE("menu.settings.mode"),
	NORMAL("menu.settings.normal"),
	EXPERT("menu.settings.expert"),
	COLORMODE("menu.settings.colormode"),
	COLORMODE_ON("menu.settings.colormode.on"),
	COLORMODE_OFF("menu.settings.colormode.off"),
	LANGUAGE("menu.settings.language"),
	HELP("menu.help"),
	HELP_HELP("menu.help.help"),
	ABOUT("menu.help.about"),

	REMOVEGATE("removegate"),
	MIRROR("mirror"),
	ROTATE("rotate"),
	ADDINPUT("addinput"),
	REMOVEINPUT("removeinput"),

	INPUTS("inputs"),

	SIMULATE("simulate"),
	INPUTNORM("inputnorm"),
	INPUTINV("inputinv"),
	INPUTHIGH("inputhigh"),
	INPUTLOW("inputlow"),
	WIRENEW("wirenew"),
	ADDPOINT("addpoint"),
	REMOVEPOINT("removepoint"),
	ZOOMIN("zoomin"),
	ZOOMOUT("zoomout"),

	INPUTNORM_HELP("inputnorm.help"),
	INPUTINV_HELP("inputinv.help"),
	INPUTHIGH_HELP("inputhigh.help"),
	INPUTLOW_HELP("inputlow.help"),
	ADDPOINT_HELP("addpoint.help"),
	REMOVEPOINT_HELP("removepoint.help"),
	WIRENEW_HELP("wirenew.help"),
	SAVED("saved"),
	ABORTED("aborted"),
	SIMULATION_STARTED("simulation.started"),
	SIMULATION_STOPPED("simulation.stopped"),
	PIN("pin"),
	WIRE("wire"),
	WIREPOINT("wirepoint"),
	WIREEDIT("wireedit"),
	PARTSDELETED("partsdeleted"),
	PARTSSELECTED("partsselected"),
	MODULE("module"),

	LABEL("label"),
	NAME("name"),
	DESCRIPTION("description"),
	TEXT("text"),
	UNNAMED("unnamed"),

	SAVECIRCUIT("savecircuit"),

	QUESTION_CONFIRMDISCARD("question.confirmdiscard"),
	QUESTION_CONFIRMSAVE("question.confirmsave"),
	QUESTION_DELETE("question.delete"),
	LSRESTART("lsrestart"),
	ALREADYMODULE("alreadymodule"),
	MODULENOTFOUND("modulenotfound"),
	NOMODULE("nomodule"),
	READERROR("readerror"),
	SAVEERROR("saveerror"),

	OK("ok"),
	CANCEL("cancel"),
	YES("yes"),
	NO("no"),
	DONTSAVE("dontsave");

	private final String key;

	Lang(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
