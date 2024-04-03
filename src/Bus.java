public class Bus {
    private Memory memory;

    public Bus() {

    }
    public void associate(Memory memory) {
        this.memory = memory;
    }
    public void initialize() {

    }
    public int load(CPU.EDeviceId eDeviceId, int mAR) {
        if (eDeviceId == CPU.EDeviceId.eMemory) {
            return memory.load(mAR);
        }
        return 0;
    }

    public void finish() {

    }

}