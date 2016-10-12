package camp.computer.clay.model.action;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.Host;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.Portable;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.model.util.PortGroup;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ImageGroup;
import camp.computer.clay.util.image.util.ShapeGroup;
import camp.computer.clay.util.time.Time;

public class Camera {

    // TODO: Caption generation for each Perspective/Camera

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    /**
     * Width of perspective --- actions (e.g., touches) are interpreted relative to this point
     */
    protected double width;

    /**
     * Height of perspective
     */
    protected double height;

    /**
     * The {@code Space} displayed from this perspective
     */
    protected Space space = null;

    // Scale
    protected final double DEFAULT_SCALE = 1.0f;
    protected double targetScale = DEFAULT_SCALE;
    protected double scale = DEFAULT_SCALE;
    protected int scalePeriod = DEFAULT_SCALE_PERIOD;
    protected double scaleDelta = 0;

    // Position
    protected final Point DEFAULT_POSITION = new Point(0, 0);
    protected Point targetPosition = DEFAULT_POSITION;
    protected Point position = new Point(targetPosition.x, targetPosition.y);
    protected int positionFrameIndex = 0;
    protected int positionFrameLimit = 0;
    protected Point originalPosition = new Point();

    public Camera() {
    }

    public Camera(Space space) {
        this.space = space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return this.width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return this.height;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point position) {
        setPosition(position.x, position.y, DEFAULT_ADJUSTMENT_PERIOD);
    }

    public void setPosition(double x, double y) {
        setPosition(x, y, DEFAULT_ADJUSTMENT_PERIOD);
    }

    private void setPosition(double x, double y, double duration) {

//        if (targetPosition.x == position.x && targetPosition.y == position.y) {
//            return;
//        }

        if (duration < 5.0) {

            this.targetPosition.set(-x, -y);
            this.originalPosition.set(x, y);
            this.position.set(x, y);

            positionFrameIndex = positionFrameLimit;

        } else {

            /*
            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
            // this.targetPosition.setAbsoluteX(-targetPosition.x * targetScale);
            // this.targetPosition.setAbsoluteY(-targetPosition.y * targetScale);
            */

            //this.targetPosition.setAbsolute(-targetPosition.x, -targetPosition.y);
            this.targetPosition.set(-x, -y);

            // <PLAN_ANIMATION>
            originalPosition.set(position);

            positionFrameLimit = (int) (Application.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            positionFrameIndex = 0;
            // </PLAN_ANIMATION>
        }
    }

    public void adjustPosition() {
        Point centerPosition = getSpace().getImages().filterType(Host.class, Extension.class).getCenterPoint();
        setPosition(centerPosition.x, centerPosition.y, 0);
    }

    public void setOffset(double dx, double dy) {
        this.targetPosition.offset(dx, dy);
        this.originalPosition.offset(dx, dy);
        this.position.offset(dx, dy);
    }

    public void setOffset(Point point) {
        setOffset(point.x, point.y);
    }

    public void setScale(double scale, double duration) {

        this.targetScale = scale;

        if (duration == 0) {
            this.scale = scale;
        } else {
            double frameCount = Application.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            scaleDelta = Math.abs(scale - this.scale) / frameCount;
        }
    }

    public double getScale() {
        return this.scale;
    }

    public void adjustScale() {
        adjustScale(Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        Rectangle boundingBox = getSpace().getImages().filterType(Host.class, Extension.class).getBoundingBox();
        if (boundingBox.width > 0 && boundingBox.height > 0) {
            adjustScale(boundingBox, duration);
        }
    }

    /**
     * Adjusts the {@code Camera} to fit the bounding box {@code boundingBox}. This sets the
     * duration of the scale adjustment to the default value {@code DEFAULT_SCALE_PERIOD}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     */
    public void adjustScale(Rectangle boundingBox) {
        adjustScale(boundingBox, Camera.DEFAULT_SCALE_PERIOD);
    }

    /**
     * Adjusts the {@code Camera} to fit the bounding box {@code boundingBox}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     * @param duration    The duration of the scale adjustment.
     */
    public void adjustScale(Rectangle boundingBox, double duration) {

        /*
        // Multiply the bounding box
        double paddingMultiplier = 1.0; // 1.10;
        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
        */

        double horizontalScale = getWidth() / boundingBox.getWidth();
        double verticalScale = getHeight() / boundingBox.getHeight();

        if (horizontalScale <= MAXIMUM_SCALE || horizontalScale <= MAXIMUM_SCALE) {
            if (horizontalScale < verticalScale) {
                setScale(horizontalScale, duration);
            } else if (horizontalScale > horizontalScale) {
                setScale(verticalScale, duration);
            }
        } else {
            setScale(MAXIMUM_SCALE, DEFAULT_SCALE_PERIOD);
        }
    }

    /**
     * Adjusts the focus for the prototype {@code Path} being created.
     *
     * @param sourcePort
     * @param targetPosition
     */
    public void setFocus(Port sourcePort, Point targetPosition) {

        // Check if a Host Image is nearby
        Image nearestHostImage = getSpace().getImages().filterType(Host.class).getNearestImage(targetPosition);
        if (nearestHostImage != null) {

            Portable sourcePortable = sourcePort.getPortable();
            PortableImage sourcePortableImage = (PortableImage) space.getImage(sourcePortable);

            double distanceToPortable = Geometry.distance(sourcePortableImage.getPosition(), targetPosition);

            if (distanceToPortable > 800) {
                setScale(0.6f, 100); // Zoom out to show overview
            } else {
                setScale(1.0f, 100); // Zoom out to show overview
            }
        }
    }

    public void setFocus(Host host) {

        // <REFACTOR>
        HostImage hostImage = (HostImage) space.getImage(host);

        // Reduce transparency of other all Portables (not electrically connected to the PhoneHost)
        ImageGroup otherPortableImages = getSpace().getImages().filterType(Host.class, Extension.class);
        otherPortableImages.remove(hostImage);
        otherPortableImages.setTransparency(0.1);

        // Get ports along every Path connected to the Ports on the touched PhoneHost
        Group<Port> basePathPorts = new Group<>();
        Group<Port> hostPorts = hostImage.getHost().getPorts();
        for (int i = 0; i < hostPorts.size(); i++) {
            Port port = hostPorts.get(i);

            if (!basePathPorts.contains(port)) {
                basePathPorts.add(port);
            }

            PathGroup portPaths = port.getPaths();
            for (int j = 0; j < portPaths.size(); j++) {
                Path path = portPaths.get(j);
                if (!basePathPorts.contains(path.getSource())) {
                    basePathPorts.add(path.getSource());
                }
                if (!basePathPorts.contains(path.getTarget())) {
                    basePathPorts.add(path.getTarget());
                }
            }
        }
        // </REFACTOR>

        ShapeGroup hostPathPortShapes = getSpace().getShapes().filterEntity(basePathPorts);
        Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());

        // Update scale and position
        adjustScale(boundingBox);
        setPosition(boundingBox.getPosition());
    }

    public void setFocus(Extension extension) {

        // <REFACTOR>
        ExtensionImage extensionImage = (ExtensionImage) space.getImage(extension);

        // Reduce transparency of other all Portables (not electrically connected to the PhoneHost)
        ImageGroup otherPortableImages = getSpace().getImages().filterType(Host.class, Extension.class);
        otherPortableImages.remove(extensionImage);
        otherPortableImages.setTransparency(0.1);

        // Get ports along every Path connected to the Ports on the selected Host
        Group<Port> hostPathPorts = new Group<>();
        Group<Port> extensionPorts = extensionImage.getExtension().getPorts();
        for (int i = 0; i < extensionPorts.size(); i++) {
            Port port = extensionPorts.get(i);

            if (!hostPathPorts.contains(port)) {
                hostPathPorts.add(port);
            }

            PathGroup portPaths = port.getPaths();
            for (int j = 0; j < portPaths.size(); j++) {
                Path path = portPaths.get(j);
                if (!hostPathPorts.contains(path.getSource())) {
                    hostPathPorts.add(path.getSource());
                }
                if (!hostPathPorts.contains(path.getTarget())) {
                    hostPathPorts.add(path.getTarget());
                }
            }
        }
        // </REFACTOR>

        ShapeGroup hostPathPortShapes = getSpace().getShapes().filterEntity(hostPathPorts);
        Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getPositions());

        // Update scale and position
        adjustScale(boundingBox);
        setPosition(boundingBox.getPosition());
    }

