public class RandomSimulation {
    public static void main(String[] args) {
        int port = 8080;

        AmazonsServer server = new AmazonsServer(port,100);
        AmazonsClient clientA = new AmazonsClient();
        clientA.registerListener(new MyAIClientListener());
        AmazonsClient clientB = new AmazonsClient();
        clientB.registerListener(new RandomAIClientListener("ClientB"));
//        clientB.registerListener(new TestAIClientListener());
        clientA.connect(8080);
        clientB.connect(8080);
    }
}
