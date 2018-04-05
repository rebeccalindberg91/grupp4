/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.domain;

import com.owlike.genson.annotation.JsonIgnore;
import sportstats.domain.dao.ArenaDao;
import sportstats.domain.dao.GameDao;
import sportstats.domain.dao.ResultDao;
import sportstats.domain.dao.RoundDao;
import sportstats.domain.dao.TeamDao;

/**
 *
 * @author Rebecca
 */
public class Game {
    private final GameDao dao;
    
    public Game() {
        this(new GameDao());
    }
    
    private Game(GameDao dao) {
        this.dao = dao;
    }
    
    public static Game of(GameDao dao) {
        return dao == null ? null : new Game(dao);
    }
    
    public String getName() {
        return getHomeTeam().getName() + " - " + getAwayTeam().getName();
    }
    
    @JsonIgnore //In favor of id
    public Team getHomeTeam() {
        return Team.of(TeamDao.findById(dao.getLong("home_team_id")));
    }
    
    public Long getHomeTeamId() {
        return dao.getLong("home_team_id");
    }

    public void setHomeTeam(Team team) {
        if (team.getId() == null) {
            team.save();
        }
        dao.setLong("home_team_id", team.getId());
    }

    @JsonIgnore //In favor of id
    public Team getAwayTeam() {
        return Team.of(TeamDao.findById(dao.getLong("away_team_id")));
    }

    public Long getAwayTeamId() {
        return dao.getLong("away_team_id");
    }
    
    public void setAwayTeam(Team team) {
        if (team.getId() == null) {
            team.save();
        }
        dao.setLong("away_team_id", team.getId());
    }
    
    @JsonIgnore //To avoid loop while serializing
    public Round getRound() {
        return Round.of(dao.parent(RoundDao.class));
    }
    
    public void setRound(Round round) {
        round.setAsChild(dao);
    }
    
    @JsonIgnore //In favor of getting the id instead
    public Arena getArena() {
        return Arena.of(dao.parent(ArenaDao.class));
    }
    
    public Long getArenaId() {
        return dao.parent(ArenaDao.class).getLongId();
    }
    
    public void setArena(Arena arena) {
        arena.setAsChild(dao);
    }

    public void setAsChild(ResultDao resultDao) {
        resultDao.setParent(dao);
    }
    
    public void save() {
        dao.save();
    }
}   
    
