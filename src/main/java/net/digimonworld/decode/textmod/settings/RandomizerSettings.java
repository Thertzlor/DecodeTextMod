package net.digimonworld.decode.textmod.settings;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;

import net.digimonworld.decodetools.core.Tuple;
import net.digimonworld.decodetools.data.keepdata.GlobalKeepData;
import net.digimonworld.decodetools.data.keepdata.LanguageKeep;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Tab;
import javafx.scene.layout.FlowPane;
import net.digimonworld.decode.textmod.RandoLogger.LogLevel;
import net.digimonworld.decode.textmod.RandomizationContext;

public class RandomizerSettings {

    private NamingSettings namingSettings = new NamingSettings();
    private StringRandomizeSettings stringRandoSettings = new StringRandomizeSettings();

    public void randomize(RandomizationContext context) {
        logSettings(context);
        namingSettings.randomize(context);
        stringRandoSettings.randomize(context);
    }

    private void logSettings(RandomizationContext context) {
        context.logLine(LogLevel.ALWAYS, "Randomizer Settings: ");

        Map<String, Object> configMap = new HashMap<>();
        configMap.put("seed", context.getInitialSeed());
        configMap.put("settings", serialize());
        configMap.put("raceLogging", context.isRaceLogging());

        try (StringWriter writer = new StringWriter()) {
            Yaml.createYamlPrinter(writer).print(Yaml.createYamlDump(configMap).dump());
            context.logLine(LogLevel.ALWAYS, writer.toString());
        } catch (IOException e) {
            // should never happen
            e.printStackTrace();
        }

        context.logLine(LogLevel.ALWAYS, "");
    }

    public List<Tab> create(GlobalKeepData inputData, LanguageKeep languageKeep) {
        return getSettingsMap().stream().map(a -> {
            FlowPane generalPane = new FlowPane();
            generalPane.setVgap(10);
            generalPane.setHgap(10);
            generalPane.setPadding(new Insets(10));
            generalPane.setOrientation(Orientation.VERTICAL);
            generalPane.setPrefWrapLength(400);

            for (Setting setting : a.getValue())
                generalPane.getChildren().add(setting.create(inputData, languageKeep));

            return new Tab(a.getKey(), generalPane);
        }).collect(Collectors.toList());
    }

    private List<Tuple<String, List<Setting>>> getSettingsMap() {
        return List.of(
                Tuple.of("Patch Names", Arrays.asList(namingSettings)),
                Tuple.of("Randomize Names", Arrays.asList(stringRandoSettings))
                );
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("namingSettings", namingSettings.serialize());
        map.put("stringRandomSettings", stringRandoSettings.serialize());
        return map;
    }

    public void load(YamlMapping map) {
    }
}
