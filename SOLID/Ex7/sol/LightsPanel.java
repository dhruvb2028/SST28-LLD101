public class LightsPanel implements SmartClassroomDevice, LightingControl {
    @Override public void powerOn() { /* always on */ }
    @Override public void powerOff() { System.out.println("Lights OFF"); }

    @Override public void setBrightness(int pct) { System.out.println("Lights set to " + pct + "%"); }
}
