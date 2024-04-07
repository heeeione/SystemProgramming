import java.util.HashMap;

public class Memory {
    private HashMap<Integer, String> data;

    public Memory() {
        data = new HashMap<>();
    }

    public void initialize() {
    }

    public void store(int address, String instruction) {
        data.put(address, instruction); // 주어진 주소에 값을 저장
       // System.out.println(data);
    }

    public int load(int address) {
        if (address >= 0 && address < data.size()) {
            String instruction = data.get(address);
            // 명령어에 포함된 0x를 제거하고 정수로 변환
            instruction = instruction.replaceAll("0x", "");
            System.out.println(instruction);
            return Integer.parseInt(instruction, 16); // 주어진 주소에서 값을 로드
        }
        return 0; // 유효하지 않은 주소면 0 반환
    }

    public void finish() {
    }
}