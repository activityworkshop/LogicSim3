package logicsim.gatelist;

import logicsim.Gate;
import logicsim.localization.I18N;

public class GateDefinition {
    private final String category;
    private final String gateType;
    private final int complexity;
    private final GateCreator creator;
    private final String gateName;
    private final String gateDesc;


    public GateDefinition(String category, String gateType,
                          int complexity, GateCreator creator) {
        this(category, gateType, complexity, creator, null, null);
    }

    public GateDefinition(String category, String gateType,
                          int complexity, GateCreator creator, String gateName, String gateDesc) {
        this.category = category;
        this.gateType = gateType;
        this.complexity = complexity;
        this.creator = creator;
        this.gateName = gateName;
        this.gateDesc = gateDesc;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return gateType;
    }

    public boolean isType(String type) {
        return gateType.equalsIgnoreCase(type);
    }

    public int getComplexity() {
        return complexity;
    }

    public String getTitleString() {
        if (gateName != null && !gateName.isEmpty()) {
            return gateName;
        }
        return I18N.tr("gate." + gateType + ".title");
    }

    public String getDescriptionString() {
        if (gateDesc != null && !gateDesc.isEmpty()) {
            return gateDesc;
        }
        String descKey = "gate." + gateType + ".description";
        if (I18N.hasString(descKey)) {
            return I18N.tr(descKey);
        }
        return getTitleString();
    }

    public Gate create() {
        return creator.create();
    }
}
