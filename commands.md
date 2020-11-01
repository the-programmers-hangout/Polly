# Commands

## Key 
| Symbol      | Meaning                        |
| ----------- | ------------------------------ |
| (Argument)  | Argument is not required.      |

## Basics
| Commands   | Arguments | Description                                                |
| ---------- | --------- | ---------------------------------------------------------- |
| LogChannel | Channel   | Set the channel where logs will be output.                 |
| Prefix     | Prefix    | Set the prefix required for the bot to register a command. |
| Setup      |           | Setup a guild to use Macaroni                              |
| StaffRole  | Role      | Set the role required to use this bot.                     |

## IgnoreList
| Commands   | Arguments               | Description                                           |
| ---------- | ----------------------- | ----------------------------------------------------- |
| IgnoreList | (add/remove/list), User | List users and add/remove users from the ignore list. |

## Macros
| Commands     | Arguments                           | Description                                                                                                   |
| ------------ | ----------------------------------- | ------------------------------------------------------------------------------------------------------------- |
| add          | Name, Category, (Channel), Contents | Adds a macro to a specific channel or globally, if no channel is given                                        |
| edit         | Name, (Channel), Contents           | Edits the contents of a macro                                                                                 |
| editcategory | Name, (Channel), New Category       | Edits the category of a macro                                                                                 |
| list         | (Channel)                           | Lists all macros available in the given channel. If no channel is specified, defaults to the current channel. |
| listall      |                                     | Lists all macros available in the guild, grouped by channel.                                                  |
| remove       | Name, (Channel)                     | Removes a macro                                                                                               |

## Utility
| Commands | Arguments | Description          |
| -------- | --------- | -------------------- |
| Help     | (Command) | Display a help menu. |

