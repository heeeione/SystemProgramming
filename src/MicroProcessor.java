public class MicroProcessor {
    private boolean bPowerOn;
    private CPU cpu;
    private Memory memory;
    private Bus bus;

    public MicroProcessor() {
        this.bPowerOn = true;
        this.cpu = new CPU();
        this.memory = new Memory();
        this.bus = new Bus();

        this.cpu.associate(this.bus);
        this.bus.associate(this.memory);
    }

    public void initialize() {
        bus.initialize();
        cpu.initialize();
        memory.initialize();
    }
    public void run() {
        while(bPowerOn) {
            cpu.run();
        }
    }

    public void finish() {

    }
}
