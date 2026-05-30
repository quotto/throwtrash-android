# Task: developer

## ステータス
- [x] 完了

## 役割
- UI修正、文言更新、UIテスト更新

## 作業内容
1. `activity_calendar.xml` を中心に、drawer 内 `ダークモード` 行の背景色をメニュー背景と揃える。
2. `strings.xml` と `LegalDocuments.kt` のユーザー向け `自動取り込み` 表記を `AI取り込み` へ更新する。
3. 一覧画面、ダイアログ、通知、起動時ダイアログ周辺で新しい表記に崩れがないことを確認する。
4. 必要に応じて UI テストを更新または追加し、今回の変更を検証できる状態にする。

## 制約
- AI取り込み機能のロジックや API 呼び出しは変更しない。
- resource ID、testTag、class 名、notification channel ID などの内部識別子は、必要性がない限り変更しない。
- 既存のテーマ切り替え挙動は維持し、見た目と文言だけを最小限で直す。

## 完了条件
- `ダークモード` 行がメニュー背景と自然に揃って見える。
- 主要なユーザー向け `自動取り込み` 表記が `AI取り込み` に更新されている。
- 変更範囲に対応する UI テストが通る、または新たな検証観点が明確になっている。

## 完了内容
- `CalendarActivity` で `NavigationView` の background を `darkModeContainer` に複製適用し、drawer 下部の背景色をメニュー本体と揃えた。
- `strings.xml` と `LegalDocuments.kt` のユーザー向け `自動取り込み` 表記を `AI取り込み` に更新した。
- `CalendarDarkModeToggleTest` と `TrashListScreenTest` に今回の変更を確認するテストを追加した。
- `testDevDebugUnitTest`、`assembleDevDebug`、`assembleDevDebugAndroidTest` の実行でコンパイルと既存テストへの影響がないことを確認した。

## 期待成果物
- `app/src/main/res/layout/activity_calendar.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/net/mythrowaway/app/module/other/presentation/view/LegalDocuments.kt`
- 関連する UI / test ファイル
