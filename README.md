# AMTE

AMTE, or "another modal text editor" is, as the name implies, a modal text editor, heavily inspired by vim. It is written in Java.

## MODAL EDITING
AMTE is split into three modes, each with their own functionality and purpose:

### TOOL MODE
The equivalent to vim's normal mode, tool mode uses the keys for navigating and editing rather than typing. A list of tools can be found below

- a: enter typing mode after the current position of the cursor
- A: enter typing mode at the end of the paragraph
- b: move the cursor the the beginning of the next word (ended by whitespace of special characters), or the beginning of the previous word if already on the beginning.

- B: move the cursor to the beginning of the current WORD (ended by whitespace), or the beginning of the previous WORD if already on the beginning
- c: equivalent to 'd' + 'i'
- C: delete from the cursor position to the end of the current paragraph and enter typing mode
- d: start a delete chord. repeat the character to delete the entire paragraph. Follow with a cursor moving chord (w, W, b, B, e, E, f, F, t, T) to delete between the current cursor position and where the cursor lands. Follow with 'a' or 'i' and one of the follwing: ([{<\"' to delete between the delimiters excluding ('i') or including ('a') the delimiter, or w / W to delete the current word or WORD, respectively
- D: delete from the cursor position to the end of the current paragraph
- e: move the cursor to the end of the current word (ended by whitespace or special characters), or the end of the next word if already on the end
- E: move the cursor to the end of the current WORD (ended by whitespace), or the end of the next WORD if already on the end
- f: move the cursor to the next occurrence of the next character typed
- F: move the cursor to the previous occurrence of the next character typed
- h: move the cursor one character to the left
- i: enter typing mode
- I: enter typing mode at the start of the paragraph
- j: move the cursor one paragraph down
- J: join the next paragraph to the end of the current paragraph
- k: move the cursor one paragraph up
- l: move the cursor one character to the right
- n: move the cursor to the next occurrence of the last search using the find command
- N: move the cursor to the previous occurrence of the last search using the find command
- o: create a new paragraph below the current one and enter typing mode at the start of it
- O: create a new paragraph above the current one and enter typing mode at the start of it
- p: paste copied content from the internal clipboard after the cursor
- p: paste copied content from the internal clipboard before the cursor
- s: delete the character under the cursor and enter typing mode (equivalent to 'x' + 'i')
- t: move the cursor to just before the next occurrence of the next character typed
- t: move the cursor to just after the previous occurrence of the next character typed
- u: undo the last modifying action
- U: redo the last undone action
- w: move the cursor to the start of the next word (words are separated by whitespace or special characters)
- W: move the cursor to the start of the next WORD (WORDs are separated by whitespace)
- x: delete the character under the cursor
- X: delete the character directly before the cursor
- y: start a copy chord. repeat the character to delete the entire paragraph. Follow with a cursor moving chord (w, W, b, B, e, E, f, F, t, T) to copy between the current cursor position and where the cursor lands. Follow with 'a' or 'i' and one of the follwing: ([{<\"' to copy between the delimiters excluding ('i') or including ('a') the delimiter, or w / W to copy the current word or WORD, respectively
- : (colon): enter command mode. use command 'list' to get a list of commands
- ? (question mark): suggest spelling corrections for the word under the cursor

### TYPING MODE
The equivalent to vim's insert mode, typing mode allows the user to type normally. The cursor can still be moved with the arrow keys, and the escape key returns to tool mode.

### COMMAND MODE
Command mode is primarily used for manipulating the active file. Below is a full list of supported commands:

- list: displays a list of all commands.
- info: provides usage information for a given command
- help: provides usage information for a given tool
- rename: changes the name of the file to a given new name
- save-quit: saves all changes, then closes the file
- quit: tries to close the file, aborting if there are unsaved changes
- force-quit: closes the file, discarding any unsaved changes
- save: saves all changes to the file
- open: opens a new file
- next: switches to the next open file
- previous: switches to the previous open file
- find: move the cursor to the next occurrence of a specified string

## SPELL CHECKING
If an unknown word is found, it will be highlighted. Pressing '?' while the cursor is over an unknown word generates suggested corrections for that word. The user can then accept one of those corrections or add the unknown word to the dictionary. What language is used for spell checking, as well as if spell checking is done at all can be specified in the settings.

## CUSTOMIZATION
All user customizable data is held in the '.amte' folder, which is located in the user's home directory.
`settings.json` contains user preferences, such as what information should be shown in the UI panel at the bottom of the screen, how spell checking and copy tools behave, and more.
The `dict` folder contains word lists used by the spell checker. Users can add new languages by providing a word list in `dict/base`. Words added to the dictionary through the editor are added in `dict/user`. The active spell checking language can be changed in the settings.
The `languages` folder contains language files for all text in the user interface. The active language chan be changed in the settings.
The `rendering` folder contains rendering data for code files and markdown code blocks. The editor will automatically use these when relevant.
The `themes` folder contains color themes. If a foreground or background color is not specified, the color specified by its container is used (the four containers are the number panel, the ui panel, the text area and the area for temporary messages).


## SMART LISTS
When currently typing a bullet list, checkbox list or a numbered list, pressing enter automatically inserts a continuation of that list on the new line.

## MULTI-FILE
It is possible to have multiple files open at the same time. See the section about command mode above for the specific commands. Closing all open files closes the editor.

## HOT LOADING
If the underlying file is changed by a different program, the user is notified of the change. If the file has not been modified since being opened, the file contents are automatically updated.


## TECHNICAL DETAILS
The following section covers some details about the implementation of how amte works under the hood.

### EXTERNAL DEPENDENCIES
The displaying of content and accepting of input is done with lanterna 3.1.1. User-provided json files (e.g. color themes, settings) are parsed using jackson 2.17.2.

### INPUT PROCESSING & UNDO-REDO HISTORY
Each mode the editor can be in implements its own input processing logic in a State Pattern. Inputs are converted to corresponding implementations of a command pattern, which also allows for a light weight undo-redo system, as each command knows how to undo itself. 

### RENDERING
The rendering logic is performed on a background thread to avoid input delays. The file contents are copied atomically, and then parsed line by line, in a double buffered data structure to avoid having to lock every read access. The rendering logic only runs when needed, i.e. after a full rendering pass, it won't re-run unless there has been a change that would require a new rendering pass. Highlighting of code works with a simple pattern matching approach, as this is primarily a text editor.

### SYSTEM CLIPBOARD
Interaction with the system clipboard is done with the java standard library.

### PARSING USER-PROVIDED DATA
The user is able to provide additional data to customize the editor. They can specify color themes, rendering instructions for code files and markdown code blocks, and settings as json files. They are parsed with jackson, ignoring wrongly formatted data by replacing them with default values.

### SPELL CHECKING
The spell checking is based on a custom implementation of a Trie (Prefix Tree). Users can add new languages by providing a word list in the data folder and changing the active language. Users can choose to add words to the dictionary, which will be stored in a separate file. The editor can suggest corrections by searching the Trie for words with a Levenshtein Distance of 1, as well as words where two adjacent letters are swapped.

