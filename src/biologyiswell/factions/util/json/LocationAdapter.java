package biologyiswell.factions.util.json;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject json = new JsonObject();
        json.addProperty("x", location.getX());
        json.addProperty("y", location.getY());
        json.addProperty("z", location.getZ());
        json.addProperty("yaw", location.getYaw());
        json.addProperty("pitch", location.getPitch());
        json.addProperty("world", location.getWorld().getName());
        return json;
    }

    @Override
    public Location deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject json = jsonElement.getAsJsonObject();

        World world = Bukkit.getWorld(json.get("world").getAsString());

        if (world == null) {
            throw new JsonParseException("Location deserialization not found world " + json.get("world").getAsString());
        }

        return new Location(
                world,
                json.get("x").getAsDouble(),
                json.get("y").getAsDouble(),
                json.get("z").getAsDouble(),
                json.get("yaw").getAsFloat(),
                json.get("pitch").getAsFloat()
        );
    }
}
