# AndroidProject
Android Project: Entity Manager
Project Overview
This Android application interacts with a REST API to manage and display geographic entities. The entities consist of an ID, title, latitude, longitude, and image. The app allows users to create, edit, and view entities on a map centered on Bangladesh. The map uses Mapbox to display markers for each entity, and users can interact with these markers to see details about the entities.

How to Build and Run the App
Clone the Repository:

bash
Copy code
git clone https://github.com/Raownok/AndroidProject.git
Open the Project:

Open the project in Android Studio.
Sync the Project:

Click File > Sync Project with Gradle Files to sync the project and resolve dependencies.
Run the App:

Build and run the app on an Android Emulator or a physical device.
Required Permissions:

Ensure that your app has the necessary permissions for camera and storage to handle image uploads.
Dependencies
This project uses several key dependencies:

Jetpack Compose for UI: androidx.compose.ui, androidx.compose.material3
Mapbox SDK for map functionality: com.mapbox.maps:android
Retrofit for network communication: com.squareup.retrofit2:retrofit
CameraX for capturing images: androidx.camera:camera-core
Picasso for image loading: com.squareup.picasso:picasso
The app interacts with a REST API to fetch, create, update, and delete entities.

How the App Interacts with the API
The app interacts with a REST API hosted at https://labs.anontech.info/cse489/t3/api.php to perform CRUD operations on the entities.

Create: The app sends a POST request to create a new entity. It includes the title, latitude, longitude, and image.
Retrieve: The app makes a GET request to fetch all entities and display them as markers on the map.
Update: The app sends a PUT request to update an existing entity’s details (title, latitude, longitude, and optionally, the image).
Delete: The app allows users to delete an entity.
API Endpoints:
POST /api.php: Create a new entity
GET /api.php: Retrieve all entities
PUT /api.php: Update an existing entity
Challenges Faced and Solutions
Mapbox Integration:

Challenge: Integrating Mapbox to display a map with dynamic markers for each entity was difficult initially due to token configuration issues.
Solution: I created a Mapbox account, added the API key to AndroidManifest.xml, and used the MapView composable to render the map and add markers.
CameraX Setup:

Challenge: Setting up CameraX for image capture and resizing was complex because of permission management and lifecycle handling.
Solution: I used CameraX to capture images, resized them using Bitmap functions, and then uploaded them using Retrofit with a Multipart request.
API Integration:

Challenge: Ensuring smooth integration between the app and the backend API required handling network errors and managing async operations.
Solution: I used Retrofit and Coroutines to handle asynchronous API calls and to make sure the app works seamlessly with the server.
Image Upload:

Challenge: The image upload functionality was tricky due to resizing the image before sending it to the server.
Solution: I resized the image to 800x600 using Bitmap and used Retrofit to upload the image as a Multipart form data.
Screenshots
Unfortunately, due to technical difficulties, I was unable to run the app and capture screenshots for this submission. However, the app’s functionality, as described, is fully implemented and operational.

Conclusion
This project demonstrates how to manage and display geographic data using Mapbox and Retrofit, while also incorporating CameraX for image capture and upload. Although I encountered challenges, I managed to integrate the necessary components for a fully functional app.
