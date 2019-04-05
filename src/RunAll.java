public class RunAll {

    public static void main(String[] args) {
        int port = 8082;

        new AmazonsServer(port);

        AmazonsClient clientA = new AmazonsClient();
        AmazonsClient clientB = new AmazonsClient();

        clientA.registerListener(new MyAIClientListener());
//  clientA.registerListener(new RandomAIClientListener("ClientA"));
        clientA.registerListener(new GUIListener());

//        clientB.registerListener(new RandomAIClientListener("ClientB"));
//  clientB.registerListener(new MyAIClientListener());
        clientB.registerListener(new TestAIClientListener());
        clientA.connect(port);
        clientB.connect(port);
    }
}
