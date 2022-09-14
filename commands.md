# Commands

## Key 
| Symbol      | Meaning                        |
|-------------|--------------------------------|
| [Argument]  | Argument is not required.      |
| /Category   | This is a subcommand group.    |

## /Macros
| Commands        | Arguments                         | Description                                           |
|-----------------|-----------------------------------|-------------------------------------------------------|
| Add             | Name, Category, Contents          | Adds a macro (for all channels)                       |
| AddAlias        | Macro, Alias, [Channel]           | Add an alias to a macro                               |
| AddChannelMacro | Name, Category, Channel, Contents | Adds a macro to a specific channel                    |
| AddTrackedMacro | Name, Category, Contents          | Adds a tracked macro (for all channels)               |
| Edit            | Macro, Contents, [Channel]        | Edits the contents of a macro                         |
| EditCategory    | Macro, Category, [Channel]        | Edits the category of a macro                         |
| Remove          | Macro, [Channel]                  | Removes a macro                                       |
| RemoveAlias     | Macro, Alias, [Channel]           | Remove an alias from a macro                          |
| Track           | Macro, [Channel]                  | Converts an existing macro to a tracked (alert) macro |
| Untrack         | Macro, [Channel]                  | Removes tracking from an existing macro               |

## Basics
| Commands      | Arguments                                         | Description                                                   |
|---------------|---------------------------------------------------|---------------------------------------------------------------|
| AlertChannel  | Channel                                           | Set the channel where alerts will be output.                  |
| Cooldown      | Time                                              | Set the cooldown between macro invokes                        |
| LogChannel    | Channel                                           | Set the channel where logs will be output.                    |
| Prefix        | Prefix                                            | Set the prefix required legacy macro invocations.             |
| Setup         | LogChannel, AlertChannel, Cooldown, TrackedMacros |                                                               |
| TrackedMacros | Enabled                                           | Toggle tracked macros (macros that post to the alert channel) |

## IgnoreList
| Commands   | Arguments    | Description                            |
|------------|--------------|----------------------------------------|
| Ignore     | option, User | Add/remove users from the ignore list. |
| IgnoreList |              | Show ignore list.                      |

## Macros
| Commands      | Arguments        | Description                                                  |
|---------------|------------------|--------------------------------------------------------------|
| ListAllMacros |                  | Lists all macros available in the guild, grouped by channel. |
| ListMacros    | [Channel]        | Lists all macros available in the given channel.             |
| MacroInfo     | Macro, [Channel] | Get Information for a macro                                  |
| MacroStats    | [option]         | Get statistics on most and least used macros                 |
| SearchMacros  | Text             | Search the available macros available                        |
| macro         | Macro, [Target]  | Search and send a macro                                      |

## Utility
| Commands | Arguments | Description          |
|----------|-----------|----------------------|
| Help     | [Command] | Display a help menu. |
| info     |           | Bot info for Polly   |

