# RadioBotsEU QueryBots - Inaktiv

Dies ist die allgemeine Dokumentation zur Nutzung der RESTful-API


# Contexts

Es gibt verschiedene Contexts (Endpoints) für die QueryBots. Sie werden einfach per **Request** aufgerufen.
Es gibt in der Version **1.2.2-PRODUCTION** folgende Contexts:

 - ChannelsContext *(Gibt eine Auflistung aller Channel des TeamSpeaks zurück. (Nicht chronologisch)*
	 - Benötigte Argumente: `id`
	 - URL: `channels`
- Create Context *(Erstellt neue QueryBots)*
	 - Benötigte Argumente: `querypw` (TS-Query-Passwort), `queryuser` (TS-Query-Nutzer), `server` (TS3-Adresse), `nickname` (Nickname des Bots)
	 -  URL: `create`
- DevContex (Wird als Test-Context verwendet):
	> **Note:** Dieser Context ist in der PRODUCTION Version **deaktiviert**.
	- Benötigte Argumente:*Keine*
	- URL: `dev`
- RefreshContext *(Löscht alle aus der Datenbank gespeicherten, lokalen Daten und fetcht sie neu)*
	 - Benötigte Argumente: `id`
	 - URL: `refresh`
- SendMessageContext *(Versende eine private Nachricht an Nutzer)*
	 - Benötigte Argumente: `id`, `user` (Empfänger), `message` (Nachricht)
	 - URL: `sendmessage`
- SetNameContext *(Änder den Nicknamen des Bots)*
	 - Benötigte Argumente: `id`, `name` (Neuer Name)
	 - URL: `setname`
- StartContext *(Starte den QueryBot)*
	 - Benötigte Argumente: `id`
	 - URL: `start`
- StatusContext *(Erhalte Informationen über den Bot)*
	- Rückgabe von: Server-IP, RadioBots-Host-Standort, Nickname, Onlinenutzer auf dem TS, aktive Module (als Array), Querybot-UUID
	 - Benötigte Argumente: `id`
	 - URL: `status`
- StopContext *(Stoppe den QueryBot)*
	 - Benötigte Argumente: `id`
	 - URL: `stop`
 
 Die Ziel-URL setz sich zusammen: http://85.202.163.131:`PORT (48)`/`Context-URL` =>*http://85.202.163.131:48/start*

Bei **jedem** APICall muss der Wert `apikey` übergeben werden. Dies ist das `masterPassword` in der config.json.

Die Werteübergabe erfolgt über einen einzigen **Headerparameter** namens `data`. Dieser muss einen JSON String mit allen Argumenten obligatorisch für den Context + Master-APIKey übergeben.
Für den StartContext beispielsweise: `{"apikey": "masterPassword", id: "2"}`

## Module

Module sind de/-aktivierbar. In Version 1.2.2-PRODUCTION sind diese derzeit nur über MySQL Query veränderbar. Module werden beim Start eines QueryBots aktiviert.
> **Übriges:** Jede Aktivierung eines Moduls wird in der Console geloggt.

Um Module zu aktivieren müssen diese in der `modules` Zelle **als String-Array** eingetragen werden. Als String-Index nutzt man die Modul-ID.

Es gibt folgende Module:
- AFKBot
	- Modul-ID: `afkbot`
	- Verschiebt User ab bestimmter Zeit in Channel **mit dem Channel-Topic** `AFK_CHANNEL` - Wird kein Channel gefunden wird der Nutzer in den Default-Channel verschoben.
	- Wartezeit bis Move wird im Table `query_bot_entity` in **Sekunden** angegeben.
	- Nutzer wird bei Move angestubst. => Nachricht änderbar in `query_bot_entity`
- BadNickNames
	- Modul-ID: `badnicks`
	- Alle 10 Sekunden werden alle Spieler auf ihren Nicknamen kontrolliert. Der Filter wird selbst über den Table `query_bot_badnicks` festgelegt. Gespeichert wird *| id | nick |*. Jeder Bot hat seine eigenen Filter, welche vom Nutzer eingestellt werden.
	> Context zum Hinzufügen / Entfernen von nicht erlaubten Nicks ist im Anmarsch. huch
	
- Support Bot
	- Modul-ID: `supportbot`
	- Der Channel mit dem **Channel-Topic** `SUPPORT_WAITING` wird **nur** vom Bot iteriert.
	- Die Tabelle `query_bot_support_bot_entity` enthält alle Daten zum Modul. Die Spalte `tsgroup` definiert die TS-Gruppe, die bei Beitritt des Channels benachrichtigt wird.
	- Die Team-Nachricht kann `%user%` mit dem Nutzernamen ersetzen.
- Willkommensnachricht
	- Modul-ID: `welcome`
	- Die Willkommensnachricht wird in der Tabelle `query_bot_entity` gespeichert.
	- Es kann `%time%` mit der derzeitigen Uhrzeit ersetzt werden.

## Good to know

Hier noch ein paar Dinge, die man vielleicht wissen sollte:
- Die MySQL wird jede Stunde mit einer `SELECT` Query am Leben erhalten, um random Disconnections über Nacht zu vermeiden.
- Das Querypasswort wird in Base64 gespeichert. Überleg dir also, wem du Datenbankzugriff gibst.
- Java ist sick, deswegen ist es das System auch :*
- Die Console logt ziemlich jeden Scheiß, zeigt also auch jede StackTrace an, die ich vergessen habe zu minimieren. Log-File System folgt demnächst.
