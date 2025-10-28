package net.digimonworld.decode.randomizer.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import java.text.ParseException;
import java.util.NoSuchElementException;

import org.controlsfx.control.ToggleSwitch;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlSequence;

import net.digimonworld.decodetools.data.keepdata.GlobalKeepData;
import net.digimonworld.decodetools.data.keepdata.LanguageKeep;
import net.digimonworld.decodetools.res.kcap.NormalKCAP;
import net.digimonworld.decodetools.res.payload.BTXPayload;
import net.digimonworld.decodetools.core.Tuple;
import net.digimonworld.decodetools.res.payload.BTXPayload.BTXEntry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.geometry.Pos;

import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import net.digimonworld.decode.randomizer.RandomizationContext;
import net.digimonworld.decode.randomizer.utils.JavaFXUtils;

public class StringRandomizeSettings implements Setting {

    private final List<String> skippable = List.of("", "None", "Unused Item", "???", "NO DATA", "n");
    private final BooleanProperty randomizeEnabled = new SimpleBooleanProperty();
    private final Map<String, BooleanProperty> randoMap = new HashMap<>();
    private final List<String> randoTypes = List.of("Digimon Names", "Finisher Names", "Skill Names", "Character Names", "Item Names", "Medal Names");

    /**
     * Visualization of a replacement map:
     *
     * { "part0\arcv\Keep\LanguageKeep_jp.res\11:12" : [ [0,4,2] , [8,13,-1] ] }
     *
     * Path schema works like this:
     *
     * [string path to the actual file]/[index of BTX file]:[BTX line]
     *
     * Replacement info pattern:
     *
     * [match start, match end, offset of result]
     *
     * Replacements are saved to prevent replacing terms that have already been
     * processed by a prior replacement. The offsets are used to map a position
     * of a match to its position in the unmodified line, making it possible to
     * exclude replacing matches at specific indices in a line based on the
     * unmodified contents.
     */
    public Map<String, ArrayList<int[]>> replacementMap = new HashMap<>();

    /**
     * DIGIMONMULTI designates names like WarGreymon or MetalGarurumon.
     *
     * All other DIGIMON names are simply other terms contained in a file
     * designated as Digijmon name file.
     *
     * Everything else is classified as "GENERAL"
     *
     */
    protected enum TermType {
        GENERAL,
        DIGIMON,
        DIGIMONMULTI
    }

    private static void btxSwitch(BTXEntry btxA, BTXEntry btxB) {
        String a = btxA.getString();
        String b = btxB.getString();
        btxA.setString(b);
        btxB.setString(a);
    }


    @Override
    public TitledPane create(GlobalKeepData data, LanguageKeep language) {


        VBox randoBox = new VBox(8);

        randoBox.setAlignment(Pos.TOP_LEFT);

        TitledPane pane = new TitledPane("Randomizer Settings", randoBox);


        pane.setCollapsible(false);


        randoBox.getChildren().addAll(
                JavaFXUtils.buildToggleSwitch("Enabled", Optional.empty(), Optional.of(randomizeEnabled)));

        for (String r : randoTypes) {
            randoMap.put(r, new SimpleBooleanProperty(false));
            ToggleSwitch swit = JavaFXUtils.buildToggleSwitch(r, Optional.empty(), Optional.of(randoMap.get(r)));
            swit.disableProperty().bind(randomizeEnabled.not());
            randoBox.getChildren().add(swit);
        }

        return pane;
    }

    private class PathResolver {

        private final RandomizationContext context;
        private final Map<String, String> shortcuts = new HashMap<>();
        public final Map<String, String> keepMap = new HashMap<>();

        public PathResolver(RandomizationContext context) {
            this.context = context;
            this.shortcuts.put("keep", "Keep\\LanguageKeep_jp.res");
            this.shortcuts.put("map", "map\\text");
            this.keepMap.put("DigimonNames", "11");
            this.keepMap.put("ItemNames", "0");
            this.keepMap.put("KeyItemNames", "3");
            this.keepMap.put("AccessoryNames", "5");
            this.keepMap.put("SkillNames", "7");
            this.keepMap.put("FinisherNames", "9");
            this.keepMap.put("CharacterNames", "13");
            this.keepMap.put("NatureNames", "16");
            this.keepMap.put("MedalNames", "17");
            this.keepMap.put("GlossaryNames", "25");
            this.keepMap.put("CardNames1", "27");
            this.keepMap.put("CardNames2", "28");
            this.keepMap.put("CardSetNames", "30");
        }



        /**
         * Resolves a path directly into BTX payloads while applying shortcuts
         */
        public Tuple<String, BTXPayload> resolve(String path) throws ParseException {
            ArrayList<String> frag = new ArrayList<>(
                    List.of((keepMap.containsKey(path) ? ("keep-" + keepMap.get(path)) : path).split("-")));
            int btxIndex = Integer.parseInt(frag.remove(frag.size() - 1));
            String finalPath = "part0\\arcv\\" + frag.stream()
                    .map(s -> shortcuts.containsKey(s) ? shortcuts.get(s) : s)
                    .collect(Collectors.joining("\\"));
            try {
                NormalKCAP pk = (NormalKCAP) context.getFile(finalPath).get();
                if (frag.get(frag.size() - 1).equals("keep")) pk = (NormalKCAP) pk.get(0);
                return new Tuple<>(finalPath + "\\" + btxIndex, (BTXPayload) pk.get(btxIndex));
            } catch (NoSuchElementException exc) {throw new ParseException("csv not correctly mapped", 0);}
        }
    }




    @Override
    public void randomize(RandomizationContext context) {
        

        PathResolver res = new PathResolver(context);
 
            //Randomizing different lists of strings
            Random rand = new Random(context.getInitialSeed() * "ShuffleTerms".hashCode());
            randoTypes.stream().filter(k -> randoMap.get(k).get()).map(s -> s.replaceAll(" ", "")).forEach(name -> {
                try {
                    //creating a list of all btx entries in the payload without empty/filler fields
                    ArrayList<BTXEntry> entries = new ArrayList<>(res.resolve(name).getValue().getEntries().stream().map(e -> e.getValue()).filter(v -> !skippable.contains(v.getString())).collect(Collectors.toList()));

                    BTXEntry firstEntry = null;
                    //Switching the value of a random pair of BTX entries and removing them from the list.
                    while (entries.size() > 1) {
                        int i = rand.nextInt(entries.size());
                        BTXEntry btxA = entries.remove(i);
                        if (firstEntry == null) firstEntry = btxA;
                        int n = rand.nextInt(entries.size());
                        BTXEntry btxB = entries.remove(n);
                        btxSwitch(btxA, btxB);
                    }
                    //In case there's an uneven number of entries we switch the leftover entry with the first entry we processed previously
                    if (entries.size() == 1) btxSwitch(firstEntry, entries.get(0));
                } catch (ParseException e) {e.printStackTrace();}
            });
        
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("randomizeEnabled", randomizeEnabled.get());
        map.put("randomChecked", randoMap.entrySet().stream().filter(a -> a.getValue().get()).map(Map.Entry::getKey).collect(Collectors.toList()));
        return map;
    }

    @Override
    public void load(YamlMapping map) {
        if (map == null) return;
        
        YamlSequence list = map.yamlSequence("randomChecked");
        List<String> activeList = list == null ? new ArrayList<>() : list.values().stream().map(a -> a.toString()).collect(Collectors.toList());
        randoMap.forEach((a, b) -> b.set(activeList.contains(a)));
        randomizeEnabled.set(Boolean.parseBoolean(map.string("randomizeEnabled")));
    }
}