    /**
     * Adjusts the focus so the {@code Path}s in {@code paths} are in view.
     *
     * @param paths
     */
    public void setFocus(PathGroup paths) {

        // Get bounding box around the Ports in the specified Paths
        ShapeGroup pathPortShapes = getSpace().getShapes(paths.getPorts());
        List<Point> portPositions = pathPortShapes.getPositions();
        Rectangle boundingBox = Geometry.getBoundingBox(portPositions);

        // Update scale and position
        adjustScale(boundingBox);
        setPosition(Geometry.getCenterPoint(portPositions));
    }

    public void setFocus(Space space) {

        // Hide Portables' Ports.
        space.hidePortables();

        // Update distance between Hosts and Extensions
        space.setPortableSeparation(300);

        // Update scale and position
        adjustScale();
        adjustPosition();
    }

    public void update() {

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        scale = this.targetScale;

        position.setAbsoluteX(targetPosition.getAbsoluteX());
        position.setAbsoluteY(targetPosition.getAbsoluteY());

        position.setAbsoluteX(position.getAbsoluteX() * scale);
        position.setAbsoluteY(position.getAbsoluteY() * scale);
        */

        // Scale
        if (Math.abs(scale - targetScale) <= scaleDelta) {
            scale = targetScale;
        } else if (scale > targetScale) {
            scale -= scaleDelta;
        } else {
            scale += scaleDelta;
        }

        // Position
        if (positionFrameIndex < positionFrameLimit) {

            double totalDistanceToTarget = Geometry.distance(originalPosition, targetPosition);
            double totalDistanceToTargetX = targetPosition.x - originalPosition.x;
            double totalDistanceToTargetY = targetPosition.y - originalPosition.y;

            double currentDistanceTarget = ((((double) (positionFrameIndex + 1) / (double) positionFrameLimit) * totalDistanceToTarget) / totalDistanceToTarget) /* (1.0 / scale) */;

            position.set(
                    scale * (currentDistanceTarget * totalDistanceToTargetX + originalPosition.x),
                    scale * (currentDistanceTarget * totalDistanceToTargetY + originalPosition.y)
            );

            positionFrameIndex++;

        } else { // if (positionFrameIndex >= positionFrameLimit) {

            position.x = targetPosition.x * scale;
            position.y = targetPosition.y * scale;

        }
    }
}
