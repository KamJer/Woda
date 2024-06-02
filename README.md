# Woda

## Overview

Woda is an Android OS application for recording consumed drinks and their types. Woda allows you to:
- Record the amount of drinks consumed
- Define types of drinks
- Set notifications to remind you to stay hydrated

## Prerequisites

To compile and use the project, you will need:
- Android Studio with SDK installed
- A device with Android OS, minimum SDK 26

## Installation

1. Clone or download the repository
2. Import the project into Android Studio
3. Generate the APK using the "Build" -> "Build Bundle(s)/APK(s)" option
4. Transfer the APK to your device
5. Install the application

## Usage

The application will open to the main view.

### Main View

The main view contains:
1. Calendar Button (top left corner)
2. User Button (top right corner, first)
3. Notifications Button (top right corner, second)

<img src="https://github.com/KamJer/Woda/assets/104097463/3def9499-4efe-4371-90c0-cf1f350f1b3f" width="360" height="800"/>

Additionally, it contains a graphical representation of consumed drinks and a progress bar showing the goal set for a specific day.

#### Adding/Removing Consumed Drinks

By clicking the "+" or "-" button at the bottom of the screen, a dialog will be displayed asking the user to enter the volume of the consumed drink or the drink to be removed and its type.

### Calendar View

The calendar view shows information about all the drinks entered so far and displays them in a calendar format.

The amount of drinks consumed is color-coded:
1. Green: fully achieved goal for the day
2. Red: no data entered

For intermediate data, the application interpolates colors between red and green.

Clicking on a specific day will automatically take the user to the main view with the selected date.

### User View

This view allows the user to set the drinking goal for the currently selected day and all subsequent days, and to define user-specific drink types.

### Notifications View

The notifications view allows you to define whether, at what frequency, and during which period notifications should not appear.
