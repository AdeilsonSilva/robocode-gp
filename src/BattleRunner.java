
import java.util.Arrays;
import robocode.BattleResults;
import robocode.control.*;
import robocode.control.events.*;

public class BattleRunner {

    RobocodeEngine engine;
    BattlefieldSpecification battlefield;
    BattleObserver battleObserver;

    final static int BATTLE_HANDICAP = RunGP.BATTLE_HANDICAP;

    public BattleRunner() {
        engine = new RobocodeEngine(new java.io.File("/home/adeilson/robocode/"));
        battleObserver = new BattleObserver();
        engine.addBattleListener(battleObserver);
        engine.setVisible(false);
        battlefield = new BattlefieldSpecification(800, 600);
    }

    public double[] runBatchWithSamples(String bots[], String samples[], int rounds) {
        double fitnesses[] = new double[bots.length];
        String bot, opponent;
        BattleResults[] results;

        System.out.println("Running battles against sample batch");
        for (int i = 0; i < bots.length; i++) {
            double fitnessScore = 0;
            for (String sample : samples) {
                bot = bots[i];
                opponent = sample;
                RobotSpecification[] selectedBots = engine.getLocalRepository(bot + ", " + opponent);
                BattleSpecification battleSpec = new BattleSpecification(rounds, battlefield, selectedBots);
                engine.runBattle(battleSpec, true);
                results = battleObserver.getResults();
                int myBot = (results[0].getTeamLeaderName().equals(bots[i]) ? 0 : 1);
                int opBot = (myBot == 1 ? 0 : 1);
                int botScore = results[myBot].getScore();
                double totalScore = botScore + results[opBot].getScore();
                double roundFitness = (botScore + BATTLE_HANDICAP) / (totalScore + BATTLE_HANDICAP);
                fitnessScore += roundFitness;
            }
            fitnesses[i] = fitnessScore / samples.length;	// take average of each round score

        }

        return fitnesses;
    }

    public double[] runBatchWithCoevolution(String bots[], int rounds) {
        double fitnesses[] = new double[bots.length];
        return fitnesses;
    }

}

// based on example from Robocode Control API JavaDocs
class BattleObserver extends BattleAdaptor {

    robocode.BattleResults[] results;

    @Override
    public void onBattleCompleted(BattleCompletedEvent e) {
        results = e.getIndexedResults();
    }

    @Override
    public void onBattleError(BattleErrorEvent e) {
        System.out.println("Error running battle: " + e.getError());
    }

    public BattleResults[] getResults() {
        return results;
    }

}
