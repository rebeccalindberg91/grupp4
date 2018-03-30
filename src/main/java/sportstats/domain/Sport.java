package sportstats.domain;

import com.owlike.genson.annotation.JsonIgnore;
import java.util.List;
import java.util.stream.Collectors;
import sportstats.domain.dao.LeagueDao;
import sportstats.domain.dao.SportDao;

public class Sport {
    
    private final SportDao dao;
    
    public Sport() {
        this(new SportDao());
    }
    
    private Sport(SportDao dao) {
        this.dao = dao;
    }
    
    public static Sport of(SportDao dao) {
        return dao == null ? null : new Sport(dao);
    }
    
    public Long getId() {
        return dao.getLong("id");
    }
    
    public String getName() {
        return dao.getString("name");
    }
    
    public void setName(String name) {
        dao.setString("name", name);
    }
    
    @JsonIgnore
    public List<League> getLeagues() {
        return dao.getAll(LeagueDao.class).stream()
                .map(leagueDao -> League.of(leagueDao))
                .collect(Collectors.toList());
    }

    void setAsChild(LeagueDao leagueDao) {
        leagueDao.setParent(dao);
    }
    
    @Override
    public String toString() {
        return getName() + " (id: " + getId() + ")";
    }
    
    public void save(){
        dao.save();
    }
}
