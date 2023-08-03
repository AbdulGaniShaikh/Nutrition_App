package com.miniproject.tournamentapp;

public class Keys {

    public static String TOUR_COLLECTION = "tournaments";

    public static String TOUR_NAME = "name";
    public static String TOUR_STARTD = "startd";
    public static String TOUR_STARTT = "startt";
    public static String TOUR_ENDD = "endd";

    public static String TOUR_ENDT = "endt";
    public static String TOUR_TIMESTAMP = "timestamp";
    public static String TOUR_HOST = "host";
    public static String TOUR_HOSTNAME = "hostname";

    public static String TOUR_GAME = "game";
    public static String TOUR_PRIZE = "prize";
    public static String TOUR_TOTALCAPACITY = "capacity";
    public static String TOUR_PARTICIPANTS = "participants";

    public static String TOUR_PARTICIPATION_TYPE = "participationtype";
    public static String TOUR_DESP = "desp";
    public static String TOUR_DISCORD = "discord";

    /**************************/

    public static String USER_COLLECTION = "users";
    //
    public static String USER_NAME = "name";
    public static String USER_TEAM = "team";
    public static String USER_DESP = "desp";
    public static String USER_AVATAR = "avatar";
    //
    public static String USER_EMAIL = "email";
    public static String USER_REQUESTEDTEAM = "teamrequest";
    public static String USER_REQUESTEDPLAYERS = "buddyrequest";
    public static String USER_BUDDIES = "buddies";
    //
    public static String USER_REQUESTS = "request";
    public static String USER_INTERESTS = "interests";
    public static String USER_TOURNAMENTS = "mytournaments";
    public static String USER_PARTICIPATIONS = "currentparticipations";
    public static String USER_TEAMPART = "teamparticipation";

    /**************************/

    public static String TEAM_COLLECTION = "teams";

    public static String TEAM_NAME = "name";
    public static String TEAM_DESP = "desp";
    public static String TEAM_PLAYERS = "players";
    public static String TEAM_REQUESTS = "requests";

    public static String TEAM_TOURNAMENTS = "tournaments";
    public static String TEAM_LEADER = "leader";


    public static String SOLO = "Solo";
    public static String TEAM = "Team";

    public static int USER_INTENT= 0;
    public static int TOPIC_INTENT= 1;

    public static int NO_STATUS = 0;
    public static int STATUS_REQUESTED = 1;
    public static int STATUS_FRIEND = 2;

    public static int getGame(String i){
        switch (i){
            case "Fortnite PC":
                return R.drawable.icon_fortnite;
            case "Critical Ops":
                return R.drawable.icon_cops;
            case "Clash Royale":
                return R.drawable.icon_clashroyale;
            case "PUBG Mobile":
            case "PUBG PC":
                return R.drawable.icon_pubg;
            case "Call Of Duty":
                return R.drawable.icon_cod;
            case "Fortnite":
                return R.drawable.icon_fortnite;
            case "Brawl Stars":
                return R.drawable.icon_brawlstars;
            case "Stand Off":
                return R.drawable.icon_standoff;
            case "Valorant":
                return R.drawable.icon_valorant;
            case "Overwatch":
                return R.drawable.icon_overwatch;
            case "DOTA 2":
                return R.drawable.icon_dota;
            case "Rocket League":
                return R.drawable.icon_rl;
            case "Apex Legends":
                return R.drawable.icon_apex;
            case "CS:GO":
                return R.drawable.icon_scgo;
            case "COD: Modern Warfare":
                return R.drawable.icon_codmw;
            default:
                return R.drawable.icon_coc;
        }
    }

    public static int getAvatar(int i){
        switch (i){
            case 1:
                return R.drawable.ic_avat1;
            case 2:
                return R.drawable.ic_avat2;
            case 3:
                return R.drawable.ic_avat3;
            case 4:
                return R.drawable.ic_avat4;
            case 5:
                return R.drawable.ic_avat5;
            case 6:
                return R.drawable.ic_avat6;
            case 7:
                return R.drawable.ic_avat7;
            case 8:
                return R.drawable.ic_avat8;
            case 9:
                return R.drawable.ic_avat9;
            case 10:
                return R.drawable.ic_avat10;
            case 11:
                return R.drawable.ic_avat11;
            case 12:
                return R.drawable.ic_avat12;
            case 13:
                return R.drawable.ic_avat13;
            case 14:
                return R.drawable.ic_avat14;
            case 15:
                return R.drawable.ic_avat15;
            default:
                return R.drawable.ic_avat0;
        }
    }
}
