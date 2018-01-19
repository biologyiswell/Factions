package biologyiswell.factions.datamanager.chunk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class ChunkData
{

    /**
     * This represents the X and Z coordinates from chunk
     */
    private int x, z;

    /**
     * This represents the world name from chunk and the faction owner from chunk
     */
    private String worldName, factionOwner;
}
