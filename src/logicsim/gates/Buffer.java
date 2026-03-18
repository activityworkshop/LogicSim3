package logicsim.gates;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.LSProperties;

/**
 * Buffer Gate for LogicSim
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Buffer extends Gate {
	public Buffer() {
		this("buffer");
	}

	protected Buffer(String type) {
		super("basic", type);
		label = "1";
		createInputs(1);
		createOutputs(1);
	}

	@Override
	public void simulate() {
		super.simulate();
		// call pin directly
		getPin(1).changedLevel(new LSLevelEvent(this, getPin(0).getLevel(), force));
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		simulate();
	}

	@Override
	protected void drawRotated(Graphics2D g2) {
		final String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_ANSI)) {
			Path2D p = new Path2D.Double();
			double yu = getY() + CONN_SIZE + 6;
			double xr = getX() + width - 10 - CONN_SIZE + 1;
			double yb = getY() + height - CONN_SIZE - 6;
			double xl = getX() + 10 + CONN_SIZE;
			p.moveTo(xl, yc);
			p.lineTo(xl, yb);
			p.lineTo(xr, yc);
			p.lineTo(xl, yu);
			p.closePath();

			g2.setPaint(Color.WHITE);
			g2.fill(p);
			g2.setPaint(Color.black);
			g2.draw(p);
		}
	}

	@Override
	protected void setPinPositions() {
		final String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC)) {
			// check coordinates of pins if they have already been moved inwards
			if (getPin(0).getX() == getX() + 10) {
				getPin(0).setX(getPin(0).getX() - 10);
				getPin(1).setX(getPin(1).getX() + 10);
			}
			else if (getPin(0).getX() == getX() + width - 10) {
				getPin(0).setX(getPin(0).getX() + 10);
				getPin(1).setX(getPin(1).getX() - 10);
			}
			else if (getPin(0).getY() == getY() + 10) {
				getPin(0).setY(getPin(0).getY() - 10);
				getPin(1).setY(getPin(1).getY() + 10);
			}
			else if (getPin(0).getY() == getY() + height - 10) {
				getPin(0).setY(getPin(0).getY() + 10);
				getPin(1).setY(getPin(1).getY() - 10);
			}
		}
		else {
			// check coordinates of pins, x coordinates should be a little more inwards
			if (getPin(0).getX() == getX()) {
				getPin(0).setX(getPin(0).getX() + 10);
				getPin(1).setX(getPin(1).getX() - 10);
			}
			else if (getPin(0).getX() == getX() + width) {
				getPin(0).setX(getPin(0).getX() - 10);
				getPin(1).setX(getPin(1).getX() + 10);
			}
			else if (getPin(0).getY() == getY()) {
				getPin(0).setY(getPin(0).getY() + 10);
				getPin(1).setY(getPin(1).getY() - 10);
			}
			else if (getPin(0).getY() == getY() + height) {
				getPin(0).setY(getPin(0).getY() - 10);
				getPin(1).setY(getPin(1).getY() + 10);
			}
		}
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC)) {
			super.drawFrame(g2);
		}
	}
}
