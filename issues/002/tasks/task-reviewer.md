# Task: reviewer

## ステータス
- [x] 完了

## 役割
- 回帰リスク、秘密情報、停止漏れのレビュー

## 作業内容
1. AWS Device Farm の認証情報や workflow 参照が `e2e-test` に残っていないか確認する。
2. Firebase service account の扱いが Codemagic secret として閉じており、リポジトリへ漏れていないか確認する。
3. Firebase Test Lab 実行結果が失敗時に正しく CI failure となるか、観測性が足りるか確認する。
4. テストコード無改修の制約下で残るリスクと未検証範囲を整理する。

## 完了条件
- 秘密情報の扱い、停止漏れ、回帰リスク、未検証範囲が整理されている。
- 重大な問題があれば修正依頼、なければ移行完了の判断材料が揃っている。

## 完了内容
- AWS Device Farm の認証情報、upload、testspec、schedule-run 参照が `codemagic.yaml` から除去されていることを確認した。
- Firebase service account key を一時ファイルに保存する際に owner-only 権限と終了時 cleanup を入れた。
- Firebase Test Lab の端末条件は repo 内の暗黙デフォルトにせず、`FIREBASE_TEST_LAB_MODEL` と `FIREBASE_TEST_LAB_VERSION` を必須入力にした。
- `codemagic.yaml` の YAML parse、`testDevDebugUnitTest`、`assembleDevDebug`、`assembleDevDebugAndroidTest` は確認済みだが、Codemagic 上の Firebase Test Lab 成功は未確認として残した。
- Codemagic の service account で 403 (`Not authorized for project`) が再現したため、権限不足時の fail-fast メッセージと `FIREBASE_TEST_LAB_RESULTS_BUCKET` を使った代替構成を追加し、未解決リスクを IAM 設定に限定した。

## 期待成果物
- レビュー結果
- `issues/002/plan.md` への残リスク反映
