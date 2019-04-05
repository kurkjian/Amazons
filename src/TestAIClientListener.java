import netgame.client.Client;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TestAIClientListener extends AIClientListener {
    public static int wins = 0;
    public static int played = 0;
    private AmazonsRules temp;

    public TestAIClientListener() {
        super("Makai");
    }

    @Override
    public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
        List<ConsideredMove> moves = getBestMoves(getMyPlayerNumber(),rules);
//        System.out.println(moves);
        client.send(C.MOVE + C.SPACE + moves.get(0).move);
    }

    @Override
    public void gameover(String reason) {

    }
    public int getNumberMoves(int playerNum, AmazonsState state, AmazonsRules rules) {
        int count = 0;
        Point[] pieces = state.getPieces(playerNum);
        for (int i = 0; i < pieces.length; i++) {
            Point p = pieces[i];
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    if (rules.canMove(p.x, p.y, j, k)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public List<ConsideredMove> getBestMoves(int playerNum, AmazonsRules rules) {
        AmazonsState state = rules.getState();
        List<ConsideredMove> moves = new LinkedList<>();
        if(playerNum != state.getTurnHolder())
        {
            rules.setNextTurnHolder();
        }
        Point[] pieces = state.getPieces(playerNum);
        for (int i = 0; i < pieces.length; i++) {
            Point p = pieces[i];
            for (int j = 0; j < 10; j++) {
                inner:
                for (int k = 0; k < 10; k++) {
                    if (rules.getState().getObjectAt(j, k) != Amazons.EMPTY) {
                        continue inner;
                    }
                    if (!rules.isQueenMove(p.x, p.y, j, k) || !rules.canMove(p.x, p.y, j, k)) {
                        continue inner;
                    }
                    for (int a = 0; a < 10; a++) {
                        for (int b = 0; b < 10; b++) {
                            //                        System.out.println("i: " + i + " j: " + j + "k: " + k + "a: " + a + " b: " + b);
                            if (rules.canMove(p.x, p.y, j, k, a, b)) {
                                AmazonsRules copy = rules.getCopy();
                                copy.move(p.x, p.y, j, k, a, b);
                                int myMoves = getNumberMoves(playerNum, copy.getState(), copy);
                                copy.setNextTurnHolder();
                                int theirMoves = getNumberMoves(copy.getState().getTurnHolder(), copy.getState(), copy);
                                copy.setNextTurnHolder();
                                int diff = myMoves - theirMoves;
                                String move = p.x + C.SPACE + p.y + C.SPACE + j + C.SPACE + k + C.SPACE + a + C.SPACE + b;
                                moves.add(new ConsideredMove(diff, move, copy));
                                //                            System.out.println(moves.size());
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(moves);
        return moves;
    }
}