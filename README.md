# VulcanVoting

A comprehensive Minecraft voting plugin that rewards players for voting on server lists. Features vote rewards, vote parties, queued votes for offline players, and PlaceholderAPI integration.

## Features

- **Vote Rewards**: Automatically reward players when they vote for your server
- **Vote Parties**: Trigger server-wide rewards when a target number of votes is reached
- **Queued Votes**: Store votes for offline players and reward them when they join
- **Vote GUI**: Interactive menu for viewing vote stats, links, and vote party progress
- **PlaceholderAPI Integration**: Display voting statistics in other plugins
- **Service Cooldowns**: Prevent vote spam with per-service cooldown tracking
- **Toggle Vote Messages**: Players can enable/disable vote broadcast messages
- **Persistent Data**: All vote data is saved and loaded automatically

## Requirements

- Java 8 or higher
- Spigot/Paper 1.8+
- [NuVotifier](https://github.com/NuVotifier/NuVotifier) 2.7.2+
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 2.11.5+
- [VulcanAPI](https://github.com/VulcanDev) 2.7+

## Installation

1. Download the latest release of VulcanVoting
2. Install Dependencies
3. Place VulcanVoting.jar in your plugins folder
4. Restart your server
5. Configure the plugin in `plugins/VulcanVoting/config.yml`

## Configuration

The plugin generates a `config.yml` file with the following customizable options:

### Vote Rewards
```yaml
vote-rewards:
  - "eco give %player% 100000"
  - "crates give %player% common 1"
  - "gold give %player% 25"
```

### Vote Party
```yaml
vote-party:
  amount-needed: 100
  rewards:
    - "crates give %player% legendary 1"
```

### Vote Links
```yaml
vote-links:
  - "&6Links for voting below"
  - "&ehttps://your-server-voting-link.com"
```

### Custom Messages
Fully customize vote messages for both the voting player and server-wide broadcasts.

### Vote GUI
Configure the appearance, items, and layout of the voting GUI menu.

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/vote` | Open the vote GUI | `vulcanvoting.vote` |
| `/vote` (alias: `/vulcanvote`) | Alternative command | `vulcanvoting.vote` |

## Placeholders

When PlaceholderAPI is installed, the following placeholders are available:

- `%vulcanvoting_totalvotes%` - Total number of votes for a player
- `%vulcanvoting_voteparty_current%` - Current vote party progress
- `%vulcanvoting_voteparty_amount%` - Votes needed for vote party

## Developer API

VulcanAPI provides an API for developers to integrate voting functionality:


## Building from Source

1. Clone the repository:
```bash
git clone https://github.com/yourusername/VulcanVoting.git
```

2. Build with Maven:
```bash
mvn clean install
```

3. The compiled jar will be in the `target` folder

**Note**: You'll need to provide the following dependencies locally:
- WineSpigot.jar
- VulcanLib.jar
- nuvotifier-2.7.2.jar
- VulcanAPI.jar

Place these in `C:/Development/libs/` or update the paths in `pom.xml`.

## Support

If you encounter any issues or have suggestions:
- Open an issue on GitHub
- Contact the developer: OfficialGaming

## License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

## Credits

- **Author**: OfficialGaming
- **Version**: 1.0
- **Dependencies**: NuVotifier, PlaceholderAPI, VulcanLib, VulcanAPI

## Changelog

### Version 1.0
- Initial release
- Vote rewards system
- Vote party functionality
- Queued votes for offline players
- Interactive vote GUI
- PlaceholderAPI integration
- Service cooldown tracking