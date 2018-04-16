# Tyrata Android App
## Installation
1. Clone or download the project
2. Open the project with Android Studio
3. Build the project
4. Run on Emulator or physical Android device

## Specs
The app targets (Android 4.2 JellyBean API 17 and later),
which is 96% of Android devices on market.

## Activities
#### 1. User activity (registration and log-in)
    For new user, you should register with your user information and then login.
#### 2. Main activity (main view navigation)
    In the main activity page, you can view the account information as well as a list of vehicles owned by this account.
    At the top right corner, you can see the two-level navigation hamburger button: 
* Add Car
* Report Accident
* Communication
    * GPS
    * BLUETOOTH
    * HTTP TEST
    * TEST XML
    * GET TIRESNAPSHOT LIST
    * GET DATABASE
* Sign Out
    
#### 3. Calibration activity (vehicle and tire)
##### * Add Car
    Click the "Add Car" button in the hamburger navigation bar, you can add three types of vehicles: four-wheel, ten-wheel and eighteen-wheel. 
    After you add a new vehicle, you'll be directed to main page and then click on the added vehicle in the list, you are able to calibrate/edit the 
    vehicle information, delete the vehicle or add tires to this vehicle.
##### * Add Tire
    Click on the tire you want to calibrate in the list of respective vehicle, you're able to calibrate/edit the tire information or delete this tire.

#### 4. Storage activity (local store and load)
    Click on "Communication" -> "GET DATABASE"
    Then the user, vehicle and tire informatio received from server will be saved into localdatabase.

#### 5. Bluetooth activity (discover, connect, and transmit/receive)
##### * Discover and connect
    Click on "Communication" -> "BLUETOOTH"
    You can see the paired device in the first part and the discovered devices in the second part, which you can connect the devices in the sedon part.
##### * transmit/receive
    Click on "Communication" -> "GET TIRESNAPSHOT LIST"
    Here you will get the snapshot sent from the simulator. The app will process the raw data and save them in local database.

#### 6. GPS activity (get device GPS)
##### Click on "Communication" -> "GPS"
    The GPS location will be displayed on the screen.

#### 7. HTTP activity (send/receive data to/from the database)
##### Click on "Communication" -> "HTTP TEST"
    You will be directed to a new page where you can test the HTTP connection.

## Communications
* The app communicates with the simulator through bluetooth
* The app communicates with the database through HTTP
* The message uses XML format, details for the message format can be found:
*     Bluetooth: https://docs.google.com/document/d/19h3uDUiwvbqEmfie1sN9KuPJ4GxLfGlR5GvPxORzuBs/edit?usp=sharing
*     HTTP: https://docs.google.com/document/d/1m62Nkwoneb2JllnBDGVYYLbOiGOqPR6h3vGn0bQhrZc/edit?usp=sharing