# Task: devops

## ステータス
- 状態: 完了

## 役割
- Codemagic / CI 影響分析

## 作業内容
1. `codemagic.yaml` の workflow が SDK 37 更新後も成立するか確認する。
2. Java、Gradle、Android SDK、Device Farm 連携に必要な変更点を整理する。
3. ローカルで再現できない CI リスクを明文化する。

## 完了内容
- SDK 37 には AGP `9.1.1` と Gradle `9.3.1` が必要であることを確認した。
- `assembleAndroidTest` を `assembleDevDebugAndroidTest` に変更し、flavor 付き構成での曖昧さを解消した。
- Device Farm 補助 APK の探索先を `app/build/devicefarm/androidTestUtil` に変更し、未検出時に fail するチェックを追加した。
- Dev APK、AndroidTest APK、orchestrator/test-services APK の未生成時に即失敗させる安全策を追加した。

## 成果物
- `codemagic.yaml`
- CI 影響分析結果（`issues/001/plan.md` に反映）

## 残課題
- Device Farm 実行自体はローカル未検証のため、Codemagic 上での動作確認が別途必要。

## 完了条件
- Codemagic 更新方針と CI 上の確認ポイントが明確になっている。
