/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.service.games;

import sportstats.service.sports.AddSportService;
import sportstats.service.teams.AddTeamService;
import sportstats.service.seasons.AddSeasonService;
import sportstats.service.leagues.AddLeagueService;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;
import sportstats.db.DbConn;
import sportstats.domain.Game;
import sportstats.domain.League;
import sportstats.domain.Result;
import sportstats.domain.Round;
import sportstats.domain.Season;
import sportstats.domain.Sport;
import sportstats.domain.Team;
import sportstats.rest.shapes.GameShape;
import sportstats.service.ServiceRunner;

/**
 *
 * @author Davik
 */
public class AddResultServiceIT {

    @Test
    public void runService() {

        Sport sport = null;
        Team firstTeam = null;
        Team secondTeam = null;
        League league = null;
        Season season = null;
        Round round = null;
        Game game = null;
        Result result = null;
        try {
            sport = new ServiceRunner<>(new AddSportService("AddResultServiceIT"))
                    .executeWithoutJson();
            firstTeam = new ServiceRunner<>(new AddTeamService("AddResultServiceIT First team", sport.getId()))
                    .executeWithoutJson();
            secondTeam = new ServiceRunner<>(new AddTeamService("AddResultServiceIT Second team", sport.getId()))
                    .executeWithoutJson();
            league = new ServiceRunner<>(new AddLeagueService("AddResultServiceIT League", sport.getId()))
                    .executeWithoutJson();
            season = new ServiceRunner<>(new AddSeasonService(1999L, Boolean.TRUE, league.getId()))
                    .executeWithoutJson();

            List<GameShape> games = new ArrayList<>();
            GameShape gameShape = new GameShape();
            gameShape.awayTeamId = firstTeam.getId();
            gameShape.homeTeamId = secondTeam.getId();
            games.add(gameShape);
            round = new ServiceRunner<>(new AddRoundService(season.getId(), games))
                    .executeWithoutJson();
            
            game = new ServiceRunner<>(new GetGamesByRoundIdService(round.getId()))
                    .executeWithoutJson()
                    .get(0);

            result = new ServiceRunner<>(new AddResultService(game.getId(), 1L, 3L))
                    .executeWithoutJson();

            System.out.println("Created result");
            System.out.println("Game id: " + result.getGameId().toString());
            System.out.println("Score away team: " + result.getScoreAwayTeam().toString());
            System.out.println("Score home team: " + result.getScoreHomeTeam().toString());
        } catch (Exception ex) {
            System.err.println("Something went wrong.");
            ex.printStackTrace(System.err);
            fail();
        } finally {
            DbConn conn = new DbConn();
            System.out.println("Cleaning up...");
            
            conn.open();
            if (result != null && result.getDao().delete()) {
                System.out.println("Deleted result");
            }
            if (game != null && game.getDao().delete()) {
                System.out.println("Deleted game");
            }
            if (round != null && round.getDao().delete()) {
                System.out.println("Deleted round");
            }
            if (season != null && season.getDao().delete()) {
                System.out.println("Deleted season");
            }
            if (league != null && league.getDao().delete()) {
                System.out.println("Deleted league");
            }
            if (firstTeam != null && firstTeam.getDao().delete()) {
                System.out.println("Deleted firstTeam");
            }
            if (secondTeam != null && secondTeam.getDao().delete()) {
                System.out.println("Deleted secondTeam");
            }
            if (sport != null && sport.getDao().delete()) {
                System.out.println("Deleted sport");
            }
            conn.close();
        }
    }
}
