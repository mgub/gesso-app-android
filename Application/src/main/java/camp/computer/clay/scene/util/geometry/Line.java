package camp.computer.clay.scene.util.geometry;

import java.util.List;

import camp.computer.clay.application.Surface;

public class Line extends Shape {
    private Point source = new Point(0, 0);
    private Point target = new Point(0, 0);

    public Line () {}

    @Override
    public List<Point> getVertices() {
        return null;
    }

    @Override
    public List<Line> getSegments() {
        return null;
    }

    @Override
    public void draw(Surface surface) {
        if (isVisible()) {
            Surface.drawLine(this, surface);
        }
    }

    public Line (Point source, Point target) {
        this.source = source;
        this.target = target;
    }

    public Point getSource() {
        return this.source;
    }

    public Point getTarget() {
        return this.target;
    }

    public void setSource(Point source) {
        this.source = source;
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public double getLength() {
        return Geometry.calculateDistance(source, target);
    }
}
