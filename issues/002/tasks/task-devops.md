# Task: devops

## ステータス
- [x] 実装完了
- [ ] Codemagic 実行確認待ち

## 役割
- Codemagic / Firebase Test Lab 移行

## 作業内容
1. `codemagic.yaml` の `e2e-test` workflow を AWS Device Farm 実行から Firebase Test Lab 実行へ置き換える。
2. Codemagic environment group を AWS 系から Firebase 系へ切り替え、service account 認証と `FIREBASE_PROJECT` 設定を反映する。
3. `gcloud firebase test android run --type instrumentation` の device、timeout、results history、失敗伝播方針を確定する。
4. AWS Device Farm 固有の upload / testspec / schedule-run ステップを停止し、不要な参照が残らないようにする。

## 制約
- テストケースやテストコードは変更しない。
- unit test workflow には影響を入れない。

## 完了条件
- `e2e-test` workflow で Firebase Test Lab instrumentation test が起動する。
- `e2e-test` workflow から AWS Device Farm 実行処理が除去されている。
- 実行失敗時に Codemagic job が失敗扱いになり、原因追跡に必要な情報が残る。

## 完了内容
- `e2e-test` workflow の environment group を `firebase_credentials` へ切り替えた。
- AWS Device Farm の upload / testspec / auxiliary APK / schedule-run を削除した。
- `gcloud firebase test android run --type instrumentation` を導入し、`GCLOUD_KEY_FILE`、`FIREBASE_PROJECT`、`FIREBASE_TEST_LAB_MODEL`、`FIREBASE_TEST_LAB_VERSION` を必須化した。
- `results-history-name` と一意な `results-dir` を設定し、失敗時は Codemagic job がそのまま失敗する構成にした。
- service account key は owner-only 権限で作成し、終了時に削除するようにした。
- `release-build` workflow は `release` ブランチへの push で起動する設定を明示し、trigger 記述を簡潔化した。
- Codemagic 上では Firebase Test Lab catalog の参照権限を事前確認し、IAM 不足時は必要な権限構成が分かるメッセージで失敗させるようにした。あわせて `FIREBASE_TEST_LAB_RESULTS_BUCKET` を設定した場合は `--results-bucket` を使えるようにした。

## 残課題
- Firebase Test Lab の実行成功は Codemagic 上で未確認。

## 期待成果物
- `codemagic.yaml`
- Firebase Test Lab 用の環境変数要件メモ（必要なら `issues/002/plan.md` に反映）
