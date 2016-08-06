package camp.computer.clay.model.interactivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.architecture.Device;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Layer;
import camp.computer.clay.visualization.image.DeviceImage;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PathImage;
import camp.computer.clay.visualization.image.PortImage;
import camp.computer.clay.visualization.util.Geometry;

public class Body {

    private Perspective perspective = null;

    public List<Interaction> interactions = new ArrayList<>();

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public boolean hasPerspective() {
        return perspective != null;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    /**
     * Returns the most recent interaction.
     *
     * @return The most recent interaction.
     */
    public Interaction getInteraction() {
        if (interactions.size() > 0) {
            return this.interactions.get(interactions.size() - 1);
        } else {
            return null;
        }
    }

    public void onStartInteraction(Impression impression) {

        impression.setBody(this);

        // Having an idea is just accumulating intention. It's a suggestion from your existential
        // controller.

        Interaction interaction = new Interaction();
        interaction.add(impression);

        interactions.add(interaction);

        // TODO: Cache and store the apply interactivites before deleting them completely! Do it in
        // TODO: (cont'd) a background thread.
        if (interactions.size() > 3) {
            interactions.remove(0);
        }

        onTouchListener(interaction, impression);
    }

    public void onContinueInteraction(Impression impression) {

        impression.setBody(this);

        // Current
        impression.isTouching[impression.pointerIndex] = true;

        Interaction interaction = getInteraction();
        interaction.add(impression);

        // Calculate drag distance
        interaction.dragDistance[impression.pointerIndex] = Geometry.calculateDistance(impression.getPosition(), interaction.getFirst().touchPositions[impression.pointerIndex]);

        // Classify/Callback
        if (interaction.dragDistance[impression.pointerIndex] > Impression.MINIMUM_DRAG_DISTANCE) {
            onDragListener(interaction, impression);
        }
    }

    public void onStopInteraction(Impression impression) {

        impression.setBody(this);

        Interaction interaction = getInteraction();
        interaction.add(impression);

        // Stop listening for a hold impression
        interaction.timerHandler.removeCallbacks(interaction.timerRunnable);

        // Current
        impression.isTouching[impression.pointerIndex] = false;

        if (interaction.getDuration() < Impression.MAXIMUM_TAP_DURATION) {
            onTapListener(interaction, impression);
        } else {
            onReleaseListener(interaction, impression);
        }
    }

    private void onTouchListener(Interaction interaction, Impression impression) {

        impression.setType(Impression.Type.TOUCH);

        Image touchedImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(touchedImage);

//        if (perspective.hasFocusImage()) {
//
//            if (perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (impression.contains() && impression.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Impression", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) impression.getImageByPosition());
//                }
//            }
//
//            if (perspective.getFocusImage().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (impression.contains() && impression.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) impression.getImageByPosition());
//                }
//            }
//        }
//
//        // Reset object impression state
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (impression.contains() && impression.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) impression.getImageByPosition());
//            }
//
//        }
//
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//            if (!impression.contains()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(Interaction interaction, Impression impression) {

        impression.setType(Impression.Type.HOLD);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        interaction.isHolding[impression.pointerIndex] = true;

        if (impression.isTouching()) {

            if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                // TODO:

            } else if (impression.getTargetImage().isType(PortImage.TYPE)) {

                // TODO:

            }
        }
    }

    private void onDragListener(Interaction interaction, Impression impression) {

        impression.setType(Impression.Type.DRAG);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("onDragListener", "" + impression.getType() + ": " + impression.getTargetImage());

        Log.v("Impression", "onDrag");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        if (impression.getTargetImage() != null) {
            Log.v("Impression", "apply type: " + impression.getTargetImage().getType());
        }
        Log.v("Impression", "-");

        if (interaction.getSize() > 1) {
            impression.setTargetImage(interaction.getFirst().getTargetImage());
        }

        interaction.isDragging[impression.pointerIndex] = true;

        // Dragging and holding
        if (interaction.isHolding[impression.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (impression.isTouching()) {

                if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) impression.getTargetImage();
                    frameImage.apply(impression);
                    frameImage.setPosition(impression.getPosition());

                    // Zoom out to show overview
//                    perspective.setScale(0.8f);

                } else if (impression.getTargetImage().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) impression.getTargetImage();
                    portImage.isTouched = true;
//                    Impression impression = new Impression(impression.getPosition(), Impression.Type.DRAG);
//                    portSprite.touchPositions(impression);

                    portImage.setDragging(true);
                    portImage.setPosition(impression.getPosition());
                }

            } else if (perspective.isAdjustable()) {

//                perspective.setScale(0.9f);
//                perspective.setOffset(
//                        impression.getPosition().getX() - interaction.getFirst().getPosition().getX(),
//                        impression.getPosition().getY() - interaction.getFirst().getPosition().getY()
//                );

                perspective.focusOnPerspectiveAdjustment(interaction);

            }

        } else {

            // Dragging only (not holding)

            // TODO: Put into callback
            if (impression.isTouching()) {

                if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) impression.getTargetImage();
                    frameImage.apply(impression);
                    frameImage.setPosition(impression.getPosition());

                    perspective.focusOnFrame(this, interaction, impression);

                } else if (impression.getTargetImage().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) impression.getTargetImage();
                    portImage.apply(impression);

                    perspective.focusOnNewPath(interaction, impression);

                } else if (impression.getTargetImage().isType(DeviceImage.TYPE)) {

                    Log.v("Device", "Drag peripheral");

                    DeviceImage deviceImage = (DeviceImage) impression.getTargetImage();
                    deviceImage.setPosition(impression.getPosition());
                    deviceImage.apply(impression);

                }

            } else if (perspective.isAdjustable()) {

                perspective.setScale(0.9f);
                if (interaction.getSize() > 1) {
                    perspective.setOffset(
                            impression.getPosition().getX() - interaction.getPrevious(impression).getPosition().getX(),
                            impression.getPosition().getY() - interaction.getPrevious(impression).getPosition().getY()
                    );
                }

            }

