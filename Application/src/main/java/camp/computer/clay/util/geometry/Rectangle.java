package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Rectangle<T extends Entity> extends Shape<T> {

    public double width = 1.0;

    public double height = 1.0;

    public double cornerRadius = 0.0;

    private ArrayList<Segment> segments = new ArrayList<>();

    public Rectangle(T entity) {
        this.entity = entity;
        setup();
    }

    public Rectangle(double width, double height) {
        super();
        this.width = width;
        this.height = height;
        setup();
    }

    public Rectangle(double left, double top, double right, double bottom) {
        position.x = (right + left) / 2.0;
        position.y = (top + bottom) / 2.0;
        width = (right - left);
        height = (bottom - top);

        setup();
        updateBoundary(); // TODO: Replace with updateExtensionGeometry(this.position)
    }

    protected void setup() {
        setupGeometry();
    }

    private void setupGeometry() {

        // Create vertex Points (relative to the Shape)
        Transform topLeft = new Transform(0 - (width / 2.0), 0 - (height / 2.0));
        Transform topRight = new Transform(0 + (width / 2.0), 0 - (height / 2.0));
        Transform bottomRight = new Transform(0 + (width / 2.0), 0 + (height / 2.0));
        Transform bottomLeft = new Transform(0 - (width / 2.0), 0 + (height / 2.0));

        boundary.add(topLeft);
        boundary.add(topRight);
        boundary.add(bottomRight);
        boundary.add(bottomLeft);

        // Create segment Lines (relative to the Shape)
        Segment top = new Segment(topLeft, topRight);
        Segment right = new Segment(topRight, bottomRight);
        Segment bottom = new Segment(bottomRight, bottomLeft);
        Segment left = new Segment(bottomLeft, topLeft);

        segments.add(top);
        segments.add(right);
        segments.add(bottom);
        segments.add(left);
    }

    @Override
    protected List<Transform> getVertices() {

        List<Transform> vertices = new LinkedList<>();

        vertices.add(new Transform(
                0 - (width / 2.0),
                0 - (height / 2.0)
        ));
        vertices.add(new Transform(
                0 + (width / 2.0),
                0 - (height / 2.0)
        ));
        vertices.add(new Transform(
                0 + (width / 2.0),
                0 + (height / 2.0)
        ));
        vertices.add(new Transform(
                0 - (width / 2.0),
                0 + (height / 2.0)
        ));

        return vertices;
    }

    public double getCornerRadius() {
        return this.cornerRadius;
    }

    public void setCornerRadius(double cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidate();
    }

    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
        invalidate();
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height;
        invalidate();
    }

    @Override
    public void draw(PlatformRenderSurface platformRenderSurface) {
        if (isVisible()) {
            platformRenderSurface.drawRectangle(this);

            /*
            // Draw bounding box!
            display.paint.setColor(Color.GREEN);
            display.paint.setStyle(Paint.Style.STROKE);
            display.paint.setStrokeWidth(2.0f);
            display.drawPolygon(getBoundingBox());
            */
        }
    }
}