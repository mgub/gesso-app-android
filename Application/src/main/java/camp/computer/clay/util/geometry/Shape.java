package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.util.Color;
import camp.computer.clay.engine.component.Transform;

public abstract class Shape extends Groupable {

    protected String label = "";

    protected double targetTransparency = 1.0;
    protected double transparency = targetTransparency;

    protected Transform imagePosition = null;
    protected Transform position = new Transform(0, 0);

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    protected List<Transform> boundary = new ArrayList<>();

    public boolean isValid = false;

    /**
     * <em>Invalidates</em> the {@code Shape}. Invalidating a {@code Shape} causes its cached
     * geometry, such as its boundary, to be updated during the subsequent call to {@code updateImage()}.
     * <p>
     * Note that a {@code Shape}'s geometry cache will only ever be updated when it is first
     * invalidated by calling {@code invalidate()}. Therefore, to cause the {@code Shape}'s
     * geometry cache to be updated, call {@code invalidate()}. The geometry cache will be updated
     * in the first call to {@code updateImage()} following the call to {@code invalidate()}.
     */
    public void invalidate() {
        this.isValid = false;
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
//        parentImage.updateLayers();
    }
    // </LAYER>

    public Shape() {
    }

    public Shape(Transform position) {
        this.position.set(position);
    }

    // TODO: Move into ImageSystem, PortableLayoutSystem, or RenderSystem
    public void setImagePosition(Transform point) {
        if (imagePosition == null) {
            imagePosition = new Transform();
        }
        this.imagePosition.set(point.x, point.y);
        this.imagePosition.setRotation(point.rotation);
        invalidate();
    }

    public Transform getImagePosition() {
        return this.imagePosition;
    }

    public Transform getPosition() {
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
        invalidate();
    }

    public void setPosition(Transform point) {
        this.position.set(point.x, point.y);
        invalidate();
    }

    public void setRotation(double angle) {
        this.position.rotation = angle;
        invalidate();
    }

    public double getRotation() {
        return this.position.rotation;
    }

    public abstract List<Transform> getVertices();

    // TODO: Delete! Get boundary in BoundarySystem.
    public List<Transform> getBoundary() {
        return this.boundary;
    }

    public void setColor(String color) {
        this.color = color;

        // <ANDROID>
        this.colorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
    }

    public String getColor() {
        return color;
    }

    // <ANDROID>
    public int colorCode = android.graphics.Color.WHITE;
    public int outlineColorCode = android.graphics.Color.BLACK;
    // </ANDROID>

    public void setTransparency(final double transparency) {
        this.targetTransparency = transparency;

        // Color
        int intColor = android.graphics.Color.parseColor(getColor());
        intColor = Color.setTransparency(intColor, this.targetTransparency);
        setColor(Color.getHexColorString(intColor));

        // Outline Color
        int outlineColorIndex = android.graphics.Color.parseColor(getOutlineColor());
        outlineColorIndex = Color.setTransparency(outlineColorIndex, this.targetTransparency);
        setOutlineColor(Color.getHexColorString(outlineColorIndex));

        this.transparency = this.targetTransparency;
    }

    public void setOutlineColor(String color) {
        this.outlineColor = color;

        // <ANDROID>
        this.outlineColorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineThickness(double thickness) {
        this.outlineThickness = thickness;
    }

    public double getOutlineThickness() {
        return outlineThickness;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
