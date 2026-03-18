package logicsim.gates;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Parent class for OnDelay and OffDelay
 */
public abstract class Delay extends Gate implements Runnable {
	protected static final String DELAY = "delay";

	protected static final String DELAY_DEFAULT = "500";

	protected int delayTimeMs = 1000;
	protected final boolean targetLevel;

	protected Delay(String type, boolean targetLevel) {
		super("inputs", type);
		this.targetLevel = targetLevel;
		createInputs(1);
		createOutputs(1);
		loadProperties();
	}

	@Override
	public void loadProperties() {
		try {
			delayTimeMs = Integer.parseInt(getPropertyWithDefault(DELAY, DELAY_DEFAULT));
		}
		catch (NumberFormatException ignored) {}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(0))) {
			if (e.level == targetLevel) {
				// edge is the one we want to react to
				Thread thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			} else {
				// other one is let through
				LSLevelEvent evt = new LSLevelEvent(this, e.level);
				getPin(1).changedLevel(evt);
			}
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(delayTimeMs);
		} catch (InterruptedException ignored) {
		}
		LSLevelEvent evt = new LSLevelEvent(this, targetLevel);
		getPin(1).changedLevel(evt);
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, DELAY), I18N.tr(Lang.SETTINGS),
				JOptionPane.QUESTION_MESSAGE, null, null, Integer.toString(delayTimeMs));
		if (h != null && !h.isEmpty()) {
			try {
				delayTimeMs = Integer.parseInt(h);
			}
			catch (NumberFormatException ignored) {}
			setPropertyInt(DELAY, delayTimeMs);
		}
		return true;
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		final int cd = 15;
		g2.drawOval(xc - cd / 2, yc - cd / 2 + 5, cd, cd);
		g2.drawLine(getX() + cd, getY() + cd, getX() + width - cd, getY() + cd);
		g2.setFont(smallFont);
		g2.drawString(targetLevel ? "0" : "1", getX() + cd, getY() + cd + 12);
		g2.drawString(targetLevel ? "1" : "0", getX() + width - cd - 6, getY() + cd + 12);
		Path2D ptr = new Path2D.Double();
		ptr.moveTo(xc, yc);
		ptr.lineTo(xc, yc + 5);
		ptr.lineTo(xc + 3, yc + 5);
		g2.draw(ptr);
	}
}
