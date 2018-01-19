package biologyiswell.factions;

public class FactionsConfig {

    /**
     * Default Power and Default Power Max, this configuration represents the default power and power max that when
     * new player join in server receive
     */
    public float defaultPower, defaultPowerMax = 20f;

    /**
     * Time To Accept Invite, this configuration represents the time that a player have to accept an invite from
     * faction, this time is in minutes
     */
    public byte timeToAcceptInvite = 1;

    /**
     * Max Chunks Without Check, this configuration represents the maximum of chunks that the faction can be protected
     * without check from the (power and trophies)
     */
    public byte maxChunksWithoutCheck = 2;

    /**
     * Chunk Cost, this configuration represents the cost that a player need pay to protect chunk
     */
    public float chunkCost = 1000f;

    /**
     * This represents the max faction name length and max faction tag length
     */
    public byte maxFactionNameLength = 12, maxTagLength = 3;

    // Permissions
}
