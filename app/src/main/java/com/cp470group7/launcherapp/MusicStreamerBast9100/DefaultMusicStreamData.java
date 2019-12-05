package com.cp470group7.launcherapp.MusicStreamerBast9100;

public class DefaultMusicStreamData {
    public static String[] defualtMusic = new String[]{

            "91.5",
            "StreamLink",
            "103.5",
            "StreamLink",
            "92.5"
            , "StreamLink"
    };

    public static String[] getBestThingsToSellOnEBay() {
        return defualtMusic;
    }

    public static void setBestThingsToSellOnEBay(String[] defualtMusic) {
        DefaultMusicStreamData.defualtMusic = defualtMusic;
    }
    public static int count()
    {
        return DefaultMusicStreamData.defualtMusic.length;
    }
}

