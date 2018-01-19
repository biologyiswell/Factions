package biologyiswell.factions.datamanager;

import biologyiswell.factions.FactionsPlugin;
import biologyiswell.factions.datamanager.chunk.ChunkData;
import biologyiswell.factions.datamanager.faction.Faction;
import com.google.common.io.Files;
import com.google.gson.JsonArray;
import org.apache.commons.io.Charsets;
import org.bukkit.Chunk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ChunkManager extends Manager
{

    /**
     * This represents the storage from the chunks data
     */
    private final ArrayList<ChunkData> CHUNKS_LIST = new ArrayList<>();

    // package-private
    ChunkManager()
    {
    }

    @Override
    protected void initData()
    {
        File chunksFile = new File(FactionsPlugin.INSTANCE.getDataFolder(), "chunks.json");

        if (!chunksFile.exists())
        {
            try
            {
                if (chunksFile.createNewFile()) FactionsPlugin.LOGGER.info("Factions ChunksData has been created.");
                else FactionsPlugin.LOGGER.warning(String.format("O arquivo %s não foi criado", chunksFile.getName()));
            } catch (IOException e)
            {
                FactionsPlugin.LOGGER.severe(String.format("Ocorreu um erro ao criar o arquivo %s.", chunksFile.getName()));
                e.printStackTrace();
            }
        }
        // NOTE: This is make to avoid exception, because after the chunks file created the method loadAllChunks load all chunks
        // and like the archive is a new, then the chunks can not load
        else
        {
            // NOTE: This method load  the all chunks from factions from database
            loadAllChunks();
        }
    }

    @Override
    protected void deInitData()
    {
        // NOTE: This method save the all chunks from factions to database
        saveAllChunks();
    }

    // public-methods

    /**
     * This method add chunk data to chunks list
     */
    public void addChunkData(ChunkData data)
    {
        if (!containsChunkData(data)) CHUNKS_LIST.add(data);
    }

    /**
     * This method remove the chunk data from chunks list
     */
    public void removeChunkData(ChunkData identifier)
    {
        ChunkData chunkData = CHUNKS_LIST.stream().filter(chunk ->
                chunk.getX() == identifier.getX() && chunk.getZ() == identifier.getZ() && chunk.getWorldName().equals(identifier.getWorldName())).findFirst().orElse(null);
        if (chunkData != null) CHUNKS_LIST.remove(chunkData);
    }

    /**
     * This method check if contains this chunk data in chunks list
     */
    public boolean containsChunkData(ChunkData identifier)
    {
        return CHUNKS_LIST.stream().anyMatch(chunk -> chunk.getX() == identifier.getX() && chunk.getZ() == identifier.getZ() && chunk.getWorldName().equals(identifier.getWorldName()));
    }

    /**
     * This method get the chunk data from chunk
     */
    public ChunkData getChunkData(Chunk chunk)
    {
        return CHUNKS_LIST.stream().filter(data -> data.getX() == chunk.getX() && data.getZ() == chunk.getZ() && data.getWorldName().equals(chunk.getWorld().getName())).findFirst().orElse(null);
    }

    /**
     * This method remove the all chunks from faction from chunks list
     */
    public void removeAllChunksFromFaction(Faction faction)
    {
        if (faction == null) throw new NullPointerException("faction can not be null");
        CHUNKS_LIST.stream().filter(chunkData -> chunkData.getFactionOwner().equalsIgnoreCase(faction.getName())).collect(Collectors.toList())
                .forEach(CHUNKS_LIST::remove);
    }

    // private-methods

    /**
     * This method load the all chunks from database
     */
    private void loadAllChunks()
    {
        // NOTE: This field is to calculate the time from the method
        long start = System.currentTimeMillis();

        File chunksFile = new File(FactionsPlugin.INSTANCE.getDataFolder(), "chunks.json");

        try
        {
            JsonArray chunksArray = GSON.fromJson(Files.newReader(chunksFile, Charsets.UTF_8), JsonArray.class);
            chunksArray.forEach(chunkDataJson -> CHUNKS_LIST.add(GSON.fromJson(chunkDataJson, ChunkData.class)));
        } catch (FileNotFoundException e)
        {
            FactionsPlugin.LOGGER.severe("Ocorreu um erro ao carregar todas as chunks da database.");
            e.printStackTrace();
        }

        if (FactionsPlugin.DEBUG)
            FactionsPlugin.LOGGER.info(String.format("Todas as chunks foram carregadas (Total: %s, Tempo: %sms.)", CHUNKS_LIST.size(), (System.currentTimeMillis() - start)));
    }

    /**
     * This method save the all chunks to database
     */
    private void saveAllChunks()
    {
        // NOTE: This field is to calculate the time from the method
        long start = System.currentTimeMillis();

        File chunksFile = new File(FactionsPlugin.INSTANCE.getDataFolder(), "chunks.json");
        JsonArray chunksArray = new JsonArray();

        CHUNKS_LIST.forEach(chunkData -> chunksArray.add(GSON.toJsonTree(chunkData)));

        try
        {
            Files.write(GSON.toJson(chunksArray), chunksFile, Charsets.UTF_8);
        } catch (IOException e)
        {
            FactionsPlugin.LOGGER.severe("Ocorreu um erro ao salvar todas as chunks.");
            e.printStackTrace();
        }

        if (FactionsPlugin.DEBUG)
            FactionsPlugin.LOGGER.info(String.format("Todas as chunks foram salvas (Total: %s, Tempo: %sms.)", CHUNKS_LIST.size(), (System.currentTimeMillis() - start)));
    }
}
