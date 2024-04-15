import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class Memory extends Vector<Process>{//Memory에는 여러개의 프로세스들이 존재한다.

    private CPU.Register MAR;
    private CPU.Register MBR;
    Scanner sc;


    private Process currentProcess;//현재 실행중인 프로세스

    public Memory() {
    }
    public Process getCurrentProcess() {
        return currentProcess;
    }

    public void setCurrentProcess(Process process) {
        currentProcess = process;
    }

    public void updateCurruentProcess() {
        currentProcess = this.get(0);
    }

    public void load(CPU.Register MAR, CPU.Register MBR) {
        currentProcess.load(MAR,MBR);
    }


    public void store(int processNumber,int index, int value) {
        Process processtoLoad = this.get(processNumber);
        processtoLoad.store(index,value);
    }

    public void associate(CPU.Register MAR, CPU.Register MBR) {
        //CPU에 MAR, MBR을 장착시켜 연결시킴
        this.MAR = MAR;
        this.MBR = MBR;

    }
}
