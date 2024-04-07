public class MicroProcessor {
    private boolean bPowerOn;
    private CPU cpu;
    private Memory memory;
    private Bus bus;
    private Loader loader;

    public MicroProcessor() {
        this.bPowerOn = true;
        this.cpu = new CPU();
        this.memory = new Memory();
        this.bus = new Bus();
        this.loader = new Loader(memory);
        loader.loadFromFile("data/1to10.txt");

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
