package biologyiswell.factions.datamanager.faction;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role
{
    LEADER("Líder", '⚔'),
    CAPTAIN("Capitão", '✵'),
    SARGEANT("Sargento", '✯'),
    SOLDIER("Soldado", '✴');

    private String translatedName;
    private char icon;
}
