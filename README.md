```
  ` : | | | |:  ||  :     `  :  |  |+|: | : : :|   .        `              .
      ` : | :|  ||  |:  :    `  |  | :| : | : |:   |  .                    :
         .' ':  ||  |:  |  '       ` || | : | |: : |   .  `           .   :.
                `'  ||  |  ' |   *    ` : | | :| |*|  :   :               :|
        *    *       `  |  : :  |  .      ` ' :| | :| . : :         *   :.||
             .`            | |  |  : .:|       ` | || | : |: |          | ||
      '          .         + `  |  :  .: .         '| | : :| :    .   |:| ||
         .                 .    ` *|  || :       `    | | :| | :      |:| |
 .                .          .        || |.: *          | || : :     :|||
        .            ._______                                   
                      ___    |___  ___________________________ _
                      __  /| |  / / /_  ___/  __ \_  ___/  __ `/
                      _  ___ / /_/ /_  /   / /_/ /  /   / /_/ / 
                      /_/  |_\__,_/ /_/    \____//_/    \__,_/  
                                                                
                         . *    .   .  ` |||.  +        + '| |||  .  ||`
     .             *              .     +:`|!             . ||||  :.||`
 +                      .                ..!|*          . | :`||+ |||`
     .                         +      : |||`        .| :| | | |.| ||`     .
       *     +   '               +  :|| |`     :.+. || || | |:`|| `
                            .      .||` .    ..|| | |: '` `| | |`  +
  .       +++                      ||        !|!: `       :| |
              +         .      .    | .      `|||.:      .||    .      .    `
          '                           `|.   .  `:|||   + ||'     `
  __    +      *                         `'       `'|.    `:
"'  `---"""----....____,..^---`^``----.,.___          `.    `.  .    ____,.,-
    ___,--'""`---"'   ^  ^ ^        ^       """'---,..___ __,..---""'
