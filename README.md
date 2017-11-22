# AuthYou - beta v1.2.1

![N|Solid](https://proxy.spigotmc.org/1764aa089b66a910c0d68ca867ac20685fae3fd2?url=http%3A%2F%2Fplugins.mreturkey.de%2Fpics%2Fauthyou-logo.jpg)

# First of all, what is AuthYou ?
AuthYou is a lightweight and performance-friendly login/authentication plugin for cracked server.
This plugin is similar and compatible to [AuthMe Reloaded](https://www.spigotmc.org/resources/authme-reloaded.6269/), but not a copy.

# Is AuthYou compatible with AuthMe?
Yes, you only need to copy your config.yml to the DataFolder of AuthYou.
Add **"mySQLColumnUUID: uuid"** to DataSource in the config.yml.
But I recommend: don't copy the config.yml from AuthMe! Let AuthYou generate the new config.
Then you need to add a new column (uuid) to your old **AuthMe Database**.
Download **AuthYou**.
At least replace the jar-file of AuthMe (reloaded) with the downloaded jar-file of AuthYou.

Tutorials will coming soon...

_Or just delete all tables and files from AuthMe to generate new files._

# Are there differences between AuthMe and AuthYou ?
Yes, here is a list:
* Plugin is x10 smaller then AuthMe
* UUID Support
* Session's will be saved in a separate table
* AuthYou only use MySQL as data soruce.
* All saved passwords are encrypted with SHA256 and is compatible with all already saved passwords in AuthMe encrypted with * SHA256.
* E-Mail Service will not supported yet.
* GEO-Blocking will not supported yet.

# Features
* UUID Support
* Multi-Threading (lag-free)
* Username spoofing protection.
* Session Login
* Editable translations and messages
* Caching
* Custom MySQL tables/columns names (useful with forums databases) - only for registrations
* Saves the quit location of the player
* Compatible with AuthMe Reloaded
* Protect player's inventory until a correct Authentication
* Supports currently only SHA256 encryption algorithm for passwords
* Install the dependencies and devDependencies and start the server.

# Setup
Detailed Video-Tutorials,How To's and Setup Guide will coming soon!

# Commands
### Player Commands
* ```/register <password> <password> OR /reg <password> <password>```
* ```/login <password>``` OR ```/l <password>```
* ```/logout```
* ```/changepassword <password> <password>```

### Admin Commands
* ```/authyou reload``` - Reloads the config
* ```/authyou changepassword <username> <password>``` - Changes the password of the given username

# Permissions
* authyou.player.* - _for all player commands_
* authyou.admin.* - _for all admin commands_
 
# Config
```sh
DataSource:
  mySQLHost: localhost   #host of mysql
  mySQLPort: 3306   #port of mysql 
  mySQLDatabase: authyou   #database of mysql
  mySQLUsername: username   #username for login into mysql
  mySQLColumnUUID: uuid   #uuid column-name of 'authyou' table
  mySQLPassword: pass1234   #password for login into mysql
  mySQLTablename: authyou   #mysql table name for registrations
  mySQLColumnName: username   #username column-name of 'authyou' table
  mySQLColumnPassword: password  #password column-name of 'authyou' table
  mySQLColumnIp: ip  #ip column-name of 'authyou' table
  mySQLColumnLastLogin: last_login  #last login column-name of 'authyou' table
  mySQLlastlocX: x  #X Location column-name of 'authyou' table
  mySQLlastlocY: y  #Y Location column-name of 'authyou' table
  mySQLlastlocZ: z  #Z Location column-name of 'authyou' table
  mySQLlastlocWorld: world  #world name column-name of 'authyou' table
  mySQLColumnId: id  #id column-name of 'authyou' table
  mySQLColumnLogged: is_logged  #is logged column-name of 'authyou' table
settings:
  sessions:
    enabled: true  #recommend: true
    timeout: 3   #is linked with "TimeUnit" for example: 3 DAYS (timeout means, when session will be expired)
    TimeUnit: DAYS  #is linked with "timeout" for example: 3 DAYS
    sessionExpireOnIpChange: true   #if true, the session will expire when the ip changes. (recommend: true)
  restrictions:
    allowChat: false   #allow the chat while login
    allowCommands:   #list of commands which can bypassed
    - /login
    - /l
    - /register
    - /reg
    maxRegPerIp: 1   #max registrations per ip
    maxNicknameLength: 20   #max lenght of player's username
    kickNonRegistered: false   #kick all non-registered players
    kickOnWrongPassword: false   #kick player if password was wrong
    kickViaBungeeCord: false   #If you have a bungeecord server, set this to 'true', to kick vie bungecord
    minNicknameLength: 3   #min lenght of player's username
    allowMovement: false   #is movement allowed while login
    timeout: 30   #how long will be wait until the player logs in
    allowedNicknameCharacters: '[a-zA-Z0-9_]*'   #allowed nickname characters
    allowedPasswordCharacters: '[\x21-\x7E]*'   #allowed password characters
  registration:
    enabled: true   #is registrations enabled
  security:
    minPasswordLength: 4   #min length of passwords
Security:
  SQLProblem:
    stopServer: true   #stop's the server if the plugin throws a SQL Exeption. (recommend: true) 
```
# Bugs
Please contact me, if you found bugs.

**Good Luck! & Thanks!**
\- mReTurkey


