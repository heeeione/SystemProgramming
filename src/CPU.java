public class CPU {

    int registers[] = new int[ERegisters.values().length];

    private Bus bus;

    public enum EDeviceId{
        eCpu,
        eMemory,
    }
    public enum EOpcode{
        eMove,
        eLoad,
        eStore,
        eAdd,

    }

    enum ERegisters {
        eMAR,
        eMBR,
        ePC,
        eIR,
        eR0,
        eR1
    }

    public CPU() {

    }
    public void associate(Bus bus) {
        this.bus = bus;
    }
    public void initialize() {

    }
    private void move(ERegisters eTarget, ERegisters eSourse) {
        registers[eTarget.ordinal()] = registers[eSourse.ordinal()]; // move
    }
    private int get(ERegisters eRegister) {
        return registers[eRegister.ordinal()];
    }
    private int set(ERegisters eRegister, int value) {
        return registers[eRegister.ordinal()] = value;
    }


    private void excute() {

    }


    public void run() {
        this.fetch();
        this.decode();
        this.excute();
    }
    private void fetch() {
        System.out.println("----------fetch----------");
        move(ERegisters.eMAR, ERegisters.ePC);
        set(ERegisters.eMBR, bus.load(EDeviceId.eMemory, get(ERegisters.eMAR)));
        move(ERegisters.eIR, ERegisters.eMBR);
    }
    private void decode() {
        int opCode = get(ERegisters.eIR) >> 24;
        if (opCode == EOpcode.eMove.ordinal()) {
            int operand1 = get(ERegisters.eIR) & 0x00FF0000;
            operand1 = operand1 >> 16;
            int operand2 = get(ERegisters.eIR) & 0x0000FF00;
            operand2 = operand2 >> 8;
            move(ERegisters.values()[operand1], ERegisters.values()[operand2]);

        } else if (opCode == EOpcode.eAdd.ordinal()) {

        }
    }

    public void finish() {

    }

}
