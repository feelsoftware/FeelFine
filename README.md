## FeelFine - activity tracker app

Application to track your fitness activities with [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect "Health Connect") and [Google Fit](https://developers.google.com/fit "Google Fit") APIs.
- Kotlin
- Jetpack Compose
- [Koin](https://github.com/InsertKoinIO/koin "Koin") for DI
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart "MPAndroidChart") for activity charts

------------



### [Welcome screen](https://github.com/feelsoftware/FeelFine/blob/main/app/src/main/java/com/feelsoftware/feelfine/ui/welcome/WelcomeContent.kt "Welcome screen")
<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/welcome.png" width="180" height="360" />

Welcome screen if user is not signed it yet.



------------



### [Onboarding flow](https://github.com/feelsoftware/FeelFine/tree/main/app/src/main/java/com/feelsoftware/feelfine/ui/onboarding "Onboarding flow")
<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/onboarding_name.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/onboarding_gender.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/onboarding_weight.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/onboarding_birthday.png" width="180" height="360" />

Four simple onboarding steps to pick your name, gender, weight and birthday.



------------



### [Today's score with details](https://github.com/feelsoftware/FeelFine/tree/main/app/src/main/java/com/feelsoftware/feelfine/ui/score "Today's score with details") (Not Compose yet)

<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/score_1.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/score_2.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/score_3.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/score_4.png" width="180" height="360" />

Users data as steps, sleep, walking, running and other combined activities are synced via [Health Connect](https://developer.android.com/health-and-fitness/guides/health-connect "Health Connect") or [Google Fit](https://developers.google.com/fit "Google Fit") APIs. Users have an every day score, based on their own metrics.


------------


### [Activities statistic](https://github.com/feelsoftware/FeelFine/tree/main/app/src/main/java/com/feelsoftware/feelfine/ui/statistic "Activities statistic") (Not Compose yet)

<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/statistic_1.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/statistic_2.png" width="180" height="360" /> <img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/statistic_3.png" width="180" height="360" />

Users could observe week/month/custom range activities statistics.


------------


### [Mood score](https://github.com/feelsoftware/FeelFine/blob/main/app/src/main/java/com/feelsoftware/feelfine/ui/score/MoodFragment.kt "Mood score") (Not Compose yet)

<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/mood.png" width="180" height="360" />

Every day we are asking users 'How are you?' for the mood score with 9 options.


------------


### [User profile](https://github.com/feelsoftware/FeelFine/blob/main/app/src/main/java/com/feelsoftware/feelfine/ui/profile/ProfileFragment.kt "User profile") (Not Compose yet)

<img src="https://github.com/feelsoftware/FeelFine/raw/main/readme/profile.png" width="180" height="360" />

User profile with wage, age and goals (steps, sleep, activity) customizations.


------------


### What's next
- Migrate to Kotlin Coroutines from RxJava
- Migrate to Compose
- Migrate to Kotlin Multiplatform  (use cross-platform library https://github.com/vitoksmile/HealthKMP)
- Night theme
- Edit goals
