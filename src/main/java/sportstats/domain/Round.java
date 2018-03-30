/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.domain;

import sportstats.domain.dao.GameDao;
import sportstats.domain.dao.RoundDao;
import sportstats.domain.dao.SeasonDao;

/**
 *
 * @author Rebecca
 */
public class Round {
    private final RoundDao dao;
    
    public Round() {
        this(new RoundDao());
    }
    
    private Round(RoundDao dao) {
        this.dao = dao;
    }
    
    public static Round of(RoundDao dao) {
        return dao == null ? null : new Round(dao);
    }
    
    public Season getSeason() {
        return Season.of(dao.parent(SeasonDao.class));
    }
    
    public void setSeason(Season season) {
        season.setAsChild(dao);
    }

    public void setAsChild(GameDao gameDao) {
        gameDao.setParent(dao);
    }
    
}
