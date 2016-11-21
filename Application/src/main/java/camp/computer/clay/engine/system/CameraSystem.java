package camp.computer.clay.engine.system;

import android.util.Log;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.lib.ImageBuilder.Shape;
import camp.computer.clay.util.Geometry;

public class CameraSystem extends System {

    public CameraSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        Group<Entity> cameras = world.Manager.getEntities().filterWithComponent(Camera.class);
        for (int i = 0; i < cameras.size(); i++) {
            updateCamera(cameras.get(i));
        }
    }

    private void updateCamera(Entity camera) {

        Camera cameraComponent = camera.getComponent(Camera.class);
        Transform transformComponent = camera.getComponent(Transform.class);
        Physics physicsComponent = camera.getComponent(Physics.class);

        transformComponent.scale = physicsComponent.targetTransform.scale;

        // Position
        camera.getComponent(Transform.class).set(
                physicsComponent.targetTransform.x * transformComponent.scale,
                physicsComponent.targetTransform.y * transformComponent.scale
        );

    }

    // <REFACTOR/DELETE>
    // TODO: Put into PlatformRenderSurface? Viewport? Elsewhere? Screen descriptor structure?
    public void setWidth(Entity camera, double width) {
        camera.getComponent(Camera.class).width = width;
    }

    public double getWidth(Entity camera) {
        return camera.getComponent(Camera.class).width;
    }

    public void setHeight(Entity camera, double height) {
        camera.getComponent(Camera.class).height = height;
    }

    public double getHeight(Entity camera) {
        return camera.getComponent(Camera.class).height;
    }
    // </REFACTOR/DELETE>

    private void setPosition(Entity camera, Transform position, double duration) {
        Log.v("CameraSystem", "setPosition");
        camera.getComponent(Physics.class).targetTransform.set(-position.x, -position.y);
//        camera.getComponent(Transform.class).set(-position.x, -position.y);
    }

    private void adjustPosition(Entity camera) {
        Log.v("CameraSystem", "adjustPosition");
        Transform centerPosition = world.Manager.getEntities().filterWithComponent(Host.class, Extension.class).getCenterPoint();
        setPosition(camera, centerPosition, Camera.DEFAULT_ADJUSTMENT_PERIOD);
    }

    public void setOffset(Entity camera, double dx, double dy) {
        camera.getComponent(Physics.class).targetTransform.offset(dx, dy);
    }

    public void setOffset(Entity camera, Transform point) {
        setOffset(camera, point.x, point.y);
    }

    private void setScale(Entity camera, double scale, double duration) {

        /*
        // TODO: Implement Zoom Levels
        if (Math.abs(scale - Camera.SCALE_LEVEL_1) < Math.abs(scale - Camera.SCALE_LEVEL_2)) {
            camera.getComponent(Camera.class).targetScale = Camera.SCALE_LEVEL_1;
            camera.getComponent(Transform.class).scale = Camera.SCALE_LEVEL_1;
        } else {
            camera.getComponent(Camera.class).targetScale = Camera.SCALE_LEVEL_2;
            camera.getComponent(Transform.class).scale = Camera.SCALE_LEVEL_2;
        }
         */

        camera.getComponent(Physics.class).targetTransform.scale = scale;
        camera.getComponent(Transform.class).scale = scale;
    }

    public double getScale(Entity camera) {
        return camera.getComponent(Transform.class).scale;
    }

    private void adjustScale(Entity camera, double duration) {
        Rectangle boundingBox = world.Manager.getEntities().filterWithComponent(Host.class, Extension.class).getBoundingBox();
        if (boundingBox.width > 0 && boundingBox.height > 0) {
            adjustScale(camera, boundingBox, duration);
        }
    }

    /**
     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}. This sets the
     * duration of the scale adjustment to the default value {@code DEFAULT_SCALE_PERIOD}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     */
    private void adjustScale(Entity camera, Rectangle boundingBox) {
        adjustScale(camera, boundingBox, Camera.DEFAULT_SCALE_PERIOD);
    }

    /**
     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     * @param duration    The duration of the scale adjustment.
     */
    private void adjustScale(Entity camera, Rectangle boundingBox, double duration) {

        /*
        // Multiply the bounding box
        double paddingMultiplier = 1.0; // 1.10;
        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
        */

        double horizontalScale = getWidth(camera) / boundingBox.getWidth();
        double verticalScale = getHeight(camera) / boundingBox.getHeight();

        if (horizontalScale <= Camera.MAXIMUM_SCALE || horizontalScale <= Camera.MAXIMUM_SCALE) {
            if (horizontalScale < verticalScale) {
                setScale(camera, horizontalScale, duration);
            } else if (horizontalScale > horizontalScale) {
                setScale(camera, verticalScale, duration);
            }
        } else {
            setScale(camera, Camera.MAXIMUM_SCALE, Camera.DEFAULT_SCALE_PERIOD);
        }
    }

    public void setFocus(Entity camera, Entity entity) {

        if (entity == null) {

            Log.v("SetFocus", "setFocus(World)");

            // Hide Portables' Ports.
            world.Manager.getEntities().filterWithComponent(Path.class, Port.class).setVisibility(Visible.INVISIBLE);

            // Update distance between Hosts and Extensions
            world.getSystem(PortableLayoutSystem.class).setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);

            // Update scale and position
            adjustScale(camera, Camera.DEFAULT_SCALE_PERIOD);
            adjustPosition(camera);

        } else if (entity.hasComponent(Host.class)) {

            Log.v("SetFocus", "setFocus(HostEntity)");

            // <REFACTOR>
            /*
            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
            otherPortables.setTransparency(0.1);
            */

            // Get all Ports in all Path connected to any of the Host's Ports
            Group<Entity> hostPathPorts = new Group<>();
            Group<Entity> hostPorts = Portable.getPorts(entity);
            for (int i = 0; i < hostPorts.size(); i++) {
                Entity port = hostPorts.get(i);

                if (!hostPathPorts.contains(port)) {
                    hostPathPorts.add(port);
                }

                Group<Entity> portPaths = Port.getPaths(port);
                for (int j = 0; j < portPaths.size(); j++) {
                    Entity path = portPaths.get(j);
                    if (!hostPathPorts.contains(Path.getSource(path))) {
                        hostPathPorts.add(Path.getSource(path));
                    }
                    if (Path.getTarget(path) != null // HACK: for case when singleton Path has no Target Port
                            && !hostPathPorts.contains(Path.getTarget(path))) {
                        hostPathPorts.add(Path.getTarget(path));
                    }
                }
            }
            // </REFACTOR>

            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());

            // Update scale and position
            adjustScale(camera, boundingBox);
            setPosition(camera, entity.getComponent(Transform.class), Camera.DEFAULT_ADJUSTMENT_PERIOD);

        } else if (entity.hasComponent(Extension.class)) {

            Log.v("SetFocus", "setFocus(Extension)");

            // <REFACTOR>
            /*
            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
            otherPortables.setTransparency(0.1);
            */

            // Get Ports along every Path connected to the Ports on the selected Host
            Group<Entity> hostPathPorts = new Group<>();
            Group<Entity> extensionPorts = Portable.getPorts(entity);
            for (int i = 0; i < extensionPorts.size(); i++) {
                Entity port = extensionPorts.get(i);

                if (!hostPathPorts.contains(port)) {
                    hostPathPorts.add(port);
                }

                Group<Entity> portPaths = Port.getPaths(port);
                for (int j = 0; j < portPaths.size(); j++) {
                    Entity path = portPaths.get(j);
                    if (!hostPathPorts.contains(Path.getSource(path))) {
                        hostPathPorts.add(Path.getSource(path));
                    }
                    if (!hostPathPorts.contains(Path.getTarget(path))) {
                        hostPathPorts.add(Path.getTarget(path));
                    }
                }
            }
            // </REFACTOR>

            // Increase distance between Host and Extension
            Entity host = Portable.getHosts(entity).get(0);
            world.getSystem(PortableLayoutSystem.class).setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);

            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());

            // Update scale and position
            adjustScale(camera, boundingBox);
            setPosition(camera, entity.getComponent(Transform.class), Camera.DEFAULT_ADJUSTMENT_PERIOD);

        }
    }
}
