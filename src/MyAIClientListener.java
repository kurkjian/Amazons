import netgame.client.Client;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyAIClientListener extends AIClientListener {
    public static int wins = 0;
    public static int played = 0;
    private AmazonsRules temp;

    public MyAIClientListener() {
        super("Sandbox v0.4.69");
    }

    @Override
    public void yourTurn(AmazonsRules rules, Client<AmazonsState, AmazonsRules> client) {
        List<ConsideredMove> moves = getBestMoves(getMyPlayerNumber(),rules);
        for(int i = 0; i < 10; i++)
        {
            if(i >= moves.size())
            {
                break;
            }
            ConsideredMove cm = moves.get(i);
            cm.score += lookAhead(cm.rules, 2);
        }
        Collections.sort(moves);
//        System.out.println(moves);
        client.send(C.MOVE + C.SPACE + moves.get(0).move);
    }

    @Override
    public void gameover(String reason) {
        int num = getMyPlayerNumber();
        int winner = Integer.parseInt(reason.split(" ")[1]);
        if (num == winner) {
            wins++;
        }
        played++;
        System.out.println(wins + "/" + played);
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

    public int lookAhead(AmazonsRules rules, int depth) {
        if(depth == 0)
        {
            return 0;
        }
        int maxSum = 0;
        AmazonsRules r = rules.getCopy();
        List<ConsideredMove> moves = getBestMoves(getMyPlayerNumber(),r);
        if(moves.size() > 0)
        {
            maxSum += moves.get(0).score;
            for(int i = 0; i < 5; i++)
            {
                if(i == moves.size())
                {
                    break;
                }
                ConsideredMove cm = moves.get(i);
                String[] move = cm.move.split(" ");
                AmazonsRules copy = r.getCopy();
                copy.move(Integer.parseInt(move[0]),Integer.parseInt(move[1]),Integer.parseInt(move[2]),Integer.parseInt(move[3]),Integer.parseInt(move[4]),Integer.parseInt(move[5]));
                cm.rules = copy;
                maxSum += lookAhead(cm.rules, depth - 1);
            }
        }
//        System.out.println(maxSum);
        return maxSum;
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
                                if(theirMoves == 0)
                                {
                                    theirMoves *= -100;
                                }
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

class ConsideredMove implements Comparable<ConsideredMove> {
    int score;
    String move;
    AmazonsRules rules;

    public ConsideredMove(int s, String m, AmazonsRules r) {
        score = s;
        move = m;
        rules = r;
    }

    public int compareTo(ConsideredMove other) {
        return other.score - this.score;
    }

    public String toString() {
        return "" + score;
    }
}
