# VulcanVoting

A Minecraft voting plugin that rewards players for voting on server lists. Features vote rewards, vote parties, queued votes for offline players, and PlaceholderAPI integration.

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

- [NuVotifier](https://github.com/NuVotifier/NuVotifier)
- [VulcanAPI](https://github.com/VulcanDev)

## Installation

This plugin is free through the Vulcan Loader found in the client panel [Here](https://vulcandev.net/).

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
- WineSpigot.jar (Or just normal spigot 1.8+)
- VulcanLib.jar
- nuvotifier-2.7.2.jar
- VulcanAPI.jar

Place these in `C:/Development/libs/` or update the paths in `pom.xml`.

## Support

If you encounter any issues or have suggestions:
- Open an issue on GitHub
- Developers discord tags: xanthard001 or officialgaming