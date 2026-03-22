package logicsim.gatelist;

import logicsim.Gate;
import logicsim.module.MODIN;
import logicsim.module.MODOUT;
import logicsim.gates.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GateLibrary {
    private static List<GateDefinition> definitions = null;

    public static void populateListModel(DefaultListModel<Object> model, int complexity) {
        if (definitions == null) {
            definitions = makeDefinitions();
        }
        model.clear();
        String prevCategory = "";
        for (GateDefinition gateDef : definitions) {
            if (gateDef.getComplexity() > complexity) {
                continue;
            }
            final String category = gateDef.getCategory();
            if (!category.equals(prevCategory)) {
                model.addElement("category." + category);
                prevCategory = category;
            }
            model.addElement(gateDef);
        }
    }

    public static void addModule(GateDefinition moduleDef) {
        if (definitions == null) {
            definitions = makeDefinitions();
        }
        for (GateDefinition gateDef : definitions) {
            if (gateDef.getCategory().equals(moduleDef.getCategory())
                    && gateDef.isType(moduleDef.getType())) {
                return;
            }
        }
        definitions.add(moduleDef);
    }

    private static List<GateDefinition> makeDefinitions() {
        return new ArrayList<>(List.of(
            new GateDefinition("basic", AND.GATE_TYPE, 0, AND::new),
            new GateDefinition("basic", OR.GATE_TYPE, 0, OR::new),
            new GateDefinition("basic", XOR.GATE_TYPE, 0, XOR::new),
            new GateDefinition("basic", Buffer.GATE_TYPE, 1, Buffer::new),
            new GateDefinition("basic", NOT.GATE_TYPE, 0, NOT::new),
            new GateDefinition("basic", NAND.GATE_TYPE, 0, NAND::new),
            new GateDefinition("basic", NOR.GATE_TYPE, 0, NOR::new),
            new GateDefinition("basic", EQU.GATE_TYPE, 0, EQU::new),
            new GateDefinition("inputs", Switch.GATE_TYPE, 0, Switch::new),
            new GateDefinition("inputs", Switch4.GATE_TYPE, 2, Switch4::new),
            new GateDefinition("inputs", Switch8.GATE_TYPE, 1, Switch8::new),
            new GateDefinition("inputs", CLK.GATE_TYPE, 0, CLK::new),
            new GateDefinition("inputs", BinIn.GATE_TYPE, 1, BinIn::new),
            new GateDefinition("inputs", OnDelay.GATE_TYPE, 2, OnDelay::new),
            new GateDefinition("inputs", OffDelay.GATE_TYPE, 2, OffDelay::new),
            new GateDefinition("outputs", LED.GATE_TYPE, 0, LED::new),
            new GateDefinition("outputs", LED8.GATE_TYPE, 2, LED8::new),
            new GateDefinition("outputs", Counter.GATE_TYPE, 0, Counter::new),
            new GateDefinition("outputs", BinDisp.GATE_TYPE, 0, BinDisp::new),
            new GateDefinition("outputs", SevenSegmentDriver.GATE_TYPE, 1, SevenSegmentDriver::new),
            new GateDefinition("outputs", SevenSegment.GATE_TYPE, 1, SevenSegment::new),
            new GateDefinition("outputs", Display4d.GATE_TYPE, 3, Display4d::new),
            new GateDefinition("outputs", TextLabel.GATE_TYPE, 2, TextLabel::new),
            new GateDefinition("outputs", TriStateOutput.GATE_TYPE, 2, TriStateOutput::new),
            new GateDefinition("flipflops", DFlipFlop.GATE_TYPE, 3, DFlipFlop::new),
            new GateDefinition("flipflops", DRFlipFlop.GATE_TYPE, 3, DRFlipFlop::new),
            new GateDefinition("flipflops", DSRFlipFlop.GATE_TYPE, 3, DSRFlipFlop::new),
            new GateDefinition("flipflops", JKFlipFlop.GATE_TYPE, 3, JKFlipFlop::new),
            new GateDefinition("flipflops", SRFlipFlop.GATE_TYPE, 3, SRFlipFlop::new),
            new GateDefinition("flipflops", SRLatch.GATE_TYPE, 3, SRLatch::new),
            new GateDefinition("flipflops", SRRFlipFlop.GATE_TYPE, 3, SRRFlipFlop::new),
            new GateDefinition("flipflops", MonoFlop.GATE_TYPE, 3, MonoFlop::new),
            new GateDefinition("cpu", ALU8.GATE_TYPE, 4, ALU8::new),
            new GateDefinition("cpu", CtrlLogic.GATE_TYPE, 4, CtrlLogic::new),
            new GateDefinition("cpu", Memory128.GATE_TYPE, 4, Memory128::new),
            new GateDefinition("cpu", ProgramCounter4.GATE_TYPE, 4, ProgramCounter4::new),
            new GateDefinition("cpu", Register4.GATE_TYPE, 4, Register4::new),
            new GateDefinition("cpu", Register8.GATE_TYPE, 4, Register8::new)
        ));
    }

    public static Gate createGate(String type) {
        if (definitions == null) {
            definitions = makeDefinitions();
        }
        for (GateDefinition gateDef : definitions) {
            if (gateDef.isType(type)) {
                return gateDef.create();
            }
        }
        if (type.equals("modin")) {
            return new MODIN();
        }
        else if (type.equals("modout")) {
            return new MODOUT();
        }
        return null;
    }
}
