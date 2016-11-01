package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;

public class Host extends Component {

    // TODO: Move this into LayoutComponent for use by PortableLayoutSystem
    public double distanceToExtensions = 500;

    public List<List<Entity>> headerExtensions = new ArrayList<>();

    public Host() {
        super();
        setupHeaderExtensions();
    }

    public void setupHeaderExtensions() {
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
    }
}
