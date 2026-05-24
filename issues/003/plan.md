# UI軽微改修 実施計画

## 概要
- issue ID: `003`
- 対象:
  - メニュー内 `ダークモード` 行の背景色調整
  - `自動取り込み` に関するユーザー向け表記の `AI取り込み` への変更
  - 上記変更に追従する UI テストの更新
- 非対象:
  - AI取り込み機能の挙動そのもの
  - API / repository / usecase の仕様変更
  - 内部識別子の大規模リネーム（resource 名、class 名、testTag 名、notification channel ID など）

## 現状把握
- `ダークモード` 行は `activity_calendar.xml` の drawer 下部に `darkModeContainer` + `darkModeSwitch` として独立配置されており、`CalendarActivity` 側では ON/OFF の状態同期のみを行っている。
- `自動取り込み` のユーザー向け表記は `strings.xml` に集約されており、一覧画面ボタン、ダイアログ、通知メッセージ、通知チャンネル名/説明で使われている。
- 一部の説明文は `LegalDocuments.kt` にハードコードされており、ここにも `自動取り込み` 表記が残っている。
- UI テストは `CalendarDarkModeToggleTest` で drawer 内のダークモード切り替えを確認しており、`AndroidTestHelper` では起動時に出る取り込みダイアログのタイトル文字列を使ってクローズしている。

## 要件解釈
1. `ダークモード` 行はメニュー本体と視覚的に分離して見えないことが要件であり、少なくとも drawer 背景と同系色に揃える必要がある。
2. `自動取り込み` → `AI取り込み` の変更対象は、ユーザーが直接目にする文言全般と解釈する。
3. ただし内部識別子まで一括で改名すると影響範囲が広がるため、まずは表示文言を優先し、内部名は維持する方針が安全。
4. 完了条件の「UIテストが正常に完了」は、少なくとも今回の変更に関係する instrumentation test を通し、文言変更や drawer UI 調整の回帰がない状態を指す。

## 改修方針
1. `ダークモード` 行の背景色を drawer メニューと同一トーンに揃える。
   - 変更候補は `activity_calendar.xml` の `darkModeContainer` / `darkModeSwitch` 周辺。
   - `CalendarActivity` のトグル挙動は既存実装を維持し、見た目だけを調整する。
   - light / dark 両テーマでコントラストが崩れない配色を優先する。
2. `自動取り込み` の表示文言を `AI取り込み` に統一する。
   - `strings.xml` のボタン、ダイアログ、通知メッセージ、通知チャンネル文言を更新する。
   - `LegalDocuments.kt` の説明文も同じ呼称に揃える。
   - resource ID や testTag は既存のままにし、参照箇所の修正量を必要最小限に留める。
3. UI テストを要件に合わせて維持・補強する。
   - 既存の `CalendarDarkModeToggleTest` でトグル動作が崩れていないことを確認する。
   - 必要に応じて drawer 下部行の背景色整合を確認するテストを追加する。
   - 文言依存のテストやヘルパーは新しい表示名でも安定して動作する状態にする。

## 実施順
- [x] 要件と現行 UI / 文言配置を分析する
- [x] 影響ファイルと変更対象外を切り分ける
- [x] `ダークモード` 行の背景色を調整する
- [x] `AI取り込み` 表記へユーザー向け文言を更新する
- [x] 変更に追従する UI テストを更新・追加する
- [x] 差分レビューと残リスク整理を行う

## エージェント構成
- `manager`: 要件解釈、計画策定、タスク分解、進行管理
- `developer`: UI修正、文言更新、UIテスト更新
- `reviewer`: 変更漏れ、不要な内部リネーム、回帰リスクの確認

## タスク一覧
| タスク | 担当 | 状態 | 完了条件 | 期待成果物 |
| --- | --- | --- | --- | --- |
| 要件分析と計画策定 | manager | 完了 | 変更範囲、非対象、実施順、リスクが文書化されている | `issues/003/plan.md`、`issues/003/tasks/task-manager.md` |
| UI修正と文言更新 | developer | 完了 | drawer 内 `ダークモード` 行の見た目が揃い、ユーザー向け `自動取り込み` 表記が `AI取り込み` に更新されている | `CalendarActivity.kt`、関連 resource / test |
| レビューと検証観点整理 | reviewer | 完了 | 表記変更漏れ、不要な内部変更、テーマ差分、テスト不足が整理されている | レビュー結果、`issues/003/plan.md` 更新 |

## 影響想定ファイル
- `app/src/main/res/layout/activity_calendar.xml`
- `app/src/main/java/net/mythrowaway/app/module/trash/presentation/view/calendar/CalendarActivity.kt`
- `app/src/main/res/values/strings.xml`
- `app/src/main/java/net/mythrowaway/app/module/trash/presentation/view/ScheduleSearchImportDialog.kt`
- `app/src/main/java/net/mythrowaway/app/module/trash/presentation/view/edit/TrashListScreen.kt`
- `app/src/main/java/net/mythrowaway/app/module/trash/presentation/view_model/ScheduleSearchImportNotifier.kt`
- `app/src/main/java/net/mythrowaway/app/module/other/presentation/view/LegalDocuments.kt`
- `app/src/androidTest/java/net/mythrowaway/app/calendar/CalendarDarkModeToggleTest.kt`
- `app/src/androidTest/java/net/mythrowaway/app/lib/AndroidTestHelper.kt`

## 想定リスク
1. `自動取り込み` の表記が `strings.xml` 外にも散在しているため、表示文言の変更漏れが起きやすい。
2. 通知チャンネル名・説明は OS 管理の影響を受けるため、既存インストール端末で見え方の確認が必要になる可能性がある。
3. 背景色調整を XML だけで済ませるか、`MaterialSwitch` の tint まで触るかで影響範囲が変わる。
4. 色差の確認は見た目依存になりやすいため、必要なら instrumentation test で背景色比較を補う。

## 実装結果
- drawer 下部の `darkModeContainer` は、`NavigationView` と同じ background を複製して適用する形へ変更し、メニュー背景と同じ見た目になるようにした。
- `strings.xml` のダイアログ題名、ボタン、通知チャンネル名/説明、通知タイトル、開始メッセージ、留意事項を `AI取り込み` ベースへ更新した。
- `LegalDocuments.kt` に残っていた `自動取り込み` 表記を `AI取り込み` に統一した。
- `CalendarDarkModeToggleTest` に light / dark 両モードで drawer 背景一致を確認するテストを追加した。
- `TrashListScreenTest` に `AI取り込み（β）` のボタンとダイアログ題名を確認するテストを追加した。

## 未完了事項
- `connectedDevDebugAndroidTest` は接続デバイスがなかったため未実行。UI テスト実行確認はエミュレータまたは実機が利用可能な環境で補完が必要。

## 検証方針
1. `./gradlew assembleDevDebug`
2. `./gradlew assembleDevDebugAndroidTest`
3. 実行環境があれば `./gradlew connectedDevDebugAndroidTest`
4. 必要に応じて `CalendarDarkModeToggleTest` など対象 UI テストを個別に確認
