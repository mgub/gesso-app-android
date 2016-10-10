package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Polyline<T extends Entity> extends Shape<T> {

    private List<Point> vertices = new ArrayList<>();

    public Polyline() {
        super();
    }

    public Polyline(T entity) {
        this.entity = entity;
    }

    public Polyline(Point position, List<Point> vertices) {
        super(position);
        this.vertices.addAll(vertices);
    }

    public void addVertex(Point point) {
        this.vertices.add(point);
    }

    public Point getVertex(int index) {
        return vertices.get(index);
    }

    public void setVertices(List<Point> vertices) {
        this.vertices.clear();
        this.vertices.addAll(vertices);
    }

    @Override
    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            segments.add(new Line(vertices.get(i), vertices.get(i + 1)));
        }
        return segments;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawPolyline(this);
        }
    }

}
