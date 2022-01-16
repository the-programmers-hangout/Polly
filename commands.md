# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| [Argument]  | Argument is not required.      |

## Basics
| Commands      | Arguments | Description                                                   |
| ------------- | --------- | ------------------------------------------------------------- |
| AlertChannel  | Channel   | Set the channel where alerts will be output.                  |
| Cooldown      | Time      | Set the cooldown between macro invokes                        |
| LogChannel    | Channel   | Set the channel where logs will be output.                    |
| Prefix        | Prefix    | Set the prefix required for the bot to register a command.    |
| Setup         |           | Setup a guild to use Polly                                    |
| StaffRole     | Role      | Set the role required to use this bot.                        |
| TrackedMacros | Enabled   | Toggle tracked macros (macros that post to the alert channel) |

## IgnoreList
| Commands   | Arguments        | Description                            |
| ---------- | ---------------- | -------------------------------------- |
| Ignore     | add/remove, User | Add/remove users from the ignore list. |
| IgnoreList |                  | Show ignore list.                      |

## Macros
| Commands        | Arguments                         | Description                                                                                                   |
| --------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| AddAlias        | Name, [Channel], Alias            | Add an alias to a macro                                                                                       |
| AddChannelMacro | Name, Category, Channel, Contents | Adds a macro to a specific channel                                                                            |
| AddMacro        | Name, Category, Contents          | Adds a macro (for all channels)                                                                               |
| AddTrackedMacro | Name, Category, Contents          | Adds a tracked macro (for all channels)                                                                       |
| EditCategory    | Name, [Channel], New Category     | Edits the category of a macro                                                                                 |
| EditMacro       | Name, [Channel], Contents         | Edits the contents of a macro                                                                                 |
| ListAllMacros   |                                   | Lists all macros available in the guild, grouped by channel.                                                  |
| ListMacros      | [Channel]                         | Lists all macros available in the given channel. If no channel is specified, defaults to the current channel. |
| MacroInfo       | Name, [Channel]                   | Get Information for a macro                                                                                   |
| MacroStats      | [asc/desc]                        | Get statistics on most and least used macros                                                                  |
| RemoveAlias     | Name, [Channel], Alias            | Remove an alias from a macro                                                                                  |
| RemoveMacro     | Name, [Channel]                   | Removes a macro                                                                                               |
| SearchMacros    | Text                              | Search the available macros available                                                                         |
| Track           | Name, [Channel]                   | Converts an existing macro to a tracked (alert) macro                                                         |
| Untrack         | Name, [Channel]                   | Removes tracking from an existing macro                                                                       |

## Utility
| Commands | Arguments | Description          |
| -------- | --------- | -------------------- |
| Help     | [Command] | Display a help menu. |

