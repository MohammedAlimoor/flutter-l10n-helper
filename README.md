# Flutter L10n Helper Plugin for IntelliJ IDEA

**Flutter L10n Helper** is an IntelliJ IDEA plugin designed to help Flutter developers easily internationalize string literals in their Dart code. The plugin simplifies the process of extracting ha[...]

## Features

- **String Literal Detection**: Automatically detects string literals within Dart files, including single quotes, double quotes, and multiline strings.
- **Context Menu Action**: Provides a context menu action to generate localization keys from string literals.
- **Key Generation**: Generates appropriate localization keys by converting text strings (e.g., "Hello World" → "hello_world").
- **ARB File Management**: Automatically adds new key-value pairs to the specified ARB file (e.g., `lib/l10n/app_en.arb`).
- **Code Replacement**: Replaces hardcoded strings in the code with references to the localization keys using `AppLocalizations.of(context)!`.
- **Import Management**: Ensures that the necessary import for `AppLocalizations` is present in the file.

## Installation

1. Download and install IntelliJ IDEA (Community Edition or Ultimate Edition).
2. Open IntelliJ IDEA and navigate to `File > Settings > Plugins`.
3. Search for "Flutter L10n Helper" and install the plugin.

## Usage

1. Open a Flutter project in IntelliJ IDEA.
2. Locate a Dart file containing string literals that you want to internationalize.
3. Right-click on any string literal (e.g., `'Hello World'`, `"Button text"`, etc.).
4. Select "Generate L10n Key" from the context menu.
5. The plugin will:
   - Extract the string content.
   - Generate a key (e.g., "Hello World" → "hello_world").
   - Add the key-value pair to your ARB file (`lib/l10n/app_en.arb`).
   - Replace the string literal with `AppLocalizations.of(context)!.hello_world`.
   - Add the necessary import if needed.

## Example

Given the following Flutter code:

```dart
Text('Hello World')