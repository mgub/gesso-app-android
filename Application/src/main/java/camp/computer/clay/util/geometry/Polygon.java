package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Entity;
import camp.computer.clay.util.image.Shape;

public class Polygon<T extends Entity> extends Shape<T> {

    private List<Point> vertices = new ArrayList<>();

    public Polygon(T entity) {
        this.entity = entity;
    }

    public Polygon(Point position, List<Point> vertices) {
        super(position);
        this.vertices.addAll(vertices);
    }

    public Point getVertex(int index) {
        return vertices.get(index);
    }

    public void setVertices(List<Point> vertices) {
        this.vertices.clear();
        this.vertices.addAll(vertices);
    }

    @Override
    public List<Point> getBoundary() {
        return vertices;
    }

    @Override
    public void draw(Display display) {
        if (isVisible()) {
            display.drawPolygon(this);
        }
    }

}
