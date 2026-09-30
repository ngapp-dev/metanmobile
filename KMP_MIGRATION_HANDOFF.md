# MetanMobile KMP — контекст для продолжения

Дата состояния: 2026-09-11. Рабочая ветка: `feature/kmp`, последний коммит
на момент сохранения: `bfacd47 KMP migration`.

## Цель миграции

Перенести существующее Android-приложение MetanMobile на Kotlin Multiplatform
и Compose Multiplatform, сохранив **существующие продуктовые экраны и дизайн из
`master`**, а не заменяя их упрощёнными новыми экранами. Ориентир по структуре
и механике — `/Users/ngapps/Android/ngapp-dev/Quottie-KMP`:

- каждый feature — самостоятельный KMP-модуль;
- UI, ViewModel, state, navigation и ресурсы feature должны жить в feature и
  быть доступны Android и iOS;
- `composeApp` только собирает приложение, тему, DI и navigation host; он не
  должен становиться местом, куда переносят UI фич;
- `core:designsystem` и `core:ui` — общий CMP-код;
- DI — Koin, Hilt должен быть устранён;
- API используется через Cloudflare backend
  `/Users/ngapps/Android/Github/metan-ecogas-api`; RSS больше не используется.

## Самое важное на следующем шаге

Пользователь явно попросил восстановить **его исходный дизайн из ветки
`master`, но на CMP**. Не принимать текущие простые shared-заглушки за
результат. Исходные реализации можно безопасно читать через `git show
master:<path>` либо сравнивать с `git diff master -- <path>`. Не делать
`git checkout`/`reset`: рабочее дерево очень грязное и содержит результаты
миграции.

Практический порядок:

1. Аудитировать старый `core/designsystem` в `master`: компоненты, `Color.kt`,
   `Theme.kt`, `Type.kt`, `Shape.kt`, icon set, animations, Lottie, изображения.
2. Перенести portable Compose-код в `core/designsystem/src/commonMain`; всё,
   что привязано к Android (`Context`, Android resources, WebView, Google Map,
   Android drawable API), вынести в `androidMain` за `expect/actual` или
   заменить multiplatform-реализацией.
3. Перенести исходные UI-файлы каждой feature в `commonMain` вместе с её
   ViewModel/state/navigation и заменить Android-only зависимости на общие.
   Нельзя оставлять в `commonMain` упрощённый экран, если в master был полный
   экран в `src/main`.
4. Ресурсы каждой фичи переносить в её собственные `commonMain` resources по
   тому же соглашению, что в Quottie KMP; общие логотипы/изображения — в
   `resources`. Не держать feature UI или onboarding assets в `composeApp`.
5. После каждого логического блока собирать Android и iOS.

## Что уже сделано

### KMP foundation

- Созданы `composeApp`, `resources`, iOS Xcode project (`iosApp`).
- Android `MainActivity` запускает общий `SharedMetanMobileContent()`.
- iOS запускает Compose view controller через общий Kotlin framework.
- В `composeApp` есть общий typed navigation host:
  `composeApp/src/commonMain/kotlin/com/ngapp/metanmobile/composeapp/navigation/MetanMobileNavHost.kt`.
- Navigation включает onboarding, главные root tabs (home, stations, news,
  favorites), station/news detail, menu, cabinet, FAQ, contacts, calculators,
  about, careers и legal-подэкраны.
- Начато подключение Koin для shared feature/core graph. Koin-модули feature
  находятся в их `commonMain`.
- DataStore/user settings вынесены в shared слой; `MetanMobileApp` читает
  `UserDataRepository` и передаёт `DarkThemeConfig` в shared тему.

### Feature migration (незавершена визуально)

В `commonMain` уже находятся либо начаты следующие фичи:

- `feature/onboarding`
- `feature/home`
- `feature/stations`
- `feature/stationdetail`
- `feature/news` (list/detail)
- `feature/favorites`
- `feature/cabinet`
- `feature/menu/main` и menu child modules.

Они пока не равны исходному Android дизайну. Во многих местах ранее были
созданы упрощённые Compose-представления — их требуется заменить портом UI из
`master`, а не полировать как финальный дизайн.

### Текущий shared theme/design system skeleton

Уже изменены/добавлены:

- `core/designsystem/src/commonMain/.../theme/Color.kt`
  — исправлен некорректный `ElevatedDarkBlue` и уточнены роли цветов;
- `theme/Theme.kt`
  — `MMTheme` и `shouldUseDarkTheme(DarkThemeConfig)`;
- `component/AppChrome.kt`
  — базовые common `MMAppScaffold`, standard/center top app bars, bottom
  navigation, `MMCard`, divider;
- `component/CommonComponents.kt`
  — common primary/outlined/text buttons;
- `core/ui/.../CoreUiShared.kt`
  — `MMInfoCard` начал использовать `MMCard`;
- `composeApp/MetanMobileTheme.kt` и `MetanMobileApp.kt`
  — общая тема на основе сохранённого user setting;
