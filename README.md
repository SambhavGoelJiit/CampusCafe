
## 🧑‍💻CampusCafe🍴

The project comprises of two apps, one for the admin and one for the user, these apps help to place and manage food orders in the college cafeteria. It specializes for dine in orders in cafeteria.



## ⚙️Installation

```
Install my UserApp.apk (for users)
Install my AdminApp.apk (for admin)
```
    
## ⚙️Setup
Clone the repository on your machine. Open the project on your IDE and connect it to firebase and add dependencies and everything will be setup.

Clone the Repository Repo

https://github.com/SambhavGoelJiit/campuscafe
https://github.com/SambhavGoelJiit/admincampuscafe

Open the Project in Android Studio. For installation Follow this

* Add the dependencies and connect with Firebase.

* Open CampusCafe App and Admin App into the Emulator or Physical Device.
## 📸 App Screenshots
![Algorithm schema](./images/schema.jpg)
## About CampusCafe
It uses firebase for Storaging Data. It uses Firebase Authentication for email based auth and Google auth, as well as Firebase Cloud Messaging Service.

* Fully functionable.

* Clean and Simple UI so that any people can use.
* View the menu created by admin in user app.
* Add to cart functionality, and no redundancy in cart.
* Active search from the complete menu.
* Basic token System implementaion in place of money.
* View all orders history and details.
* Expected time of arrival for each order.
* Check for availability of items before placing order in between checkout and payout time.

## Built With 🛠
* Kotlin - First class and official programming language for Android development.
* CampusCafe supports both email based and Social media authentication like Google authentication. Planning to add Facebook authentication too.

* Dependencies used:
    * AndroidX Core-KTX (1.12.0): Provides Kotlin extensions for core AndroidX libraries, simplifying and optimizing Android app development with Kotlin.

    * AndroidX AppCompat (1.6.1): Offers backward compatibility for modern Android features, ensuring a consistent look and feel across different Android versions.

    * Material Components for Android (1.10.0): Implements Google's Material Design, offering ready-to-use UI components for a visually appealing and consistent user experience.

    * ConstraintLayout (2.1.4): Allows for flexible and responsive layouts in Android apps, simplifying the creation of complex user interfaces.

    * Navigation Component - Fragment-KTX (2.7.5): Facilitates in-app navigation by providing a framework for implementing navigation between different app destinations.

    * Navigation Component - UI-KTX (2.7.5): Offers Kotlin extensions specifically for navigation-related UI components, streamlining navigation development with Kotlin.

    * RecyclerView (1.3.2): Efficiently displays large sets of data in a scrollable list or grid format, optimizing memory usage and UI performance.

    * Google Play Services - Auth (20.7.0): Enables authentication within apps using Google accounts, allowing users to sign in securely and easily.

    * ImageSlideshow by Deniz Coskun (0.1.2): A library that facilitates the creation of image slideshows within Android applications.

    * Firebase UI - Auth (7.2.0): Offers pre-built UI components to simplify Firebase authentication implementation, ensuring a seamless sign-in experience.

    * Firebase SDK - Auth (22.3.0): Provides tools and APIs for integrating Firebase authentication into Android apps, enabling secure user authentication.

    * Firebase SDK - Database (20.3.0): Allows real-time synchronization and data storage using Firebase Realtime Database within Android applications.

    * Firebase SDK - Storage (20.3.0): Enables efficient storage of user-generated content using Firebase Cloud Storage, supporting secure and scalable file uploads and downloads.

    * Firebase SDK - Messaging (23.3.1): Facilitates the sending and handling of push notifications within Android applications using Firebase Cloud Messaging (FCM).

    * JUnit (4.13.2): A testing framework for Java that aids in writing and running unit tests, ensuring code reliability and correctness.

    * AndroidX Test - JUnit (1.1.5): Provides extensions to the JUnit framework for writing Android instrumented tests, validating app behavior on a device or emulator.

    * Espresso Core (3.5.1): An Android testing framework that allows the creation of concise and reliable UI tests using an intuitive API.

    * Glide (4.16.0): A fast and efficient image loading library for Android that supports fetching, decoding, and displaying images easily.

    * SwipeRefreshLayout (1.1.0): Implements a standard pull-to-refresh UI pattern for Android apps, allowing users to update content with a swipe gesture.
## Package Structure 👿
```
com.example.campuscafe    # Root Package(user app)

├── adapter                     # adapter for inflating all views 
│   ├── BuyAgainAdapter
│   ├── CartAdapter
│   ├── MenuAdapter
│   ├── NotificationAdapter
│   ├── PopularAdapter
│   └── RecentBuyAdapter
|
├── Fragment                    # fragments for bottomSheetNav
│   ├── CartFragment
│   ├── HistoryFragment
│   ├── HomeFragment
│   ├── ProfileFragment
│   └── SearchFragment
|
├── model                       # type of view and its data
│   ├── CartItems
│   ├── MenuItems
│   ├── OrderDetails
│   └── UserModel
|
├── LoginActivity                   #  all activities
├── MainActivity
├── MenuBottomSheetFragment
├── MyFirebaseMessagingService          # for implementing firebase notification feature
├── OrderDetailsActivity
├── PayoutActivity
├── SignActivity
├── Splash_Screen
└── StartActivity
```
```
com.example.admincampuscafe    # Root Package(admin app)

├── adapter                     # adapter for inflating all views 
│   ├── MenuItemAdapter
│   ├── OrderDetailsAdapter
│   ├── PendingOrderAdapter
│   └── StatusAdapter
|
├── model                       # type of view and its data
│   ├── AllMenu
│   ├── OrderDetails
│   └── UserModel
|
├── AddItemActivity                 #  all activities
├── AllItemActivity
├── LoginActivity
├── MainActivity
├── OrderDetailsActivity
├── OrderStatusActivity
├── PendingOrderActivity
├── SignActivity
└── Splash_Screen
```
## Future Improvements ☠️
* Adding Facebook authentication as well.
* Working on improving the UI a little bit.
* Increase Accuracy of time estimation and handle more edge cases.
* Integrate Payment Gateways.
* Host it on my own server using MongoDB
* Create my own API
* Add notification at each order placement and recieve.