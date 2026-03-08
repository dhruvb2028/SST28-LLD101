public class ClassroomController {
    private final DeviceRegistry reg;

    public ClassroomController(DeviceRegistry reg) { this.reg = reg; }

    public void startClass() {
        ProjectorControl projector = reg.getFirstByCapability(ProjectorControl.class);
        projector.powerOn();
        projector.connectInput("HDMI-1");

        LightingControl lights = reg.getFirstByCapability(LightingControl.class);
        lights.setBrightness(60);

        CoolingControl ac = reg.getFirstByCapability(CoolingControl.class);
        ac.setTemperatureC(24);

        AttendanceRead scan = reg.getFirstByCapability(AttendanceRead.class);
        System.out.println("Attendance scanned: present=" + scan.scanAttendance());
    }

    public void endClass() {
        System.out.println("Shutdown sequence:");
        reg.getFirstByCapability(ProjectorControl.class).powerOff();
        reg.getFirstByCapability(LightingControl.class).powerOff();
        reg.getFirstByCapability(CoolingControl.class).powerOff();
    }
}