- `MetanMobileNavHost.kt`
  — common scaffold и bottom navigation;
- некоторые feature screens начали использовать `MMStandardTopAppBar`.

Это **каркас**, не финальная реконструкция старого дизайна. Старый Android
designsystem дополнительно сохранён в рабочем дереве как
`core/designsystem/legacyAndroidKotlin/`, но источник истины всё равно `master`.
Его нужно переносить осмысленно, не копировать Android imports в `commonMain`.

## Важные известные проблемы

1. До недавних правок базовые компиляции были успешны, но финальная сборка
   после последних theme/appbar изменений ещё требует повторной проверки.
2. Есть предупреждение/потенциальный runtime-риск версий: Coil `3.4.0`
   подтягивает Skiko `0.9.22.2`, Compose резолвит Skiko `0.144.6`. Компиляция
   раньше проходила, но это надо выровнять при переносе image layer.
3. Реальные Android-only элементы, которые нельзя оставлять common как есть:
   Google Map, WebView, некоторые Android resource/bitmap API, Lottie Android
   assets, scroll/reorder utilities с Android haptic. Для них нужны
   expect/actual или platform implementation.
4. Пользователь ранее видел Koin errors для `StationsRepository` и
   `StationsViewModel`; Koin graph был дополнен. После любых модульных правок
   проверять запуск и Koin registrations, не только компиляцию.
5. Ранее iOS показывал белый экран. Navigation/onboarding перенесены в общий
   host, но требуется фактический запуск на simulator/device после сборки.
6. Xcode installation issue (`missing/invalid CFBundleExecutable`) исправлялся
   в проекте iOS ранее, но проверять реальным запуском. `iosApp/README.md`
   содержит команды/framework notes.

## Проверочные команды

Из корня проекта:

```bash
./gradlew :feature:cabinet:compileAndroidMain --console=plain
./gradlew :composeApp:compileAndroidMain :app:compileDebugKotlin --console=plain
./gradlew :composeApp:compileKotlinIosSimulatorArm64 --console=plain
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64 --console=plain
```

Для очередной фичи добавлять её `:feature:<name>:compileAndroidMain`.
После common UI changes всегда запускать iOS compile; Android compile не ловит
iOS-specific зависимости.

## Полезные точки входа

```text
composeApp/src/commonMain/kotlin/com/ngapp/metanmobile/composeapp/MetanMobileApp.kt
composeApp/src/commonMain/kotlin/com/ngapp/metanmobile/composeapp/MetanMobileTheme.kt
composeApp/src/commonMain/kotlin/com/ngapp/metanmobile/composeapp/navigation/MetanMobileNavHost.kt
composeApp/src/iosMain/kotlin/com/ngapp/metanmobile/composeapp/MainViewController.kt
composeApp/src/iosMain/kotlin/com/ngapp/metanmobile/composeapp/SharedKoin.kt
app/src/main/kotlin/com/ngapp/metanmobile/MainActivity.kt
core/designsystem/src/commonMain/kotlin/com/ngapp/metanmobile/core/designsystem/
core/ui/src/commonMain/kotlin/com/ngapp/metanmobile/core/ui/
resources/src/commonMain/moko-resources/
```

## Как искать оригинальный UI в master

```bash
git show master:core/designsystem/src/main/kotlin/com/ngapp/metanmobile/core/designsystem/theme/Theme.kt
git ls-tree -r --name-only master feature/onboarding/src/main
git show master:feature/onboarding/src/main/kotlin/com/ngapp/metanmobile/feature/onboarding/OnboardingScreen.kt
git diff --stat master -- core/designsystem feature/onboarding feature/home
```

Не переносить файловую структуру `src/main` буквально: для KMP цель —
`src/commonMain`, `src/androidMain`, `src/iosMain`. Но визуальные размеры,
цвета, типографику, layout, состояния, строки и поведение нужно сохранить.

## Правила продолжения из требований пользователя

- Не останавливать работу на промежуточных вопросах; сообщать только о
  существенных результатах и о реальных блокерах.
- Показывать в комментариях, что именно сейчас меняется.
- Копировать организацию Quottie KMP, но не его продуктовый UI вместо UI
  MetanMobile.
- Core UI должен быть общим на 100% там, где платформа технически это позволяет;
  platform integration — через `expect/actual`.
- Полная функциональность Android обязана сохраниться; iOS должен пройти
  onboarding и использовать общий UI/navigation, далее расширяться до
  функционального паритета.

## Состояние рабочего дерева

Рабочее дерево содержит очень много staged/unstaged переносов, удалений старых
`src/main` файлов и новых commonMain файлов. Это ожидаемо, но опасно:

- не делать `git reset --hard`, `git checkout .`, массовое удаление;
- перед изменением конкретной фичи сверять `git status --short -- <path>`;
- не удалять `legacyAndroidKotlin` до того, как соответствующий shared компонент
  перенесён, скомпилирован и визуально проверен;
- текущая ветка `feature/kmp` не является чистой точкой восстановления.

