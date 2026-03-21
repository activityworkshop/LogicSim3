package logicsim.gatelist;

import logicsim.Gate;
import logicsim.localization.I18N;

public class GateDefinition {
    private final String category;
    private final String gateType;
    private final int complexity;
    private final GateCreator creator;


    GateDefinition(String category, String gateType,
                          int complexity, GateCreator creator) {
        this.category = category;
        this.gateType = gateType;
        this.complexity = complexity;
        this.creator = creator;
    }

    public String getCategory() {
        return category;
    }

    public boolean isType(String type) {
        return gateType.equals(type);
    }

    public int getComplexity() {
        return complexity;
    }

    public String getTitleString() {
        return I18N.tr("gate." + gateType + ".title");
    }

    public String getDescriptionString() {
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
