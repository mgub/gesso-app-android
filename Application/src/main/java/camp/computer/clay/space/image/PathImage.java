package camp.computer.clay.space.image;

import android.graphics.Color;
import android.graphics.Paint;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class PathImage extends Image<Path> {

    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

    public PathImage(Path path) {
        super(path);
        setup();
    }

    private void setup() {
        setupGeometry();
        layerIndex = -10;
    }

    private void setupGeometry() {
        Segment segment;

        // Board
        segment = new Segment<>();
        segment.setOutlineThickness(2.0);
        segment.setLabel("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
        addShape(segment);
    }

    public Path getPath() {
        return getEntity();
    }

    public void drawTrianglePath(Display display) {

        Paint paint = display.paint;

        Path path = getPath();

        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.parseColor(sourcePortShape.getColor()));

        double pathRotation = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
        Transform sourcePoint = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotation, 2 * triangleSpacing);
        Transform targetPoint = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotation + 180, 2 * triangleSpacing);

        display.drawTrianglePath(sourcePoint, targetPoint, triangleWidth, triangleHeight);
    }

    public void drawLinePath(Display display) {

        Paint paint = display.paint;

        Path path = getPath();
        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        if (sourcePortShape != null && targetPortShape != null) {

            // Show target port
            targetPortShape.setVisibility(Visibility.VISIBLE);
            //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(Color.parseColor(sourcePortShape.getColor()));

            double pathRotationAngle = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotationAngle, 0);
            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotationAngle + 180, 0);

//            display.drawSegment(pathStartCoordinate, pathStopCoordinate);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
//            double pathRotation = getSpace().getImages(getPath().getHosts()).getRotation();

            Segment segment = (Segment) getShape("Path");
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(pathStartCoordinate);
            segment.setTarget(pathStopCoordinate);

            display.drawSegment(segment);
        }
    }

    public void drawPhysicalPath(Display display) {

        Path path = getPath();

        // Get Host and Extension Ports
        Port hostPort = path.getSource();
        Port extensionPort = path.getTarget();

        // Draw the connection to the Host's Port

        PortableImage hostImage = (PortableImage) hostPort.getPortable().getComponent(Image.class);
        PortableImage extensionImage = (PortableImage) extensionPort.getPortable().getComponent(Image.class);

        if (hostImage.headerContactPositions.size() > hostPort.getIndex() && extensionImage.headerContactPositions.size() > extensionPort.getIndex()) {
            Transform hostConnectorPosition = hostImage.headerContactPositions.get(hostPort.getIndex()).getPosition();
            Transform extensionConnectorPosition = extensionImage.headerContactPositions.get(extensionPort.getIndex()).getPosition();

            // Draw connection between Ports
            display.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(extensionPort.getType())));
            display.paint.setStrokeWidth(10.0f);
//            display.drawSegment(hostConnectorPosition, extensionConnectorPosition);

//            Polyline polyline = new Polyline();
//            polyline.addVertex(hostConnectorPosition);
//            polyline.addVertex(extensionConnectorPosition);
//            display.drawPolyline(polyline);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) getShape("Path");
            segment.setOutlineThickness(10.0);
            segment.setOutlineColor(camp.computer.clay.util.Color.getColor(extensionPort.getType()));

            segment.setSource(hostConnectorPosition);
            segment.setTarget(extensionConnectorPosition);

            display.drawSegment(segment);
        }
    }
}
