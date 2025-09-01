# CritterGuard

CritterGuard is a plugin for protecting your pets from death and theft.

It introduces its own taming system so mobs not typically able to be tamed, like camels and happy ghasts, can be locked to players.

## Taming

### Horses, Mules, and Donkeys
These are tamed through the normal vanilla method of riding it until it accepts your love.

### Llamas
Must be tamed by clicking on a llama with a lead.

### Camels and Happy Ghasts
These are tamed simply by riding them once.

### Dogs, Cats, and Parrots
These are tamed through the normal vanilla method of smacking them with their tame items.

## Access
CritterGuard adds 2 access groups to mounts (horses, mules, donkeys, llamas, camels, and happy ghasts): passenger and full.

### Passenger
Can only be applied to mounts with multiple seats (camels and happy ghasts, currently).

This access only allows a player to ride a mount if someone else is already controlling it who is the owner or has full access.

To add or remove someone to or from your mount with passenger access, use `/critter access <add/remove> passenger <player>` and click on the mount you want the access to be added to.

### Full
Can be applied to all mounts.

This access allows a player to ride and control your mount. It also grants access to any chests that may be on it (like on donkeys and llamas).

If in-game, you will be notified when a player with full access takes control of your mount.

To add or remove someone to or from your mount with passenger access, use `/critter access <add/remove> full <player>` and click on the mount you want the access to be added to.

## The "identifier" parameter
This parameter is required in certain commands and you can give it one of three things:
- The critter's name (if it was named with a nametag)
- The critter's UUID
- The critter's index number from `/critter list`

NOTE: This accepts partial matches. That means if you have a dog named "Fido", you can just do `/critter gps fi` and it'll work. However, this can also cause issues if you have 2 critters with similar names or UUIDs, as it will match to 2 critters. In this case, it always picks the one with the lowest index number.

## Useful Commands

### `/critter list [entityType] [player] [page]`
Provides you with a list of tamed critters depending on the parameters given.

NOTE: All parameters are optional but **must** be entered in the same order as above.

#### entityType
Allows you to filter by entity type. Can be useful if you have a lot of critters.

Valid types:
- all
- camel
- cat
- donkey
- happy_ghast
- horse
- llama
- mule
- parrot
- wolf

#### player
This is simply the name of the player whose critters you want a list of.

#### page
The page you want to view, in the event you have enough critters to populate multiple pages. Must be a number above 0.

### `/critter gps <identifier>`
Gives you the location of, and points your player at, the critter specified.

#### idenfifier
[See the section dedicated to this](#The-"identifier"-parameter).

### `/critter untame`
After running this command, click on a critter you own to untame it.

## Staff Commands
These are commands that should, typically, only be given to staff or higher.

### `/critter tame <player>`
After running this command, click on an untamed critter to tame it to a player.

#### player
This is the name of the player who you're taming the critter to.

### `/critter tp <player> <identifier>`
Teleports you to someone else's critter.

#### player
This is the name of the player whose pet is being teleported to.

#### Identifier
[See the section dedicated to this](#The-"identifier"-parameter).

### `/critter tphere <player> <identifier>`

#### player
This is the name of the player whose pet is being teleported to you.

#### Identifier
[See the section dedicated to this](#The-"identifier"-parameter).

### `/critter reload`
Reloads the plugin's configuration file.

## All Commands & Permissions
| Command                                                | Description                                                                                                 | Permission                                              |
|--------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| /critter access <add/remove> <full/passenger> <player> | Grant or remove a player's access to a mount.                                                               | critterguard.access                                     |
| /critter list [entityType] [player] [page]             | Get a list of critters based on the specified criteria.                                                     | critterguard.list                                       |
| /critter gps <identifier>                              | Get the coordinates of, and point your camera to, a critter you own that matches the specified identifier.  | critterguard.gps                                        |
| /critter untame                                        | Untames a critter.                                                                                          | critterguard.untame.own <br> critterguard.untame.others |
| /critter tame                                          | Tames a critter to the specified player.                                                                    | critterguard.tame                                       |
| /critter tp <player> <identifier>                      | Teleports you to the critter that belongs to the player specified and who matches the specified identifier. | critterguard.tp                                         |
| /critter tphere <player> <identifier>                  | Teleports the critter who belongs to the specified player and matches the specified identifier to you.      | critterguard.tphere                                     |
| /critter reload                                        | Reloads the plugin's configuration file.                                                                    | critterguard.reload                                     |
