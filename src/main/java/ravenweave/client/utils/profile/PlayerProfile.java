package ravenweave.client.utils.profile;

import com.google.gson.JsonObject;
import ravenweave.client.utils.Utils;

public class PlayerProfile {
    public boolean isPlayer = true;
    public boolean nicked;
    public int wins;
    public int losses;
    public int winStreak;
    public String inGameName;
    public String uuid;
    private final Utils.Profiles.DuelsStatsMode statsMode;

    public PlayerProfile(UUID uuid, Utils.Profiles.DuelsStatsMode mode) {
        this.uuid = uuid.uuid;
        this.statsMode = mode;
    }

    public PlayerProfile(String name, Utils.Profiles.DuelsStatsMode mode) {
        this.inGameName = name;
        this.statsMode = mode;
    }

    public void populateStats() {
        if (uuid == null) {
            this.uuid = Utils.Profiles.getUUIDFromName(inGameName);
            if (uuid.isEmpty()) {
                this.isPlayer = false;
                return;
            }
        }

        String textFromURL = Utils.URLS
                .getTextFromURL("https://api.hypixel.net/player?key=" + Utils.URLS.hypixelApiKey + "&uuid=" + uuid);
        if (textFromURL.isEmpty()) {
            this.nicked = true;
        } else if (textFromURL.equals("{\"success\":true,\"player\":null}")) {
            this.nicked = true;
        } else {
            JsonObject d;
            try {
                JsonObject pr = Utils.Profiles.parseJson(textFromURL).getAsJsonObject("player");
                this.inGameName = pr.get("displayname").getAsString();
                d = pr.getAsJsonObject("stats").getAsJsonObject("Duels");
            } catch (NullPointerException var8) {
                return;
            }

            switch (statsMode) {
            case OVERALL:
                this.wins = Utils.Profiles.getValueAsInt(d, "wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak");
                break;
            case BRIDGE:
                this.wins = Utils.Profiles.getValueAsInt(d, "bridge_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "bridge_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_bridge_duel");
                break;
            case UHC:
                this.wins = Utils.Profiles.getValueAsInt(d, "uhc_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "uhc_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_uhc_duel");
                break;
            case SKYWARS:
                this.wins = Utils.Profiles.getValueAsInt(d, "sw_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "sw_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_sw_duel");
                break;
            case CLASSIC:
                this.wins = Utils.Profiles.getValueAsInt(d, "classic_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "classic_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_classic_duel");
                break;
            case SUMO:
                this.wins = Utils.Profiles.getValueAsInt(d, "sumo_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "sumo_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_sumo_duel");
                break;
            case OP:
                this.wins = Utils.Profiles.getValueAsInt(d, "op_duel_wins");
                this.losses = Utils.Profiles.getValueAsInt(d, "op_duel_losses");
                this.winStreak = Utils.Profiles.getValueAsInt(d, "current_winstreak_mode_op_duel");
            }
        }
    }
}
