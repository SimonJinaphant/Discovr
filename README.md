# Discovr
A personalized UBC companion app.

Instructional and informational wiki: [Discovr Wiki](https://github.com/SimonJinaphant/Discovr/wiki)

### Setting up
Before continuing make sure you have Android Studio installed and set up.

1. Open Android Studio and select `Check out project from Version Control`. If you're inside a project already you can find yourself back to the home screen by choosing `File -> Close Project`
2. Select `GitHub` and enter your Github credentials.
3. For the Git Repository URL enter: `https://github.com/SimonJinaphant/Discovr.git`. You can leave the other fields blank.
4. It may take a couple of minutes for Gradle to finish building for the first time
   - If you run into errors regarding gradle build tools see the fix at the bottom.
   - You'll likely get an `Error Loading Project` because two modules (.iml files) don't exist. Ignore this error by selecting the remove option.
5. Make sure your Android phone is connected, or your emulator is configured, and press the green play button on the top toolbar.
   - If nothing happens, try making sure your phone is unlocked and press the play button again.

If you're able to see a map at this point then nothing went wrong (surprisingly).

Try clicking on the three dots on the top right corner and select either options!

### Fixing error with Gradle
If you ran into problem with gradle try the following fix:

1. For Windows click on`File -> Settings`, for Mac click `Android Studio -> Preferences`
2. In the search dialog on the top left corner type in `Android SDK`
3. Near the bottom you should see `Launch Standalone SDK Manager`, click on it and it should open a new window.
4. Under Tools select `Android SDK Build-tools` with the version `24.0.3` and make sure everything else is unselected unless you want to install some more stuff.
5. Click `Install (n) Package(s)`
6. Restart Android studio  

# Additional repositories
* [Discovr-Backend](https://github.com/SimonJinaphant/Discovr-Backend) contains all of our code for the backend web service which serves as a middleman for managing user events between this app and the remote database.
* [Discovr-Crawler](https://github.com/SimonJinaphant/Discovr-Crawler) contains the code for the web crawler which scrapes the UBC events calendar and stores the result into our remote database.

### Branching and Pull Request
Reminder! If implementing a feature, create a branch from master and develop the feature on that branch. Once the feature is completed, merge it to master through a pull request. Afterwards, delete the branch to clean up the repository. If modifying your feature, create a new branch from master instead of reviving the old feature branch.
