# 둘기 (dulgi-android)

장난꾸러기 3D 비둘기가 화면 위를 돌아다니는 안드로이드 앱

OpenGL ES 2.0으로 구현된 순수 코드 기반 3D 비둘기 시뮬레이션입니다.
외부 이미지/모델 에셋 없이 모든 형상을 코드로 생성합니다.

## 특징

- OpenGL ES 2.0 3D 렌더링
- 구, 타원체, 원뿔, 상자 메시로 구성된 3D 비둘기
- 랜덤 걷기 애니메이션 (몸통 상하 바운스, 다리 흔들기)
- 주기적인 날개짓 애니메이션 (6초 주기)
- 랜덤 방향 전환 및 경계면 회피
- 체커보드 패턴 바닥 그라운드
- 램버트 확산 조명 (ambient + diffuse)

## 프로젝트 구조

```
dulgi-android/
├── build.gradle.kts              # 프로젝트 빌드 설정
├── settings.gradle.kts           # 설정
├── gradle.properties             # Gradle 속성
├── gradlew / gradlew.bat         # Gradle 래퍼
├── gradle/wrapper/               # Gradle 래퍼 파일
├── app/
│   ├── build.gradle.kts          # 앱 빌드 설정
│   ├── proguard-rules.pro        # ProGuard 규칙
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/dulgi/android/
│       │   │   ├── MainActivity.kt        # Activity (GLSurfaceView)
│       │   │   ├── PigeonRenderer.kt      # OpenGL ES 2.0 렌더러
│       │   │   ├── Pigeon.kt              # 3D 비둘기 모델
│       │   │   ├── Mesh.kt                # 3D 메시 (VBO)
│       │   │   ├── MeshBuilder.kt         # 메시 생성기
│       │   │   ├── Ground.kt              # 바닥면
│       │   │   └── Vector3.kt             # 3D 벡터
│       │   └── res/values/
│       │       ├── strings.xml
│       │       ├── colors.xml
│       │       └── themes.xml
│       └── test/java/com/dulgi/android/
│           └── Vector3Test.kt    # 단위 테스트
├── README.md
├── LICENSE
└── .gitignore
```

## 비둘기 구조 (3D 모델 다이어그램)

```
           [눈]--[머리]
                  |
             [부리]--+
                  |
        [날개]--[몸통]--[날개]
                  |
            [다리]-+-[다리]
                  |
               [꼬리]
```

### 메시 구성

| 부위   | 형상          | 크기 (radii)          | 색상                    |
|--------|---------------|-----------------------|-------------------------|
| 몸통   | 타원체         | 0.35 x 0.25 x 0.22   | 회색 (0.55,0.55,0.50)   |
| 머리   | 구             | r=0.18                | 밝은회색 (0.62,0.62,0.57)|
| 부리   | 원뿔           | r=0.04, h=0.1        | 주황 (0.95,0.70,0.15)   |
| 눈     | 구             | r=0.022               | 검정 (0,0,0)            |
| 날개   | 상자           | 0.22 x 0.015 x 0.08  | 어두운회색 (0.40,0.40,0.38) |
| 다리   | 상자           | 0.03 x 0.13 x 0.03   | 분홍 (0.90,0.40,0.30)   |
| 꼬리   | 원뿔           | r=0.09, h=0.13       | 회색 (0.42,0.42,0.40)   |

### 애니메이션 상태 다이어그램

```
        ┌──────────┐
        │  걷기     │◄──────────┐
        │ (이동중)  │           │
        └────┬─────┘           │
             │ 2~6초 후        │
             ▼                 │ 80% 확률
        ┌──────────┐    ┌──────────┐
        │ 방향전환  │───►│  대기    │
        │ (랜덤각도)│    │ 0.5~2초 │
        └──────────┘    └──────────┘
                              │ 20% 확률
                              └──────────┘
```

## ERD (클래스 관계)

```
┌──────────────────────┐       ┌──────────────────────┐
│   MainActivity       │       │  PigeonRenderer      │
│──────────────────────│       │──────────────────────│
│ - glView: GLSurface  │──────>│ - pigeon: Pigeon     │
│   View               │       │ - ground: Ground     │
│                      │       │ - shader program     │
│ onCreate()           │       │ - view/proj matrices │
└──────────────────────┘       │ onDrawFrame()        │
                               │   → pigeon.update()  │
┌──────────────────────┐       │   → pigeon.draw()    │
│   Pigeon             │       │   → ground.draw()    │
│──────────────────────│       └───────┬──────────────┘
│ - position: Vector3  │               │
│ - facingAngle: Float │               │
│ - body, head: Mesh   │               │
│ - beak, tail: Mesh   │               │
│ - wings, eyes: Mesh  │               │
│ - legs: Mesh         │               │
├──────────────────────┤               │
│ create()             │               │
│ update(dt)           │               │
│ draw(vp, prog, ...)  │               │
└──────────┬───────────┘               │
           │                           │
           ▼                           ▼
┌──────────────────────┐  ┌──────────────────────┐
│   MeshBuilder        │  │   Mesh               │
│──────────────────────│  │──────────────────────│
│ createSphere()       │  │ - vertices: Float[]  │
│ createEllipsoid()    │──>│ - indices: Short[]  │
│ createCone()         │  │ - vBuf / iBuf        │
│ createBox()          │  │ draw(program)        │
│ createGrid()         │  └──────────────────────┘
└──────────────────────┘
         │                       ┌──────────────────────┐
         └──────────────────────>│   Vector3            │
                                 │──────────────────────│
                                 │ - x, y, z: Float    │
                                 │ +, -, *, /, len()   │
                                 │ norm(), dot(), cross │
                                 └──────────────────────┘
┌──────────────────────┐
│   Ground             │
│──────────────────────│
│ - grid, plane: Mesh  │
│ create()             │
│ draw(vp, prog, ...)  │
└──────────────────────┘
```

## 빌드 및 실행

### 요구사항

- Android SDK 34
- JDK 17+
- Gradle 8.2 (래퍼 제공)

### 빌드 명령어

```bash
# 디버그 APK 빌드
./gradlew assembleDebug

# 릴리즈 APK 빌드 (서명 필요)
./gradlew assembleRelease

# 단위 테스트 실행
./gradlew testDebugUnitTest

# 모든 테스트 실행
./gradlew test

# APK 위치
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release-unsigned.apk
```

### APK 서명 (릴리즈)

```bash
# 키스토어 생성 (최초 1회)
keytool -genkey -v -keystore dulgi-release.keystore \
  -alias dulgi -keyalg RSA -keysize 2048 -validity 10000

# 서명
jarsigner -verbose -sigalg SHA1withRSA \
  -digestalg SHA1 \
  -keystore dulgi-release.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  dulgi

# 정렬 (zipalign)
zipalign -v 4 \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  app/build/outputs/apk/release/app-release.apk
```

## 릴리즈

릴리즈 APK는 GitHub Releases에 등록됩니다:
https://github.com/hslcrb/dulgi-android/releases

## 라이선스

Apache License 2.0 - 자세한 내용은 [LICENSE](LICENSE) 파일 참조

## 커밋 접두어 (한글)

| 접두어     | 의미              |
|------------|-------------------|
| [기능]     | 새 기능 추가      |
| [수정]     | 버그 수정         |
| [리팩터]   | 코드 리팩터링     |
| [문서]     | 문서 작업         |
| [설정]     | 빌드/설정 변경    |
| [테스트]   | 테스트 추가/수정  |
