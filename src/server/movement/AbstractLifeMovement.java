/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.movement;

import java.awt.Point;

public abstract class AbstractLifeMovement implements LifeMovement {

    private Point position;
    private int duration;
    private int newstate;
    private int type;
    private int fh;

    public AbstractLifeMovement(int type, Point position, int duration, int newstate, int fh) {
        super();
        this.type = type;
        this.position = position;
        this.duration = duration;
        this.newstate = newstate;
        if (type != 1 && type != 2 && type != 6 && type != 10 && type != 12 && type != 13 && type != 14 && type != 16 && type != 18 && type != 19 && type != 20 && type != 23 && type != 24) {
            this.fh = fh;
        }
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getNewstate() {
        return newstate;
    }

    @Override
    public int getFh() {
        return fh;
    }

    @Override
    public Point getPosition() {
        return position;
    }
}
