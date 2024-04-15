public class CPU {
    public enum EState{ //States of CPU
        eStopped,
        eRunning,
        ePause
    }


    public enum EOperator{
        LOD(0x01),
        STO(0x03),
        ADD(0x05),
        EQ(0x0a),
        NOT(0x0b),
        JMP(0x10),
        GZJ(0x11),
        BZJ(0x12),
        EZJ(0x13),
        DL(0x20),
        PRT(0x50),
        ;

        private int machineCode;

        private EOperator(int machineCode) {
            this.machineCode = machineCode;
        }


        private static EOperator getOperator(int machineCode) {
            for (EOperator operator : EOperator.values()) {
                if (operator.machineCode == machineCode) {
                    return operator;
                }
            }
            return null;
        }

    }

    //state
    public static EState eState;

    private int index;
    //private int value; AC에 담아놓는 것으로 대체


    private Memory memory;
    public void associate(Memory memory) {this.memory = memory;}

    private Loader loader;
    public void associate(Loader loader) {this.loader = loader;}

    //Registers
    private IR IR;
    public Register MAR,MBR;
    public Register AC;
    public Register PC;
    public Register SP; //Stack Pointer

    private int currentProcessNumber;

    //Constructor - When CPU is run
    public CPU() {
        IR = new IR();
        MAR = new Register();
        MBR = new Register();
        PC = new Register();
        AC = new Register();
        SP = new Register();
    }



    private void fetch() {
        System.out.println("********fetch**********");

        // MAR = CPU의 PC + 프로세스의 CS값
        MAR.setValue(PC.getValue()+memory.getCurrentProcess().CS);

        // MBR = memory.load(); (MBR = Memory[MAR])
        MBR.setValue(memory.getCurrentProcess().segment[MAR.getValue()]);

        // IR = MBR
        IR.setValue(MBR.getValue());
    }


    private void decode() {
        System.out.println("--------decode---------");
        //(Operand가 주소일 경우 해당 주소의 데이터를 가져오고, 데이터일 경우 이 데이터 가지고 명령 실행)

        int mode = (IR.getValue()>>>15);//앞자리만 남겨서 AODcode의 내용이 값인지, 데이터인지 판별
        IR.setMode(mode);

        switch(IR.getMode()) {
            case 0: //데이터일 경우
                int instruction = (IR.getValue());

                int Opcode = instruction>>>8; //2진수로 바꿔서 8칸 오른쪽으로 밀면 0x00 처럼 Opcode만 남음
                IR.setOperator(Opcode);

                int AODcode = instruction - (Opcode<<8);//Opcode를 8칸 왼쪽으로 민다음 오른쪽을 0으로 채우고. 그걸 instruction에서 빼면 AODcode만 남음.

                IR.setOperand(AODcode);


                break;
            //주소일 경우
            case 2: //Global Variable
                instruction = (IR.getValue()-0x10000); //맨 앞에 AddMode 1을 뺀 Opcode+AODcode

                Opcode = instruction>>>8;
                IR.setOperator(Opcode);

                AODcode = instruction - (Opcode<<8);
                IR.setOperand(AODcode);
                break;
            case 4: //Local Variable
                instruction = (IR.getValue()-0x20000); //맨 앞에 AddMode 1을 뺀 Opcode+AODcode

                Opcode = instruction>>>8;
                IR.setOperator(Opcode);

                AODcode = instruction - (Opcode<<8);
                IR.setOperand(AODcode);
                break;
            case 6: //Member Variable
                instruction = (IR.getValue()-0x30000); //맨 앞에 AddMode 1을 뺀 Opcode+AODcode

                Opcode = instruction>>>8;
                IR.setOperator(Opcode);

                AODcode = instruction - (Opcode<<8);
                IR.setOperand(AODcode);
                break;
            default:
                break;
        }

        System.out.println("Mode: "+IR.getMode());
        System.out.println("Operator: "+IR.getOperator());
        System.out.println("Operand: "+IR.getOperand());
    }

    private void execute() {
        System.out.println("========execute========");
        switch (EOperator.getOperator(IR.getOperator())) {
            case LOD :
                if(IR.getMode()==0) {//값이면
                    MBR.setValue(IR.getOperand());//Load to MBR
                    AC.setValue(MBR.getValue());//AC = MBR
                    System.out.println("LOD. MBR = "+MBR.getValue()+", AC = "+AC.getValue());
                }
                else {
                    index = IR.getOperand();
                    //전역변수 - DS에 저장된 값 가져오기 MBR = Memory[DS+MAR(index)]
                    if(IR.getMode()==2) {
                        MBR.setValue(memory.getCurrentProcess().segment[memory.getCurrentProcess().DS+index]);
                        System.out.print("LOD From DS["+index+"]");
                    }
                    //지역변수 MBR = Memory[SS+MAR]
                    if(IR.getMode()==4) {
                        MBR.setValue(memory.getCurrentProcess().segment[memory.getCurrentProcess().SS+index]);
                        System.out.print("LOD From SS["+index+"]");
                    }
                    //멤버변수 MBR = Memory[HS+MAR]
                    if(IR.getMode()==6) {
                        MBR.setValue(memory.getCurrentProcess().segment[memory.getCurrentProcess().HS+index]);
                        System.out.print("LOD From DS["+index+"]");
                    }
                    AC.setValue(MBR.getValue());//AC = MBR
                    System.out.println(", AC: "+AC.getValue());
                }
                break;
            case STO :
                index = IR.getOperand();
                MBR.setValue(AC.getValue()); //AC -> MBR
                //전역변수 주소일 경우 memory[DS+index]에 해당하는 위치에 저장
                if(IR.getMode()==2) {
                    memory.store(currentProcessNumber,index+memory.getCurrentProcess().DS,MBR.getValue());
                    System.out.println("STO to DS["+index+"] = "+AC.getValue());
                }
                //지역변수 주소일 경우 memory[SS+index]에 해당하는 위치에 저장
                if(IR.getMode()==4) {
                    memory.store(currentProcessNumber,index+memory.getCurrentProcess().SS,MBR.getValue());
                    System.out.println("STO to SS["+index+"] = "+AC.getValue());
                }
                //멤버변수 memory[HS+index]
                if(IR.getMode()==6) {
                    memory.store(currentProcessNumber,index+memory.getCurrentProcess().HS,MBR.getValue());
                    System.out.println("STO to HS["+index+"] = "+AC.getValue());
                }
                break;
            case ADD :
                if(IR.getMode()==0) AC.setValue(AC.getValue()+IR.getOperand());//값일 경우 그대로 더하고
                //전역변수 주소일 경우 그 주소에 해당하는 값을 DS에서 찾아와서 더함
                if(IR.getMode()==2) AC.setValue(AC.getValue()+memory.getCurrentProcess().segment[memory.getCurrentProcess().DS+IR.getOperand()]);
                //지역변수 주소일 SS에서 찾아와서 더함
                if(IR.getMode()==4) AC.setValue(AC.getValue()+memory.getCurrentProcess().segment[memory.getCurrentProcess().SS+IR.getOperand()]);
                //멤버변수 HS
                if(IR.getMode()==6) AC.setValue(AC.getValue()+memory.getCurrentProcess().segment[memory.getCurrentProcess().HS+IR.getOperand()]);
                System.out.println("ADD. current value in AC: "+AC.getValue());
                break;
            case EQ :
                if(IR.getMode()==0) {
                    if(AC.getValue()==IR.getOperand()) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==2) {
                    if(AC.getValue()==memory.getCurrentProcess().segment[memory.getCurrentProcess().DS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==4) {
                    if(AC.getValue()==memory.getCurrentProcess().segment[memory.getCurrentProcess().SS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==6) {
                    if(AC.getValue()==memory.getCurrentProcess().segment[memory.getCurrentProcess().HS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                break;
            case NOT :
                if(IR.getMode()==0) {
                    if(AC.getValue()!=IR.getOperand()) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==2) {
                    if(AC.getValue()!=memory.getCurrentProcess().segment[memory.getCurrentProcess().DS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==4) {
                    if(AC.getValue()!=memory.getCurrentProcess().segment[memory.getCurrentProcess().SS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                if(IR.getMode()==6) {
                    if(AC.getValue()!=memory.getCurrentProcess().segment[memory.getCurrentProcess().HS+IR.getOperand()]) AC.setValue(0);
                    else AC.setValue(1);
                }
                break;
            case JMP :
                PC.setValue(IR.getOperand());
                System.out.println("jump to "+PC.getValue());
                break;
            case GZJ :
                if(AC.getValue()>0) {
                    PC.setValue(IR.getOperand()); // operand 값으로 jump
                    System.out.println("GZJ to "+PC.getValue());
                }
                break;
            case BZJ :
                if(AC.getValue()<0) {
                    PC.setValue(IR.getOperand());
                    System.out.println("BZJ to "+PC.getValue());
                }
                break;
            case EZJ :
                if(AC.getValue()==0) {
                    PC.setValue(IR.getOperand()); // operand 값으로 jump
                    System.out.println("EZJ to "+PC.getValue());
                }
                break;
            case DL :
                if(IR.getMode()==4) {
                    index = IR.getOperand();
                    //현재 SP값 저장
                    memory.store(currentProcessNumber,index+memory.getCurrentProcess().SS,SP.getValue());
                    System.out.print("Dinamic Link at "+SP.getValue());
                    //SP += 4
                    SP.setValue(SP.getValue()+4);
                    System.out.println(". Current Stack Pointer = "+SP.getValue());
                }
                break;

            case PRT :
                if(IR.getMode()==0) System.out.println("|------------------------------------------------------------------------PRT. "+IR.getOperand()+"------------------------------------------------------------------------|");
                if(IR.getMode()==2) System.out.println("|------------------------------------------------------------------------PRT. "+memory.getCurrentProcess().segment[memory.getCurrentProcess().DS+IR.getOperand()]+"------------------------------------------------------------------------|");
                if(IR.getMode()==4) System.out.println("|------------------------------------------------------------------------PRT. "+memory.getCurrentProcess().segment[memory.getCurrentProcess().SS+IR.getOperand()]+"------------------------------------------------------------------------|");
                if(IR.getMode()==6) System.out.println("|------------------------------------------------------------------------PRT. "+memory.getCurrentProcess().segment[memory.getCurrentProcess().HS+IR.getOperand()]+"------------------------------------------------------------------------|");
                break;
            default:
                break;
        }

        PC.setValue(PC.getValue()+1);

    }

    public void contextSwitch(String fileName) {
        //기존의 PC값, AC값, SP값, index(CS 몇줄까지 진행했는지) 저장
        memory.getCurrentProcess().setPCValue(PC.getValue());
        memory.getCurrentProcess().setACValue(AC.getValue());
        memory.getCurrentProcess().setSPValue(SP.getValue());
        memory.getCurrentProcess().setIndex(this.index);

        //새로운 프로세스 실행
        loader.runProcess(fileName);
        //새로운 프로세스의 PC값, AC값, SP값 index 로드
        PC.setValue(memory.getCurrentProcess().getPCValue());
        AC.setValue(memory.getCurrentProcess().getACValue());
        SP.setValue(memory.getCurrentProcess().getSPValue());
        this.index = memory.getCurrentProcess().getIndex();
        //현재 실행중인 프로세스 넘버 변경
        currentProcessNumber = memory.getCurrentProcess().getProcessNumber();

        //flush - IO버퍼 초기화
        memory.IOBuffer.clear();

    }


    public void startNonBlockingMode() {
        CPU.eState = EState.eRunning; //cpu가 시작되면 CPU상태를 Running으로
        //multiThreading - CPU의 사이클과, 인터럽트 입력은 각각 독립적으로 작동
        //CPU가 돌아가는 와중에
        Thread CPUcycle = new Thread(new Runnable() {
            @Override
            public void run() {
                CPU.this.run();
            }
        });
        //인터럽트 입력도 돌아가야함
        Thread IOinturrupt = new Thread(new Runnable() {
            @Override
            public void run() {
                ioDevice.stanbyInturrupt();
            }
        });

        //start Threads
        CPUcycle.start();
        IOinturrupt.start();

    }

    public void startBlockingMode() {
        CPU.eState = EState.eRunning;
        this.runWithNoTimer();
    }

    public void run() {
        //특정 시간주기로 사이클 돌기 위해 설정 - 인터럽트 주기 위해(안그러면 바로 다 실행버려서 인터럽트 칠 시간이 없음)
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long interval = 1000; // 1초 간격으로 반복 실행

        while (CPU.eState == EState.eRunning) {
            //2초마다 반복문 동작
            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= interval) {
                this.fetch();
                this.decode();
                this.execute();
                startTime = System.currentTimeMillis();
            }
        }
    }

    public void runWithNoTimer() {
        while (CPU.eState == EState.eRunning) {
            this.fetch();
            this.decode();
            this.execute();
        }
    }


    public class Register{
        int value;

        public Register() {
            value = 0;
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(Integer value) {
            this.value = value;

        }

    }

    private class IR extends Register {
        private int mode;
        private int Operator;
        private int operand;

        public void setMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return this.mode;
        }

        public int getOperator() {
            return Operator;
        }
        public void setOperator(int opcode) {
            this.Operator = opcode;
        }
        public int getOperand() {
            return operand;
        }
        public void setOperand(int operand) {
            this.operand = operand;
        }
    }

}
