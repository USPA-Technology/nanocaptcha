package net.logicsquad.nanocaptcha.text.filter;

import java.awt.image.BufferedImage;
import com.jhlabs.image.ShadowFilter;

/**
 * Adds a dark drop-shadow.
 * 
 * @author <a href="mailto:james.childers@gmail.com">James Childers</a>
 * @author <a href="mailto:paulh@logicsquad.net">Paul Hoadley</a>
 */
public class DropShadowImageFilter implements ImageFilter {
	private static final int DEFAULT_RADIUS = 3;
	private static final int DEFAULT_OPACITY = 75;
	
	private final int _radius;
	private final int _opacity;
	
	public DropShadowImageFilter() {
		this(DEFAULT_RADIUS, DEFAULT_OPACITY);
	}
	
	public DropShadowImageFilter(int radius, int opacity) {
		_radius = radius;
		_opacity = opacity;
	}

	@Override
    public void filter(BufferedImage image) {
        ShadowFilter sFilter = new ShadowFilter();
        sFilter.setRadius(_radius);
        sFilter.setOpacity(_opacity);
        ImageFilter.applyFilter(image, sFilter);
    }
}