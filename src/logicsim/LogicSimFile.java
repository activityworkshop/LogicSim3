package logicsim;

import java.io.File;
import java.util.*;

public class LogicSimFile {
    public Circuit circuit = new Circuit();
    public Map<String, String> info = new HashMap<>();
    public String fileName;
	public boolean changed = false;
	private final ArrayList<String> errors = new ArrayList<>();

	public LogicSimFile(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * extract the pure file name from an absolute path
	 *
	 * @return file name without path and extension
	 */
	public String extractFileName() {
		final String name = new File(fileName).getName();
		// strip extension
		final int dotPos = name.lastIndexOf('.');
		return dotPos < 0 ? name : name.substring(0, dotPos);
	}

	public List<Gate> getGates() {
		return circuit.getGates();
	}

	public void setGates(Vector<Gate> gates) {
		circuit.setGates(gates);
	}

	public void setWires(Vector<Wire> wires) {
		circuit.setWires(wires);
	}

	public Vector<Wire> getWires() {
		return circuit.getWires();
	}

	private String getKey(String key) {
		return info.getOrDefault(key, null);
	}

    public void setName(String value) {
        info.put("name", value);
    }

	public void setLabel(String value) {
		info.put("label", value);
	}

	public void setDescription(String value) {
		info.put("description", value);
	}

    public String getName() {
        return getKey("name");
    }

	public String getLabel() {
		return getKey("label");
	}

	public String getDescription() {
		return getKey("description");
	}

	@Override
	public String toString() {
		String s = "File: ";
		if (fileName != null)
			s += fileName;
		if (circuit != null)
			s += " circuit: " + circuit.parts.size() + " parts";
		return s;
	}

	public void addError(String s) {
		errors.add(s);
	}

	public String getErrorString() {
		if (errors.isEmpty()) {
			return null;
		}
		return String.join(" ", errors);
	}
}
