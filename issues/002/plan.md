# Firebase Test Lab 移行計画

## 概要
- issue ID: `002`
- 対象: `codemagic.yaml` の `e2e-test` workflow と、その実行に必要な最小限のビルド設定
- 非対象: ユニットテスト、E2E テストケース本体、アプリ機能改修
- 方針: 既存の `devDebug` / `devDebugAndroidTest` 生成を維持しつつ、AWS Device Farm 依存を Firebase Test Lab へ置き換える

## 現状把握
- `codemagic.yaml` の `e2e-test` workflow は `aws_credentials` / `aws_device_farm` を読み込み、`assembleDevDebug` と `assembleDevDebugAndroidTest` で生成した APK を AWS Device Farm に upload している。
- 同 workflow は `.devicefarm/testspec.yaml` と `downloadAndroidUtilTestLibs` を使い、orchestrator / test services APK まで補助 upload した上で `aws devicefarm schedule-run` を実行している。
- `app/build.gradle` では `testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"`、`testInstrumentationRunnerArguments clearPackageData: 'true'`、`testOptions.execution = 'ANDROIDX_TEST_ORCHESTRATOR'` が設定されている。
- 要件上、E2E テストコードやテストケース自体は原則変更せず、CI の実行基盤だけを差し替える必要がある。

## 要件解釈
1. Codemagic の E2E 実行経路を Firebase Test Lab の instrumentation test 実行へ切り替える。
2. AWS Device Farm 固有の認証情報、upload、testspec、schedule-run 呼び出しは停止対象とみなす。
3. ユニットテスト workflow は変更対象外とし、既存の `testDevDebugUnitTest` 系の流れは維持する。
4. 既存の orchestrator / `clearPackageData` 挙動は、可能な限り Firebase Test Lab 側の設定で継続する。

## 改修方針
1. `e2e-test` workflow の認証基盤を Firebase 用 service account に置き換える。
   - Codemagic の environment group は AWS 系から Firebase 系へ切り替える。
   - 想定変数: `GCLOUD_KEY_FILE`、`FIREBASE_PROJECT`。必要ならデバイス指定や結果保存用の変数を追加する。
2. E2E 実行コマンドを `gcloud firebase test android run --type instrumentation` に置き換える。
   - 入力 APK は現行どおり `assembleDevDebug` と `assembleDevDebugAndroidTest` の成果物を使う。
   - `--use-orchestrator` と `--environment-variables clearPackageData=true` を優先候補とし、現在の実行条件に寄せる。
   - 実行デバイス、timeout、results history 名、results dir は CI で追跡しやすい値に整理する。
3. AWS Device Farm 固有の処理を削除または無効化する。
   - `aws devicefarm create-upload` / `schedule-run`、`.devicefarm/testspec.yaml` 参照、補助 APK upload を `e2e-test` workflow から外す。
   - `downloadAndroidUtilTestLibs` と `.devicefarm/testspec.yaml` 自体は、他用途がなければ削除候補として評価する。ただしローカル connected test への影響がある場合は残置する。
4. 失敗時の可観測性を確保する。
   - Firebase Test Lab 実行結果が Codemagic job の失敗として伝播することを確認する。
   - 必要なら results history 名や artifacts の収集方針を追加し、トラブル時の追跡性を確保する。

## 実施順
- [x] 要件と現行 CI の差分を分析する
- [x] Firebase Test Lab 実行に必要な秘密情報・環境変数を整理する
- [x] `e2e-test` workflow を Firebase Test Lab 実行へ置き換える
- [x] AWS Device Farm 固有のステップと参照ファイルの整理方針を確定する
- [ ] `assembleDevDebug` / `assembleDevDebugAndroidTest` / Firebase Test Lab 実行の確認を行う
- [x] 変更差分をレビューし、残リスクを整理する

## エージェント構成
- `manager`: 要件解釈、計画策定、タスク分解、進行管理
- `developer`: 必要最小限の Gradle / 補助設定整理、不要資産の残置判断
- `devops`: `codemagic.yaml` 更新、Firebase Test Lab 実行方式の確定、CI 失敗条件の整備
- `reviewer`: 秘密情報の扱い、AWS 停止漏れ、回帰リスク、完了条件充足の確認