--"'                                 ^                         ``--..,__
D. Rice                                          
```

_Ooooo, pretty lights_

Aurora is a spigot plugin designed to create fast and reliable visual experiences in Minecraft. It's intended to be
controlled in realtime via HTTP requests.

## Installation

### Prerequisites

A server running spigot or paper is required, with at least 4GB of RAM. ProtocolLib is required to be installed
beforehand.

This plugin currently works on 1.16.4 only. It will most likely run on any 1.16 version (1.16-1.16.5) but the guardian
lasers will not work due to depending on certain APIs. I'm working to resolve this.

### Instructions

1. Stop your server
2. Drag and drop the .jar file into the plugins folder
3. Start your server to generate plugin files

## Configuration

### What do all these files do?

#### config.yml

This file is a standard plugin config. Here, you can configure what IP and port that the plugin listens to HTTP requests
on. Control over caching behavior is planned.

#### points.yml

The Aurora effects system works off of points. they tell the plugin where effects should go and allows for more
efficient requests. You can either add points ingame or do so here directly (be careful!).

#### staticEffects.json [WIP]

If you want certain effects to be always active while the plugin is running, you can define them here. This feature is
still under development and won't be documented until completed.

## Usage

### Important Terminology

- Effect - A visual effect, such as particles, guardian lasers, or the dying dragon animation.
- Point - A representation of a coordinate in a given minecraft world which is assigned an integer id.

### Defining Points

Every effect, either internally or externally, relies on at least 1 point being defined. As it currently stands, Aurora
only officially supports effects in 1 world, as some effects use the first point you define (point 0) to get the world.
As mentioned in the points.yml section, you can define points ingame with `/point add [x] [y] [z]`. This command cannot
be executed from the console. You can also define points manually in the points file, but it's not recommended doing so.

### Actually Making These Effects Happen

Aurora uses a custom REST API to manage effects. This API is hosted on port 8001 by default, but this can be changed in
the config if necesarry. **It is strongly recommended to *not* expose the API to the open internet. There is no
authorization or protection in place against attackers, and the API has full console access.** To trigger an effect
action, simply send a POST request to the respective endpoint.

#### Wait, what's a REST API?

If this is your first time hearing about this, worry not for this section exists. A REST API is one way for computers to communicate with each other over the internet, based on the http protocol (the same one which you use to surf the web). Here's an [article](https://pusher.com/tutorials/understanding-rest-api#rest) if you want to read up on this a bit more.

#### OK, how do I send this new fandangled http requests?

There are a lot of tools for all sorts of programming languages to send these requests for you (e.g OkHttp for java, axios for javascript, simply google "send an http request with [insert your programming language here]" and you shall find). There's even tools like cURL and Insomnia (the latter is what I use to test the plugin!)

## API Endpoints

### Stop

#### Stop all

`POST /effects/all/stop`

Stops all active effect groups

#### Stop specific effect

`POST /effects/{uuid}/stop`

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID) of an active effect group. regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``.

### Bossbar

#### Clear

`POST /bar/clear`

Clears any active boss bars

#### Set

`POST /bar/set`

Sets the specified boss bars

**Request Body:**

```json
[
  {
    "color": "{color}",
    "title": "{title}"
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
|  `color`  | `string` | the boss bar color. possible values are `PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE` |
|  `title`  | `string` | the boss bar title

### Console

#### Commands

`POST /commands`

Run an array of console commands

**Request Body:**

```json
["{command}", //...]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `command` | `string` | a spigot console command to run

### Dragon

#### Start

`POST /effects/dragon/{uuid}/start`

Starts a group of dying dragon effects

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "pointId": {pointId},
    "static": {isStatic}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `pointId` | `int` | an existing point id where the dragon will spawn
| `static`  | `boolean` | whether or not the dragon should rise into the air

#### Stop

`POST /effects/dragon/stop`

Stops all effect groups of the dragon type

#### Restart

`POST /effects/dragon/{uuid}/restart`

Restarts the death animation of an existing group of dragon effects

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

### Lasers

#### End Crystal Laser

##### Start

`POST /effects/endlaser/{uuid}/start`

Starts a group of end crystal lasers

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "start": {start},
    "end": {end}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `start` | `int` | an existing point id where the laser will start
| `end` | `int` | an existing point id which is the laser's target

##### Stop

`POST /effects/endlaser/stop`

Stops all effect groups of the end crystal laser type

#### Guardian Laser

##### Start

`POST /effects/laser/{uuid}/start`

Starts a group of guardian lasers

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "start": {start},
    "end": {end}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `start` | `int` | an existing point id where the laser will start
| `end` | `int` | an existing point id which is the laser's target

##### Trigger

`POST /effects/laser/{uuid}/trigger`

Causes the guardian's beam to restart changing colors

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID) of an existing guardian laser effect group. regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``.

##### Stop

`POST /effects/laser/stop`

Stops all effect groups of the guardian laser type

#### Player Targeting Guardian Laser

##### Start

`POST /effects/targetedlaser/{uuid}/start`

Starts a group of guardian lasers

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "start": {start}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `start` | `int` | an existing point id where the laser will start

##### Restart

`POST /effects/targetedlaser/{uuid}/restart`

Causes the guardian's beam to change its target to a new player

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID) of an existing guardian laser effect group. regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``.

##### Trigger

`POST /effects/targetedlaser/{uuid}/trigger`

Causes the guardian's beam to restart changing colors

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID) of an existing guardian laser effect group. regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``.

##### Stop

`POST /effects/targetedlaser/stop`

Stops all effect groups of the guardian laser type

### Lightning

#### Start

`POST /effects/lightning/{uuid}/start`

Starts a group of lightning strikes that occur every game tick

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "pointIds": {pointIds},
    "spigotStrike": {spigotStrike}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `pointId` | `int array` | an existing point id where the dragon will spawn
| `spigotStrike`  | `boolean` | whether or not the plugin uses spigot's built-in API to strike lightning. in most cases this can just be set to false to use protocollib, but if that's not working try setting this to true.

#### Trigger

`POST /effects/lightning/{uuid}/trigger`

Triggers a group of lightning strikes to occur once.

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "pointIds": {pointIds},
    "spigotStrike": {spigotStrike}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `pointId` | `int array` | an existing point id where the dragon will spawn
| `spigotStrike`  | `boolean` | whether or not the plugin uses spigot's built-in API to strike lightning. in most cases this can just be set to false to use protocollib, but if that's not working try setting this to true.

### Particles

[WIP]

### Potion

#### Start

`POST /effects/potion/{uuid}/start`

Applies a potion effect to every player

**NOTE: point 0 MUST be defined in order for this effect to work. The effect will be applied to all players in point 0's
world**

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "type": {type},
    "amplifier": {amplifier}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `type` | `string` | the potion effect to be applied (for possible values, see [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html)
| `amplifier`  | `int` | the potion effect's amplifier

#### Stop

`POST /effects/potion/stop`

Stops all effect groups of the potion type

### Time Shift

#### Start

`POST /effects/time/{uuid}/start`

Shifts the ingame time of the world every other game tick

**NOTE: point 0 MUST be defined in order for this effect to work. The effect will be applied to point 0's world**

**Parameters:**

| Parameter | Description |
| --------- | ----------- |
| `uuid`    | a type 4 unique identifier (UUID). regex: ``[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}``. this UUID should be unique to this group.

**Request Body:**

```json
[
  {
    "amount": {amount}
  }
  //...
]
```

| Parameter |   Type   | Description |
| --------- | -------- | ----------- |
| `amount` | `int` | the amount that the ingame time will be shifted by in ticks