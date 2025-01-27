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
package server.life;

import constants.GameConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import client.inventory.MapleInventoryType;
import database.DatabaseConnection;
import java.io.File;
import java.util.Map.Entry;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructFamiliar;

public class MapleMonsterInformationProvider {

    private static final MapleMonsterInformationProvider instance = new MapleMonsterInformationProvider();
    private final Map<Integer, ArrayList<MonsterDropEntry>> drops = new HashMap<Integer, ArrayList<MonsterDropEntry>>();
    private final List<MonsterGlobalDropEntry> globaldrops = new ArrayList<MonsterGlobalDropEntry>();
    private static final MapleDataProvider stringDataWZ = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));
//    private static final MapleData mobStringData = stringDataWZ.getData("MonsterBook.img");

    public static MapleMonsterInformationProvider getInstance() {
        return instance;
    }

    public List<MonsterGlobalDropEntry> getGlobalDrop() {
        return globaldrops;
    }

    public void load() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            final Connection con = DatabaseConnection.getConnection();
            ps = con.prepareStatement("SELECT * FROM drop_data_global WHERE chance > 0");
            rs = ps.executeQuery();

            while (rs.next()) {
                globaldrops.add(
                        new MonsterGlobalDropEntry(
                        rs.getInt("itemid"),
                        rs.getInt("chance"),
                        rs.getInt("continent"),
                        rs.getByte("dropType"),
                        rs.getInt("minimum_quantity"),
                        rs.getInt("maximum_quantity"),
                        rs.getInt("questid")));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT dropperid FROM drop_data");
            List<Integer> mobIds = new ArrayList<Integer>();
            rs = ps.executeQuery();
            while (rs.next()) {
                if (!mobIds.contains(rs.getInt("dropperid"))) {
                    loadDrop(rs.getInt("dropperid"));
                    mobIds.add(rs.getInt("dropperid"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving drop" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
    }

    public ArrayList<MonsterDropEntry> retrieveDrop(final int monsterId) {
        return drops.get(Integer.valueOf(monsterId));
    }

    private void loadDrop(final int monsterId) {
        final ArrayList<MonsterDropEntry> ret = new ArrayList<MonsterDropEntry>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(monsterId);
            if (mons == null) {
                return;
            }
            ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM drop_data WHERE dropperid = ?");
            ps.setInt(1, monsterId);
            rs = ps.executeQuery();
            int itemid;
            int chance;
            int min;
            int max;
//            boolean doneMesos = false;
            while (rs.next()) {
                itemid = rs.getInt("itemid");
                chance = rs.getInt("chance");
                min = rs.getInt("minimum_quantity");
                max = rs.getInt("maximum_quantity");
                if (!mons.isBoss() || mons.getLevel() <= 100) {
                    if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
                        chance /= 8; // 裝備
                    }
                    if (itemid / 10000 == 204) {
                        chance /= 5; // 卷軸
                    }
                    if (itemid / 1000 == 2060 || itemid / 1000 == 2061) {
                        max = 30; // 箭矢跟弩箭矢
                    }
                    if (itemid / 10000 == 207) {
                        chance /= 5; // 飛鏢
                    }
                    if (itemid / 10000 == 233) {
                        chance = 50; // 子彈
                    }
                    if (itemid / 10000 == 229) {
                        chance /= 25; // 技能書
                    }
                    if (itemid / 10000 == 238) {
                        chance = 0; // 怪物圖鑑卡片
                    }
                    if (itemid >= 2022157 && itemid <= 2022163) {
                        chance *= 20; // 怪物擂台賽道具
                    }
                    if (itemid >= 2022164 && itemid <= 2022166) {
                        chance = 10; // 怪物擂台賽道具
                    }
                    if (itemid >= 2022174 && itemid <= 2022178) {
                        chance *= 20; // 怪物擂台賽道具
                    }
                    if (itemid / 10000 == 401 || itemid / 10000 == 402 || itemid / 10000 == 404) {
                        chance /= 10; // 礦石
                    }
                    if (itemid / 10000 == 406) {
                        chance = 100; // 召喚石 魔法石
                    }
                    if (itemid / 10000 == 407) {
                        chance = 0; // 魔法粉
                    }
                    if (itemid == 4000244) {
                        chance /= 25; // 龍魂石
                    }
                    if (itemid == 4000245) {
                        chance /= 25; // 閃耀的龍鱗
                    }
                    if (itemid == 4003004) {
                        chance /= 5; // 堅硬的羽毛
                    }
                    if (itemid == 4003005) {
                        chance /= 5; // 柔軟的羽毛
                    }
                    if (itemid / 1000 == 4130) {
                        chance = 50; // 催化劑
                    }
                    if (itemid / 1000 == 4131) {
                        chance /= 10; // 製作捲 1張1w的
                    }
                }
                if (mons.isBoss() && (itemid / 10000 == 200 || itemid / 10000 == 201 || itemid / 10000 == 202) && monsterId / 10000 != 941) {
                    min = 2;
                    max = 5;
                    if (monsterId == 8500002 || monsterId == 8510000 || monsterId == 8800002 || monsterId == 8810018) {
                        min = 20;
                        max = 40;
                    }
                }
                if ((itemid / 10000 == 206)) {
                    min = 5;
                    max = 15;
                }
                ret.add(new MonsterDropEntry(
                    itemid,
                    chance,
                    min,
                    max,
                    rs.getInt("questid")));
/*                if (itemid == 0) {
                    doneMesos = true;
                }*/
            }
            if (monsterId / 100000 != 93) {
                if (mons.isBoss()) {
                    ret.add(new MonsterDropEntry(0, 999999, mons.getLevel() * 50, mons.getLevel() * 150, 0));
                    ret.add(new MonsterDropEntry(0, 999999, mons.getLevel() * 50, mons.getLevel() * 150, 0));
                    ret.add(new MonsterDropEntry(0, 999999, mons.getLevel() * 50, mons.getLevel() * 150, 0));
                    ret.add(new MonsterDropEntry(0, 999999, mons.getLevel() * 50, mons.getLevel() * 150, 0));
                    ret.add(new MonsterDropEntry(0, 999999, mons.getLevel() * 50, mons.getLevel() * 150, 0));
                } else {
                    ret.add(new MonsterDropEntry(0, 600000, mons.getLevel() * 2 + Randomizer.nextInt(mons.getLevel()), mons.getLevel() * 4, 0));
                }
            }
/*            if (!doneMesos) {
                addMeso(mons, ret);
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
                return;
            }
        }
        drops.put(Integer.valueOf(monsterId), ret);
    }

/*    public void addExtra() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (Entry<Integer, ArrayList<MonsterDropEntry>> e : drops.entrySet()) {
            for (int i = 0; i < e.getValue().size(); i++) {
                if (e.getValue().get(i).itemId != 0 && !ii.itemExists(e.getValue().get(i).itemId)) {
                    e.getValue().remove(i);
                }
            }
            final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(e.getKey());
            Integer item = ii.getItemIdByMob(e.getKey());
            if (item != null && item.intValue() > 0) {
                e.getValue().add(new MonsterDropEntry(item.intValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
            }
            StructFamiliar f = ii.getFamiliarByMob(e.getKey().intValue());
            if (f != null) {
                e.getValue().add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
            }
        }
        for (Entry<Integer, Integer> i : ii.getMonsterBook().entrySet()) {
            if (!drops.containsKey(i.getKey())) {
                final MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(i.getKey());
                ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                e.add(new MonsterDropEntry(i.getValue().intValue(), mons.isBoss() ? 1000000 : 10000, 1, 1, 0));
                StructFamiliar f = ii.getFamiliarByMob(i.getKey().intValue());
                if (f != null) {
                    e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                }
                addMeso(mons, e);
                drops.put(i.getKey(), e);
            }
        }
        for (StructFamiliar f : ii.getFamiliars().values()) {
            if (!drops.containsKey(f.mob)) {
                MapleMonsterStats mons = MapleLifeFactory.getMonsterStats(f.mob);
                ArrayList<MonsterDropEntry> e = new ArrayList<MonsterDropEntry>();
                e.add(new MonsterDropEntry(f.itemid, mons.isBoss() ? 10000 : 100, 1, 1, 0));
                addMeso(mons, e);
                drops.put(f.mob, e);
            }
        }
    }*/

/*    public void addMeso(MapleMonsterStats mons, ArrayList<MonsterDropEntry> ret) {
        final double divided = (mons.getLevel() < 100 ? (mons.getLevel() < 10 ? (double) mons.getLevel() : 10.0) : (mons.getLevel() / 10.0));
        final int max = mons.isBoss() && !mons.isPartyBonus() ? (mons.getLevel() * mons.getLevel()) : (mons.getLevel() * (int) Math.ceil(mons.getLevel() / divided));
        for (int i = 0; i < mons.dropsMeso(); i++) {
            ret.add(new MonsterDropEntry(0, mons.isBoss() && !mons.isPartyBonus() ? 1000000 : (mons.isPartyBonus() ? 100000 : 200000), (int) Math.floor(0.66 * max), max, 0));
        }
    }*/

    public void clearDrops() {
        drops.clear();
        globaldrops.clear();
        load();
//        addExtra();
    }

    public boolean contains(ArrayList<MonsterDropEntry> e, int toAdd) {
        for (MonsterDropEntry f : e) {
            if (f.itemId == toAdd) {
                return true;
            }
        }
        return false;
    }

/*    public int chanceLogic(int itemId) { //not much logic in here. most of the drops should already be there anyway.
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
            return 50000; //with *10
        } else if (GameConstants.getInventoryType(itemId) == MapleInventoryType.SETUP || GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH) {
            return 500;
        } else {
            switch (itemId / 10000) {
                case 204:
                case 207:
                case 233:
                case 229:
                    return 500;
                case 401:
                case 402:
                    return 5000;
                case 403:
                    return 5000; //lol
            }
            return 20000;
        }
    }*/
    //MESO DROP: level * (level / 10) = max, min = 0.66 * max
    //explosive Reward = 7 meso drops
    //boss, ffaloot = 2 meso drops
    //boss = level * level = max
    //no mesos if: mobid / 100000 == 97 or 95 or 93 or 91 or 90 or removeAfter > 0 or invincible or onlyNormalAttack or friendly or dropitemperiod > 0 or cp > 0 or point > 0 or fixeddamage > 0 or selfd > 0 or mobType != null and mobType.charat(0) == 7 or PDRate <= 0
}
