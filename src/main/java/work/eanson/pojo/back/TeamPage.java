package work.eanson.pojo.back;

import work.eanson.pojo.TeamUser;

public class TeamPage {
    private Integer counts;

    private TeamUser teamUser;

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public TeamUser getTeamUser() {
        return teamUser;
    }

    public void setTeamUser(TeamUser teamUser) {
        this.teamUser = teamUser;
    }
}
