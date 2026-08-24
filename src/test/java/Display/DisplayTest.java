package Display;

public class DisplayTest {
    public static void main(String[] args) {
        Display display = new LanternaDisplay(new MockController());
        display.run();
    }
}
