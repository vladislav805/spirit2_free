
package fm.a2d.sf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.graphics.Canvas;

@SuppressWarnings("FieldCanBeLocal")
public class DialView extends RelativeLayout implements OnGestureListener {

	private static int m_obinits = 1;

	private GestureDetector mGestureDetector;
	private int mWidth;
	private int mHeight;

	private ImageView mImageViewDial;
	private Bitmap mBitmapDial;

	private double cornerLower = 0.8;//0.85;
	private double cornerHigher = 0.2;//0.15;

	private boolean is_down_powr = false;
	private boolean is_down_dial = false;
	private boolean is_down_next = false;
	private boolean is_down_prev = false;

	// Callback interface:
	private OnDialChangedListener mListener;

	@SuppressWarnings("UnnecessaryInterfaceModifier")
	interface OnDialChangedListener {
		// Previously had (boolean newstate), but now just an externally handled toggle
		public boolean onStateChanged();

		public boolean onAngleChanged(double angle);

		public boolean freq_go();

		public boolean prev_go();

		public boolean next_go();
	}


	// Dial Constructor
	public DialView(Context context, int dialId, int needleId, int width, int height) {
		super(context);

		com_uti.logd("m_obinits: " + m_obinits++);
		com_uti.logd("dialId: " + dialId + "  needleId: " + needleId + "  width: " + width + "  height: " + height);

		// Save width and height
		mWidth = width;
		mHeight = height;

		// Load dial image
		Bitmap bitmapBase = BitmapFactory.decodeResource(context.getResources(), dialId);
		Bitmap sourceBitmap;

		if (needleId >= 0) {
			Bitmap src_bmp_dial2 = BitmapFactory.decodeResource(context.getResources(), needleId);
			sourceBitmap = combineBitmaps(bitmapBase, src_bmp_dial2);
		} else {
			sourceBitmap = bitmapBase;
		}

		double scaleWidth = ((double) width) / sourceBitmap.getWidth();
		double scaleHeight = ((double) height) / sourceBitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.postScale((float) scaleWidth, (float) scaleHeight);
		mBitmapDial = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

		// Create dial
		mImageViewDial = new ImageView(context);
		LayoutParams lpImageViewDial = new LayoutParams(width, height);
		lpImageViewDial.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(mImageViewDial, lpImageViewDial);

		mImageViewDial.setImageBitmap(mBitmapDial);

		mGestureDetector = new GestureDetector(getContext(), this); // Enable gesture detector
	}

	// Public APIs:
	public void setOnDialChangedListener(OnDialChangedListener listener) {
		com_uti.logd("listener: " + listener);
		mListener = listener;
	}

	/**
	 * Set dial to angle
	 */
	public void setAngleDial(double angle) {
		com_uti.logd("angle: " + angle);
		angle = angle % 360;
		Matrix matrix = new Matrix();
		mImageViewDial.setScaleType(ScaleType.MATRIX);
		matrix.postRotate((float) angle, mWidth / 2, mHeight / 2);
		mImageViewDial.setImageMatrix(matrix);
	}

	// Internal APIs:

	/**
	 * Cartesian x,y to Polar degrees
	 */
	private double getAngle(double x, double y) {
		return -Math.toDegrees(Math.atan2(x - 0.5, y - 0.5));
	}


