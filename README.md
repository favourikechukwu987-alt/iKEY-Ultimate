# iKEY Ultimate 2.0 — editable Android keyboard project

This is an Android Studio source project, not an APK. The ZIP supplied in the previous chat contained only `app-debug.apk`; it did not contain the original source, so this project is a fresh implementation based on the requested feature direction.

## Implemented in this source
- Android system keyboard (IME) with iPhone-inspired rounded light/dark keys
- QWERTY, French AZERTY-style, and Spanish-ready language slot (language switch key)
- Number row toggle
- Symbol/number layouts
- Shift/capitalisation and sentence-start preference
- Backspace, long-press backspace, space, enter/action key
- Emoji panel with category tabs and emoji insertion
- Clipboard paste panel for current Android clipboard item
- Haptic feedback toggle
- Light/dark theme
- Settings screen and setup flow
- Small offline autocorrection dictionary
- Suggestion/toolbar strip toggle
- Privacy-first: no keystroke collection or transmission

## Not yet implemented
- Full Apple emoji catalogue/Apple emoji artwork (emoji glyphs come from Android)
- iOS proprietary prediction engine and full autocorrect dictionary
- Swipe/QuickPath typing
- Voice dictation
- Live cloud predictions, translation, GIF/sticker search, themes download, or sync
- True iOS key animations and every iOS accessibility behavior

The INTERNET permission is present for future opt-in online services, but this build does not transmit typed content. Never upload keystrokes without clear user consent.

## Build
1. Install Android Studio and Android SDK Platform 35.
2. Open this folder in Android Studio.
3. Let Gradle sync.
4. Choose Build > Build Bundle(s) / APK(s) > Build APK(s).
5. Install the generated debug APK on your Android phone.

No Gradle wrapper is included. Android Studio can sync with its configured Gradle installation.

## Enable
Open iKEY Ultimate, tap Enable iKEY Ultimate, enable it in Android settings, then return and select iKEY Ultimate as the active input method.
