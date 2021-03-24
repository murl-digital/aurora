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