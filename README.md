# Daily Expenses: Android app

A simple, offline money diary for Android. Anyone can set it up for their own life in a minute:

- **First-time setup:** choose a currency, tick the categories you use, add your own (for example "Mother's medicine"), and optionally enter your monthly salary.
- **Expenses** by category, paid with Card, Cash, Mobile or Bank, with monthly and yearly budgets.
- **Income:** salary, bonus, business, rent, investment profit and so on. The app shows how much you saved each month.
- **Cash withdrawal (ATM / bank):** keeps a running cash-in-wallet balance, with a "Count cash" check.
- **Loans:** money you lent, money you borrowed, and bank loans with EMI, each with the balance left.
- **Investments:** FDR, DPS, savings certificates, shares, gold, land and more. Money invested isn't counted as spending; profit is counted as income.
- **Repeat monthly** for salary, rent, EMI, DPS and monthly profit.
- **CSV backup and restore**, which opens in Excel or Google Sheets.

All data stays on the phone. There is no account and no internet connection is needed.

## Get the APK built for free (no coding)

GitHub builds the APK for you in the cloud. You only upload this folder.

1. **Create a free GitHub account** at github.com (skip this if you have one).
2. **Create a new repository:** tap **+** → **New repository** → name it `daily-expenses` → choose **Public** → **Create repository**.
3. **Upload the files:** on the new repository page, click **uploading an existing file**. Unzip `DailyExpenses-android.zip` on your computer, open the `DailyExpenses-android` folder, select **everything inside it** (including the hidden `.github` folder) and drag it into the browser. Click **Commit changes**.
   - On Windows, if you can't see `.github`, go to File Explorer → **View** → **Show** → **Hidden items**.
4. **Wait about 5 minutes:** open the **Actions** tab. A run called **Build APK** starts by itself. When it shows a green tick, it's done.
   - If it doesn't start, click **Build APK** on the left, then **Run workflow**.
5. **Download the APK:** go to the repository's main page, click **Releases** on the right, and download **DailyExpenses.apk**.

Anyone can download the APK from that Releases page link. Share the link on WhatsApp, by email or anywhere else.

## Install on an Android phone

1. Open the downloaded **DailyExpenses.apk** on the phone.
2. Android will ask to allow installs from this source (Chrome, Files or WhatsApp). Tap **Settings** → turn on **Allow from this source** → go back.
3. Tap **Install**. If Google Play Protect warns that it doesn't recognise the app, tap **More details** → **Install anyway**. This happens with any app that isn't from the Play Store.
4. Open **Daily Expenses** from the app drawer.

## Updating the app later

Change `app/src/main/assets/index.html` (that file is the whole app) and upload it again. GitHub builds a new APK automatically. Before uploading, raise `versionCode` in `app/build.gradle` by 1 (for example 1 → 2) so phones accept the update over the old version. Data on the phone is kept.

## Notes

- **Backup:** Settings (the gear icon) → **Export CSV** saves a file to Downloads and opens the share menu (Drive, Gmail, WhatsApp). Use **Import CSV** to restore entries on a new phone.
- **Signing key:** the app is signed with `app/release.keystore`, which is included so every build has the same signature. If you ever publish on the Play Store, create your own private key instead and don't share it publicly.
- **Requirements:** Android 7.0 or newer.
