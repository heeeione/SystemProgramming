import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Loader {
    private Memory memory;

    public Loader(Memory memory) {
        this.memory = memory;
    }

    public void loadFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int address = 0; // 메모리 주소

            while ((line = br.readLine()) != null) {
                memory.store(address++, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
