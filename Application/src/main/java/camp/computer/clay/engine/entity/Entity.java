package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Structure;
import camp.computer.clay.engine.manager.EntityManager;
import camp.computer.clay.engine.manager.Group;

public final class Entity {

    // <HACK>
    public long uuid = EntityManager.count++; // entityManager.INVALID_UID;
    // </HACK>

    public long getUuid() {
        return uuid;
    }

    public boolean isActive = false;
    public boolean isDestroyable = false;

    private Group<Component> components = null;

    public Entity() {
        setup();
    }

    private void setup() {
        components = new Group<>(); // Create list of Components

        components.add(new Structure());
    }

    public <C extends Component> void addComponent(C component) {
        component.setEntity(this); // Associate Component with Entity
        components.add(component); // Add to Entity
    }

    public <C extends Component> C getComponent(Class<C> type) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getClass() == type) {
                return type.cast(components.get(i));
            }
        }
        return null;
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }

    public boolean hasComponents(Class<? extends Component>... types) {
        for (int i = 0; i < types.length; i++) {
            if (!hasComponent(types[i])) {
                return false;
            }
        }
        return true;
    }

    public void removeComponents() {
        components.clear();
    }

    public <C extends Component> C removeComponent(Class<C> type) {
        C component = getComponent(type);
        if (component != null) {
            components.remove(component);
        }
        return component;
    }


    // TODO: <DELETE>
//    private Entity parent;

//    public void setParent(Entity parent) {
////        this.parent = parent;
//        getComponent(Structure.class).parentEntity = parent;
//    }
//
//    public Entity getParent() {
////        return this.parent;
//        return getComponent(Structure.class).parentEntity;
//    }
    // TODO: </DELETE>
}
