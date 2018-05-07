/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.service.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.javalite.activejdbc.Base;
import sportstats.domain.TableRow;
import sportstats.service.BaseService;

/**
 *
 * @author Rebecca
 */
public class GetTableByRoundInterval extends BaseService<List<TableRow>> {
    private final Long seasonId;
    private final Long round1;
    private final Long round2;
    
    public GetTableByRoundInterval(Long seasonId, Long round1, Long round2) {
        this.round1 = round1;
        this.round2 = round2;
        this.seasonId = seasonId;
    }
    
   public List<TableRow> execute() {
        List<Map> result = Base.findAll("SELECT\n" +
"	seasons_teams.season_id as season_id,\n" +
"	teams.id AS team_id,\n" +
"	teams.name AS team_name,\n" +
"       rounds.round AS round,\n" +
"    (SELECT COUNT(games.id) FROM games, rounds WHERE (games.away_team_id=teams.id OR games.home_team_id=teams.id) AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_played,\n" +
"    (SELECT COUNT(games.id) FROM games, results, rounds WHERE ((games.away_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team > results.score_home_team)) OR (games.home_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team < results.score_home_team))) AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_won,\n" +
"    (SELECT COUNT(games.id) FROM games, results, rounds WHERE ((games.away_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team = results.score_home_team)) OR (games.home_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team = results.score_home_team))) AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_tied,\n" +
"    (SELECT COUNT(games.id) FROM games, results, rounds WHERE ((games.away_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team < results.score_home_team)) OR (games.home_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team > results.score_home_team))) AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_lost,\n" +
"    (SELECT COUNT(games.id) FROM games, results, rounds WHERE ((games.away_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team > results.score_home_team)) OR (games.home_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team < results.score_home_team))) AND results.win_type > 0 AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_won_ot,\n" +
"    (SELECT COUNT(games.id) FROM games, results, rounds WHERE ((games.away_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team < results.score_home_team)) OR (games.home_team_id=teams.id AND (results.game_id=games.id AND results.score_away_team > results.score_home_team))) AND results.win_type > 0 AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as games_lost_ot,\n" +
"    (SELECT SUM(case when games.away_team_id=teams.id then results.score_away_team else results.score_home_team end)  FROM games, rounds, results WHERE (games.away_team_id=teams.id OR games.home_team_id=teams.id) AND results.game_id=games.id AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as goals,\n" +
"	(SELECT SUM(case when games.away_team_id=teams.id then results.score_home_team else results.score_away_team end)  FROM games, rounds, results WHERE (games.away_team_id=teams.id OR games.home_team_id=teams.id) AND results.game_id=games.id AND games.round_id=rounds.id AND rounds.season_id=seasons_teams.season_id) as goals_against\n" +
"FROM\n" +
"	teams, games, seasons_teams, rounds\n" +
"WHERE\n" +
"	(games.away_team_id=teams.id OR games.home_team_id=teams.id)\n" +
"    AND team_id=seasons_teams.team_id\n" +
"    AND seasons_teams.season_id=?\n" +
"    AND rounds.round BETWEEN ? AND ?\n" +
"    AND games.round_id = rounds.id\n" +
"GROUP BY seasons_teams.season_id, teams.id, games.round_id, rounds.round\n" +
"HAVING games_played > 0\n" +
"ORDER BY round_id, team_name ASC", seasonId, round1, round2);
        
        List<TableRow> table = new ArrayList<>();
        for(Map row : result) {
            TableRow tr = new TableRow();
            tr.addSeasonId((Integer)row.get("season_id"));
            tr.setRound((Integer)row.get("round"));
            tr.setTeamId((Integer)row.get("team_id"));
            tr.setTeamName((String)row.get("team_name"));
            tr.setGamesPlayed((Long)row.get("games_played"));
            tr.setGamesWon((Long)row.get("games_won"));
            tr.setGamesTied((Long)row.get("games_tied"));
            tr.setGamesLost((Long)row.get("games_lost"));
            tr.setGamesWonOT((Long)row.get("games_won_ot"));
            tr.setGamesLostOT((Long)row.get("games_lost_ot"));
            tr.setGoals((BigDecimal)row.get("goals"));
            tr.setGoalsAgainst((BigDecimal)row.get("goals_against"));
            
            //Calculate points according to SHL standards
            long points = 0;
            
            //3 points for every normal win
            for (int i=0; i < tr.getGamesWon(); i++) {
                points += 3;
            }
            
            //2 points for every overtime/shootout win
            for (int i=0; i < tr.getGamesWonOT(); i++) {
                points += 2;
            }
            
            //1 points for every overtime/shootout lost, and tied.
            for (int i=0; i < (tr.getGamesLostOT()+tr.getGamesTied());i++) {
                points += 1;
            }
            //0 points for every normal loss
            
            tr.setPoints(points);
            
            table.add(tr);
        }
        
        return table;
    }
}