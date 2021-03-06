package net.logicsquad.nanocaptcha.image.backgrounds;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

/**
 * Adds squiggles to background.
 *
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * @author <a href="mailto:paulh@logicsquad.net">Paul Hoadley</a>
 * @since 1.0
 */
public class SquigglesBackgroundProducer implements BackgroundProducer {
	/**
	 * Alpha value of background
	 */
	private static final float ALPHA = 0.75f;

	@Override
	public BufferedImage getBackground(int width, int height) {
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = result.createGraphics();

		BasicStroke bs = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f,
				new float[] { 2.0f, 2.0f }, 0.0f);
		graphics.setStroke(bs);
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA);
		graphics.setComposite(ac);

		graphics.translate(width * -1.0, 0.0);
		double delta = 5.0;
		double xt;
		Arc2D arc;
		for (xt = 0.0; xt < (2.0 * width); xt += delta) {
			arc = new Arc2D.Double(0, 0, width, height, 0.0, 360.0, Arc2D.OPEN);
			graphics.draw(arc);
			graphics.translate(delta, 0.0);
		}
		graphics.dispose();
		return result;
	}
}
