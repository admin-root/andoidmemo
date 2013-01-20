package com.phodev.android.tools.widget;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 透明间距的TextView
 * 
 * @author skg
 * 
 */
public class AlphaLineSpaceTextView extends TextView {
	// as thin as possible
	private boolean thinStyle = true;

	public AlphaLineSpaceTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public AlphaLineSpaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaLineSpaceTextView(Context context) {
		super(context);
	}

	@Override
	public void setBackgroundDrawable(Drawable d) {
		if (d != null) {
			d = new DrawableWraper(d);
		}
		super.setBackgroundDrawable(d);
	}

	class DrawableWraper extends Drawable {
		Drawable wrap;

		public DrawableWraper(Drawable drawable) {
			wrap = drawable;
		}

		@Override
		public void draw(Canvas canvas) {
			Layout layout = getLayout();
			if (wrap != null && layout != null) {
				Paint paint = layout.getPaint();
				float spacingAdd = layout.getSpacingAdd();
				if (spacingAdd > 0 && paint != null) {
					int linesCount = getLineCount();
					int maxWith = getWidth();
					int saveCount = canvas.save();
					FontMetrics fm = paint.getFontMetrics();
					int descent = (int) (fm.descent + 0.5f);
					int topOffset;
					int bottomOffset;
					Path path = new Path();
					if (thinStyle) {
						// clip first line top
						path.moveTo(0, 0);
						path.lineTo(maxWith, 0);
						path.lineTo(maxWith, descent);
						path.lineTo(0, descent);
						path.close();
						canvas.clipPath(path, Op.DIFFERENCE);
						//
						topOffset = -descent;
						bottomOffset = 0;
					} else {
						topOffset = -descent - 2;
						bottomOffset = 2;
					}
					// clip every line bottom
					for (int i = 0; i < linesCount; i++) {
						int top = layout.getLineBaseline(i) - topOffset;
						int bottom = layout.getLineBottom(i) - bottomOffset;
						/*
						 * Log.d("ttt", "---- baseline:" +
						 * layout.getLineBaseline(i) + " bottom:" +
						 * layout.getLineBottom(i) + "descent:" + descent);
						 */
						path.reset();
						path.moveTo(0, top);
						path.lineTo(maxWith, top);
						path.lineTo(maxWith, bottom);
						path.lineTo(0, bottom);
						path.close();
						canvas.clipPath(path, Op.DIFFERENCE);
					}
					//
					wrap.draw(canvas);
					canvas.restoreToCount(saveCount);
				} else {
					wrap.draw(canvas);
				}
			}
		}

		@Override
		public void setBounds(int left, int top, int right, int bottom) {
			if (wrap != null) {
				wrap.setBounds(left, top, right, bottom);
			}
		}

		@Override
		public void setBounds(Rect bounds) {
			if (wrap != null) {
				wrap.setBounds(bounds);
			}
		}

		@Override
		public void setChangingConfigurations(int configs) {
			if (wrap != null) {
				wrap.setChangingConfigurations(configs);
			}
		}

		@Override
		public int getChangingConfigurations() {
			if (wrap != null) {
				return wrap.getChangingConfigurations();
			} else {
				return super.getChangingConfigurations();
			}
		}

		@Override
		public void setDither(boolean dither) {
			if (wrap != null) {
				wrap.setDither(dither);
			} else {
				super.setDither(dither);
			}
		}

		@Override
		public void setFilterBitmap(boolean filter) {
			if (wrap != null) {
				wrap.setFilterBitmap(filter);
			} else {
				super.setFilterBitmap(filter);
			}
		}

		@Override
		public void invalidateSelf() {
			if (wrap != null) {
				wrap.invalidateSelf();
			} else {
				super.invalidateSelf();
			}
		}

		@Override
		public void scheduleSelf(Runnable what, long when) {
			if (wrap != null) {
				wrap.scheduleSelf(what, when);
			} else {
				super.scheduleSelf(what, when);
			}
		}

		@Override
		public void unscheduleSelf(Runnable what) {
			if (wrap != null) {
				wrap.unscheduleSelf(what);
			} else {
				super.unscheduleSelf(what);
			}
		}

		@Override
		public void setAlpha(int alpha) {
			if (wrap != null) {
				wrap.setAlpha(alpha);
			}
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			if (wrap != null) {
				wrap.setColorFilter(cf);
			}
		}

