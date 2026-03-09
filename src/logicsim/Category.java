package logicsim;

import java.util.ArrayList;

public class Category {
	private final String title;
	private final ArrayList<Gate> gates = new ArrayList<>();

	public Category(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<Gate> getGates() {
		return gates;
	}

	public void addGate(Gate g) {
		gates.add(g);
	}

	@Override
	public String toString() {
		return "[Category: " + title + "/#gates: " + gates.size() + "]";
	}
}
