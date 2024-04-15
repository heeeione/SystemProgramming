public class MicroProcessor {
    //한 프로세스의 사이즈 = loader에게 page 단위로 할당받음
    public int[] segment;

    //Bus
    public CPU.Register MAR;
    public CPU.Register MBR;

    //PCB Header
    private String processName;
    private int processNumber;
    private int ACValue;
    private int PCValue;
    private int SPValue; //컨텍스트 스위칭 시 last stack index을 알기 위해 스택포인터 값을 저장
    private int index; //현재 코드가 몇줄까지 진행되었는지 알기 위해

    //Each Segments' Size
    public int DSSize;
    public int CSSize;
    public int SSSize;
    public int HSSize;

    //Each Segments' Start Address
    public int DS;
    public int CS;
    public int SS;
    public int HS;

    public int heapLast;//heap에 객체나 멤버변수 선언 시 어디부터 저장 가능한지 알기 위해

    public Process(String fileName, int processNumber, int DSSize, int CSSize, int SSSize, int HSSize, int pageSize) {
        //initialize PCB Header, Size, relative address
        this.ACValue = 0;
        this.PCValue = 0;
        this.processName = fileName;
        this.processNumber = processNumber;
        this.SPValue = 0;
        this.index = 0;
        this.heapLast = 0;
        this.DSSize = DSSize;
        this.CSSize = CSSize;
        this.SSSize = SSSize;
        this.HSSize = HSSize;
        this.segment = new int[pageSize];
        //상대주소 세팅
        this.DS = 0;
        this.CS = this.DSSize;//만일 DS의 크기가 12라면 0~11 인덱스 까지가 DS니까 12부터 CS
        this.SS = this.DSSize+this.CSSize;
        this.HS = this.DSSize+this.CSSize+this.SSSize;
    }

    public void stackFree(int start, int last) {
        for (int i = start; i < last; i++) {
            this.segment[i] = 0;
        }
    }
    public int getProcessNumber() {
        return this.processNumber;
    }

    public void setProcessNumber(int processNumber) {
        this.processNumber = processNumber;
    }

    public String getProcessName() {
        return this.processName;
    }

    public int getSPValue() {
        return this.SPValue;
    }

    public void setSPValue(int SPvalue) {
        this.SPValue = SPvalue;
    }

    public int getPCValue() {
        return PCValue;
    }
    public void setPCValue(int PCValue) {
        this.PCValue = PCValue;
    }

    public int getACValue() {
        return ACValue;
    }

    public void setACValue(int ACValue) {
        this.ACValue = ACValue;
    }


    public void load(CPU.Register MAR, CPU.Register MBR) {
        int address = MAR.getValue();
        //codeSegment.get(address)
        MBR.setValue(this.segment[CS+address]);
    }

    public void store(int index, int value) {
        this.segment[index] = value;

    }
    public void associate(CPU.Register MAR, CPU.Register MBR) {
        this.MAR = MAR;
        this.MBR = MBR;
    }
    public void setIndex(int index) {
        this.index = index;

    }
    public int getIndex() {
        return this.index;
    }


}
