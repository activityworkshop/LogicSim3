package logicsim;

public class LSLevelEvent {
	public final CircuitPart source;
	public final LSLevelListener target;
	public final boolean level;
	public final boolean force;

	public LSLevelEvent(CircuitPart source) {
		this(source, false);
	}

	public LSLevelEvent(CircuitPart source, boolean level) {
		this(source, level, false);
	}

	public LSLevelEvent(CircuitPart source, boolean level, boolean force) {
		this(source, level, force, null);
	}

	public LSLevelEvent(CircuitPart source, boolean level, boolean force, LSLevelListener target) {
		this.source = source;
		this.level = level;
		this.force = force;
		this.target = target;
	}

	@Override
	public String toString() {
		String s = "LevelEvt: (" + source.getClass().getSimpleName() + ") "
				+ source.getId() + " is " + (level ? "HIGH" : "LOW")
				+ " force: " + force;
		if (target != null) {
			s += " --> " + ((CircuitPart) target).getId();
		}
		return s;
	}
}
