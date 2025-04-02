# Flutter L10n Helper Plugin for IntelliJ IDEA

**Flutter L10n Helper** is an IntelliJ IDEA plugin designed to help Flutter developers easily internationalize string literals in their Dart code. The plugin simplifies the process of extracting hardcoded text strings and converting them into localization keys, which are then added to ARB (Application Resource Bundle) files.

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
```

After using the "Generate L10n Key" action, it will be transformed into:

```dart
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

Text(AppLocalizations.of(context)!.hello_world)
```

And the ARB file (`lib/l10n/app_en.arb`) will be updated with:

```json
{
  "hello_world": "Hello World"
}
```

## Contributing

Contributions are welcome! Please follow these steps to contribute:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes and commit them (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or suggestions, please open an issue on the [GitHub repository](https://github.com/MohammedAlimoor/flutter-l10n-helper/issues).

---

**Author:** Mohammed Alimoor
**Date:** April 2, 2025