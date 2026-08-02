# 실습 환경 준비 (1회, 5분)

이 프로젝트에는 Gradle wrapper(`gradlew`)가 포함돼 있지 않다. 바이너리를 리포지토리에 넣지 않기 위해서다.
**실습 전에 한 번만** 생성한다.

## 필요한 것
- **JDK 17 이상** (Spring Boot 3.x 요구사항)
- **Gradle 8.x** (wrapper 생성용. 생성 후에는 불필요)

## 확인
```bash
java -version     # 17 이상이어야 한다
gradle -v         # 없으면 아래 설치
```

## Gradle 설치 (없을 때)

```bash
# macOS
brew install gradle openjdk@17

# Ubuntu / 시험 샌드박스
sudo apt-get update && sudo apt-get install -y gradle openjdk-17-jdk
# 또는 SDKMAN
curl -s "https://get.sdkman.io" | bash && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.12-tem && sdk install gradle
```

## Wrapper 생성 + 최초 빌드

```bash
cd ~/Claude/Projects/ai_native_project/3_실습_과제/실습1_재고동시성
gradle wrapper --gradle-version 8.10
./gradlew build          # 최초 실행은 의존성 다운로드로 2~5분 걸린다
./gradlew test           # 기존 테스트 3개가 통과해야 정상
```

기대 출력:
```
InventoryServiceTest > 재고를_예약할_수_있다 PASSED
InventoryServiceTest > 예약하면_가용재고가_줄어든다 PASSED
InventoryServiceTest > 전체_가용재고를_조회할_수_있다 PASSED
BUILD SUCCESSFUL
```

## 훅 동작 확인

```bash
bash ../../2_시험장_반입파일/bootstrap.sh .
claude
```

Claude Code 안에서 **에이전트에게 파일 수정을 시킨다.** (직접 에디터로 고치면 안 된다 — 아래 주의 참고)

```
src/main/java/com/oliveyoung/inventory/domain/Stock.java 맨 위에 주석 한 줄을 추가하라.
```

| stderr 출력 | 의미 |
|---|---|
| `[HOOK] 컴파일/테스트 통과` | 정상. 훅이 살아 있다 |
| `[HOOK] 자동 검증 실패 (exit=...)` | 훅은 정상. 코드가 깨진 것 |
| `[HOOK] 빌드 도구를 찾지 못해...` | wrapper 생성이 안 됐다. 위로 돌아갈 것 |
| 아무것도 안 뜸 | 훅이 로드되지 않았다. `/hooks` 로 확인 |

> ⚠️ **에디터로 직접 저장하면 훅은 발동하지 않는다.**
> `PostToolUse` matcher가 `Edit|Write|MultiEdit` 이다. 즉 훅은 **파일시스템 변경을 감시하는 게 아니라
> Claude Code의 도구 호출을 가로채는** 구조다. vim/VS Code로 저장한 변경은 도구 호출이 아니므로 훅이 모른다.
>
> 이게 하네스의 성질을 그대로 보여준다: **훅은 "사람이 뭘 했는지"가 아니라 "에이전트가 뭘 했는지"를 강제한다.**
> 시험장에서 훅이 안 뜨는 것 같으면, 내가 직접 편집한 건 아닌지부터 확인하라.

---

## Gradle을 못 쓰는 상황이라면

훅 없이도 실습의 핵심(결함 발견 · 적대적 리뷰 · 도메인 체크리스트 순회)은 그대로 할 수 있다.
빌드가 안 되는 상태로 진행하되, **훅이 죽어 있다는 것을 인지하고** 수동 검증으로 대체한다.

> 오히려 이 상황을 연습으로 삼아라. 시험 환경에서도 빌드 도구가 예상과 다를 수 있다.
> **부트스트랩 12분 안에 "빌드/테스트 명령이 실제로 도는 것"을 확인하는 이유**가 이것이다.