	private Bitmap combineBitmaps(Bitmap bmp1, Bitmap bmp2) {
		com_uti.logd("bmp1: " + bmp1 + "  bmp2: " + bmp2);
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, 0, 0, null);
		return bmOverlay;
	}

	/**
	 * Return true if within center (power)
	 */
	private boolean isInCenter(double x, double y) {
		return x >= 0.3 && x <= 0.7 && y >= 0.3 && y <= 0.7; // If within center boundaries...
	}

	/**
	 * Return true if within next
	 */
	private boolean isInNext(double x, double y) {
		return x >= cornerLower && x <= 1.1 && y >= -0.1 && y <= cornerHigher; // If within next boundaries...
	}

	/**
	 * Return true if within prev
	 */
	private boolean isInPrevious(double x, double y) {
		return x >= -0.1 && x <= cornerHigher && y >= -0.1 && y <= cornerHigher; // If within prev boundaries...
	}

	private boolean setAngle(double x, double y) {
		double angle = getAngle(1 - x, 1 - y); // 1- to correct our custom axis direction

		// If angle not a valid number...
		if (Double.isNaN(angle)) {
			return false; // Event NOT consumed
		}

		// Deny full rotation, start start and stop point, and get a linear scale
		//if (angle < 210 && angle > 150) {
		//	return false; // Event NOT consumed
		//}

		boolean consumed = false;

		if (mListener != null) {
			consumed = mListener.onAngleChanged(angle);
		}

		if (consumed) {
			setAngleDial(angle); // Rotate
		}

		return consumed;
	}

	// For android.view.GestureDetector.OnGestureListener:
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true); // Don't do horizontal scroll

		boolean haveGestureDetection = mGestureDetector.onTouchEvent(event);
		//com_uti.logd ("event: " + event + "  have_gest_det: " + have_gest_det);
		if (haveGestureDetection) {
			return true; // Event consumed
		}

		if (is_down_dial) {
			double x = getMotionX(event);
			double y = getMotionY(event);
			//com_uti.logd ("event: " + event + "  x: " + x + "  y: " + y);

			if (isInCenter(x, y)) { // If within center boundaries...
				return true; // Event consumed
			}
			boolean consumed = setAngleMotion(event);
			if (consumed) {
				return true;
			}
		}

		return super.onTouchEvent(event); // Event consumed or NOT consumed as per super
	}

	//private state_toggle

	private double getMotionX(MotionEvent event) {
		return event.getX() / ((double) getWidth());
	}

	private double getMotionY(MotionEvent event) {
		return event.getY() / ((double) getHeight());
	}

	/**
	 * Called when tap starts/down
	 */
	public boolean onDown(MotionEvent motion_event) {
		//com_uti.logd ("motion_event: " + motion_event);
		double x = getMotionX(motion_event);
		double y = getMotionY(motion_event);
		//angle_down = getAngle (1 - x, 1 - y); // 1- to correct our custom axis direction
		com_uti.logd("motion_event: " + motion_event + "  x: " + x + "  y: " + y);

		if (isInCenter(x, y)) { // If within center boundaries for power... let up handle
			is_down_powr = true;
			return true; // Event consumed
		} else if (isInNext(x, y)) {
			is_down_next = true;
			return true; // Event consumed
		} else if (isInPrevious(x, y)) {
			is_down_prev = true;
			return true; // Event consumed
		} else {
			is_down_dial = true;
			return setAngle(x, y);
		}

	}

	//private double angle_down, angle_up;

	// Called when tap ends/up
	public boolean onSingleTapUp(MotionEvent event) {
		com_uti.logd("motion_event: " + event);
		double x = getMotionX(event);
		double y = getMotionY(event);
		//angle_up = getAngle (1 - x, 1 - y); // 1- to correct our custom axis direction
		com_uti.logd("x: " + x + "  y: " + y);

		boolean consumed = false;
		//if (! Float.isNaN (angle_down) && ! Float.isNaN (angle_up) && Math.abs (angle_up-angle_down) < 10) {  // If click up where we clicked down it's just a button press
		if (is_down_dial) {
			is_down_dial = false;
			com_uti.logd("Finish freq_set");
			if (mListener != null)
				consumed = mListener.freq_go();
			return (consumed);//true); // Event consumed
		} else if (is_down_next) {
			is_down_next = false;
			if (mListener != null)
				consumed = mListener.next_go();
			return (consumed);//true); // Event consumed
		} else if (is_down_prev) {
			is_down_prev = false;
			if (mListener != null)
				consumed = mListener.prev_go();
			return (consumed);//true); // Event consumed
		} else if (is_down_powr) {//isInCenter (x, y)) { // If within center boundaries...
			is_down_powr = false;
			if (mListener != null)
				consumed = mListener.onStateChanged();
			return (consumed);//true); // Event consumed
		}

		return false; // Event NOT consumed
	}

	private boolean setAngleMotion(MotionEvent event) {
		double x = getMotionX(event);
		double y = getMotionY(event);
		return setAngle(x, y);
	}

	public boolean onScroll(MotionEvent motionEvent1, MotionEvent event2, float x, float y) {
		com_uti.logd("motion_event1: " + motionEvent1 + "motion_event2: " + event2 + "  dist_x: " + x + "  dist_y: " + y);
		return setAngleMotion(event2);
	}

	public void onShowPress(MotionEvent event) {
		com_uti.logd("motion_event: " + event);
	}

	public boolean onFling(MotionEvent event1, MotionEvent event2, float fling1, float fling2) {
		return false; // Event NOT consumed
	}

	public void onLongPress(MotionEvent event) {
		com_uti.logd("motion_event: " + event);
	}

