/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sportstats.rest.shapes;

import sportstats.domain.WinType;

/**
 *
 * @author Davik
 */
public class ResultShape {
    public Long gameId;
    public Long scoreAwayTeam;
    public Long scoreHomeTeam;
    //Optional WinType, must match enum exactly. case sensitive
    public WinType winType;
}
