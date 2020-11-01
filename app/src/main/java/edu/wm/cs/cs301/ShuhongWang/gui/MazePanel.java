package edu.wm.cs.cs301.ShuhongWang.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class MazePanel extends View {
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;

    private static final int RGB_DEF = 20;
    private static final int RGB_DEF_GREEN = 60;
    private int viewWidth;  // = 400;
    private int viewHeight; // = 400;
    static final int greenWM = Color.parseColor("#115740");
    static final int goldWM = Color.parseColor("#916f41");
    static final int yellowWM = Color.parseColor("#FFFF99");
    static final int MAIN_COLOR = greenWM;
    static final int CIRCLE_HIGHLIGHT = Color.argb(1.0f, 1.0f, 1.0f, 0.8f);
    static final int CIRCLE_SHADE = Color.argb(1.0f, 1.0f, 1.0f, 0.3f);
    static final int MARKER_COLOR = Color.BLACK;

    static final int WHITE = Color.WHITE;
    static final int GRAY = Color.GRAY;
    static final int RED = Color.RED;
    static final int YELLOW = Color.YELLOW;

    private Typeface markerFont;

    public MazePanel(Context context) {
        super(context);
        bitmap = Bitmap.createBitmap(1200, 1200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        this.viewHeight = 400;
        this.viewWidth = 400;
        decodeFont("Serif-PLAIN-16");
//        this.myTestImage(canvas);
    }

    public MazePanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        bitmap = Bitmap.createBitmap(1200, 1200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        this.viewHeight = 400;
        this.viewWidth = 400;
        decodeFont("Serif-PLAIN-16");
//        this.myTestImage(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        this.myTestImage(canvas);
//        this.setColor(Color.RED);
//        this.addFilledRectangle(0,0,1200,1200);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        update();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(1200,1200);
    }

//    /**
//     * Constructor. Object is not focusable.
//     */
//    public MazePanel() {
//        this.viewWidth = 400;
//        this.viewHeight = 400;
//        setFocusable(false);
//        bufferImage = null; // bufferImage initialized separately and later
//        graphics = null;	// same for graphics
//        this.markerFont = Font.decode("Serif-PLAIN-16");
//    }
//
//    public MazePanel(int viewWidth, int viewHeight) {
//        this.viewWidth = viewWidth;
//        this.viewHeight = viewHeight;
//        setFocusable(false);
//        bufferImage = null; // bufferImage initialized separately and later
//        graphics = null;	// same for graphics
//    }

//	@Override
//	public void update(Graphics g) {
//		paint(g);
//	}
	/**
	 * Method to draw the buffer image on a graphics object that is
	 * obtained from the superclass.
	 * Warning: do not override getGraphics() or drawing might fail.
	 */
	public void update() {
	    invalidate();
	}

//    /**
//     * Draws the buffer image to the given graphics object.
//     * This method is called when this panel should redraw itself.
//     * The given graphics object is the one that actually shows
//     * on the screen.
//     */
//    public void paint(Canvas c) {
////        if (null == g) {
////            System.out.println("MazePanel.paint: no graphics object, skipping drawImage operation");
////        }
////        else {
////            g.drawImage(bufferImage,0,0,null);
////        }
//    }

//    /**
//     * Obtains a graphics object that can be used for drawing.
//     * This MazePanel object internally stores the graphics object
//     * and will return the same graphics object over multiple method calls.
//     * The graphics object acts like a notepad where all clients draw
//     * on to store their contribution to the overall image that is to be
//     * delivered later.
//     * To make the drawing visible on screen, one needs to trigger
//     * a call of the paint method, which happens
//     * when calling the update method.
//     * @return graphics object to draw on, null if impossible to obtain image
//     */
//    public Graphics getBufferGraphics() {
////        // if necessary instantiate and store a graphics object for later use
////        if (null == graphics) {
////            if (null == bufferImage) {
////                bufferImage = createImage(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT);
////                if (null == bufferImage)
////                {
////                    System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
////                    return null; // still no buffer image, give up
////                }
////            }
////            graphics = (Graphics2D) bufferImage.getGraphics();
////            if (null == graphics) {
////                System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
////            }
////            else {
////                // System.out.println("MazePanel: Using Rendering Hint");
////                // For drawing in FirstPersonDrawer, setting rendering hint
////                // became necessary when lines of polygons
////                // that were not horizontal or vertical looked ragged
////                graphics.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
////                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
////                graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
////                        java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
////            }
////        }
////        return graphics;
//    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    public void commit() {
//        paint(getGraphics());
//        canvas.drawBitmap(bitmap,0,0,paint);
        invalidate();
    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    public boolean isOperational() {
//        if (graphics == null) {
//            return false;
//        }
//        return true;
        if (canvas == null){
            return false;
        }
        return true;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * Substitute for Graphics.setColor method.
     * @param rgb gives the red green and blue encoded value of the color
     */
    public void setColor(int rgb) {
//        color = new Color(rgb);
//        graphics.setColor(color);
        paint.setColor(rgb);
    }

    /**
     * Returns the RGB value for the current color setting.
     * @return integer RGB value
     */
    public int getColor() {
//        return color.getRGB();
        return paint.getColor();
    }

    /**
     * Determines the color for a wall.
     * Supports color determination for the Wall.initColor method.
     * @param distance is the distance to the exit
     * @param cc is an obscure parameter used in Wall for color determination, just passed in here
     * @param extensionX is the wall's length and direction (sign), horizontal dimension
     * @return the rgb value for the color of the wall
     */
    public int getWallColor(int distance, int cc, int extensionX) {
        final int d = distance / 4;
        // mod used to limit the number of colors to 6
        final int rgbValue = calculateRGBValue(d, extensionX);
        //System.out.println("Initcolor rgb: " + rgbValue);
        switch (((d >> 3) ^ cc) % 6) {
            case 0:
                return Color.rgb(rgbValue, RGB_DEF, RGB_DEF);
            case 1:
                return Color.rgb(RGB_DEF, RGB_DEF_GREEN, RGB_DEF);
            case 2:
                return Color.rgb(RGB_DEF, RGB_DEF, rgbValue);
            case 3:
                return Color.rgb(rgbValue, RGB_DEF_GREEN, RGB_DEF);
            case 4:
                return Color.rgb(RGB_DEF, RGB_DEF_GREEN, rgbValue);
            case 5:
                return Color.rgb(rgbValue, RGB_DEF, rgbValue);
            default:
                return Color.rgb(RGB_DEF, RGB_DEF, RGB_DEF);
        }
    }

    /**
     * Computes an RGB value based on the given numerical value.
     *
     * @param distance
     *            value to select color
     * @return the calculated RGB value
     */
    private int calculateRGBValue(int distance, int extensionX) {
        final int part1 = distance & 7;
        final int add = (extensionX != 0) ? 1 : 0;
        final int rgbValue = ((part1 + 2 + add) * 70) / 8 + 80;
        return rgbValue;
    }

    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * Colors transition from black to gold and from grey to green.
     * Substitute for FirstPersonView.drawBackground method.
     * @param percentToExit gives the distance to exit
     */
    public void addBackground(float percentToExit) {
//        // black rectangle in upper half of screen
//        // graphics.setColor(Color.black);
//        // dynamic color setting:
        this.setColor(getBackgroundColor(percentToExit, true));
        this.addFilledRectangle(0, 0, viewWidth, viewHeight/2);
//        // grey rectangle in lower half of screen
//        // graphics.setColor(Color.darkGray);
//        // dynamic color setting:
        this.setColor(getBackgroundColor(percentToExit, false));
        this.addFilledRectangle(0, viewHeight/2, viewWidth, viewHeight/2);
    }

    private int getBackgroundColor(float percentToExit, boolean top) {
//        return top? blend(yellowWM, goldWM, percentToExit) :
//                blend(Color.lightGray.getRGB(), greenWM, percentToExit);
        return top? blend(yellowWM, goldWM, percentToExit) :
                blend(Color.LTGRAY, greenWM, percentToExit);
    }

    private int blend(int rgb0, int rgb1, float weight0) {
//        Color c0 = new Color(rgb0);
//        Color c1 = new Color(rgb1);
//        if (weight0 < 0.1)
//            return c1.getRGB();
//        if (weight0 > 0.95)
//            return c0.getRGB();
//        double r = weight0 * c0.getRed() + (1-weight0) * c1.getRed();
//        double g = weight0 * c0.getGreen() + (1-weight0) * c1.getGreen();
//        double b = weight0 * c0.getBlue() + (1-weight0) * c1.getBlue();
//        double a = Math.max(c0.getAlpha(), c1.getAlpha());
//
//        return (new Color((int) r, (int) g, (int) b, (int) a)).getRGB();
        int c0 = rgb0;
        int c1 = rgb1;
        if (weight0 < 0.1)
            return c1;
        if (weight0 > 0.95)
            return c0;
        float r = weight0 * Color.red(c0) + (1-weight0) * Color.red(c1);
        float g = weight0 * Color.green(c0) + (1-weight0) * Color.green(c1);
        float b = weight0 * Color.blue(c0) + (1-weight0) * Color.blue(c1);
        float a = weight0 * Color.alpha(c0) + (1-weight0) * Color.alpha(c1);

        return Color.argb(a,r,g,b);
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * Substitute for Graphics.fillRect() method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    public void addFilledRectangle(int x, int y, int width, int height) {
//        graphics.fillRect(x, y, width, height);
        Rect rectangle = new Rect(x, y, x + width, y + height);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectangle, paint);
    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//        graphics.fillPolygon(xPoints, yPoints, nPoints);
       paint.setStyle(Paint.Style.FILL);
       Path path = new Path();
       path.reset();
       path.moveTo(xPoints[0], yPoints[0]);
       for(int i = 1; i < nPoints; i++){
           path.lineTo(xPoints[i], yPoints[i]);
       }
       path.lineTo(xPoints[0], yPoints[0]);
       canvas.drawPath(path, paint);
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
//        graphics.drawPolygon(xPoints, yPoints, nPoints);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.reset();
        path.moveTo(xPoints[0], yPoints[0]);
        for(int i = 1; i < nPoints; i++){
            path.lineTo(xPoints[i], yPoints[i]);
        }
        path.lineTo(xPoints[0], yPoints[0]);
        canvas.drawPath(path, paint);
    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * Substitute for Graphics.drawLine method
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    public void addLine(int startX, int startY, int endX, int endY) {
//        graphics.drawLine(startX, startY, endX, endY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(startX, startY, endX, endY, paint);
    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * Substitute for Graphics.fillOval method
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the oval
     * @param height is the height of the oval
     */
    public void addFilledOval(int x, int y, int width, int height) {
//        graphics.fillOval(x, y, width, height);
        paint.setStyle(Paint.Style.FILL);
        RectF oval = new RectF(x, y, x + width, y + height);
        canvas.drawOval(oval, paint);
    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * Substitute for Graphics.drawArc method
     * @param x the x coordinate of the upper-left corner of the arc to be drawn.
     * @param y the y coordinate of the upper-left corner of the arc to be drawn.
     * @param width the width of the arc to be drawn.
     * @param height the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param arcAngle the angular extent of the arc, relative to the start angle.
     */
    public void addArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
//        graphics.drawArc(x, y, width, height, startAngle, arcAngle);
        paint.setStyle(Paint.Style.STROKE);
        RectF rectF = new RectF(x, y, x + width, y + height);
        canvas.drawArc(rectF, startAngle, arcAngle, false, paint);
    }

    /**
     * Adds a string at the given position.
     * Substitute for CompassRose.drawMarker method
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    public void addMarker(float x, float y, String str) {
//        GlyphVector gv = markerFont.createGlyphVector(graphics.getFontRenderContext(), str);
//        Rectangle2D rect = gv.getVisualBounds();
//
//        x -= rect.getWidth() / 2;
//        y += rect.getHeight() / 2;
//
//        graphics.drawGlyphVector(gv, x, y);
        paint.setTypeface(markerFont);
        canvas.drawText(str, x, y, paint);
    }

//    /**
//     * Sets the value of a single preference for the rendering algorithms.
//     * Hint categories include controls for rendering quality
//     * and overall time/quality trade-off in the rendering process.
//     * Refer to the awt RenderingHints class for definitions of some common keys and values.
//     * @param hintKey the key of the hint to be set.
//     * @param hintValue the value indicating preferences for the specified hint category.
//     */
//    public void setRenderingHint(RenderingHints hintKey, RenderingHints hintValue) {
//        Key key = null;
//        Object value = null;
//
//        switch (hintKey) {
//            case KEY_RENDERING:
//                key = java.awt.RenderingHints.KEY_RENDERING;
//                break;
//            case KEY_ANTIALIASING:
//                key = java.awt.RenderingHints.KEY_ANTIALIASING;
//                break;
//            case KEY_INTERPOLATION:
//                key = java.awt.RenderingHints.KEY_INTERPOLATION;
//                break;
//            default:
//                break;
//        }
//        switch(hintValue) {
//            case VALUE_RENDER_QUALITY:
//                value = java.awt.RenderingHints.VALUE_RENDER_QUALITY;
//                break;
//            case VALUE_ANTIALIAS_ON:
//                value = java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
//                break;
//            case VALUE_INTERPOLATION_BILINEAR:
//                value = java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
//                break;
//            default:
//                break;
//        }
//        graphics.setRenderingHint(key, value);
//    }

    /**
     * get the current font.
     * @return markerFont
     */
    public Typeface getFont() {
//        return this.markerFont;
        return markerFont;
    }

    /**
     * Set the font with a string.
     * @param str name of the font.
     */
    public void decodeFont(String str) {
        markerFont = Typeface.create(str, Typeface.NORMAL);
    }

    /**
     * For test purpose. Draw shapes on a polygon.
     * @param c the canvas to draw on
     */
    private void myTestImage(Canvas c){
        // red ball
        this.setColor(Color.RED);
        this.addFilledOval(0,0, 200, 200);

        // green ball
        this.setColor(Color.GREEN);
        this.addFilledOval(400,0, 200, 200);

        // yellow rectangle
        this.setColor(Color.YELLOW);
        this.addFilledRectangle(0, 300, 300, 200);

        // blue polygon
        this.setColor(Color.BLUE);
        int[] x = {400,590,740,620,583};
        int[] y = {300,205,380,520,530};
        this.addFilledPolygon(x,y,5);

        // straight line
        this.addLine(20, 750, 70, 850);

        // arc
        this.addArc(20, 850, 250, 260, 20, 40);
    }

    /**
     * set anti alias for the paint
     * @param bool true if want antialias, false otherwise
     */
    public void setAntiAlias(boolean bool){
        paint.setAntiAlias(bool);
    }
}