//            else {
//
//                perspective.focusOnPerspectiveAdjustment(interaction);
//
//            }

        }
    }

    private void onTapListener(Interaction interaction, Impression impression) {

        impression.setType(Impression.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("Impression", "onTap");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        Log.v("Impression", "-");

        if (impression.isTouching()) {

            if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                // Frame
                FrameImage frameImage = (FrameImage) impression.getTargetImage();
                perspective.focusOnFrame(this, interaction, impression);
                frameImage.apply(impression);

            } else if (impression.getTargetImage().isType(PortImage.TYPE)) {

                // Port
                PortImage portImage = (PortImage) impression.getTargetImage();
                portImage.apply(impression);

            } else if (impression.getTargetImage().isType(PathImage.TYPE)) {

                // Path
                PathImage pathImage = (PathImage) impression.getTargetImage();
                pathImage.apply(impression);

//                perspective.tap_focusOnPath();

            } else if (impression.getTargetImage().isType(DeviceImage.TYPE)) {

                // Device
                DeviceImage deviceImage = (DeviceImage) impression.getTargetImage();
                deviceImage.apply(impression);

            }

        } else if (!impression.isTouching()) {

            perspective.focusReset();
        }

    }

    private void onReleaseListener(Interaction interaction, Impression impression) {
        impression.setType(Impression.Type.RELEASE);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("Impression", "onRelease");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        Log.v("Impression", "-");

        if (impression.isTouching()) {

            // First apply was on a frame image...
            if (interaction.getFirst().getTargetImage().isType(FrameImage.TYPE)) {

                if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                    // If first apply was on the same form, then respond
                    if (interaction.getFirst().isTouching() && interaction.getFirst().getTargetImage().isType(FrameImage.TYPE)) {

                        // Frame
                        FrameImage frameImage = (FrameImage) impression.getTargetImage();
                        frameImage.apply(impression);

                        // Perspective
                        perspective.focusReset();
                    }

                }

            } else if (interaction.getFirst().getTargetImage().isType(PortImage.TYPE)) {

                // First apply was on a port image...

                if (impression.getTargetImage().isType(FrameImage.TYPE)) {

                    // ...last apply was on a frame image.

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();
                    sourcePortImage.setCandidatePathVisibility(false);

                } else if (impression.getTargetImage().isType(PortImage.TYPE)) {

                    // ...last apply was on a port image.

                    // PortImage portImage = (PortImage) impression.getImageByPosition();
                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    if (sourcePortImage.isDragging()) {

                        // Get nearest port image
                        PortImage nearestPortImage = (PortImage) getPerspective().getVisualization().getImages().filterType(PortImage.TYPE).getNearest(impression.getPosition());
                        Port nearestPort = nearestPortImage.getPort();
                        Log.v("DND", "nearestPort: " + nearestPort);

                        // TODO: When dragging, enable pushing ports?

                        // Remove the paths from the port and move them to the selected port
//                        Port nearestPort = nearestPortImage.getPort();
//                        while (sourcePortImage.getPort().getPaths().size() > 0) {
//                            Path path = sourcePortImage.getPort().getPaths().remove(0);
//                            path.setSource(nearestPort);
//                            nearestPort.addPath(path);
//                        }

                        Port sourcePort = sourcePortImage.getPort();

                        List<Path> paths = getPerspective().getVisualization().getSimulation().getPaths();

                        // Copy configuration
                        nearestPort.setDirection(sourcePort.getDirection());
                        nearestPort.setType(sourcePort.getType());
                        nearestPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                        // Reset port configuration
                        sourcePort.setDirection(Port.Direction.NONE);
                        sourcePort.setType(Port.Type.NONE);
                        sourcePortImage.updateUniqueColor();

                        // Clear the port's list of paths
                        sourcePort.getPaths().clear();

                        // Copy paths
                        for (Path path : paths) {

                            // Update source
                            if (path.getSource() == sourcePort) {
                                path.setSource(nearestPort);
                                Log.v("DND", "Updating source");
                            }

                            // Update target
                            if (path.getTarget() == sourcePort) {
                                path.setTarget(nearestPort);
                                Log.v("DND", "Updating target");
                            }

//                            Path replacementPath = new Path(nearestPortImage.getPort(), path.getTarget());
//                            nearestPortImage.getPort().addPath(replacementPath);
//
//                            PathImage replacementPathImage = new PathImage(path);
//                            replacementPathImage.setVisualization(perspective.getVisualization());
//                            perspective.getVisualization().addImage(path, replacementPathImage, "paths");
//
//                            PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
//                            targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                            nearestPort.addPath(path);

                        }

                        // Restore port image's position
                        sourcePortImage.setDragging(false);

                        // Perspective
                        perspective.focusOnPath(sourcePort);

                    } else {

//                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {
//
//                    // Model
//                    Device peripheral = new Device();
//                    getPerspective().getVisualization().getSimulation().addDevice(peripheral);
//
//                    // Visualization (Layer)
//                    String layerTag = "peripherals";
//                    getPerspective().getVisualization().addLayer(layerTag);
//                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);
//
//                    // Image
//                    DeviceImage peripheralImage = new DeviceImage(peripheral);
//                    peripheralImage.setPosition(impression.getPosition());
//                    peripheralImage.setVisualization(getPerspective().getVisualization());
//
//                    // Visualization
//                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);
//
//                }

                        // Show ports of nearby forms
                        boolean useNearbyPortImage = false;
                        for (FrameImage nearbyFrameImage : perspective.getVisualization().getFrameImages()) {

                            Log.v("Impression", "A");

                            // Update style of nearby machines
                            double distanceToFrameImage = Geometry.calculateDistance(
                                    impression.getPosition(),
                                    nearbyFrameImage.getPosition()
                            );

                            if (distanceToFrameImage < nearbyFrameImage.getShape().getHeight() + 50) {

                                Log.v("Impression", "B");

                                // TODO: Use overlappedImage instanceof PortImage

                                for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                                    if (nearbyPortImage != sourcePortImage) {
                                        if (nearbyPortImage.contains(impression.getPosition(), 50)) {

                                            Log.v("Impression", "C");

                                            Port port = sourcePortImage.getPort();
                                            Port nearbyPort = nearbyPortImage.getPort();

                                            useNearbyPortImage = true;

                                            if (port.getDirection() == Port.Direction.NONE) {
                                                port.setDirection(Port.Direction.INPUT);
                                            }
                                            if (port.getType() == Port.Type.NONE) {
                                                port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                            }

                                            nearbyPort.setDirection(Port.Direction.OUTPUT);
                                            nearbyPort.setType(Port.Type.next(nearbyPort.getType()));

                                            // Create and add path to port
                                            Port sourcePort = (Port) perspective.getVisualization().getModel(sourcePortImage);
                                            Port targetPort = (Port) perspective.getVisualization().getModel(nearbyPortImage);

                                            if (!sourcePort.hasAncestor(targetPort)) {

                                                Log.v("Impression", "D.1");

                                                Path path = new Path(sourcePort, targetPort);
                                                sourcePort.addPath(path);

                                                PathImage pathImage = new PathImage(path);
                                                pathImage.setVisualization(perspective.getVisualization());
                                                perspective.getVisualization().addImage(path, pathImage, "paths");

                                                PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
                                                targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                                // Perspective
                                                perspective.focusOnPath(sourcePort);
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        }

//                portImage.apply(impression);

                        if (!useNearbyPortImage) {

                            Port port = (Port) sourcePortImage.getModel();

                            port.setDirection(Port.Direction.INPUT);

                            if (port.getType() == Port.Type.NONE) {
                                port.setType(Port.Type.next(port.getType()));
                            }
                        }

                        sourcePortImage.setCandidatePathVisibility(false);

                        // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustScale(boundingBox);
//
//                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                impression.setTargetImage(interaction.getFirst().getImageByPosition());
//                impression.setType(Impression.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + impression.getImageByPosition());
//                targetImage.apply(impression);

                    }
                }

            } else if (interaction.getFirst().getTargetImage().isType(PathImage.TYPE)) {

                // First apply was on a path image...

                if (impression.getTargetImage().isType(PathImage.TYPE)) {
                    PathImage pathImage = (PathImage) impression.getTargetImage();
                }
            }

        } else if (!impression.isTouching()) {

            if (interaction.getFirst().getTargetImage() != null
                    && interaction.getFirst().getTargetImage().isType(PortImage.TYPE)) {

                // PortImage portImage = (PortImage) impression.getImageByPosition();
                PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {

                    // Model
                    Device device = new Device();
                    // Add port to model
                    for (int j = 0; j < 3; j++) {
                        Port port = new Port();
                        device.addPort(port);
                    }

                    getPerspective().getVisualization().getSimulation().addDevice(device);

                    // Visualization (Layer)
                    String layerTag = "peripherals";
                    getPerspective().getVisualization().addLayer(layerTag);
                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);

                    // Image
                    DeviceImage deviceImage = new DeviceImage(device);
                    deviceImage.setPosition(impression.getPosition());
//                    deviceImage.setRotation();
                    deviceImage.setVisualization(getPerspective().getVisualization());

                    // Port Images
                    for (Port port : device.getPorts()) {
                        PortImage portImage = new PortImage(port);
                        portImage.setVisualization(getPerspective().getVisualization());
                        getPerspective().getVisualization().addImage(port, portImage, "ports");
                    }

                    double pathRotationAngle = Geometry.calculateRotationAngle(
                            sourcePortImage.getPosition(),
                            deviceImage.getPosition()
                    );
                    deviceImage.setRotation(pathRotationAngle + 90);

                    // Visualization
                    getPerspective().getVisualization().addImage(device, deviceImage, layerTag);

//                    // Add a port sprite for each of the associated base's ports
//                    for (Port port : device.getPorts()) {
//                        PortImage portImage = new PortImage(port);
//                        portImage.setVisualization(getPerspective().getVisualization());
//                        getPerspective().getVisualization().addImage(port, portImage, "ports");
//                    }

                }

            }

//            // No touchPositions on board or port. Touch is on map. So hide ports.
//            for (FrameImage formImage : perspective.getVisualization().getFrameImages()) {
//                formImage.hidePortImages();
//                formImage.hidePathImages();
//                formImage.setTransparency(1.0f);
//            }
//
//            // Adjust panning
//            // Auto-adjust the perspective
//            Point centroidPosition = perspective.getVisualization().getImages().filterType(FrameImage.TYPE).calculateCentroid();
//            perspective.setPosition(new Point(centroidPosition.x, centroidPosition.y));
//
//            adjustScale();

            // Check if first apply was on an image
            if (interaction.getFirst().isTouching()) {
                if (interaction.getFirst().getTargetImage().isType(PortImage.TYPE)) {
                    ((PortImage) interaction.getFirst().getTargetImage()).setCandidatePathVisibility(false);
                }
            }

            perspective.focusReset();

        }

        // Interaction
        perspective.setAdjustability(true);
    }
}