/* Don't need:
  private double xDistance, yDistance, lastX, lastY;
  @Override
  public boolean onInterceptTouchEvent (MotionEvent motion_event) {
    com_uti.logd ("motion_event: " + motion_event);

    boolean ret = super.onInterceptTouchEvent  (motion_event);
    if (ret) {

      final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById (R.id.hsv);
      if (scrollView != null)
        scrollView.requestDisallowInterceptTouchEvent (true);

      getParent ().requestDisallowInterceptTouchEvent (true);

    }
    return ret;
  }
*/
//return (false);
/*
    switch (motion_event.getAction ()) {
        case MotionEvent.ACTION_DOWN:
            xDistance = yDistance = 0f;
            lastX = motion_event.getX ();
            lastY = motion_event.getY ();
            break;
        case MotionEvent.ACTION_MOVE:
            final double curX = motion_event.getX ();
            final double curY = motion_event.getY ();
            xDistance += Math.abs(curX - lastX);
            yDistance += Math.abs(curY - lastY);
            lastX = curX;
            lastY = curY;
            if (xDistance > yDistance)                                  // If more horizontal than vertical...
                return (false);                                         // Event NOT consumed
    }

    return super.onInterceptTouchEvent (motion_event);
  }
*/

/*  private Bitmap bmp_dial_on, bmp_dial_off;

From constructor:

    Bitmap src_bmp_off = BitmapFactory.decodeResource (context.getResources (), top_id_off);
    Bitmap src_bmp_off_a = combineBitmaps (src_bmp_dial, src_bmp_off);
    double off_scale_width = ((double) width) / src_bmp_off_a.getWidth();
    double off_scale_height = ((double) height) / src_bmp_off_a.getHeight();
    Matrix off_matrix = new Matrix ();
    off_matrix.postScale (off_scale_width, off_scale_height);
    bmp_dial_off = Bitmap.createBitmap (src_bmp_off_a, 0, 0, src_bmp_off_a.getWidth(), src_bmp_off_a.getHeight() , off_matrix , true);

    Bitmap src_bmp_on = BitmapFactory.decodeResource (context.getResources (), top_id_off);
    Bitmap src_bmp_on_a  = combineBitmaps (src_bmp_dial, src_bmp_on);
    double on_scale_width = ((double) width) / src_bmp_on_a.getWidth();
    double on_scale_height = ((double) height) / src_bmp_on_a.getHeight();
    Matrix on_matrix = new Matrix ();
    on_matrix.postScale (on_scale_width, on_scale_height);
    bmp_dial_on = Bitmap.createBitmap (src_bmp_on_a, 0, 0, src_bmp_on_a.getWidth(), src_bmp_on_a.getHeight() , on_matrix , true);
*/

/*  From onTouchEvent:
    if (motion_event.getAction () == MotionEvent.ACTION_DOWN || motion_event.getAction () == MotionEvent.ACTION_MOVE) {
      final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById (R.id.hsv);
      if (scrollView != null)
        scrollView.requestDisallowInterceptTouchEvent (true);

      //return (false);                                                 // Event NOT consumed
    }
*/
/*
      final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById (R.id.hsv);
      if (scrollView != null)
        scrollView.requestDisallowInterceptTouchEvent (true);
*/
/*
    boolean ret = false;
    ret = super.onTouchEvent (motion_event);
    //if (ret)
      getParent().requestDisallowInterceptTouchEvent (true);
    return ret;
*/

}
