# Simple Text Editor

A simple text editor application written in Java using Swing.

## Features

- Basic text editing functionality
- File operations (new, open, save, save as)
- Edit operations (undo, redo, cut, copy, paste, find, select all)
- Change tracking to prompt for saving unsaved changes

## Project Structure

- `src/Main.java` - The main class that launches the application
- `src/TextEditor.java` - The text editor implementation
- `src/test/TextEditorTest.java` - Unit tests for the text editor
- `src/test/IntegrationTest.java` - Integration tests for the application

## How to Run

### Running the Application

To run the application, execute the `Main` class:

```bash
javac src/TextEditor.java src/Main.java
java -cp src Main
```

### Running the Tests

To run the unit tests:

```bash
javac src/TextEditor.java src/test/TextEditorTest.java
java -cp src test.TextEditorTest
```

To run the integration tests:

```bash
javac src/TextEditor.java src/Main.java src/test/IntegrationTest.java
java -cp src test.IntegrationTest
```

## Usage

1. Launch the application
2. Use the menu bar to access file and edit operations
3. Edit text in the main text area
4. Save your work using the File menu

## Implementation Details

The text editor is implemented using Java Swing and includes:

- A JTextArea for text editing
- A JMenuBar with File and Edit menus
- File operations using Java I/O
- Undo/redo functionality using UndoManager
- Change tracking to prompt for saving unsaved changes

## Testing

The application includes two types of tests:

1. Unit tests that verify the core functionality of the text editor
2. Integration tests that verify the application can be launched and used

The tests use a simple assertion framework and don't require any external testing libraries.