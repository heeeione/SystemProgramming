public class Main {


    private MicroProcessor microprcessor;


    private Main( ) {
        this.microprcessor = new MicroProcessor();
    }


    private void initialize() {
        this.microprcessor.initialize();
    }


    private void run() {
        this.microprcessor.run();
    }


    private void finish() {
        this.microprcessor.finish();
    }



    public static void main(String[] args) {
        Main main = new Main();
        main.initialize();
        main.run();
        main.finish();
    }



}
