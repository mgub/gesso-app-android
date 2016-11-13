package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Rectangle;
import camp.computer.clay.util.ImageBuilder.Shape;

public class Boundary extends Component {

    public static HashMap<Shape, ArrayList<Transform>> innerBoundaries = new HashMap<>();

    private List<Transform> boundary = new ArrayList<>();

    public void setBoundary(List<Transform> points) {
        this.boundary.clear();
        this.boundary.addAll(points);
    }

    public List<Transform> getBoundary() {
        return this.boundary;
    }

    /**
     * Returns {@code true} if any of the {@code Shape}s in the {@code Image} contain the
     * {@code point}.
     *
     * @param point
     * @return
     */
    public static boolean contains(Entity entity, Transform point) {

        Image image = entity.getComponent(Image.class);

        List<Shape> shapes = image.getImage().getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).isBoundary
                    && Geometry.contains(BoundarySystem.getBoundary(shapes.get(i)), point)) {
                return true;
            }
        }
        return false;

        // TODO: return Geometry.contains(entity.getComponent(Boundary.class).boundary, point);

        // TODO?: return Geometry.contains(this.boundary, point);
    }

    // TODO: Compute bounding box for image when add/remove Shapes and store it here!
    public static Rectangle getBoundingBox(Entity entity) {

        List<Transform> shapeBoundaries = new ArrayList<>();

        List<Shape> shapes = entity.getComponent(Image.class).getImage().getShapes();
        for (int i = 0; i < shapes.size(); i++) {
            if (shapes.get(i).isBoundary) {
                shapeBoundaries.addAll(BoundarySystem.getBoundary(shapes.get(i)));
            }
        }

        return Geometry.getBoundingBox(shapeBoundaries);
    }
}
