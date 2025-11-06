# Digimon World Re:Digitize Undub/Text Modification Tool
This software allows replacing and modifying strings in the 2013 released 3DS game "Digimon World Re:Digitize Decode".

The main motivation for creating this tool was tool is and was replacing the dub terminology of the game's English translation patch by Operation Decode, with the original terms.

# Usage

## Prerequisites 
Before using this software make sure you

* have Java 11 or higher installed
  * you can download it from [Adoptium](https://adoptium.net/)
* have a dumped ROM of the game in .CCI/.3DS or .CIA format
  * the ROM should have the English translation patch applied
    * Replacing Japanese text *might* work (untested).
* have enough empty disk space for all files created during the randomization
  * 5-10 GiB on the drive you installed the textmod tool on should suffice

## Setup

Download the appropriate version for your OS from the [releases tab](https://github.com/Thertzlor/DecodeTextMod/releases) and unpack the downloaded .zip archive in the location you want to install the textmod tool at.

Run the `Decode-TextMod.jar`, press the "Select Base ROM" button and navigate to ROM you want to use as base for the textmod tool.
After the ROM has been successfully unpacked you'll be presented with the editing options.

The program starts with the default "undub" preset which replaces localized Digimon names and terms with their original counterparts, so if that's all you want you don't need to change any settings, just hit "Save Changes" which will generate your new rom.

> [!NOTE]
The replacement process *will* take a while, since it requires opening, editing and re-packing basically every script file in the game, so it might take around ten minutes, depending on your PC.

# Current Features

## Undub/Text Replacement

* Fully functioning Undubbing preset.
* Support for user provided renaming presets enabling replacing any term or phrase in the game script.
  * Selectors for specific files/lines inside files
  * Regex support
  * Exclusion patterns and paths.
  
### Undub/Editing Settings

* **`Replace terms in ALL text`**  
Replaces the terms of a list such as DigimonNames.csv not just in that list but will also replace it in all general scripts such as NPC dialogue.
* **`use manual CSV`**  
Enable this option if you are **not** using the built-in undub replacment, but your own csv replacements via the `renamingPresets` folder.
* **`camelCase names`**  
Use the camelCase format for Digimon names such as "WarGreymon" instead of "War Greymon".
* **`Always use 'Black' as prefix`**  
The black variant of Agumon is technically called "Agumon (Black)" but with this option its name will be rendered as "BlackAgumon"
* **`Ogremon spelling`**  
The undub patch will use the official "Orgemon" spelling by default, this option changes it to the widely used "Ogremon"
* **`Picklemon spelling`**  
By default the undub patch will use the spelling "Piccolomon" but if you really like pickles you can enable this option and stick with the official spelling, "Picklemon"

The two last buttons should only be of interest if you are planning to create your own text editing presets:
* **`Export CSVs for current names`**  
Exports the contents of the following files in the currently unpacked ROM into the `renamingPresets` directory:
  * AccessoryNames.csv, CardNames1.csv, CardNames2.csv, CardSetNames.csv, CharacterNames.csv, DigimonNames.csv, FinisherNames.csv, GlossaryNames.csv, ItemNames.csv, KeyItemNames.csv, MedalNames.csv, NatureNames.csv, SkillNames.csv
* **`Export CSVs for built-in preset`**  
Exports the CSV replacement files for the built-in undub preset into the `renamingPresets` directory, to make it easily usable as a base for custom patches.

 
## Name Randomization
This feature, which is disabled by default, is basically a silly tribute to the fact that this is forked from a randomizer tool.  

The names of...
* Digimon
* Finisher
* Skills
* Characters
* Items
* Medals

Can be randomized, so you can have some silly playthrough where you never know which item actually does what and who is who.

Each category can be toggled separately.

## Known Issues / Caveats
There are some parts of the replacement logic such as the functionality adjusting articles, that generally assume English grammar.

# Text Replacement Format
> [!NOTE]
This information is only relevant if you wish to create your own text replacement presets.  
It also assumes assumes that you have already extracted the game's script data into CSV.

To create your own replacement patch simply create a folder in the same directory as the TextMod tool called `renamingPresets` containing replacement files provided in utf8 encoded csv files.  

## Naming
Each file in the folder, [except for a number of special cases](#special-files), targets one of the game's script files based on its name, which is converted into a path:

`keep-15.csv` will target the file `Keep\languageKeep.res\btx_15.csv`.  
`map-text-are_npc353_text-1` will target the file `map\text\are_npc353_text.pack\btx_1.csv`

... and so on.
## CSV Structure
The CSV structure in each file consists of the following columns:
`index;original;replace;excludeTerms;excludePaths`

If a value for the `index` column is provided, a *targeted replacment*, the entire line the index points to will be replaced with the content of the `replace` column. In this mode the 'original' column only serves a visual hint when creating the patch.

If the index is provided as `0`, a *general replacement*, all instances of the string in the `original` column are replaced with the string in the `replacement` column for that file.

The column `excludeTerms` is used to prevent the replacement of the `original` value in specific contexts.  
For example, even though we want to replace the term "Champion" with "Adult" when it refers to the level of a Digimon, we do not want the line `"You're the Champion"` to become `"You're the Adult"`, so we add the exclusion term `"You're the $"` to our replacement definition; `$` is automatically substituted with the content of the `original` column.  
Several excluded paths can be provided, separated by commas.  
For example:

```csv
index;original;replace;excludeTerms;excludePaths
0;Champion;Adult;Colosseum $,You're the $;
```

The column `excludePaths` prevents the replacement in certain folders, files or specific lines in a file.  
For example the following line will replace the term "Fresh" with "Baby I" except in line 4227 of btx_14 within LanguageKeep_jp.res: 
`0;Fresh;Baby I;$ly;*\LanguageKeep_jp.res\14:4227`  
Omitting `:4227` prevents any replacments in that file and `*\LanguageKeep_jp.res\*` prevents it for all files on that path. Multiple paths can be defined, separated by commas.

The special value `all` will exclude a replacement from the effect of the `replace terms in ALL text` toggle.

## Special Files
There are two special files, `_general.csv` and `_general_digimon.csv` which both do not target specific script files, but will simply replace the specified terms and replacements throughout the game.  
The only difference between the two is that the contents of `_general_digimon.csv` are affected by Digimon name specific settings such as `camelCase names` and `Always use 'Black' as prefix`, see [editing Settings](#undubediting-settings).

# Planned Features
* Global string randomization.

# Credits
* SydMontague for the DecodeTools and Decode Randomizer tools which this software is based on.
* A large number of replacements for the undub preset were taken from the non-dub translation patch for "Digimon World: Re:Digitize (PSP)"
* The rest were mostly compiled from Wikimon.

# Contact
* Discord: Theigno#6256, or in either the [Digimon Modding Community](https://discord.gg/cb5AuxU6su) or [Digimon Discord Community](https://discord.gg/0VODO3ww0zghqOCO)
* directly on GitHub
* E-Mail: yggdrasilwiz9000@yahoo.com