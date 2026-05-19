# E2EテストのFirebaseTestLab移行

## 背景

- E2Eテストは現在、AWS Device Farmで実行されている。
- しかしコストが高いことが問題となっている。
- FirebaseTestLabは無料枠があるため、コスト削減が期待できる。
- このため、E2EテストをFirebaseTestLabに移行することが求められている。

## 要件

1. E2Eテストの実行環境をFirebaseTestLabに移行する。
2. CodemagicのCI/CDパイプラインを更新し、E2EテストがFirebaseTestLabで実行されるようにする。
3. 既存のAWS Device FarmでのE2Eテストを停止する。
4. 原則として、テストケースやテストコードの変更は行わない
5. 移行対象はE2Eテストのみとし、ユニットテストは現状のまま維持する。
6. FirebaseTestLab 上で起動直後に表示される自動取り込みダイアログは、E2Eテスト中に閉じてからテストを継続できるようにする。
7. Codemagic 上の Release ビルドは `release` ブランチへの push で実行されるようにする。

## 完了条件

- CodemagicのCI/CDパイプラインでE2EテストがFirebaseTestLabで実行されることを確認する。
- FirebaseTestLabでのE2Eテストの実行が成功すること

## そのほか

- Firebaseプロジェクトはすでに作成されており、必要な権限も付与されている前提とする。
