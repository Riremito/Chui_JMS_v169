package server;

import client.SkillFactory;
import client.inventory.MapleInventoryIdentifier;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import handling.world.guild.MapleGuild;
import java.sql.PreparedStatement;
import server.Timer.*;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.quest.MapleQuest;

public class Start {

//    public static long startTime = System.currentTimeMillis();
    public static final Start instance = new Start();
//    public static AtomicInteger CompletedLoadingThreads = new AtomicInteger(0);

    public void run() throws InterruptedException {
/*        if (Boolean.parseBoolean(ServerProperties.getProperty("net.sf.odinms.world.admin")) || ServerConstants.Use_Localhost) {
	    ServerConstants.Use_Fixed_IV = false;
            System.out.println("[!!! Admin Only Mode Active !!!]");
        }*/
        try {
            final PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0");
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }
//        System.out.println("[" + ServerProperties.getProperty("net.sf.odinms.login.serverName") + "] Revision: " + SuperGMCommand.Rev.getRevision());
        World.init();
        WorldTimer.getInstance().start();
	EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
//        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
	PingTimer.getInstance().start();
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll(); //(this);
//        MapleFamily.loadAll(); //(this);
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.initQuests();
//        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
//        BattleConstants.init();
        MapleItemInformationProvider.getInstance().runItems();
        SkillFactory.load();
        LoginInformationProvider.getInstance();
//        RandomRewards.load();
//        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        MobSkillFactory.getInstance();
//        SpeedRunner.loadSpeedRuns();
//        MTSStorage.load();
        MapleInventoryIdentifier.getInstance();
        CashItemFactory.getInstance().initialize();
//        MapleServerHandler.initiate();

        System.out.println("[Loading Login]");
        LoginServer.run_startup_configurations();
        System.out.println("[Login Initialized]");

        System.out.println("[Loading Channel]");
        ChannelServer.startChannel_Main();
        System.out.println("[Channel Initialized]");

        System.out.println("[Loading CS]");
        CashShopServer.run_startup_configurations();
        System.out.println("[CS Initialized]");

        //threads.
//        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
//        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        World.registerRespawn();
	//ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop(); //start it off
        ShutdownServer.registerMBean();
//        ServerConstants.registerMBean();
//        PlayerNPC.loadAll();// touch - so we see database problems early...
//        MapleMonsterInformationProvider.getInstance().addExtra();
        RankingWorker.run();
        LoginServer.setOn();
        System.out.println("スタート完了");
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            ShutdownServer.getInstance().run();
            ShutdownServer.getInstance().run();
        }
    }

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }
}
