# openweather-kata

A simple weather app for user current location developed using kotlin by following MVVM. 

# Screens

This app consists of two screens

* Home Screen - Fetch the current location and get the city weather using OpenWeatherApi
    * Temp is shown in Celcius along with city name and time of sunrise and sunset
    * Current weather details is saved in local database(Room)
    
    
* Weather List Screen - When the app opens we get the current weather and save it in the database
    * Show the list of saved weather details.
    
    
# Navigation

    * Home screen and List weather screen should be in the bottom navigation bar.

# Setup

Clone the project using below command

```bash
 https://github.com/Dharma07/openweather-kata.git
```

Here are some useful Gradle/adb commands for executing this project:

 * `./gradlew runApp` - Builds and install the debug apk on the current connected device.
 * `./gradlew compileApp` - Builds the debug apk.
 * `./gradlew runUnitTests` - Execute unit tests (both unit and integration).
 
 
 # Dependencies used
 
 - Android Hilt
 
 - Retrofit
 
 - Room
 
 - View Binding
 
 - Google Truth 
 
 - Mockk
 
 # Approaches followed 
 
- MVVM architecture

- Dependency Injection

 # Note
 Kindly add the openweather appid key in the constant file before running the app