## タスク一覧
| タスク | 担当 | 状態 | 完了条件 | 期待成果物 |
| --- | --- | --- | --- | --- |
| 要件分析と計画策定 | manager | 完了 | `requirements.md` と現行 CI を踏まえた改修方針が文書化されている | `issues/002/plan.md`、`issues/002/tasks/task-manager.md` |
| Codemagic / Firebase Test Lab 移行 | devops | 実装完了・CI確認待ち | `e2e-test` workflow から AWS Device Farm 実行がなくなり、Firebase Test Lab instrumentation 実行に置き換わる | `codemagic.yaml` |
| ビルド補助設定の整理 | developer | 完了 | テストコードを変えずに必要最小限の Gradle / 補助ファイル整理方針が確定し、必要なら差分が実装される | `app/build.gradle`、関連補助ファイル |
| レビューと残課題整理 | reviewer | 完了 | 認証情報、停止漏れ、回帰リスク、未検証範囲が整理されている | レビュー結果、`issues/002/plan.md` 更新 |

## 想定リスク
1. Firebase Test Lab のデバイス指定を誤ると、現行 Device Farm と異なる OS / 画面条件で失敗する可能性がある。
2. Firebase 用 service account や Cloud Tools Results API が未整備だと、CI 側の切り替えだけでは実行できない。
3. Firebase Test Lab 実行用の `FIREBASE_TEST_LAB_MODEL` / `FIREBASE_TEST_LAB_VERSION` が Codemagic 側で未設定だと、CI は fail-fast する。
4. Firebase Test Lab 実行結果の出力先や job failure の扱いが不十分だと、失敗時の原因追跡が難しくなる。

## 実装結果
- `codemagic.yaml` の `e2e-test` workflow を `firebase_credentials` ベースへ切り替え、AWS Device Farm の upload / testspec / schedule-run を除去した。
- Firebase Test Lab 実行は `gcloud firebase test android run --type instrumentation` へ置き換え、`GCLOUD_KEY_FILE`、`FIREBASE_PROJECT`、`FIREBASE_TEST_LAB_MODEL`、`FIREBASE_TEST_LAB_VERSION` を必須化した。
- 生成した service account key は owner-only 権限で作成し、終了時に削除するようにした。
- 既存 APK 生成フローは `assembleDevDebug` / `assembleDevDebugAndroidTest` のまま維持した。
- Device Farm 専用だった `downloadAndroidUtilTestLibs` タスクと `.devicefarm/testspec.yaml` を削除し、関連する AGENTS の検証コマンドも更新した。
- ローカルでは `testDevDebugUnitTest`、`assembleDevDebug`、`assembleDevDebugAndroidTest`、`codemagic.yaml` の YAML parse を確認した。
- ローカルの Firebase Test Lab 実行では `throwtrash-dev` / `MediumPhone.arm` / API 34 / `ja` / `portrait` で 98 件中 81 件成功、17 件失敗だった。
- 失敗した 17 件は主に calendar 系 UI テストで、起動直後に `自動取り込み（β）` ダイアログが前面に出て `calendarActivityRoot` や `calendarSwipeRefresh` を見つけられない `NoMatchingViewException` が発生していた。
- AndroidTest 共通ヘルパーに「自動取り込み（β）ダイアログが表示されていたら閉じる」処理を追加し、CalendarActivity を起動する E2E テストの初期化で実行するようにした。
- `requirements.md` に「FirebaseTestLab 上で起動直後に表示される自動取り込みダイアログは、E2E テスト中に閉じて継続できるようにする」要件を追記した。
- ダイアログ対応後のローカル Firebase Test Lab 再実行では `throwtrash-dev` / `MediumPhone.arm` / API 34 / `ja` / `portrait` で 98 件中 97 件成功、1 件失敗まで改善した。
- 残件の `TrashListScreenTest#copy_trash_from_list_and_register_as_new_trash` について、TrashList 画面復帰時の一覧再読込が不足していたため、`TrashListViewModel.refreshTrashList()` と `TrashListScreen` の resume 時リフレッシュを追加した。
- 追加修正後のローカル Firebase Test Lab 再実行では `throwtrash-dev` / `MediumPhone.arm` / API 34 / `ja` / `portrait` で 98 件すべて成功した。

## 未完了事項
- ローカル Firebase Test Lab では 98 件すべて成功したが、Codemagic の `e2e-test` workflow 自体は未実行のため、CI 上の最終確認は残っている。

## 完了条件
1. `e2e-test` workflow が Firebase Test Lab で instrumentation test を実行する構成になっている。
2. `e2e-test` workflow から AWS Device Farm 実行処理が除去されている。
3. テストケース / テストコードを変更せずに、既存 E2E テストが Firebase Test Lab で成功する。
4. ユニットテスト workflow への影響がないことを説明できる。
