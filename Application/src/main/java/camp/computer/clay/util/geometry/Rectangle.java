package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {

    private double width = 1.0;

    private double height = 1.0;

    private double cornerRadius = 0.0;

    // <CACHED_OBJECTS>
    // </CACHED_OBJECTS>

    public Rectangle(T entity) {
        this.entity = entity;
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;
    }

    public Rectangle(Point position, double width, double height) {
        super(position);
        this.width = width;
        this.height = height;
    }

    public Rectangle(double left, double top, double right, double bottom) {
        super(new Point((right + left) / 2.0, (top + bottom) / 2.0));
        this.width = (right - left);
        this.height = (bottom - top);
    }

    List<Point> vertices = new ArrayList<>(4);

    public List<Point> getVertices() {
        if (vertices.size() < 4) {
            vertices.add(getTopLeft());
            vertices.add(getTopRight());
            vertices.add(getBottomRight());
            vertices.add(getBottomLeft());
        } else {
            vertices.set(0, getTopLeft());
            vertices.set(1, getTopRight());
            vertices.set(2, getBottomRight());
            vertices.set(3, getBottomLeft());
        }
        return vertices;
    }

    public List<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        segments.add(new Line(getTopLeft(), getTopRight()));
        segments.add(new Line(getTopRight(), getBottomRight()));
        segments.add(new Line(getBottomRight(), getBottomLeft()));
        segments.add(new Line(getBottomLeft(), getTopLeft()));
        return segments;
    }

    public Line getNearestSegment(Point point) {
        double nearestDistance = Double.MAX_VALUE;
        Line nearestSegment = null;

        List<Line> segments = getSegments();
        for (int i = 0; i < segments.size(); i++) {
            double distanceToSegment = 0;
            if (i < (segments.size() - 1)) {
                distanceToSegment = Geometry.calculateDistance(point, segments.get(i).getSource()) + Geometry.calculateDistance(point, segments.get(i + 1).getTarget());
            } else {
                distanceToSegment = Geometry.calculateDistance(point, segments.get(i).getSource()) + Geometry.calculateDistance(point, segments.get(0).getTarget());
            }
            if (distanceToSegment < nearestDistance) {
                nearestDistance = distanceToSegment;
                nearestSegment = segments.get(i);
            }
        }

        return nearestSegment;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            Display.drawRectangle(this, display);
        }
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
    }

    public Line getTop() {
        return new Line(getTopLeft(), getTopRight());
    }

    public Line getRight() {
        return new Line(getTopRight(), getBottomRight());
    }

    public Line getBottom() {
        return new Line(getBottomRight(), getBottomLeft());
    }

    public Line getLeft() {
        return new Line(getBottomLeft(), getTopLeft());
    }

    // TODO: Return a Number that updates when the Point coordinates update
    public double getRelativeLeft() {
        return 0 - (width / 2.0f);
    }

    // TODO: Return a Number that updates when the Point coordinates update
    public double getRelativeTop() {
        return 0 - (height / 2.0f);
    }

    public double getRelativeRight() {
        // TODO: Return a Number that updates when the Point coordinates update
//        return this.position.getRelativeX() + (width / 2.0f);
        return 0 + (width / 2.0f);
    }

    public double getRelativeBottom() {
        // TODO: Return a Number that updates when the Point coordinates update
//        return this.position.getRelativeY() + (height / 2.0f);
        return 0 + (height / 2.0f);
    }

    // Cached descriptive {@code Point} geometry for the {@code Shape}.
    protected Point topLeft = new Point(getRelativeLeft(), getRelativeTop(), position);
    protected Point topRight = new Point(getRelativeRight(), getRelativeTop(), position);
    protected Point bottomRight = new Point(getRelativeRight(), getRelativeBottom(), position);
    protected Point bottomLeft = new Point(getRelativeLeft(), getRelativeBottom(), position);

    public Point getTopLeft() {
        //return new Point(getRelativeLeft(), getRelativeTop(), position);
        topLeft.setRelative(
                0 - (width / 2.0), // getRelativeLeft(),
                0 - (height / 2.0) // getRelativeTop()
        );
        return topLeft;
    }

    public Point getTopRight() {
        //return new Point(getRelativeRight(), getRelativeTop(), position);
        topRight.setRelative(
                0 + (width / 2.0), // getRelativeRight(),
                0 - (height / 2.0) // getRelativeTop()
        );
        return topRight;
    }

    public Point getBottomRight() {
        //return new Point(getRelativeRight(), getRelativeBottom(), position);
        bottomRight.setRelative(
                0 + (width / 2.0), // getRelativeRight(),
                0 + (height / 2.0) // getRelativeBottom()
        );
        return bottomRight;
    }

    public Point getBottomLeft() {
        //return new Point(getRelativeLeft(), getRelativeBottom(), position);
        bottomLeft.setRelative(
                0 - (width / 2.0), // getRelativeLeft(),
                0 + (height / 2.0) // getRelativeBottom()
        );
        return bottomLeft;
    }
}