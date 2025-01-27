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
package constants;

import java.net.InetAddress;
import server.ServerProperties;

public class ServerConstants /*implements ServerConstantsMBean */{

//    public static boolean TESPIA = false; // true = uses GMS test server, for MSEA it does nothing though

    public static final String IP = ServerProperties.getProperty("net.sf.odinms.channel.net.interface", "127.0.0.1");
//    public static final byte[] Gateway_IP = { (byte) 122, (byte) 116, (byte) 253, (byte) 188 };
//    public static final byte[] Gateway_IP = {(byte) 127, (byte) 0, (byte) 0, (byte) 1};
//    public static final byte[] Gateway_IP = (GameConstants.GMS ? (TESPIA ? new byte[]{(byte) 0x3F, (byte) 0xFB, (byte) 0xD9, (byte) 0x72} : new byte[]{(byte) 0x8, (byte) 0x1F, (byte) 0x62, (byte) 0x34}) : new byte[]{(byte) 0xCB, (byte) 0x74, (byte) 0xC4, (byte) 0x08}); //singapore
    //Inject a DLL that hooks SetupDiGetClassDevsExA and returns 0.

    /*
     * Specifics which job gives an additional EXP to party
     * returns the percentage of EXP to increase
     */
    // Start of Poll
//    public static final boolean PollEnabled = false;
//    public static final String Poll_Question = "Are you mudkiz?";
//    public static final String[] Poll_Answers = {"test1", "test2", "test3"};
    // End of Poll
    
    public static final short MAPLE_VERSION = (short) 169;
    public static final String MAPLE_PATCH = "0";
//    public static final short MAPLE_VERSION = (short) 181;
//    public static final String MAPLE_PATCH = "2";
    
//    public static boolean Use_Fixed_IV = false; // true = disable sniffing, false = server can connect to itself
//    public static boolean Use_Localhost = false; // true = packets are logged, false = others can connect to server
    public static boolean ShowRecvP = true;
    public static boolean ShowSendP = true;
    public static final int MIN_MTS = 100; //lowest amount an item can be, GMS = 110
    public static final int MTS_BASE = 0; //+amount to everything, GMS = 500, MSEA = 1000
    public static final int MTS_TAX = 5; //+% to everything, GMS = 10
    public static final int MTS_MESO = 10000; //mesos needed, GMS = 5000
    public static final int MAXIMUM_CONNECTIONS = 1024;
//    public static final String MASTER_LOGIN = "adminzb69", MASTER = "55$clr0CK15!", MASTER2 = "5701337570";
    public static final String SQL_USER = "root", SQL_PASSWORD = "b9ew5g"/*"v98h8tl3fp"*/;
    //master login is only used in GMS: fake account for localhost only
    //master and master2 is to bypass all accounts passwords only if you are under the IPs below
//    public static final long number1 = (142449577 + 753356065 + 611816275297389857L);
//    public static final long number2 = 1877319832;
//    public static final long number3 = 202227478981090217L;
//    public static final List<String> eligibleIP = new LinkedList<String>(), localhostIP = new LinkedList<String>();

    public static enum PlayerGMRank {

//        NORMAL('@', 0),
        NORMAL('*', 0),
        DONATOR('#', 1),
        SUPERDONATOR('$', 2),
        INTERN('%', 3),
        GM('!', 4),
        SUPERGM('!', 5),
        ADMIN('!', 6);
        private char commandPrefix;
        private int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }
    }

    public static enum CommandType {

        NORMAL(0),
        TRADE(1),
        POKEMON(2);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static final byte[] getIP() {
        try {
            final InetAddress inetAddr = InetAddress.getByName(IP);
            byte[] addr = inetAddr.getAddress();
            return addr;
        } catch (Exception e) {
            return new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1};
        }
    }

/*    public static boolean isEligibleMaster(final String pwd, final String sessionIP) {
        return pwd.equals(MASTER) && isEligible(sessionIP);
    }

    public static boolean isEligible(final String sessionIP) {
        return true;
    }

    public static boolean isEligibleMaster2(final String pwd, final String sessionIP) {
        return pwd.equals(MASTER2) && isEligible(sessionIP);
    }

    public static boolean isIPLocalhost(final String sessionIP) {
        return !Use_Fixed_IV && localhostIP.contains(sessionIP.replace("/", ""));
    }

    static {
        localhostIP.add("203.116.196.8");
        localhostIP.add("203.188.239.82");
        localhostIP.add("8.31.98.52");
        localhostIP.add("8.31.98.53");
        localhostIP.add("8.31.98.54");
    }*/
/*    public static ServerConstants instance;

    public void run() {
//        updateIP();
    }*/

/*    public void updateIP() {
        eligibleIP.clear();
        final String[] eligibleIPs = {"rm0.zapto.org"}; //change IPs here; can be no-ip or just raw address
        for (int i = 0; i < eligibleIPs.length; i++) {
            try {
                eligibleIP.add(InetAddress.getByName(eligibleIPs[i]).getHostAddress().replace("/", ""));
            } catch (Exception e) {
            }
        }
    }*/

/*    public static void registerMBean() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            instance = new ServerConstants();
//            instance.updateIP();
            mBeanServer.registerMBean(instance, new ObjectName("constants:type=ServerConstants"));
        } catch (Exception e) {
            System.out.println("Error registering Shutdown MBean");
            e.printStackTrace();
        }
    }*/
}
