# Grimorium

Grimorium is an handy reference to AD&D 2nd edition spells.

- Access multiple online spell libraries
- Powerful filtering system
- Search while typing
- Create any number of wizards or priests
- Define a different filter for each wizard or priest
- Mark favourite spells for each wizard or priest

## Spell libraries

Spell libraries are collections of spell in _json_ format.

Each library contains an header and an array of spells.

```json
{
	"format": 1,
	"version": 1,
	"name": "Custom Spell Library",
	"nameSpace": "net.ohmnibus.custom",
	"description": "This library is user defined.<br>All these spells are unofficial.",
	"spells": [ ]
}
```

`format`: Is the format of the library. Only supported value is `1` so don't change it.

`version`: Is the version of the library. Each time a change is made this value should advance,
otherwise the app won't read the changes.

`nameSpace`: Uniquely identifies the library. Each different library must have it's unique
identifier. When loading a library, Grimorium will overwrite all the spells of the library
with the same _nameSpace_.

`description`: Full description of the library. HTML is allowed. Avoid external links.

`spells`: The array of the spells defined in this library.

### Spells

Each spell in the `spells` array is defined as follow:

```json
{
	"uid": 1,
	"type": 1,
	"lvl": 1,
	"name": "Create milk from wood",
	"book": "Custom",
	"schools": "Alteration, Necromancy",
	"spheres": "Creation, Plant",
	"rng": "Touch",
	"compo": "V, S, M",
	"dur": "Special",
	"castime": "1 round",
	"aoe": "1 tree or 1 creature",
	"saving": "Special",
	"body": "Allow the priest to extract sap from a tree of at least 40cm of diameter, turning it to milk. The magic user can extract up to a liter per caster level, at the rate of a liter per round. To extract its sap, the tree must be alive or cut down no more than 24 hours.<br />Extracting sap from a tree cause deep debilitation to it. Doing this two time in 48 hours cause the death of the tree not allowing further sap extraction.<br />Extracted milk is as good as common cow or goat milk, and can be processed into dairy products. If not processed, milk will turn rancid in 6 hours (or 24 if preserved in closed containers and fresh environment); however a spell like Purify Food and Drinks will turn rancid milk as fresh as just extracted.<br />If cast against a plant creature like treants or dryads the spell instantly inflicts 1d6 points of damage for each caster level, halved down with a saving throw against Death ray. Other creatures like golem, constructs or undead are simply immune to the effect of this spell.<br />Material component of this spell is a miniature bucket made of wood or clay.",
	"author": "Emme Ci"
}
```

`uid`: Unique identifier for the spell. This value must be unique for the _spell library_.
Never use same `uid` for two spells in same library!!!

`type`: Type of the spell.
- `1`: Wizard spell
- `2`: Priest spell.

`lvl`: Spell level. Allowed values are `0` to `11`.
- `0`: Cantrip/orison
- `1`-`10`: Actual level of a spell
- `11`: Quest level spell

`name`: Name of the spell.

`book`: Source book. Always use `Custom`. All supported values are:
- `Base`: Player's Handbook
- `Tome`: Tome of Magic
- `Wizards`: Complete Wizard's Handbook
- `Priests`: The Complete Priest's Handbook
- `Spells`: Spells &amp; Magic
- `Adventures`: Forgotten Realms "Adventures"
- `Custom`: Custom spell
- `Carnal`: The Carnal Knowledge Guide
- `Alcohol`: The Net Alcohol Guide

`schools`: Schools of the spell. One or more of the following values separated by a comma.
- `All Schools`: All Schools
- `Abjuration`: Abjuration
- `Alteration`: Alteration
- `Conjuration`: Conjuration/Summoning
- `Divination`: Divination
- `Enchantment`: Enchantment/Charm
- `Evocation`: Illusion/Phantasm
- `Illusion`: Invocation/Evocation
- `Necromancy`: Necromancy
- `Air`: Elemental (Air)
- `Earth`: Elemental (Earth)
- `Fire`: Elemental (Fire)
- `Water`: Elemental (Water)
- `Dimensional`: Dimension
- `Force`: Force
- `Shadow`: Shadow
- `Alchemy`: Alchemy
- `Artifice`: Artifice
- `Geometry`: Geometry
- `Song`: Song
- `Wild`: Wild Magic
- `Mentalism`: Mentalism

`spheres`: Spheres of the prayer, valid only if `type` is `2`. One or more of the following values
separated by a comma.
- `All`: All
- `Animal`: Animal
- `Astral`: Astral
- `Charm`: Charm
- `Combat`: Combat
- `Creation`: Creation
- `Divination`: Divination
- `Elemental (Air)`: Elemental (Air)
- `Elemental (Earth)`: Elemental (Earth)
- `Elemental (Fire)`: Elemental (Fire)
- `Elemental (Water)`: Elemental (Water)
- `Guardian`: Guardian
- `Healing`: Healing
- `Necromantic`: Necromantic
- `Plant`: Plant
- `Protection`: Protection
- `Summoning`: Summoning
- `Sun`: Sun
- `Weather`: Weather
- `Chaos`: Chaos
- `Law`: Law
- `Numbers`: Numbers
- `Thought`: Thought
- `Time`: Time
- `Travelers`: Travelers
- `War`: War
- `Wards`: Wards

`rng`: Range of the spell

`compo`: Components of the spell. Either `V` for _verbal_, `S` for _somatic_ and `M` for
_material_, or a mix of the three separated by a comma.

`dur`: Duration of the spell

`castime`: Casting time of the spell

`aoe`: Area of Effect of the spell

`saving`: Saving throw, if any, or `None`

`body`: Description of the spell. HTML tags are allowed. Avoid external links. Add line breaks
with the HTML tag `<br>`.

`author`: Author of the spell. This attribute is optional and can be safely omitted.

## A word from the developer

Since I had to shutdown the official play store account, there is no point to keep an ADS version
of this app, so I decided to publicy release the source code of the free version.
