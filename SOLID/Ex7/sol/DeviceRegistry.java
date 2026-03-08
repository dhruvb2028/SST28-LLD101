import java.util.*;

public class DeviceRegistry {
    private final java.util.List<SmartClassroomDevice> devices = new ArrayList<>();

    public void add(SmartClassroomDevice d) { devices.add(d); }

    public <T> T getFirstByCapability(Class<T> capType) {
        for (SmartClassroomDevice d : devices) {
            if (capType.isInstance(d)) return capType.cast(d);
        }
        throw new IllegalStateException("Missing capability: " + capType.getSimpleName());
    }
}
