package camp.computer.clay.space.image;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.PortableProfile;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class ExtensionImage extends PortableImage {

    public ExtensionImage(Extension extension) {
        super(extension);
        setup();
    }

    private void setup() {
        setupShapes();
        setupActions();
    }

    private void setupShapes() {

        Rectangle rectangle;

        // Create Shapes for Image
        rectangle = new Rectangle(getExtension());
        rectangle.setWidth(200);
        rectangle.setHeight(200);
        rectangle.setLabel("Board");
        rectangle.setColor("#f7f7f7");
        rectangle.setOutlineThickness(1);
        addShape(rectangle);

        // Headers
        rectangle = new Rectangle(50, 14);
        rectangle.setLabel("Header");
        rectangle.setPosition(0, 107);
        rectangle.setRotation(0);
        rectangle.setColor("#3b3b3b");
        rectangle.setOutlineThickness(0);
        addShape(rectangle);
    }

    private void setupActions() {

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                    createProfile();

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Previous Action targeted also this Extension
                    // TODO: Refactor
                    if (action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getExtension()) {

                        if (action.isTap()) {
                            // TODO: Replace with script editor/timeline
                            Application.getView().openActionEditor(getExtension());
                        }

                    } else {


                        if (action.isTap()) {

                            // Focus on touched base
                            setPathVisibility(Visibility.Value.VISIBLE);
                            getPortShapes().setVisibility(Visibility.Value.VISIBLE);
                            setTransparency(1.0);

                            // Show ports and paths of touched form
                            ShapeGroup portShapes = getPortShapes();
                            for (int i = 0; i < portShapes.size(); i++) {
                                Shape portShape = portShapes.get(i);
                                Port port = (Port) portShape.getEntity();

                                PathGroup paths = port.getPaths();
                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show ports
                                    getSpace().getShape(path.getSource()).setVisibility(Visibility.Value.VISIBLE);
                                    getSpace().getShape(path.getTarget()).setVisibility(Visibility.Value.VISIBLE);

                                    // Show path
                                    getSpace().getImage(path).setVisibility(Visibility.Value.VISIBLE);
                                }
                            }

                            // Camera
                            event.getActor().getCamera().setFocus(getExtension());

                            // Title
                            space.setTitleText("Extension");
                            space.setTitleVisibility(Visibility.Value.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    // TODO: This is an action that Clay can perform. Place this better, maybe in Clay.
    private void createProfile() {
        if (!getExtension().hasProfile()) {

            // TODO: Only call promptInputText if the extension is a draft (i.e., does not have an associated PortableProfile)
            Application.getView().getActionPrompts().promptInputText(new Prompt.OnActionListener<String>() {
                @Override
                public void onComplete(String text) {
                    // Create Extension Profile
                    PortableProfile portableProfile = new PortableProfile(getExtension());
                    portableProfile.setLabel(text);

                    // Assign the Profile to the Extension
                    getExtension().setProfile(portableProfile);

                    // Cache the new Extension Profile
                    Application.getView().getClay().getPortableProfiles().add(portableProfile);

                    // TODO: Persist the profile in the user's private store (either local or online)

                    // TODO: Persist the profile in the global store online
                }
            });
        } else {
            Application.getView().getActionPrompts().promptAcknowledgment(new Prompt.OnActionListener() {
                @Override
                public void onComplete(Object result) {

                }
            });
        }
    }

    public Extension getExtension() {
        return (Extension) getEntity();
    }

    public void update() {

        // Create additional Images or Shapes to match the corresponding Entity
        updateImage();

        // Update Port style
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);
            Shape portShape = getShape(port);

            // Update color of Port shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(port.getType()));
            }
        }
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    private void updateImage() {

        updatePortShapes();

        updateHeaderShapes();

        // TODO: Clean up/delete images/shapes for any removed ports...
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code Extension}'s {@code Port}s.
     */
    private void updatePortShapes() {

        // Remove Port shapes from the Image that do not have a corresponding Port in the Entity
        ShapeGroup portShapes = getShapes(Port.class);
        for (int i = 0; i < portShapes.size(); i++) {
            Shape portShape = portShapes.get(i);

            if (!getPortable().getPorts().contains((Port) portShape.getEntity())) {
                portShapes.remove(portShape);
            }
        }

        // Create Port shapes for each of Extension's Ports if they don't already exist
        for (int i = 0; i < getExtension().getPorts().size(); i++) {
            Port port = getExtension().getPorts().get(i);

            if (getShape(port) == null) {

                // Ports
                Circle<Port> circle = new Circle<>(port);
                circle.setRadius(40);
                circle.setLabel("Port " + (getExtension().getPorts().size() + 1));
                circle.setPosition(-90, 175);
                // circle.setRelativeRotation(0);

                circle.setColor("#efefef");
                circle.setOutlineThickness(0);

                circle.setVisibility(Visibility.Value.INVISIBLE);

                addShape(circle);
            }
        }
    }

    private void updateHeaderShapes() {

        // Update Header (size, etc.)
        // Reference: http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
        double PIXELS_PER_MILLIMETER = 6.0;

        // Update dimensions of Headers based on the corresponding Entity
        Rectangle header = (Rectangle) getShape("Header");
        double headerWidth = PIXELS_PER_MILLIMETER * (2.54 * getPortable().getPorts().size() + 0.6); // +-0.6
        header.setWidth(headerWidth);

        // Update physical positions of Ports based on the corresponding Header's dimensions
        for (int i = 0; i < getPortable().getPorts().size(); i++) {

            // Calculate Port connector positions
            if (portConnectorPositions.size() > i) {
                double x = PIXELS_PER_MILLIMETER * (2.54 * i + 0.6);
                portConnectorPositions.get(i).setRelativeX(x);
            } else {
                double x = PIXELS_PER_MILLIMETER * (2.54 * i + 0.6);
                portConnectorPositions.add(new Point(x, 107, position));
            }
        }

        // Update Port positions based on the number of ports
        for (int i = 0; i < getPortable().getPorts().size(); i++) {
            Port port = getPortable().getPorts().get(i);
            Circle portShape = (Circle) getShape(port);

            if (portShape != null) {
                double portSpacing = 100;
                double x = (i * portSpacing) - (((getPortable().getPorts().size() - 1) * portSpacing) / 2.0);
                portShape.getPosition().setRelativeX(x);
                // TODO: Also update relativeY coordinate
            }
        }
    }

    public void draw(Display display) {
        if (isVisible()) {
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).draw(display);
            }
        }
    }
}

