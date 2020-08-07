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

Aurora is part of Project Stardust, alongside [Solarflare](https://github.com/eynorey/solarflare), and is a java plugin designed to run on minecraft servers with the spigot or paper architecture.

## Installation
### Prerequisites
This plugin works with every version past 1.15. A server running paper or spigot with at least 4GB of ram is required, but we recommend anywhere between 8-64GB depending on youre existing plugins/expected player traffic. ProtocolLib must be installed for this plugin to work.

### Instructions
1. Stop your spigot/paper server
2. Drag and drop the .jar file into the plugins folder
3. Start your server to generate plugin files

## Configuration
### What do all these files do?
#### config.yml
This file is a standard plugin config. Here, you can configure what IP and port that the plugin listens to HTTP requests on, and info related to Solarflare (this part is optional, you can totally code up your own control solution if you wanted to)

#### points.yml
The Aurora effects system works off of points. they tell the plugin where effects should go and allows for more efficient requests. You can either add points ingame or do so here directly (be careful!).

#### staticEffects.json
If you want certain effects to be always active while the plugin is running, you can define them here.

## Usage
TODO: write this later you dummy