package logicsim.localization;

public enum Lang {
	FILE("file"),
	NEW("new"),
	OPEN("open"),
	SAVE("save"),
	SAVEAS("saveas"),
	MODULECREATE("modulecreate"),
	PROPERTIES("properties"),
	EXPORT("export"),
	PRINT("print"),
	EXIT("exit"),

	SETTINGS("settings"),
	PAINTGRID("paintgrid"),
	AUTOWIRE("autowire"),
	GATEDESIGN("gatedesign"),
	GATEDESIGN_IEC("gatedesign.iec"),
	GATEDESIGN_ANSI("gatedesign.ansi"),
	MODE("mode"),
	NORMAL("normal"),
	EXPERT("expert"),
	COLORMODE("colormode"),
	COLORMODE_ON("colormode.on"),
	COLORMODE_OFF("colormode.off"),
	LANGUAGE("language"),
	HELP("help"),
	ABOUT("about"),

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