		@Override
		public void setColorFilter(int color, Mode mode) {
			if (wrap != null) {
				wrap.setColorFilter(color, mode);
			} else {
				super.setColorFilter(color, mode);
			}
		}

		@Override
		public void clearColorFilter() {
			if (wrap != null) {
				wrap.clearColorFilter();
			} else {
				super.clearColorFilter();
			}
		}

		@Override
		public boolean isStateful() {
			if (wrap != null) {
				return wrap.isStateful();
			} else {
				return super.isStateful();
			}
		}

		@Override
		public boolean setState(int[] stateSet) {
			if (wrap != null) {
				return wrap.setState(stateSet);
			} else {
				return super.setState(stateSet);
			}
		}

		@Override
		public int[] getState() {
			if (wrap != null) {
				return wrap.getState();
			} else {
				return super.getState();
			}
		}

		@Override
		public Drawable getCurrent() {
			if (wrap != null) {
				return wrap.getCurrent();
			} else {
				return super.getCurrent();
			}
		}

		@Override
		public boolean setVisible(boolean visible, boolean restart) {
			if (wrap != null) {
				return wrap.setVisible(visible, restart);
			} else {
				return super.setVisible(visible, restart);
			}
		}

		@Override
		public int getOpacity() {
			if (wrap != null) {
				return wrap.getOpacity();
			} else {
				return 0;
			}
		}

		@Override
		public Region getTransparentRegion() {
			if (wrap != null) {
				return wrap.getTransparentRegion();
			} else {
				return super.getTransparentRegion();
			}
		}

		private boolean invaliedonStateChange = false;

		@Override
		protected boolean onStateChange(int[] state) {
			if (wrap != null && !invaliedonStateChange) {
				invaliedonStateChange = true;
				boolean b = wrap.setState(state);// 会再次触发invaliedonStateChange
				invaliedonStateChange = false;
				return b;
			} else {
				return super.onStateChange(state);
			}
		}

		private boolean invaliedonLevelChange = false;

		@Override
		protected boolean onLevelChange(int level) {
			if (wrap != null && !invaliedonLevelChange) {
				invaliedonLevelChange = true;
				boolean b = wrap.setLevel(level);// 会再次触发onLevelChange
				invaliedonLevelChange = false;
				return b;
			} else {
				return super.onLevelChange(level);
			}
		}

		private boolean invaliedOnBoundsChanged = false;

		@Override
		protected void onBoundsChange(Rect bounds) {
			if (wrap != null && !invaliedOnBoundsChanged) {
				invaliedOnBoundsChanged = true;
				wrap.setBounds(bounds);// 会再次触发onBoundsChanged
				invaliedOnBoundsChanged = false;
			} else {
				super.onBoundsChange(bounds);
			}
		}

		@Override
		public int getIntrinsicWidth() {
			if (wrap != null) {
				return wrap.getIntrinsicWidth();
			} else {
				return super.getIntrinsicWidth();
			}
		}

		@Override
		public int getIntrinsicHeight() {
			if (wrap != null) {
				return wrap.getIntrinsicHeight();
			} else {
				return super.getIntrinsicHeight();
			}
		}

		@Override
		public int getMinimumWidth() {
			if (wrap != null) {
				return wrap.getMinimumWidth();
			} else {
				return super.getMinimumWidth();
			}
		}

		@Override
		public int getMinimumHeight() {
			if (wrap != null) {
				return wrap.getMinimumHeight();
			} else {
				return super.getMinimumHeight();
			}
		}

		@Override
		public boolean getPadding(Rect padding) {
			if (wrap != null) {
				return wrap.getPadding(padding);
			} else {
				return super.getPadding(padding);
			}
		}

		@Override
		public Drawable mutate() {
			if (wrap != null) {
				return wrap.mutate();
			} else {
				return super.mutate();
			}
		}

		@Override
		public void inflate(Resources r, XmlPullParser parser,
				AttributeSet attrs) throws XmlPullParserException, IOException {
			if (wrap != null) {
				wrap.inflate(r, parser, attrs);
			} else {
				super.inflate(r, parser, attrs);
			}
		}

		@Override
		public ConstantState getConstantState() {
			if (wrap != null) {
				return wrap.getConstantState();
			} else {
				return super.getConstantState();
			}
		}
	}

}
