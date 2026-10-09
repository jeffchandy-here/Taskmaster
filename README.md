# Taskmaster

Calendar to-do app with daily ticks. Tasks, statuses and ticks are saved on the phone.
Reminders every day at 9:00 and 22:00.

## Build with GitHub (no install needed)
1. Create a new GitHub repo and upload everything in this folder (including the hidden `.github` folder).
2. Open the Actions tab > "Build APK" and wait for the green tick (~3-5 min).
3. Download `Taskmaster-apk` from that run, unzip, copy `app-debug.apk` to your phone and install it
   (allow "install unknown apps" when asked).

## Build with Android Studio
Open this folder, let it sync, then Build > Build App Bundle(s) / APK(s) > Build APK(s).

## First run
- Allow notifications when asked.
- Settings (gear) > "Send test notification" to check they work.
- If it says reminders may arrive late, tap "Allow exact timing".
- On Samsung/Xiaomi etc., if reminders don't show up, set the app's battery usage to "Unrestricted".

## Updating
A fixed signing key is included (`app/taskmaster.jks`), so a new build installs over the old one
and keeps your data. Uninstalling the app deletes the data.
