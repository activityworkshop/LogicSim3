package logicsim.gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JOptionPane;

import logicsim.Gate;
import logicsim.localization.I18N;
import logicsim.LSLevelEvent;
import logicsim.localization.Lang;

/**
 * MonoFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class MonoFlop extends Gate implements Runnable {
	public static final String GATE_TYPE = "monoflop";

    private static final String HT = "hightime";
	private static final String HT_DEFAULT = "1000";

	private int highTime;

	public MonoFlop() {
		super("flipflops", GATE_TYPE);
		createInputs(1);
		createOutputs(1);
		reset();
		loadProperties();
	}

	@Override
	public void loadProperties() {
		highTime = Integer.parseInt(getPropertyWithDefault(HT, HT_DEFAULT));
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(0))) {
			if (e.level == HIGH) {
				LSLevelEvent evt = new LSLevelEvent(this, HIGH);
				getPin(1).changedLevel(evt);
				fireRepaint();
				// rising edge detection of input
                Thread thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(highTime);
		} catch (InterruptedException ignored) {
		}
		LSLevelEvent evt = new LSLevelEvent(this, LOW);
		getPin(1).changedLevel(evt);
		fireRepaint();
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		g2.setPaint(Color.black);
		int middleX = getX() + getWidth() / 2;
		int middleY = getY() + getHeight() / 2;
		Path2D path = new Path2D.Double();
		path.moveTo(middleX - 10, middleY + 5);
		path.lineTo(middleX - 3, middleY + 5);
		path.lineTo(middleX - 3, middleY - 5);
		path.lineTo(middleX + 7, middleY - 5);
		path.lineTo(middleX + 7, middleY + 5);
		path.lineTo(middleX + 14, middleY + 5);
		g2.draw(path);
		g2.drawString("1", middleX - 20, middleY + 5);
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, HT), I18N.tr(Lang.GATE_PROPERTIES),
				JOptionPane.QUESTION_MESSAGE, null, null, Integer.toString(highTime));
		if (h != null && !h.isEmpty()) {
			highTime = Integer.parseInt(h);
			setPropertyInt(HT, highTime);
		}
		return true;
	}
}
