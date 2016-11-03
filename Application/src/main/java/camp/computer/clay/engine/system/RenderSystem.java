package camp.computer.clay.engine.system;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.image.Visible;

public class RenderSystem extends System {

    @Override
    public boolean update(World world) {
        return true;
    }

    public boolean update(World world, Canvas canvas) {

        // <HACK>
        PlatformRenderSurface platformRenderSurface = Application.getView().platformRenderSurface;
        // Canvas canvas = platformRenderSurface.canvas;
        // </HACK>

        platformRenderSurface.canvas = canvas;
        Bitmap canvasBitmap = platformRenderSurface.canvasBitmap;
        Matrix identityMatrix = platformRenderSurface.identityMatrix;

        // Adjust the Camera
        canvas.save();

        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
        Transform cameraPosition = camera.getComponent(Transform.class);
        canvas.translate(
                (float) platformRenderSurface.originPosition.x + (float) cameraPosition.x /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                (float) platformRenderSurface.originPosition.y + (float) cameraPosition.y /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
        );
        canvas.scale(
                (float) camera.getComponent(Camera.class).getScale(),
                (float) camera.getComponent(Camera.class).getScale()
        );


        canvas.drawColor(Color.WHITE); // Draw the background

        // TODO: renderSystem.update();

//        drawPrototypes(platformRenderSurface);
        drawEntities(platformRenderSurface);

        canvas.restore();

        drawOverlay(platformRenderSurface);

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        /*
        // Alternative to the above
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();
        */

        return true;
    }

    public void drawEntities(PlatformRenderSurface platformRenderSurface) {
        for (int j = 0; j < Entity.Manager.size(); j++) {
            Entity entity = Entity.Manager.get(j);

            Canvas canvas = platformRenderSurface.canvas;
            // Paint paint = platformRenderSurface.paint;
            // World world = platformRenderSurface.getWorld();

//            if (entity.hasComponent(Host.class)) {
//
//                Visibility visibility = entity.getComponent(Visibility.class);
//                if (visibility != null && visibility.isVisible) {
//                    Image image = entity.getComponent(Image.class);
//                    canvas.save();
//                    for (int i = 0; i < image.getShapes().size(); i++) {
//                        image.getShapes().get(i).draw(platformRenderSurface);
//                    }
//                    canvas.restore();
//                }
//
//            } else if (entity.hasComponent(Extension.class)) {
//
                // TODO: <MOVE_THIS_INTO_PORTABLE_SYSTEM>
                /*
                Group<Entity> ports = entity.getComponent(Portable.class).getPorts();
                int size = ports.size();
                for (int i = 0; i < size; i++) {
                    Entity port = ports.get(i);
                    if (port.getComponent(Port.class).getExtension() == null) {
                        // TODO: Remove Port Entity!
                        ports.remove(port);

                        Entity.Manager.remove(port);

                        size--;
                    }
                }
                */
                // TODO: </MOVE_THIS_INTO_PORTABLE_SYSTEM>
//
//                Visibility visibility = entity.getComponent(Visibility.class);
//                if (visibility != null && visibility.isVisible) {
//                    Image image = entity.getComponent(Image.class);
//                    canvas.save();
//                    for (int i = 0; i < image.getShapes().size(); i++) {
//                        image.getShapes().get(i).draw(platformRenderSurface);
//                    }
//                    canvas.restore();
//                }
//
//            } else if (entity.hasComponent(Port.class)) {
//
//                Visibility visibility = entity.getComponent(Visibility.class);
//                if (visibility != null && visibility.isVisible) {
//                    Image image = entity.getComponent(Image.class);
//                    canvas.save();
//                    for (int i = 0; i < image.getShapes().size(); i++) {
//                        image.getShapes().get(i).draw(platformRenderSurface);
//                    }
//                    canvas.restore();
//                }
//
//            } else

            if (entity.hasComponent(Path.class)) {

                Image image = entity.getComponent(Image.class);

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Entity pathEntity = image.getEntity();
                    if (pathEntity.getComponent(Path.class).getType() == Path.Type.MESH) {
                        // TODO: Draw Path between wirelessly connected Ports
                        // platformRenderSurface.drawTrianglePath(pathEntity, platformRenderSurface);
                    } else if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                        platformRenderSurface.drawLinePath(pathEntity, platformRenderSurface);
                    }
                } else if (visibility != null && visibility.getVisibile() == Visible.INVISIBLE) {
                    Entity pathEntity = entity; // image.getPath();
                    if (pathEntity.getComponent(Path.class).getType() == Path.Type.ELECTRONIC) {
                        platformRenderSurface.drawPhysicalPath(pathEntity, platformRenderSurface);
                    }
                }

            }
            // TODO: <REFACTOR>
            // This was added so Prototype Extension/Path would render without Extension/Path components
            else if (entity.hasComponent(Image.class)) {

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Image image = entity.getComponent(Image.class);
                    canvas.save();
                    for (int i = 0; i < image.getShapes().size(); i++) {
                        platformRenderSurface.drawShape(image.getShapes().get(i));
                    }
                    canvas.restore();
                }

            }
            // TODO: </REFACTOR>
        }
    }

    public void drawOverlay(PlatformRenderSurface platformRenderSurface) {

        Canvas canvas = platformRenderSurface.canvas;
        Paint paint = platformRenderSurface.paint;
        // World world = platformRenderSurface.getWorld();

        int linePosition = 0;

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);

        String fpsText = "FPS: " + (int) platformRenderSurface.platformRenderer.getFramesPerSecond();
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        linePosition += 25 + fpsTextBounds.height();
        canvas.drawText(fpsText, 25, linePosition, paint);
        canvas.restore();
        // </FPS_LABEL>

        // <ENTITY_STATISTICS>
        canvas.save();
        int entityCount = Entity.Manager.size();
        int hostCount = Entity.Manager.filterWithComponent(Host.class).size();
        int portCount = Entity.Manager.filterWithComponent(Port.class).size();
        int extensionCount = Entity.Manager.filterWithComponent(Extension.class).size();
        int pathCount = Entity.Manager.filterWithComponent(Path.class).size();
        int cameraCount = Entity.Manager.filterWithComponent(Camera.class).size();

        // Entities
        String text = "Entities: " + entityCount;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Hosts
        canvas.save();
        text = "Hosts: " + hostCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Ports
        canvas.save();
        text = "Ports: " + portCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Extensions
        canvas.save();
        text = "Extensions: " + extensionCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Paths
        canvas.save();
        text = "Paths: " + pathCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();

        // Cameras
        canvas.save();
        text = "Cameras: " + cameraCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += 25 + textBounds.height();
        canvas.drawText(text, 25, linePosition, paint);
        canvas.restore();
        // </ENTITY_STATISTICS>
    }
}